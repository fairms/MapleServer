package im.cave.ms.client.character.items;

import im.cave.ms.enums.InventoryType;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character.items
 * @date 11/19 21:57
 */
@Getter
@Setter
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventoryId")
    private List<Item> items;
    private InventoryType type;
    private byte slots;

    public Inventory(InventoryType type, byte slots) {
        items = new CopyOnWriteArrayList<>();
        this.type = type;
        this.slots = slots;
    }

    public Inventory() {
        items = new CopyOnWriteArrayList<>();
        this.type = InventoryType.EQUIP;
    }


    public List<Item> newList() {
        return items;
    }

    public Item getItem(short pos) {
        return getItems().stream().filter(item -> item.pos == pos).findAny().orElse(null);
    }

    public void addItem(Item item) {
        if (item.getPos() == 0 || !isFreeSlot((byte) item.getPos())) {
            item.setPos(getNextFreeSlot());
        }
        if (getItems().size() < slots) {
            item.setInvType(getType());
            items.add(item);
            sortItemsByIndex();
            if (item.getId() == 0) {
                DataBaseManager.saveToDB(this);
            }
        }
    }

    public boolean isFreeSlot(byte pos) {
        return items.stream().filter(item -> item.getPos() == pos).findAny().orElse(null) == null;
    }

    public int getNextFreeSlot() {
        for (byte i = 1; i <= slots; i++) {
            byte finalI = i;
            Item item = items.stream().filter(e -> e.getPos() == finalI).findAny().orElse(null);
            if (item == null) {
                return i;
            }
        }
        return -1;
    }

    public void removeItem(Item item) {
        getItems().remove(item);
        sortItemsByIndex();
    }


    public void sortItemsByIndex() {
        // workaround for sort not being available for CopyOnWriteArrayList
        List<Item> temp = new ArrayList<>(getItems());
        temp.sort(Comparator.comparingInt(Item::getPos));
        getItems().clear();
        getItems().addAll(temp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Items: ");
        for (Item item : getItems()) {
            sb.append(String.format("%d id=%d slot=%d | ", item.getItemId(), item.getId(), item.getPos()));
        }
        return sb.toString();
    }

    public boolean canPickUp(Item item) {
        return !isFull() || (item.getInvType().isStackable() && getItemById(item.getItemId()) != null);
    }

    private Item getItemById(int itemId) {
        return getItems().stream().filter(item -> item.getItemId() == itemId).findFirst().orElse(null);
    }

    private boolean isFull() {
        return getItems().size() >= getSlots();
    }


    public Item getItemByItemID(int itemId) {
        return getItems().stream().filter(item -> item.getItemId() == itemId).findFirst().orElse(null);
    }

    public Item getItemByItemIDAndStackable(int itemId) {
        ItemInfo ii = ItemData.getItemInfoById(itemId);
        if (ii == null) {
            return null;
        }
        return getItems().stream()
                .filter(item -> item.getItemId() == itemId && item.getQuantity() < ii.getSlotMax())
                .findFirst()
                .orElse(null);
    }

    public Inventory deepCopy() {
        Inventory inventory = new Inventory(getType(), getSlots());
        inventory.setItems(new CopyOnWriteArrayList<>(getItems()));
        return inventory;
    }

    public Item getItemBySerialNumber(long serialNumber) {
        return Util.findWithPred(items, item -> item.getCashItemSerialNumber() == serialNumber);
    }

    public void expandSlot(int i) {
        slots += i;
    }
}
