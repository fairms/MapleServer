package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.movement.Movement;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.enums.ShopRequestType;
import im.cave.ms.enums.ShopResultType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.NpcPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.network.server.service.EventManager;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.scripting.npc.NpcConversationManager;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.scripting.quest.QuestActionManager;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static im.cave.ms.enums.InventoryOperationType.ADD;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/30 19:10
 */
public class NpcHandler {
    private static final Logger log = LoggerFactory.getLogger(NpcHandler.class);

    public static void handleUserSelectNPC(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int objId = in.readInt();
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
            chr.setConversation(true);
            chr.setNpc(npc);
            chr.announce(WorldPacket.openTrunk(npcId, chr.getAccount()));
            return;
        }
        if (script == null) {
            NpcShop shop = NpcData.getShopById(npcId);
            if (npc.isShop()) {
                chr.setShop(shop);
                chr.announce(NpcPacket.openShop(npcId, 0, shop, chr.getRepurchaseItems()));
                chr.chatMessage(String.format("Opening shop %s", npc.getTemplateId()));
                return;
            } else {
                script = String.valueOf(npcId);
            }
        }
        String finalScript = script;
        EventManager.addEvent(() -> NpcScriptManager.getInstance().start(chr.getClient(), npcId, finalScript), 0);
    }

    public static void handleUserScriptMessageAnswer(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        NpcConversationManager cm = NpcScriptManager.getInstance().getCM(c);
        QuestActionManager qm = QuestScriptManager.getInstance().getQM(c);
        if (cm == null && qm == null) {
            throw new NullPointerException("NPC对话异常");
        }
        if (cm == null) {
            cm = qm;
        }
        byte lastType = in.readByte();
        byte action = in.readByte();
        NpcMessageType messageType = cm.getNpcScriptInfo().getMessageType();
        switch (messageType) {
            case AskText:
                String response = "";
                if (action == 1) {
                    response = in.readMapleAsciiString();
                }
                cm.getNpcScriptInfo().addResponse(response);
                break;
            case AskYesNo:
                cm.getNpcScriptInfo().addResponse((int) action);
                break;
            case AskMenu:
                int select;
                if (action == 0) {
                    select = -1;
                } else {
                    select = in.readInt();
                }
                cm.getNpcScriptInfo().addResponse(select);
                break;
            case AskAvatar:
                if (in.available() >= 4) {
                    in.readShort();
                    byte option = in.readByte();
                    byte submit = in.readByte();
                    if (submit == 1) {
                        cm.getNpcScriptInfo().addResponse(((int) option));
                    } else {
                        cm.getNpcScriptInfo().addResponse(-1);
                    }
                } else {
                    cm.getNpcScriptInfo().addResponse(-1);
                }
            default:
                cm.getNpcScriptInfo().addResponse((int) action);
                break;
        }
    }

    public static void handleNpcAnimation(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.NPC_ANIMATION.getValue());
        int objectID = in.readInt();
        byte oneTimeAction = in.readByte();
        byte chatIdx = in.readByte();
        int duration = in.readInt();
        byte keyPadState = 0;
        MovementInfo movement = null;
        MapleMapObj obj = player.getMap().getObj(objectID);
        if (obj instanceof Npc && ((Npc) obj).isMove()) {
            Npc npc = (Npc) obj;
            if (in.available() > 0) {
                movement = new MovementInfo(npc.getPosition(), npc.getVPosition());
                movement.decode(in);
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
                if (in.available() > 0) {
                    keyPadState = in.readByte();
                }
            }
        }
        player.getMap().broadcastMessage(NpcPacket.npcAnimation(objectID, oneTimeAction, chatIdx, duration, movement, keyPadState));
    }

    public static void handleUserShopRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte type = in.readByte();
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
            case BUY: {
                short itemIndex = in.readShort();
                int itemId = in.readInt();
                short quantity = in.readShort();
                NpcShopItem shopItem = shop.getItemByIndex(itemIndex);
                int index = -1;
                boolean repurchase = false;
                if (itemIndex >= shop.getItems().size()) {
                    List<NpcShopItem> repurchaseItems = player.getRepurchaseItems();
                    index = repurchaseItems.size() - 1 - (itemIndex - shop.getItems().size());
                    shopItem = player.getRepurchaseItems().get(index);
                    repurchase = true;
                }
                if (shopItem == null || shopItem.getItemId() != itemId) {
                    player.chatMessage("The server's item at that position was different than the client's.");
                    log.warn(String.format("Possible hack: expected shop itemId %d, got %d (chr %d)", shopItem.getItemId(), itemId, player.getId()));
                    return;
                }
                if (!player.canHold(itemId)) {
                    player.announce(NpcPacket.shopResult(ShopResultType.FullInvMsg, repurchase, index));
                    return;
                }
                if (shopItem.getTokenItemId() != 0) {
                    int cost = shopItem.getTokenPrice() * quantity;
                    if (player.hasItemCount(shopItem.getTokenItemId(), cost)) {
                        player.consumeItem(shopItem.getTokenItemId(), cost);
                    } else {
                        player.announce(NpcPacket.shopResult(ShopResultType.NotEnoughMesosMsg));
                        return;
                    }
                } else {
                    long cost = shopItem.getPrice() * quantity;
                    if (player.getMeso() < cost) {
                        player.announce(NpcPacket.shopResult(ShopResultType.NotEnoughMesosMsg));
                        return;
                    }
                    player.deductMoney(cost);
                }
                if (repurchase) {
                    shopItem = player.getRepurchaseItems().get(index);
                    Item item = shopItem.getItem();
                    item.setQuantity(shopItem.getQuantity());
                    player.getInventory(item.getInvType()).addItem(item);
                    player.announce(UserPacket.inventoryOperation(false, ADD, (short) item.getPos(), (short) -1, 0, item));
                    player.getRepurchaseItems().remove(index);
//                    player.setRepurchaseItems(player.getRepurchaseItems().stream().filter(Objects::nonNull).collect(Collectors.toList()));
                } else {
                    int itemQuantity = shopItem.getQuantity() > 0 ? shopItem.getQuantity() : 1;
                    Item itemCopy = ItemData.getItemCopy(itemId, false);
                    itemCopy.setQuantity((short) (quantity * itemQuantity));
                    player.addItemToInv(itemCopy);
                }
                player.announce(NpcPacket.shopResult(ShopResultType.Buy, repurchase, index));
                break;
            }
            case RECHARGE: {
                short pos = in.readShort();
                Item item = player.getConsumeInventory().getItem(pos);
                if (item == null || !ItemConstants.isRechargeable(item.getItemId())) {
                    player.chatMessage(String.format("Was not able to find a chargeable item at position %d.", pos));
                    return;
                }
                ItemInfo ii = ItemData.getItemInfoById(item.getItemId());
                long cost = ii.getSlotMax() - item.getQuantity();
                if (player.getMeso() < cost) {
                    player.announce(NpcPacket.shopResult(ShopResultType.NotEnoughMesosMsg));
                    return;
                }
                player.deductMoney(cost);
                item.setQuantity(ii.getSlotMax());
                player.announce(UserPacket.inventoryOperation(true,
                        InventoryOperationType.UPDATE_QUANTITY, pos, (short) 0, 0, item));
                player.announce(NpcPacket.shopResult(ShopResultType.RechargeSuccess));
                break;
            }
            case SELL: {
                int slot = in.readShort();
                int itemId = in.readInt();
                int quantity = in.readShort();
                InventoryType it = ItemConstants.getInvTypeByItemId(itemId);
                if (it == null) {
                    return;
                }
                Item item = player.getInventory(it).getItem((short) slot);
                if (item == null || item.getItemId() != itemId) {
                    player.chatMessage("Could not find that item.");
                    return;
                }
                int cost;
                if (ItemConstants.isEquip(itemId)) {
                    cost = ((Equip) item).getPrice();
                } else {
                    cost = ItemData.getItemInfoById(itemId).getPrice() * quantity;
                }
                NpcShopItem shopItem = new NpcShopItem();
                shopItem.setItemID(itemId);
                shopItem.setItem(item);
                shopItem.setPrice(cost);
                shopItem.setQuantity((short) quantity);
                shopItem.setMaxPerSlot((short) ItemData.getItemInfoById(itemId).getSlotMax());
                player.addRepurchaseItem(shopItem);
                player.consumeItem(itemId, quantity);
                player.addMeso(cost);
                player.announce(NpcPacket.shopResult(ShopResultType.SellResult, shop, player.getRepurchaseItems()));
                break;
            }
            case CLOSE:
                player.setShop(null);
                break;
            default:
                log.error(String.format("Unhandled shop request type %s", shr));
        }
    }
}
