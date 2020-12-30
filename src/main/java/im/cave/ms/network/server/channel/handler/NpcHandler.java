package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.movement.Movement;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.enums.ShopRequestType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.NpcPacket;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.network.server.service.EventManager;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.scripting.npc.NpcConversationManager;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.scripting.quest.QuestActionManager;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/30 19:10
 */
public class NpcHandler {
    private static final Logger log = LoggerFactory.getLogger(NpcHandler.class);

    public static void handleUserSelectNPC(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int objId = inPacket.readInt();
        MapleMap map = player.getMap();
        MapleMapObj obj = map.getObj(objId);
        if (!(obj instanceof Npc)) {
            player.chatMessage(ChatType.Purple, "Unknown Error");
            return;
        }
        Npc npc = (Npc) obj;
        userSelectNpc(player, npc);
    }

    public static void talkToNPC(MapleCharacter chr, int npcId) {
        Npc npc = NpcData.getNpc(npcId);
        if (npc != null) {
            userSelectNpc(chr, npc);
        }
    }

    public static void userSelectNpc(MapleCharacter chr, Npc npc) {
        int npcId = npc.getTemplateId();
        String script = npc.getScripts().get(0);
        if (npc.getTrunkPut() > 0 || npc.getTrunkGet() > 0) {
            chr.announce(WorldPacket.openTrunk(npcId, chr.getAccount()));
            return;
        }
        if (script == null) {
            NpcShop shop = NpcData.getShopById(npcId);
            if (npc.isShop()) {
                chr.setShop(shop);
                chr.announce(NpcPacket.openShop(npcId, 0, shop));
                chr.chatMessage(String.format("Opening shop %s", npc.getTemplateId()));
                return;
            } else {
                script = String.valueOf(npcId);
            }
        }
        String finalScript = script;
        EventManager.addEvent(() -> NpcScriptManager.getInstance().start(chr.getClient(), npcId, finalScript), 0);
    }

    public static void handleUserScriptMessageAnswer(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        NpcConversationManager cm = NpcScriptManager.getInstance().getCM(c);
        QuestActionManager qm = QuestScriptManager.getInstance().getQM(c);
        if (cm == null && qm == null) {
            return;
        }
        if (cm == null) {
            cm = qm;
        }
        byte lastType = inPacket.readByte();
        byte action = inPacket.readByte();
        if (action == -1) {
            cm.dispose();
            return;
        }
        NpcMessageType messageType = cm.getNpcScriptInfo().getMessageType();
        switch (messageType) {
            case AskText:
                if (action == 1) {
                    String response = inPacket.readMapleAsciiString();
                    cm.getNpcScriptInfo().addResponse(response);
                } else {
                    cm.dispose();
                }
                break;
            case AskYesNo:
                cm.getNpcScriptInfo().addResponse(action);
                break;
            case AskMenu:
                if (action == 0) {
                    cm.getNpcScriptInfo().addResponse(-1);
                    return;
                }
                int select = inPacket.readInt();
                cm.getNpcScriptInfo().addResponse(select);
                break;
            case AskAvatar:
                if (inPacket.available() >= 4) {
                    inPacket.readShort();
                    byte option = inPacket.readByte();
                    byte submit = inPacket.readByte();
                    if (submit == 1) {
                        cm.getNpcScriptInfo().addResponse(((int) option));
                    } else {
                        cm.getNpcScriptInfo().addResponse(-1);
                    }
                } else {
                    cm.getNpcScriptInfo().addResponse(-1);
                }
                break;
            default:
                cm.dispose();
        }
    }

