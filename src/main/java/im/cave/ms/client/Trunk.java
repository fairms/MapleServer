package im.cave.ms.client;

import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.PacketHelper;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.Util;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "trunk")
public class Trunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "trunkId")
    private List<Item> items = new ArrayList<>();
    private long money;
    private byte slots;

    public Trunk() {
    }

    public Trunk(byte slots) {
        this.slots = slots;
    }

    public List<Item> getItems(InventoryType type) {
        return items.stream().filter(item -> item.getInvType().equals(type)).collect(Collectors.toList());
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public byte getSlotCount() {
        return slots;
    }

    public void setSlotCount(byte slots) {
        this.slots = slots;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean canAddMoney(long amount) {
        return getMoney() + amount <= GameConstants.MAX_MONEY;
    }

    public void addMoney(long reqMoney) {
        if (canAddMoney(reqMoney)) {
            setMoney(getMoney() + reqMoney);
        }
    }

    public void addItem(Item item, short quantity) {
        Item curItem = getItemByItemID(item.getItemId());
        if (curItem == null || curItem.getInvType() == InventoryType.EQUIP) {
            Item newItem = ItemConstants.isEquip(item.getItemId())
                    ? item
                    : ItemData.getItemCopy(item.getItemId(), false);
            newItem.setQuantity(quantity);
            getItems().add(newItem);
            sortByInvType();
        } else {
            curItem.setQuantity(curItem.getQuantity() + quantity);
        }
    }

    public Item getItemByItemID(int itemID) {
        return getItems().stream().filter(i -> i.getItemId() == itemID).findAny().orElse(null);
    }

    public void removeItem(Item getItem, int quantity) {
        if (getItem.getType() == Item.Type.EQUIP || quantity == getItem.getQuantity()) {
            getItems().remove(getItem);
        } else {
            getItem.setQuantity(getItem.getQuantity() - quantity);
        }
    }

    public void encode(OutPacket outPacket, long mask) {
        outPacket.write(getSlotCount());
        outPacket.writeLong(mask);
        ArrayList<Item> itemList = new ArrayList<>();
        if (mask == 0x7E) {
            outPacket.writeLong(getMoney());
            outPacket.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(outPacket, item);
            }
            return;
        }
        if ((mask & 0x02) != 0) {
            outPacket.writeLong(getMoney());
        }
        if ((mask & 0x04) != 0) {
            itemList.addAll(getItems(InventoryType.EQUIP));
        }
        if ((mask & 0x08) != 0) {
            itemList.addAll(getItems(InventoryType.CONSUME));
        }
        if ((mask & 0x10) != 0) {
            itemList.addAll(getItems(InventoryType.INSTALL));
        }
        if ((mask & 0x20) != 0) {
            itemList.addAll(getItems(InventoryType.ETC));
        }
        if ((mask & 0x40) != 0) {
            itemList.addAll(getItems(InventoryType.CASH));
        }
        outPacket.write(itemList.size());
        for (Item item : itemList) {
            PacketHelper.addItemInfo(outPacket, item);
        }
    }

    public void sort() {
        Comparator<Item> byType = Comparator.comparingInt(value -> value.getInvType().getVal());
        Comparator<Item> byItemId = Comparator.comparingInt(Item::getItemId);
        Comparator<Item> byLevel = Comparator.comparingInt(value -> {
            if (value instanceof Equip) {
                return ((Equip) value).getRLevel();
            } else {
                return 0;
            }
        });
        items.sort(byType.thenComparing(byItemId).thenComparing(byLevel));
    }

    public void addCashItem(Item item) {
        items.add(item);
        if (item.getId() == 0) {
            DataBaseManager.saveToDB(this);
        }
    }

    public Item getItemBySerialNumber(long serialNumber) {
        return Util.findWithPred(items, item -> item.getCashItemSerialNumber() == serialNumber);
    }

    public void removeItemBySerialNumber(long serialNumber) {
        items.removeIf(item -> item.getCashItemSerialNumber() == serialNumber);
    }

    public void trim() {
        List<Item> items = getItems().stream().filter(Objects::nonNull).collect(Collectors.toList());
        setItems(items);
    }

    public void sortByInvType() {
        Comparator<Item> byType = Comparator.comparingInt(value -> value.getInvType().getVal());
        items.sort(byType.thenComparing(byType));
    }

}
