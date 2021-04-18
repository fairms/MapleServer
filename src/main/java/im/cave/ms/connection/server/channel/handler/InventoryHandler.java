package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.InventoryOperation;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.ItemBuffs;
import im.cave.ms.client.character.items.PotionPot;
import im.cave.ms.client.character.items.ScrollUpgradeInfo;
import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.MessagePacket;
import im.cave.ms.connection.packet.NpcPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.UserRemote;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.BroadcastMsgType;
import im.cave.ms.enums.DropTimeoutStrategy;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.EquipmentEnchantType;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.ItemGrade;
import im.cave.ms.enums.ScrollStat;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.StringData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.scripting.item.ItemScriptManager;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static im.cave.ms.constants.ItemConstants.Item_Tag;
import static im.cave.ms.constants.ItemConstants.Maple_Any_Door;
import static im.cave.ms.constants.ItemConstants.Platinum_Scissors_of_Karma;
import static im.cave.ms.constants.ItemConstants.Vicious_Hammer;
import static im.cave.ms.enums.ChatType.Mob;
import static im.cave.ms.enums.ChatType.SystemNotice;
import static im.cave.ms.enums.EquipBaseStat.cuc;
import static im.cave.ms.enums.EquipBaseStat.tuc;
import static im.cave.ms.enums.InventoryOperationType.MOVE;
import static im.cave.ms.enums.InventoryOperationType.REMOVE;
import static im.cave.ms.enums.InventoryOperationType.UPDATE_QUANTITY;
import static im.cave.ms.enums.InventoryType.CASH_EQUIP;
import static im.cave.ms.enums.InventoryType.EQUIP;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/29 22:00
 */
public class InventoryHandler {

