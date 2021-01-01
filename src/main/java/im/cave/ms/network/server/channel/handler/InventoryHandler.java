package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.InventoryOperation;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemBuffs;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.items.ScrollUpgradeInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.EquipmentEnchantType;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.ItemGrade;
import im.cave.ms.enums.ScrollStat;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.network.packet.UserRemote;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.scripting.item.ItemScriptManager;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static im.cave.ms.enums.ChatType.Mob;
import static im.cave.ms.enums.ChatType.SystemNotice;
import static im.cave.ms.enums.EquipBaseStat.cuc;
import static im.cave.ms.enums.EquipBaseStat.tuc;
import static im.cave.ms.enums.InventoryOperationType.MOVE;
import static im.cave.ms.enums.InventoryOperationType.REMOVE;
import static im.cave.ms.enums.InventoryOperationType.UPDATE_QUANTITY;
import static im.cave.ms.enums.InventoryType.EQUIP;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/29 22:00
 */
public class InventoryHandler {

    public static void handleChangeInvPos(MapleClient c, InPacket inPacket) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        InventoryType invType = InventoryType.getTypeById(inPacket.readByte());
        if (invType == null) {
            return;
        }
        short oldPos = inPacket.readShort();
        short newPos = inPacket.readShort();
        short quantity = inPacket.readShort();
        InventoryType invTypeFrom = invType == EQUIP ? oldPos < 0 ? EQUIPPED : EQUIP : invType;
        InventoryType invTypeTo = invType == EQUIP ? newPos < 0 ? EQUIPPED : EQUIP : invType;
        Item item = player.getInventory(invTypeFrom).getItem(oldPos < 0 ? (short) -oldPos : oldPos);
        if (item == null) {
            return;
        }
        if (newPos == 0) {
            Drop drop;
            boolean fullDrop;
            if (!item.getInvType().isStackable() || quantity >= item.getQuantity()
                    || ItemConstants.isThrowingStar(item.getItemId()) || ItemConstants.isBullet(item.getItemId())) {
                fullDrop = true;
                player.getInventory(invTypeFrom).removeItem(item);
                item.drop();
                drop = new Drop(-1, item);
            } else {
                fullDrop = false;
                Item dropItem = ItemData.getItemCopy(item.getItemId(), false);
                dropItem.setQuantity(quantity);
                item.removeQuantity(quantity);
                drop = new Drop(-1, dropItem);
            }
            MapleMap map = player.getMap();
            Position position = player.getPosition();
            Foothold fh = map.findFootHoldBelow(new Position(position.getX(), position.getY() - GameConstants.DROP_HEIGHT));
            drop.setCanBePickedUpByPet(false);
            map.drop(drop, position, new Position(position.getX(), fh.getYFromX(position.getX())));
            if (fullDrop) {
                c.announce(UserPacket.inventoryOperation(true, REMOVE, oldPos, newPos, 0, item));
            } else {
                c.announce(UserPacket.inventoryOperation(true, UPDATE_QUANTITY, oldPos, newPos, 0, item));
            }
        } else {
            Item swapItem = player.getInventory(invTypeTo).getItem(newPos < 0 ? (short) -newPos : newPos);
            if (invType == EQUIP && invTypeFrom != invTypeTo) {
                // TODO: verify job (see item.RequiredJob), level, stat, unique equip requirements
                if (invTypeFrom == EQUIPPED) {
                    player.unequip(item);
                } else {
                    player.equip(item);
                    if (swapItem != null) {
                        player.unequip(swapItem);
                    }
                }
            }
            item.setPos(newPos < 0 ? -newPos : newPos);
            if (swapItem != null) {
                swapItem.setPos(oldPos);
            }
            c.announce(UserPacket.inventoryOperation(true, MOVE, oldPos, newPos, 0, item));
        }

    }

    public static void handleUseItem(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        short pos = inPacket.readShort();
        int itemId = inPacket.readInt();
        Item item = player.getConsumeInventory().getItem(pos);
        if (item == null || item.getItemId() != itemId) {
            return;
        }
        ItemInfo itemInfo = ItemData.getItemInfoById(itemId);
        Map<SpecStat, Integer> specStats = itemInfo.getSpecStats();
        if (specStats.size() > 0) {
            ItemBuffs.giveItemBuffsFromItemID(player, itemId);
            player.consumeItem(item);
        } else {
            player.consumeItem(item);
        }
    }

    public static void handleEquipEnchanting(InPacket inPacket, MapleClient c) {
        byte val = inPacket.readByte();
        EquipmentEnchantType type = EquipmentEnchantType.getByVal(val);
        MapleCharacter player = c.getPlayer();
        if (type == null) {
            player.dropMessage("未知的装备强化请求:" + val);
            return;
        }
        switch (type) {
            case ScrollUpgradeRequest: {
                player.setTick(inPacket.readInt());
                short pos = inPacket.readShort();
                int scrollId = inPacket.readInt();
                Inventory iv = pos < 0 ? player.getEquippedInventory() : player.getEquipInventory();
                Equip equip = (Equip) iv.getItem((short) (pos < 0 ? -pos : pos));
                Equip prevEquip = equip.deepCopy();
                List<ScrollUpgradeInfo> scrolls = ItemConstants.getScrollUpgradeInfosByEquip(equip);
                ScrollUpgradeInfo scrollUpgradeInfo = scrolls.get(scrollId);
                player.consumeItem(ItemConstants.SPELL_TRACE_ID, scrollUpgradeInfo.getCost());
                boolean success = scrollUpgradeInfo.applyTo(equip);
                equip.reCalcEnchantmentStats();
                player.announce(UserPacket.showScrollUpgradeResult(false, success ? 1 : 0, scrollUpgradeInfo.getTitle(), prevEquip, equip));
                equip.updateToChar(player);
                if (equip.getBaseStat(tuc) > 0) {
                    scrolls = ItemConstants.getScrollUpgradeInfosByEquip(equip);
                    c.announce(UserPacket.scrollUpgradeDisplay(false, scrolls));
                }
                break;
            }
            case ScrollUpgradeDisplay:
                int pos = inPacket.readInt();
                Inventory iv = pos < 0 ? player.getEquippedInventory() : player.getEquipInventory();
                Equip equip = (Equip) iv.getItem((short) (pos < 0 ? -pos : pos));
                if (equip == null) {
                    return;
                }
                List<ScrollUpgradeInfo> scrolls = ItemConstants.getScrollUpgradeInfosByEquip(equip);
                c.announce(UserPacket.scrollUpgradeDisplay(false, scrolls));
                break;
            case ScrollTimerEffective:
                break;
        }
    }

    public static void handleUserPortalScrollUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        short pos = inPacket.readShort();
        Item item = player.getConsumeInventory().getItem(pos);
        int itemId = inPacket.readInt();
        if (item.getItemId() != itemId) {
            return;
        }
        ItemInfo itemInfo = ItemData.getItemInfoById(itemId);
        int moveTo = itemInfo.getMoveTo();
        player.changeMap(moveTo);
        player.consumeItem(itemId, 1);
    }

    public static void handleUserUpgradeItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        short uPos = inPacket.readShort(); //Use Position
        short ePos = inPacket.readShort(); //Eqp Position
        Item scroll = player.getInventory(InventoryType.CONSUME).getItem(uPos);
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos < 0 ? (short) -ePos : ePos);
        if (scroll == null || equip == null || equip.hasSpecialAttribute(EquipSpecialAttribute.Vestige)) {
            player.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        }
        int scrollId = scroll.getItemId();
        boolean success = false;
        boolean boom = false;
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoById(scrollId).getScrollStats();
        if (vals.size() > 0) {
            if (equip.getBaseStat(tuc) <= 0) {
                player.announce(UserPacket.inventoryRefresh(true));
                return;
            }
            int chance = vals.getOrDefault(ScrollStat.success, 100);
            int curse = vals.getOrDefault(ScrollStat.cursed, 0);
            success = Util.succeedProp(chance);
            if (success) {
                boolean chaos = vals.containsKey(ScrollStat.randStat);
                if (chaos) {
                    boolean noNegative = vals.containsKey(ScrollStat.noNegative);
                    int max = vals.containsKey(ScrollStat.incRandVol) ? ItemConstants.RAND_CHAOS_MAX : ItemConstants.INC_RAND_CHAOS_MAX;
                    for (EquipBaseStat ebs : ScrollStat.getRandStats()) {
                        int cur = (int) equip.getBaseStat(ebs);
                        if (cur == 0) {
                            continue;
                        }
                        int randStat = Util.getRandom(max);
                        randStat = !noNegative && Util.succeedProp(50) ? -randStat : randStat;
                        equip.addStat(ebs, randStat);
                    }
                } else {
                    for (Map.Entry<ScrollStat, Integer> entry : vals.entrySet()) {
                        ScrollStat ss = entry.getKey();
                        int val = entry.getValue();
                        if (ss.getEquipStat() != null) {
                            equip.addStat(ss.getEquipStat(), val);
                        }
                    }
                }
                equip.addStat(tuc, -1);
                equip.addStat(cuc, 1);
            } else {
                if (curse > 0) {
                    boom = Util.succeedProp(curse);
                    if (boom && !equip.hasAttribute(EquipAttribute.ProtectionScroll)) {
                        player.consumeItem(equip);
                    } else {
                        boom = false;
                    }
                }
                if (!equip.hasAttribute(EquipAttribute.UpgradeCountProtection)) {
                    equip.addStat(tuc, -1);
                }
            }
            equip.removeAttribute(EquipAttribute.ProtectionScroll);
            equip.removeAttribute(EquipAttribute.LuckyDay);
            equip.removeAttribute(EquipAttribute.UpgradeCountProtection);
            c.announce(UserRemote.showItemUpgradeEffect(player.getId(), success, false, scrollId, equip.getItemId(), boom));
            if (!boom) {
                equip.reCalcEnchantmentStats();
                equip.updateToChar(player);
            }
            player.consumeItem(scroll);
        } else {
            player.chatMessage("Could not find scroll data.");
        }

    }

    public static void handleUserScriptItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        short pos = inPacket.readShort();
        int itemId = inPacket.readInt();
        int quantity = inPacket.readInt();
        Item item = player.getConsumeInventory().getItem(pos);
        if (item == null || item.getItemId() != itemId) {
            item = player.getCashInventory().getItem(pos);
        }
        if (item == null || item.getItemId() != itemId) {
            return;
        }
        String script = String.valueOf(itemId);
        int npcId = 0;
        ItemInfo ii = ItemData.getItemInfoById(itemId);
        if (ii.getScript() != null && !"".equals(ii.getScript())) {
            script = ii.getScript();
            npcId = ii.getNpcID();
        }
        ItemScriptManager.getInstance().startScript(itemId, script, npcId, c);
    }

    public static void handleUserFlameItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        short uPos = inPacket.readShort();
        short ePos = inPacket.readShort();
        Item flame = player.getInventory(InventoryType.CONSUME).getItem(uPos);
        if (flame == null) {
            flame = player.getCashInventory().getItem(uPos);
        }
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos < 0 ? (short) -ePos : ePos);
        if (flame == null || equip == null) {
            player.chatMessage(SystemNotice, "Could not find flame or equip.");
            return;
        }
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoById(flame.getItemId()).getScrollStats();
        if (vals.size() > 0) {
            int reqEquipLevelMax = vals.getOrDefault(ScrollStat.reqEquipLevelMax, 250);

            if (equip.getRLevel() + equip.getIIncReq() > reqEquipLevelMax) {
                //
                return;
            }

            boolean success = Util.succeedProp(vals.getOrDefault(ScrollStat.success, 100));

            if (success) {
                boolean eternalFlame = vals.getOrDefault(ScrollStat.createType, 6) >= 7;
                equip.randomizeFlameStats(eternalFlame); // Generate high stats if it's an eternal/RED flame only.
            }

            equip.updateToChar(player);
            c.announce(UserRemote.showItemUpgradeEffect(player.getId(), success, false, flame.getItemId(), equip.getItemId(), false));
            player.consumeItem(flame);
        }

    }

    //todo 谜之蛋
    public static void handleUserOpenMysteryEgg(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        short pos = inPacket.readShort();
        Item item = player.getConsumeInventory().getItem(pos);
        int itemId = inPacket.readInt();
        byte unk = inPacket.readByte();
        player.consumeItem(itemId, 1);
    }

    public static void handleUserGatherItemRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        InventoryType invType = InventoryType.getTypeById(inPacket.readByte());
        if (invType == null) {
            return;
        }
        Inventory inv = player.getInventory(invType);
        List<Item> items = new ArrayList<>(inv.getItems());
        items.sort(Comparator.comparingInt(Item::getPos));
        List<InventoryOperation> operations = new ArrayList<>();
        for (Item item : items) {
            int freeSlot = inv.getNextFreeSlot();
            if (freeSlot < item.getPos()) {
                short oldPos = (short) item.getPos();
                item.setPos(freeSlot);
                operations.add(new InventoryOperation(MOVE, oldPos, (short) freeSlot, item));
            }
        }
        c.announce(UserPacket.inventoryOperation(true, operations));
        c.announce(UserPacket.gatherItemResult(invType.getVal()));
    }

    public static void handleUserSortItemRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        InventoryType invType = InventoryType.getTypeById(inPacket.readByte());
        if (invType == null) {
            return;
        }
        List<InventoryOperation> operations = new ArrayList<>();
        Inventory inv = player.getInventory(invType);
        List<Item> items = new ArrayList<>(inv.getItems());
        items.sort(Comparator.comparingInt(Item::getItemId));
        for (Item item : items) {
            if (item.getPos() != items.indexOf(item) + 1) {
                operations.add(new InventoryOperation(InventoryOperationType.REMOVE, (short) item.getPos(), (short) 0, item));
            }
        }
        for (Item item : items) {
            int index = items.indexOf(item) + 1;
            if (item.getPos() != index) {
                item.setPos(index);
                operations.add(new InventoryOperation(InventoryOperationType.ADD, (short) item.getPos(), (short) 0, item));
            }
        }
        c.announce(UserPacket.inventoryOperation(true, operations));
        c.announce(UserPacket.sortItemResult(invType.getVal()));
    }

    public static void handleUserSlotExpandRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Account account = player.getAccount();
        int accId = inPacket.readInt();
        int charId = inPacket.readInt();
        int i = inPacket.readInt();
        int sn = inPacket.readInt();
        boolean cash = inPacket.readByte() != 0;
        InventoryType invType = InventoryType.getTypeById((byte) (i - 10));
        if (player.getId() != charId || account.getId() != accId || invType == null) {
            return;
        }
        player.getInventory(invType).expandSlot(6);
        player.announce(UserPacket.invExpandResult(i, account.getPoint(), cash));
    }

    public static void handleUserConsumeCashItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        short pos = inPacket.readShort();
        int itemId = inPacket.readInt();
        InventoryType inventoryType = ItemConstants.getInvTypeByItemId(itemId);
        if (inventoryType == null) {
            return;
        }
        Item item = player.getInventory(inventoryType).getItem(pos);
        if (item.getItemId() != itemId) {
            return;
        }
        player.consumeItem(itemId, 1);
        player.enableAction();
    }

    public static void handleUserLotteryItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        short pos = inPacket.readShort();
        int itemId = inPacket.readInt();
        InventoryType invType = ItemConstants.getInvTypeByItemId(itemId);
        if (invType == null) {
            return;
        }
        Item item = player.getInventory(invType).getItem(pos);
        if (item.getItemId() != itemId) {
            return;
        }
        player.dropMessage(String.format("%d 道具使用", itemId));
        player.consumeItem(itemId, 1);
    }

    //潜能附加
    public static void handleUserItemOptionUpgradeItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        short uPos = inPacket.readShort();
        short ePos = inPacket.readShort();
        inPacket.readByte(); //enchantSkill
        Item scroll = player.getConsumeInventory().getItem(uPos);
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos);
        if (scroll == null || equip == null) {
            player.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        } else if (!ItemConstants.canEquipHavePotential(equip)) {
            return;
        }
        int scrollItemId = scroll.getItemId();
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoById(scrollItemId).getScrollStats();
        int chance = vals.getOrDefault(ScrollStat.success, 100);
        int curse = vals.getOrDefault(ScrollStat.cursed, 0);
        boolean success = Util.succeedProp(chance);
        if (success) {
            short val;
            int thirdLineChance = ItemConstants.THIRD_LINE_CHANCE;
            switch (scrollItemId / 10) {
                case 204940: // Rare Pot
                case 204941:
                case 204942:
                case 204943:
                case 204944:
                case 204945:
                case 204946:
                    val = ItemGrade.HiddenRare.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                case 204970: // Epic pot
                case 204971:
                    val = ItemGrade.HiddenEpic.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                case 204974: // Unique Pot
                case 204975:
                case 204976:
                case 204979:
                    val = ItemGrade.HiddenUnique.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                case 204978: // Legendary Pot
                    val = ItemGrade.HiddenLegendary.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                default:
                    player.chatMessage(Mob, "Unhandled scroll " + scrollItemId);
                    player.enableAction();
                    return;
            }
        }
        player.getMap().broadcastMessage(UserRemote.showItemUpgradeEffect(player.getId(), success, false, scrollItemId, equip.getItemId(), false));
        equip.updateToChar(player);
        player.consumeItem(scroll);
    }

    public static void handleUserItemReleaseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        short uPos = inPacket.readShort();
        short ePos = inPacket.readShort();
        Item item = player.getConsumeInventory().getItem(uPos); // 放大镜
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos);
        if (equip == null) {
            player.chatMessage(SystemNotice, "Could not find equip.");
            return;
        }
        boolean base = equip.getOptionBase(0) < 0;
        boolean bonus = equip.getOptionBonus(0) < 0;
        if (base && bonus) {
            equip.releaseOptions(true);
            equip.releaseOptions(false);
        } else {
            equip.releaseOptions(bonus);
        }
        player.getMap().broadcastMessage(UserRemote.showItemReleaseEffect(player.getId(), ePos, bonus));
        equip.updateToChar(player);
    }
}
