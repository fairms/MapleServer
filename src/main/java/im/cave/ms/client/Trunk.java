package im.cave.ms.client;

import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.InventoryType;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 4/7/2018.
 */
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
                    ? ((Equip) item).deepCopy()
                    : ItemData.getItemCopy(item.getItemId(), false);
            newItem.setQuantity(quantity);
            getItems().add(newItem);
        } else {
            curItem.setQuantity(curItem.getQuantity() + quantity);
        }
    }

    public Item getItemByItemID(int itemID) {
        return getItems().stream().filter(i -> i.getItemId() == itemID).findAny().orElse(null);
    }

    public void removeItem(Item getItem) {
        getItems().remove(getItem);
    }
}
