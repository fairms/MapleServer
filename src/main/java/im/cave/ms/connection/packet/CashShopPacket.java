package im.cave.ms.connection.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.CashShopItem;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.PotionPot;
import im.cave.ms.client.character.items.WishedItem;
import im.cave.ms.client.storage.Locker;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.connection.server.cashshop.CashShopServer;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.CashItemType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;

import java.util.List;
import java.util.Map;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 12/30 17:21
 */
public class CashShopPacket {

    public static OutPacket getWrapToCashShop(MapleCharacter player) {
        OutPacket out = new OutPacket(SendOpcode.SET_CASH_SHOP);
        PacketHelper.addCharInfo(out, player);
        return out;
    }

    public static OutPacket setCashShop(CashShopServer cashShopServer) {
        OutPacket out = new OutPacket(SendOpcode.SET_CASH_SHOP_INFO);
        out.writeInt(0); // block items
        Map<Integer, CashShopItem> modifiedItems = cashShopServer.getModifiedItems();
        out.writeShort(modifiedItems.size());
        for (CashShopItem modifiedItem : modifiedItems.values()) {
            modifiedItem.encodeModified(out);
        }
        out.writeShort(0);
        out.writeInt(0); //randomItemInfo.size
        Map<Integer, Byte> hotItems = cashShopServer.getHotItems();
        out.writeInt(hotItems.size()); //hot items
        hotItems.forEach((sn, clazz) -> {
            out.write(clazz);
            out.writeInt(sn);
        });
        out.writeZeroBytes(12);
        out.writeLong(1);
        out.writeLong(0);
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        return out;
    }

    public static OutPacket buyPackageDone(List<Item> items, Account acc) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_BuyPackage_Done.getVal());
        out.write(items.size());
        for (Item item : items) {
            out.writeLong(item.getCashItemSerialNumber());
            out.writeLong(acc.getId());
            out.writeInt(item.getItemId());
            out.writeInt(ItemData.getSn(item.getItemId()));
            out.writeShort(item.getQuantity());
            out.writeAsciiString(item.getOwner(), 13);
            out.writeLong(item.getExpireTime());
            out.writeLong(item.getExpireTime() == MAX_TIME ? 0x1e : 0);
            out.writeZeroBytes(15);
            out.writeBool(true); //modified
            item.encode(out);
            out.write(0);
            out.writeLong(0);
        }
        return out;
    }

    public static OutPacket buyDone(Account acc, Item item) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_Buy_Done.getVal());
        out.writeLong(item.getCashItemSerialNumber());
        out.writeLong(acc.getId());
        out.writeInt(item.getItemId());
        out.writeInt(ItemData.getSn(item.getItemId()));
        out.writeShort(item.getQuantity());
        out.writeAsciiString(item.getOwner(), 13);
        out.writeLong(item.getExpireTime());
        out.writeLong(item.getExpireTime() == MAX_TIME ? 0x1e : 0);
        out.writeZeroBytes(15);
        out.writeBool(true); //modified
        item.encode(out);
        out.write(0);
        out.writeLong(0);
        return out;
    }

    public static OutPacket buyFailed(CashItemType reason) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_Buy_Failed.getVal());
        out.write(reason.getVal());
        out.writeInt(0);
        return out;
    }

    public static OutPacket queryCashResult(Account acc) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_QUERY_CASH_RESULT);
        out.writeInt(acc.getCash());
        out.writeInt(acc.getVoucher());
        out.writeInt(acc.getPoint());
        return out;
    }

    public static OutPacket initLockerDone(Account acc) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_LoadLocker_Done.getVal());
        out.write(0);
        Locker locker = acc.getLocker();
        List<Item> cashItems = locker.getItems();
        out.writeShort(cashItems.size());
        for (Item item : cashItems) {
            out.writeLong(item.getCashItemSerialNumber());
            out.writeLong(acc.getId());
            out.writeInt(item.getItemId());
            out.writeInt(ItemData.getSn(item.getItemId()));
            out.writeShort(item.getQuantity());
            out.writeAsciiString(item.getOwner(), 13);
            out.writeLong(item.getExpireTime());
            out.writeLong(item.getExpireTime() == MAX_TIME ? 0x1F : 0);
            out.writeZeroBytes(15);
            out.write(1);
            item.encode(out);
        }
        out.writeShort(GameConstants.MAX_LOCKER_SIZE);
        out.writeShort(acc.getCharacterSlots());
        out.writeShort(0);
        out.writeShort(acc.getCharacters().size());
        return out;
    }

    public static OutPacket initGiftDone() {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_LoadGift_Done.getVal());
        out.writeShort(0);
        return out;
    }

    public static OutPacket initWishDone(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_LoadWish_Done.getVal());
        for (WishedItem item : chr.getWishedItems()) {
            out.writeInt(item.getItemId());
        }
        for (int i = 0; i < 12 - chr.getWishedItems().size(); i++) {
            out.writeInt(0);
        }
        return out;
    }

    public static OutPacket initCashShopEvent() {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_EVENT_INFO);
        out.writeLong(ZERO_TIME);
        out.writeLong(MAX_TIME);
        out.writeInt(0);
        return out;
    }

    public static OutPacket moveLtoSDone(Item item) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_MoveLtoS_Done.getVal());
        out.write(1);
        out.writeShort(item.getPos());
        item.encode(out);
        out.write(0);
        out.writeInt(0);
        return out;
    }

    public static OutPacket moveStoLDone(Account account, Item item) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT.getValue());
        out.write(CashItemType.Res_MoveStoL_Done.getVal());
        out.write(0);
        out.writeLong(item.getCashItemSerialNumber());
        out.writeLong(account.getId());
        out.writeInt(item.getItemId());
        out.writeInt(ItemData.getSn(item.getItemId()));
        out.writeShort(item.getQuantity());
        out.writeAsciiString(item.getOwner(), 13);
        out.writeLong(item.getExpireTime());
        out.writeLong(item.getExpireTime() == MAX_TIME ? 0x1e : 0);
        out.writeZeroBytes(15);
        out.writeBool(true); //modified
        item.encode(out);
        return out;
    }

    public static OutPacket enableEquipSlotExtDone(int remainDays) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_EnableEquipSlotExt_Done.getVal());
        out.writeShort(0);
        out.writeShort(remainDays);
        out.writeInt(0);
        return out;
    }

    public static OutPacket createPotionPotDone(PotionPot pot) {
        OutPacket out = new OutPacket(SendOpcode.POTION_POT_CREATE);
        pot.encode(out);
        return out;
    }

    public static OutPacket destroyDone(long serialNumber) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_Destroy_Done.getVal());
        out.writeLong(serialNumber);
        return out;
    }

    public static OutPacket rebateDone(long serialNumber) {
        OutPacket out = new OutPacket(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        out.write(CashItemType.Res_Rebate_Done.getVal());
        out.writeLong(serialNumber);
        return out;

    }
}