    public static void handleNpcAnimation(InPacket inPacket, MapleClient c) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.NPC_ANIMATION.getValue());
        int objectID = inPacket.readInt();
        byte oneTimeAction = inPacket.readByte();
        byte chatIdx = inPacket.readByte();
        int duration = inPacket.readInt();
        byte keyPadState = 0;
        MovementInfo movement = null;
        MapleMapObj obj = c.getPlayer().getMap().getObj(objectID);
        if (obj instanceof Npc && ((Npc) obj).isMove()) {
            Npc npc = (Npc) obj;
            if (inPacket.available() > 0) {
                movement = new MovementInfo(npc.getPosition(), npc.getVPosition());
                movement.decode(inPacket);
                for (Movement m : movement.getMovements()) {
                    Position pos = m.getPosition();
                    Position vPos = m.getVPosition();
                    if (pos != null) {
                        npc.setPosition(pos);
                    }
                    if (vPos != null) {
                        npc.setVPosition(vPos);
                    }
                    npc.setMoveAction(m.getMoveAction());
                    npc.setFh(m.getFh());
                }
                if (inPacket.available() > 0) {
                    keyPadState = inPacket.readByte();
                }
            }
        }
        c.announce(NpcPacket.npcAnimation(objectID, oneTimeAction, chatIdx, duration, movement, keyPadState));
    }

    public static void handleUserShopRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte type = inPacket.readByte();
        ShopRequestType shr = ShopRequestType.getByVal(type);
        if (shr == null) {
            return;
        }
        NpcShop shop = player.getShop();
        if (shop == null) {
            player.chatMessage("You are currently not in a shop.");
            return;
        }
        switch (shr) {
            case BUY:
//                short itemIndex = inPacket.readShort();
//                int itemId = inPacket.readInt();
//                short quantity = inPacket.readShort();
//                NpcShopItem nsi = shop.getItemByIndex(itemIndex);
//                if (nsi == null || nsi.getItemID() != itemId) {
//                    player.chatMessage("The server's item at that position was different than the client's.");
//                    log.warn(String.format("Possible hack: expected shop itemID %d, got %d (chr %d)", nsi.getItemID(), itemId, player.getId()));
//                    return;
//                }
//                if (!player.canHold(itemId)) {
////                    player.announce(ShopDlg.shopResult(new MsgShopResult(ShopResultType.FullInvMsg)));
//                    return;
//                }
//                if (nsi.getTokenItemID() != 0) {
//                    int cost = nsi.getTokenPrice() * quantity;
//                    if (chr.hasItemCount(nsi.getTokenItemID(), cost)) {
//                        chr.consumeItem(nsi.getTokenItemID(), cost);
//                    } else {
//                        chr.write(ShopDlg.shopResult(new MsgShopResult(ShopResultType.NotEnoughMesosMsg)));
//                        return;
//                    }
//                } else {
//                    long cost = nsi.getPrice() * quantity;
//                    if (chr.getMoney() < cost) {
//                        chr.write(ShopDlg.shopResult(new MsgShopResult(ShopResultType.NotEnoughMesosMsg)));
//                        return;
//                    }
//                    chr.deductMoney(cost);
//                }
//                int itemQuantity = nsi.getQuantity() > 0 ? nsi.getQuantity() : 1;
//                Item item = ItemData.getItemDeepCopy(itemID);
//                item.setQuantity(quantity * itemQuantity);
//                chr.addItemToInventory(item);
//                chr.write(ShopDlg.shopResult(new MsgShopResult(ShopResultType.Success)));
                break;
            case RECHARGE:
//                short slot = inPacket.decodeShort();
//                item = chr.getConsumeInventory().getItemBySlot(slot);
//                if (item == null || !ItemConstants.isRechargable(item.getItemId())) {
//                    chr.chatMessage(String.format("Was not able to find a rechargable item at position %d.", slot));
//                    return;
//                }
//                ItemInfo ii = ItemData.getItemInfoByID(item.getItemId());
//                long cost = ii.getSlotMax() - item.getQuantity();
//                if (chr.getMoney() < cost) {
//                    chr.write(ShopDlg.shopResult(new MsgShopResult(ShopResultType.NotEnoughMesosMsg)));
//                    return;
//                }
//                chr.deductMoney(cost);
//                item.addQuantity(ii.getSlotMax());
//                chr.write(WvsContext.inventoryOperation(true, false,
//                        InventoryOperation.UPDATE_QUANTITY, slot, (short) 0, 0, item));
//                chr.write(ShopDlg.shopResult(new MsgShopResult(ShopResultType.Success)));
                break;
            case SELL:
//                slot = inPacket.decodeShort();
//                itemID = inPacket.decodeInt();
//                quantity = inPacket.decodeShort();
//                InvType it = ItemConstants.getInvTypeByItemID(itemID);
//                item = chr.getInventoryByType(it).getItemBySlot(slot);
//                if (item == null || item.getItemId() != itemID) {
//                    chr.chatMessage("Could not find that item.");
//                    return;
//                }
//                if (ItemConstants.isEquip(itemID)) {
//                    cost = ((Equip) item).getPrice();
//                } else {
//                    cost = ItemData.getItemInfoByID(itemID).getPrice() * quantity;
//                }
//                chr.consumeItem(itemID, quantity);
//                chr.addMoney(cost);
//                chr.write(ShopDlg.shopResult(new MsgShopResult(ShopResultType.Success)));
                break;
            case CLOSE:
                player.setShop(null);
                break;
            default:
                log.error(String.format("Unhandled shop request type %s", shr));
        }
    }
}
