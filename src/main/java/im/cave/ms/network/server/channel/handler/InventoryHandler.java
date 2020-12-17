package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemBuffs;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.items.ScrollUpgradeInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.EquipmentEnchantType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.PlayerPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.Position;

import java.util.List;
import java.util.Map;

import static im.cave.ms.enums.EquipBaseStat.tuc;
import static im.cave.ms.enums.InventoryOperation.MOVE;
import static im.cave.ms.enums.InventoryOperation.REMOVE;
import static im.cave.ms.enums.InventoryOperation.UPDATE_QUANTITY;
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
                c.announce(PlayerPacket.inventoryOperation(true, false, REMOVE, oldPos, newPos, 0, item));
            } else {
                c.announce(PlayerPacket.inventoryOperation(true, false, UPDATE_QUANTITY, oldPos, newPos, 0, item));
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
            c.announce(PlayerPacket.inventoryOperation(true, false, MOVE, oldPos, newPos, 0, item));
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
                player.announce(PlayerPacket.showScrollUpgradeResult(false, success ? 1 : 0, scrollUpgradeInfo.getTitle(), prevEquip, equip));
                equip.updateToChar(player);
                if (equip.getBaseStat(tuc) > 0) {
                    scrolls = ItemConstants.getScrollUpgradeInfosByEquip(equip);
                    c.announce(PlayerPacket.scrollUpgradeDisplay(false, scrolls));
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
                c.announce(PlayerPacket.scrollUpgradeDisplay(false, scrolls));
                break;
            case ScrollTimerEffective:
                break;
        }
    }

    public static void handleReturnScroll(InPacket inPacket, MapleClient c) {
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
    }
}
