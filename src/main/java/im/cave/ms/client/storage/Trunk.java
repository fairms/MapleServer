package im.cave.ms.client.storage;

import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.provider.data.ItemData;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.trunk
 * @date 1/6 23:59
 */
@Entity
@DiscriminatorValue("Trunk")
public class Trunk extends Storage {
    private long meso;

    public Trunk() {
        super(GameConstants.DEFAULT_TRUNK_SLOTS);
    }

    @Override
    public void putItem(Item item, int quantity) {
        Item curItem = getItemByItemId(item.getItemId());
        if (curItem == null || curItem.getInvType() == InventoryType.EQUIP) {
            Item newItem = ItemConstants.isEquip(item.getItemId())
                    ? item
                    : ItemData.getItemCopy(item.getItemId(), false);
            newItem.setQuantity(quantity);
            getItems().add(newItem);
            sort(false);
        } else {
            curItem.setQuantity(curItem.getQuantity() + quantity);
        }
    }

    public void removeItem(Item item, short quantity) {
        if (item.getType() == Item.Type.EQUIP || quantity == item.getQuantity()) {
            removeItem(item);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }
    }

    @Override
    public void sort(boolean proactive) {
        Comparator<Item> byType = Comparator.comparingInt(value -> value.getInvType().getVal());
        Comparator<Item> byItemId = Comparator.comparingInt(Item::getItemId);
        Comparator<Item> byLevel = Comparator.comparingInt(value -> {
            if (value instanceof Equip) {
                return ((Equip) value).getRLevel();
            } else {
                return 0;
            }
        });
        if (proactive) {
            getItems().sort(byType.thenComparing(byItemId).thenComparing(byLevel));
        } else {
            getItems().sort(byType);
        }
    }

    public long getMeso() {
        return meso;
    }

    public void addMeso(long amount) {
        if (canAddMeso(amount)) {
            meso += amount;
        }
    }

    private boolean canAddMeso(long amount) {
        return getMeso() + amount <= GameConstants.MAX_MONEY;
    }

    public void encode(OutPacket out, InventoryType type) {
        encode(out, Mask.getMaskByInvType(type));
    }

    public void encode(OutPacket out, Mask mask) {
        out.write(getSlots());
        out.writeLong(mask.getVal());
        ArrayList<Item> itemList = new ArrayList<>();
        if (mask == Mask.ALL) {
            out.writeLong(getMeso());
            out.write(getItems().size());
            for (Item item : getItems()) {
                item.encode(out);
            }
            return;
        }
        if ((mask.getVal() & Mask.MESO.getVal()) != 0) {
            out.writeLong(getMeso());
        }
        if ((mask.getVal() & Mask.EQUIP.getVal()) != 0) {
            itemList.addAll(getItems(InventoryType.EQUIP));
        }
        if ((mask.getVal() & Mask.CONSUME.getVal()) != 0) {
            itemList.addAll(getItems(InventoryType.CONSUME));
        }
        if ((mask.getVal() & Mask.INSTALL.getVal()) != 0) {
            itemList.addAll(getItems(InventoryType.INSTALL));
        }
        if ((mask.getVal() & Mask.ETC.getVal()) != 0) {
            itemList.addAll(getItems(InventoryType.ETC));
        }
        if ((mask.getVal() & Mask.CASH.getVal()) != 0) {
            itemList.addAll(getItems(InventoryType.CASH));
        }
        out.write(itemList.size());
        for (Item item : itemList) {
            item.encode(out);
        }
    }

    public enum Mask {
        MESO(0x2),
        EQUIP(0x04),
        CONSUME(0x08),
        INSTALL(0x10),
        ETC(0x20),
        CASH(0x40),
        ITEM(0x7C),
        ALL(0x7E);

        private final byte val;

        Mask(byte val) {
            this.val = val;
        }

        Mask(int val) {
            this.val = (byte) val;
        }

        public byte getVal() {
            return val;
        }

        public static Mask getMaskByInvType(InventoryType type) {
            return Arrays.stream(values()).filter(mask -> mask.getVal() == type.getBitfieldEncoding()).findAny().orElse(null);
        }
    }
}
