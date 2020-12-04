package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemBuffs;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.items.SpecStat;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.net.packet.PlayerPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

import java.util.Map;

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
    public static void handleChangeInvPos(MapleClient c, SeekableLittleEndianAccessor slea) {
        MapleCharacter player = c.getPlayer();
        player.setTick(slea.readInt());
        InventoryType invType = InventoryType.getTypeById(slea.readByte());
        if (invType == null) {
            return;
        }
        short oldPos = slea.readShort();
        short newPos = slea.readShort();
        short quantity = slea.readShort();
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
                Item dropItem = ItemData.getItemCopy(item.getItemId());
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

    public static void handleUseItem(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(slea.readInt());
        short pos = slea.readShort();
        int itemId = slea.readInt();
        Item item = player.getConsumeInventory().getItem(pos);
        if (item == null || item.getItemId() != itemId) {
            return;
        }
        ItemInfo itemInfo = ItemData.getItemById(itemId);
        Map<SpecStat, Integer> specStats = itemInfo.getSpecStats();
        if (specStats.size() > 0) {
            ItemBuffs.giveItemBuffsFromItemID(player, itemId);
            player.consumeItem(item);
        } else {
            player.consumeItem(item);
        }
    }
}