    public static void handleChangeInvPos(MapleClient c, InPacket in) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        InventoryType invType = InventoryType.getTypeById(in.readByte());
        if (invType == null) {
            return;
        }
        short oldPos = in.readShort();
        short newPos = in.readShort();
        short quantity = in.readShort();
        InventoryType invTypeFrom = invType == EQUIP || invType == CASH_EQUIP ? oldPos < 0 ? EQUIPPED : invType : invType;
        InventoryType invTypeTo = invType == EQUIP || invType == CASH_EQUIP ? newPos < 0 ? EQUIPPED : invType : invType;
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
            drop.setTimeoutStrategy(DropTimeoutStrategy.FFA.getVal());
            map.drop(drop, position, new Position(position.getX(), fh.getYFromX(position.getX())));
            if (fullDrop) {
                c.announce(UserPacket.inventoryOperation(true, REMOVE, oldPos, newPos, 0, item));
            } else {
                c.announce(UserPacket.inventoryOperation(true, UPDATE_QUANTITY, oldPos, newPos, 0, item));
            }
        } else {
            Item swapItem = player.getInventory(invTypeTo).getItem(newPos < 0 ? (short) -newPos : newPos);
            if ((invType == EQUIP || invType == CASH_EQUIP) && invTypeFrom != invTypeTo) {
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

    public static void handleUseItem(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        short pos = in.readShort();
        int itemId = in.readInt();
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

    public static void handleEquipEnchanting(InPacket in, MapleClient c) {
        byte val = in.readByte();
        EquipmentEnchantType type = EquipmentEnchantType.getByVal(val);
        MapleCharacter player = c.getPlayer();
        if (type == null) {
            player.dropMessage("未知的装备强化请求:" + val);
            return;
        }
        switch (type) {
            case ScrollUpgradeRequest: {
                player.setTick(in.readInt());
                short pos = in.readShort();
                int scrollId = in.readInt();
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
                int pos = in.readInt();
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
            case HyperUpgradeDisplay:
                pos = in.readInt();
                iv = pos < 0 ? player.getEquippedInventory() : player.getEquipInventory();
                equip = (Equip) iv.getItem((short) (pos < 0 ? -pos : pos));
                if (equip == null) {
                    return;
                }
                if (equip.hasSpecialAttribute(EquipSpecialAttribute.Vestige) || !ItemConstants.isUpgradable(equip.getItemId())) {
                    c.announce(UserPacket.showUnknownEnchantFailResult((byte) 0));
                    return;
                }
                c.announce(UserPacket.hyperUpgradeDisplay(equip, false, 1, 1000, 0, true));
            case MiniGameDisplay:
                c.announce(UserPacket.miniGameDisplay());
                break;
            case HyperUpgradeRequest:
                player.setTick(in.readInt());
                short ePos = in.readShort();
                boolean extraChanceFromMiniGame = in.readByte() != 0;
                boolean equippedInv = ePos < 0;
                Inventory inv = equippedInv ? player.getEquippedInventory() : player.getEquipInventory();
                equip = (Equip) inv.getItem((short) Math.abs(ePos));
                if (equip == null) {
                    player.chatMessage("Could not find the given equip.");
                    player.write(UserPacket.showUnknownEnchantFailResult((byte) 0));
                    return;
                }

                if (!ItemConstants.isUpgradable(equip.getItemId()) ||
                        (equip.getBaseStat(tuc) != 0) ||
                        player.getEquipInventory().getEmptySlots() == 0 ||
                        equip.getChuc() >= GameConstants.getMaxStars(equip) ||
                        equip.hasSpecialAttribute(EquipSpecialAttribute.Vestige)) {
                    player.chatMessage("Equipment cannot be enhanced.");
                    player.write(UserPacket.showUnknownEnchantFailResult((byte) 0));
                    return;
                }
                //todo calc cost

                Equip oldEquip = equip.deepCopy();
//                int successProp = GameConstants.getEnchantmentSuccessRate(equip);
                int successProp = 1000;
                int destroyProp = 0;
                if (extraChanceFromMiniGame) {
                    successProp *= 1.045;
                }
                boolean success = Util.succeedProp(successProp, 1000);
                boolean boom = false;
                boolean canDegrade = equip.isSuperiorEqp() ? equip.getChuc() > 0 : equip.getChuc() > 5 && equip.getChuc() % 5 != 0;

                if (success) {
                    equip.setChuc((short) (equip.getChuc() + 1));
                    equip.setDropStreak(0);
                } else if (Util.succeedProp(destroyProp, 1000)) {
                    equip.setChuc((short) 0);
                    equip.addSpecialAttribute(EquipSpecialAttribute.Vestige); //痕迹？
                    boom = true;
                    if (equippedInv) {
                        player.unequip(equip);
                        equip.setPos(player.getEquipInventory().getNextFreeSlot());
                        equip.updateToChar(player);
                        c.write(UserPacket.inventoryOperation(true, MOVE, ePos, (short) equip.getPos(), 0, equip));
                    }
                    if (!equip.isSuperiorEqp()) {
                        equip.setChuc((short) Math.min(12, equip.getChuc()));
                    } else {
                        equip.setChuc((short) 0);
                    }
                } else if (canDegrade) {
                    equip.setChuc((short) (equip.getChuc() - 1));
                    equip.setDropStreak(equip.getDropStreak() + 1);
                }
                //player.consume(starItem,cost) 消耗星星
                equip.reCalcEnchantmentStats();
                oldEquip.reCalcEnchantmentStats();
                equip.updateToChar(player);
                c.write(UserPacket.showUpgradeResult(oldEquip, equip, success, boom, canDegrade));
                player.enableAction();
                break;
        }
    }

    public static void handleUserPortalScrollUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        short pos = in.readShort();
        Item item = player.getConsumeInventory().getItem(pos);
        int itemId = in.readInt();
        if (item.getItemId() != itemId) {
            return;
        }
        ItemInfo itemInfo = ItemData.getItemInfoById(itemId);
        int moveTo = itemInfo.getMoveTo();
        player.changeMap(moveTo);
        player.consumeItem(itemId, 1);
    }

    public static void handleUserUpgradeItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort(); //Use Position
        short ePos = in.readShort(); //Eqp Position
        int quantity = in.readInt(); // use quantity only on limit break
        Item scroll = player.getInventory(InventoryType.CONSUME).getItem(uPos);
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos < 0 ? (short) -ePos : ePos);
        if (scroll == null || equip == null || equip.hasSpecialAttribute(EquipSpecialAttribute.Vestige)) {
            player.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        }
        int scrollId = scroll.getItemId();
        boolean success;
        boolean boom = false;
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoById(scrollId).getScrollStats();
        if (vals.size() > 0) {
            if (vals.containsKey(ScrollStat.incALB)) { //limit break
                int chance = vals.getOrDefault(ScrollStat.success, 100);
                int succeed = 0;
                for (int i = 0; i < quantity; i++) {
                    if (Util.succeedProp(chance)) {
                        succeed++;
                    }
                }
                int incALB = 0;
                if (succeed > 0) {
                    incALB = vals.get(ScrollStat.incALB) * succeed;
                    equip.addLimitBreak(incALB);
                    equip.updateToChar(player);
                }
                player.announce(UserPacket.openLimitBreakUI(player, succeed > 0, scroll, incALB, equip));
                player.getMap().broadcastMessage(UserRemote.showItemUpgradeEffect(player.getId(), succeed > 0, false, scrollId, equip.getItemId(), false));
                player.consumeItem(scrollId, quantity);
                return;
            }

            if (equip.getBaseStat(tuc) <= 0) {
                player.announce(UserPacket.inventoryRefresh(true));
                return;
            }
            int chance = vals.getOrDefault(ScrollStat.success, 100);
            int curse = vals.getOrDefault(ScrollStat.cursed, 0);
            success = Util.succeedProp(chance);
            if (success) {
                //todo 白衣 纯白 etc...
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
            player.getMap().broadcastMessage(UserRemote.showItemUpgradeEffect(player.getId(), success, false, scrollId, equip.getItemId(), boom));
            if (!boom) {
                equip.reCalcEnchantmentStats();
                equip.updateToChar(player);
            }
            player.consumeItem(scroll);
        } else {
            player.chatMessage("Could not find scroll data.");
        }

    }

    public static void handleUserScriptItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        short pos = in.readShort();
        int itemId = in.readInt();
        int quantity = in.readInt();
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

    //todo
    public static void handleUserAdditionalSlotExtendItemUseRequest(InPacket in, MapleClient c) {

    }

    public static void handleUserFlameItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort();
        short ePos = in.readShort();
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
    public static void handleUserOpenMysteryEgg(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        short pos = in.readShort();
        Item item = player.getConsumeInventory().getItem(pos);
        int itemId = in.readInt();
        byte unk = in.readByte();
        player.consumeItem(itemId, 1);
    }

    public static void handleUserGatherItemRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        InventoryType invType = InventoryType.getTypeById(in.readByte());
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

    public static void handleUserSortItemRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        InventoryType invType = InventoryType.getTypeById(in.readByte());
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

    public static void handleUserSlotExpandRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Account account = player.getAccount();
        int accId = in.readInt();
        int charId = in.readInt();
        int i = in.readInt();
        int sn = in.readInt();
        boolean cash = in.readByte() != 0;
        InventoryType invType = InventoryType.getTypeById((byte) (i - 10));
        if (player.getId() != charId || account.getId() != accId || invType == null) {
            return;
        }
        player.getInventory(invType).expandSlot(6);
        player.announce(UserPacket.invExpandResult(i, account.getMaplePoint(), cash));
    }

    public static void handleUserConsumeCashItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        chr.setTick(in.readInt());

        short uPos = in.readShort();
        int itemId = in.readInt();
        //通用检查
        InventoryType inventoryType = ItemConstants.getInvTypeByItemId(itemId);
        if (inventoryType == null) {
            return;
        }
        Item item = chr.getInventory(inventoryType).getItem(uPos);
        if (item.getItemId() != itemId) {
            return;
        }
        CashItemActuator.dispatch(item, chr, in);

        if (itemId / 10000 == 515) {
            ItemInfo ii = ItemData.getItemInfoById(itemId);
            int gender = ii.getGender();
            boolean choice = ii.isChoice();
            int incCharmExp = ii.getIncCharmExp();
            String script = String.format("cash_%d", itemId);
            if (gender != 2 && gender != chr.getGender()) {
                chr.announce(MessagePacket.broadcastMsg("性别不符", BroadcastMsgType.ALERT));
                chr.enableAction();
                return;
            }
            ItemScriptManager.getInstance().startScript(itemId, script, 0, c);
            return;
        }

        switch (itemId) {
            case Item_Tag: {
                short ePos = in.readShort();
                break;
            }
            case Vicious_Hammer: {
                int inc = in.readInt();
                short ePos = in.readShort();
                break;
            }
            case Platinum_Scissors_of_Karma: {
                break;
            }
            case Maple_Any_Door: {
                in.readByte();
                int targetId = in.readInt();
                chr.changeMap(targetId);
            }
        }
        chr.consumeItem(itemId, 1, false);
    }

    public static void handleUserLotteryItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        short pos = in.readShort();
        int itemId = in.readInt();
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
    public static void handleUserItemOptionUpgradeItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort();
        short ePos = in.readShort();
        in.readByte(); //enchantSkill
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

    public static void handleUserItemReleaseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort(); //20000 快捷放大镜
        short ePos = in.readShort();
        Item item = player.getConsumeInventory().getItem(uPos); // todo 放大镜
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

    /*
        药剂罐 开始
     */
    public static void handlePotionPotIncRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        in.readByte();
        int itemId = in.readInt(); //5821000
        short uPos = in.readShort();
        Item toUse = player.getCashInventory().getItem(uPos);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId != 5821000) {
            player.enableAction();
            return;
        }
        PotionPot potionPot = player.getPotionPot();
        boolean useItem = potionPot.addMaxValue();
        if (useItem) {
            player.consumeItem(itemId, 1);
        }
        c.announce(potionPot.updatePotionPot());
        c.announce(potionPot.showPotionPotMsg(1, 3));
    }

    //todo
    public static void handlePotionPotUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short pos = in.readShort();
        int itemId = in.readInt();
        Item potItem = player.getCashInventory().getItem(pos);
        if (potItem == null || potItem.getItemId() != itemId) {
            player.enableAction();
            return;
        }
        PotionPot potionPot = player.getPotionPot();
        int healHp = player.getMaxHP() - player.getHp();
        int healMp = player.getMaxMP() - player.getHp();
        if (healHp == 0 && healMp == 0) {
            c.announce(potionPot.showPotionPotMsg(0, 0));
            return;
        }
        if (healHp > potionPot.getHp() && healMp > potionPot.getMp()) {
            c.announce(potionPot.updatePotionPot());
            c.announce(potionPot.showPotionPotMsg(0, 6));
        }
    }

    public static void handleUserUpgradeAssistItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        in.readInt();
        short uPos = in.readShort();
        short ePos = in.readShort();
        in.readByte();
        Item scroll = player.getConsumeInventory().getItem(uPos);
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos < 0 ? (short) -ePos : ePos);
        if (scroll == null || equip == null) {
            player.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        }
        int scrollID = scroll.getItemId();
        switch (scrollID) {
            case 2532000: // Safety Scroll
            case 2532001: // Pet Safety Scroll
            case 2532002: // Safety Scroll
            case 2532003: // Safety Scroll
            case 2532004: // Pet Safety Scroll
            case 2532005: // Safety Scroll
                equip.addAttribute(EquipAttribute.UpgradeCountProtection);
                break;
            case 2530000: // Lucky Day
            case 2530002: // Lucky Day
            case 2530003: // Pet Lucky Day
            case 2530004: // Lucky Day
            case 2530006: // Pet Lucky Day
                equip.addAttribute(EquipAttribute.LuckyDay);
                break;
            case 2531000: // Protection Scroll
            case 2531001:
            case 2531004:
            case 2531005:
                equip.addAttribute(EquipAttribute.ProtectionScroll);
                break;
        }
        player.getMap().broadcastMessage(UserRemote.showItemUpgradeEffect(player.getId(), true, false, scrollID, equip.getItemId(), false));
        equip.updateToChar(player);
        player.consumeItem(scroll);
    }

    //强化卷
    public static void handleUserHyperUpgradeItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        in.readInt();
        short uPos = in.readShort();
        short ePos = in.readShort();
        in.readByte();
        Item scroll = player.getConsumeInventory().getItem(uPos);
        InventoryType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) player.getInventory(invType).getItem(ePos < 0 ? (short) -ePos : ePos);
        if (scroll == null || equip == null) {
            player.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        }
        int scrollId = scroll.getItemId();
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoById(scrollId).getScrollStats();

    }

    //todo
    public static void handleUserAvatarModifyCouponUseRequest(InPacket in, MapleClient c) {
        int uPos = in.readInt();
        int itemId = in.readInt();
        MapleCharacter player = c.getPlayer();
        Item item = player.getCashInventory().getItem((short) uPos);
        if (item.getItemId() != itemId) {
            return;
        }
        player.dropMessage("使用道具:" + StringData.getItemName(itemId) + " ID:" + itemId);
        //01 01 00 00 00 02 00 00 00 64 95 4E 00 00 00 00发型相同 0x1e9
        List<Integer> options = new ArrayList<>();
        player.announce(NpcPacket.avatarChangeSelector(uPos, itemId, options));
    }


    public static void handlePotionPotAddRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort();
        int itemId = in.readInt();
        in.readInt();
        Item item = player.getCashInventory().getItem(uPos);
        if (item.getItemId() != itemId) {
            player.chatMessage("物品不存在");
            return;
        }
        int playerMaxMP = player.getMaxMP();
        int playerMaxHP = player.getMaxHP();
        PotionPot potionPot = player.getPotionPot();
        int max = potionPot.getMax();
        int hp = potionPot.getHp();
        int mp = potionPot.getMp();
        ItemInfo ii = ItemData.getItemInfoById(item.getItemId());
        Map<SpecStat, Integer> specStats = ii.getSpecStats();
        int addHp = 0, addMp = 0;
        for (SpecStat stat : specStats.keySet()) {
            Integer i = specStats.getOrDefault(stat, 0);
            switch (stat) {
                case hpR:
                    addHp = addHp + i * playerMaxHP;
                    break;
                case mpR:
                    addMp = addMp + i * playerMaxMP;
                    break;
                case hp:
                    addHp += i;
                    break;
                case mp:
                    addMp += i;
                    break;
            }
        }
        addHp *= 1.2;
        addMp *= 1.2;


        int hpNeedConsume = hp == 0 ? 0 : (max - hp) / addHp;
        int mpNeedConsume = mp == 0 ? 0 : (max - mp) / addMp;
        int need = Math.max(hpNeedConsume, mpNeedConsume);
        int quantity = item.getQuantity();
        int consume = Math.min(need, quantity);
        player.consumeItem(item, consume);
        potionPot.setHp(Math.min(max, (hp + consume * addHp)));
        potionPot.setMp(Math.min(max, (mp + consume + addMp)));

        player.announce(potionPot.updatePotionPot());
        player.announce(potionPot.showPotionPotMsg(2, 0));
    }

    public static void handleUserItemSkillSocketUpgradeItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort();
        short ePos = (short) Math.abs(in.readShort());
        Item item = player.getConsumeInventory().getItem(uPos);
        Equip equip = (Equip) player.getEquippedInventory().getItem(ePos);
        if (item == null || equip == null || !ItemConstants.isWeapon(equip.getItemId()) ||
                !ItemConstants.isSoulEnchanter(item.getItemId()) || equip.getRLevel() + equip.getIIncReq() < ItemConstants.MIN_LEVEL_FOR_SOUL_SOCKET) {
            player.enableAction();
            return;
        }
        int successProp = ItemData.getItemInfoById(item.getItemId()).getScrollStats().get(ScrollStat.success);
        boolean success = Util.succeedProp(successProp);
        if (success) {
            equip.setSoulSocketId((short) (item.getItemId() % ItemConstants.SOUL_ENCHANTER_BASE_ID));
            equip.updateToChar(player);
        }
        player.getMap().broadcastMessage(UserRemote.showItemSkillSocketUpgradeEffect(player.getId(), success));
        player.consumeItem(item);

    }


    //todo 添加技能？
    public static void handleUserItemSkillOptionUpgradeItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short uPos = in.readShort();
        short ePos = (short) Math.abs(in.readShort());
        Item item = player.getConsumeInventory().getItem(uPos);
        Equip equip = (Equip) player.getEquippedInventory().getItem(ePos);
        if (item == null || equip == null || !ItemConstants.isWeapon(equip.getItemId()) ||
                !ItemConstants.isSoulEnchanter(item.getItemId()) || equip.getRLevel() + equip.getIIncReq() < ItemConstants.MIN_LEVEL_FOR_SOUL_SOCKET) {
            player.enableAction();
            return;
        }
        equip.setSoulOptionId((short) (1 + item.getItemId() % ItemConstants.SOUL_ITEM_BASE_ID));
        short option = ItemConstants.getSoulOptionFromSoul(equip.getSoulOptionId());
        int skillId = ItemConstants.getSoulSkillFromSoulID(equip.getSoulOptionId());
        equip.setSoulOption(option);
        equip.updateToChar(player);
        player.consumeItem(item);
        player.addSkill(skillId, 1, 1);
        player.getMap().broadcastMessage(UserRemote.showItemSkillOptionUpgradeEffect(player.getId(), true, false));
    }


    public static void handleHexagonalCubeModified(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        chr.setTick(in.readInt());
        List<Integer> options = new ArrayList<>();
        int i = in.readInt();
        for (int i1 = 0; i1 < i; i1++) {
            options.add(i1);
        }
        chr.announce(UserPacket.hexagonalCubeModifiedResult());
    }

    public static void handleUniqueCubeModified(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        chr.setTick(in.readInt());

        int unk = in.readInt();
        int line = 0;

        chr.announce(UserPacket.uniqueCubeModifiedResult(line));
    }

    public static void handleUserItemSlotExtendItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        chr.setTick(in.readInt());

        short uPos = in.readShort();
        short ePos = in.readShort();
        Item item = chr.getConsumeInventory().getItem(uPos);
        Item equipItem = chr.getEquipInventory().getItem(ePos);
        if (item == null || equipItem == null) {
            chr.chatMessage("Could not find either the use item or the equip.");
            return;
        }

        int itemID = item.getItemId();
        Equip equip = (Equip) equipItem;
        int successChance = ItemData.getItemInfoById(itemID).getScrollStats().getOrDefault(ScrollStat.success, 100);
        boolean success = Util.succeedProp(successChance);
        if (success) {
            switch (itemID) {
                case 2049505: // Gold Potential Stamp
                case 2049517:
                    equip.setOption(2, equip.getRandomOption(false, 2), false);
                    break;
                default:
                    chr.chatMessage("Unhandled slot extend item " + itemID);
                    return;
            }
            equip.updateToChar(chr);
        }
        chr.consumeItem(item);
        chr.announce(UserRemote.showItemUpgradeEffect(chr.getId(), success, false, itemID, equip.getItemId(), false));
    }

    public static void handlePotionOptionSetRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        PotionPot potionPot = player.getPotionPot();
        boolean autoAddPotion = in.readByte() != 0;
        boolean autoAddAlchemyPotion = in.readByte() != 0;
        if (potionPot == null) {
            return;
        }
        potionPot.setAutoAddPotion(autoAddPotion);
        potionPot.setAutoAddPotion(autoAddAlchemyPotion);
    }
}
