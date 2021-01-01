package im.cave.ms.network.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.Trunk;
import im.cave.ms.client.cashshop.CashShopItem;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.CashShopAction;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.network.server.cashshop.CashShopServer;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;

import java.util.List;
import java.util.Map;

import static im.cave.ms.tools.DateUtil.MAX_TIME;
import static im.cave.ms.tools.DateUtil.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 12/30 17:21
 */
public class CashShopPacket {

    public static OutPacket getWrapToCashShop(MapleCharacter player) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SET_CASH_SHOP.getValue());
        PacketHelper.addCharInfo(outPacket, player);
        return outPacket;
    }

    public static OutPacket setCashShop(CashShopServer cashShopServer) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SET_CASH_SHOP_INFO.getValue());
        outPacket.writeInt(0); // block items
        Map<Integer, CashShopItem> modifiedItems = cashShopServer.getModifiedItems();
        outPacket.writeShort(modifiedItems.size());
        for (CashShopItem modifiedItem : modifiedItems.values()) {
            modifiedItem.encodeModified(outPacket);
        }
        outPacket.writeShort(0);
        outPacket.writeInt(0); //randomItemInfo.size
        Map<Integer, Byte> hotItems = cashShopServer.getHotItems();
        outPacket.writeInt(hotItems.size()); //hot items
        hotItems.forEach((sn, clazz) -> {
            outPacket.write(clazz);
            outPacket.writeInt(sn);
        });
        outPacket.writeZeroBytes(12);
        outPacket.writeLong(1);
        outPacket.writeLong(0);
        outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        return outPacket;
    }

    public static OutPacket queryCashResult(Account acc) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_QUERY_CASH_RESULT.getValue());
        outPacket.writeInt(acc.getPoint());
        outPacket.writeInt(acc.getVoucher());
        outPacket.writeInt(acc.getPoint());
        return outPacket;
    }

    public static OutPacket loadLocker(Account acc) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        outPacket.write(CashShopAction.Res_LoadLocker_Done.getVal());
        outPacket.write(0);
        Trunk cashTrunk = acc.getCashTrunk();
        List<Item> cashTrunkItems = cashTrunk.getItems();
        outPacket.writeShort(cashTrunkItems.size());
        for (Item item : cashTrunkItems) {
            outPacket.writeLong(item.getCashItemSerialNumber());
            outPacket.writeLong(acc.getId());
            outPacket.writeInt(item.getItemId());
            outPacket.writeInt(ItemData.getSn(item.getItemId()));
            outPacket.writeShort(item.getQuantity());
            outPacket.writeAsciiString(item.getOwner(), 13);
            outPacket.writeLong(item.getExpireTime());
            outPacket.writeLong(item.getExpireTime() == MAX_TIME ? 0x1F : 0);
            outPacket.writeZeroBytes(15);
            outPacket.write(1);
            PacketHelper.addItemInfo(outPacket, item);
        }
        outPacket.writeShort(GameConstants.MAX_LOCKER_SIZE);
        outPacket.writeShort(acc.getCharacterSlots());
        outPacket.writeShort(0);
        outPacket.writeShort(acc.getCharacters().size());
        return outPacket;
    }

    public static OutPacket loadGift() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        outPacket.write(CashShopAction.Res_LoadGift_Done.getVal());
        outPacket.writeShort(0);
        return outPacket;
    }

    public static OutPacket loadCart(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        outPacket.write(CashShopAction.Res_SetCart_Done.getVal());
        for (Integer sn : chr.getCashCart()) {
            outPacket.writeInt(sn);
        }
        for (int i = 0; i < 12 - chr.getCashCart().size(); i++) {
            outPacket.writeInt(0);
        }
        return outPacket;
    }

    public static OutPacket cashShopEvent() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_EVENT_INFO.getValue());
        outPacket.writeLong(ZERO_TIME);
        outPacket.writeLong(MAX_TIME);
        outPacket.writeInt(0);
        return outPacket;
    }

    public static OutPacket cashItemBuyResult(Account acc, Item item) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        outPacket.write(CashShopAction.Res_Buy_Done.getVal());
        outPacket.writeLong(item.getCashItemSerialNumber());
        outPacket.writeLong(acc.getId());
        outPacket.writeInt(item.getItemId());
        outPacket.writeInt(ItemData.getSn(item.getItemId()));
        outPacket.writeShort(item.getQuantity());
        outPacket.writeAsciiString(item.getOwner(), 13);
        outPacket.writeLong(item.getExpireTime());
        outPacket.writeLong(item.getExpireTime() == MAX_TIME ? 0x1F : 0);
        outPacket.writeZeroBytes(15);
        outPacket.write(1);
        PacketHelper.addItemInfo(outPacket, item);
        outPacket.writeLong(0);
        outPacket.write(0);
        return outPacket;
    }

    public static OutPacket moveItemToTrunkResult(Item item) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        outPacket.write(CashShopAction.Res_Move_Item_Done.getVal());
        outPacket.write(1);
        outPacket.write(item.getPos());
        PacketHelper.addItemInfo(outPacket, item);
        outPacket.writeInt(0);
        outPacket.write(0);
        return outPacket;
    }

    public static OutPacket getItemFromTrunkResult(Account account, Item item) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        outPacket.write(CashShopAction.Res_Get_Item_Done.getVal());
        outPacket.writeLong(item.getCashItemSerialNumber());
        outPacket.writeLong(account.getId());
        outPacket.writeInt(item.getItemId());
        outPacket.writeInt(ItemData.getSn(item.getItemId()));
        outPacket.writeShort(item.getQuantity());
        outPacket.writeAsciiString(item.getOwner(), 13);
        outPacket.writeLong(item.getExpireTime());
        outPacket.writeLong(item.getExpireTime() == MAX_TIME ? 0x1F : 0);
        outPacket.writeZeroBytes(15);
        outPacket.write(1);
        PacketHelper.addItemInfo(outPacket, item);
        return outPacket;

    }
}
