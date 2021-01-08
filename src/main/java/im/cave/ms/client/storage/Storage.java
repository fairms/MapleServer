package im.cave.ms.client.storage;

import im.cave.ms.client.character.items.Item;
import im.cave.ms.enums.InventoryType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.trunk
 * @date 1/6 23:57
 */
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "item_storage")
@DiscriminatorColumn(name = "type")
public abstract class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int accId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "storage")
    private List<Item> items;
    private short slots;

    public Storage(short slots) {
        this.slots = slots;
        items = new ArrayList<>();
    }

    public void addSlots(byte amount) {
        slots += amount;
    }

    public Storage() {

    }

    abstract void putItem(Item item, int quantity);

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public Item getItemByItemId(int itemId) {
        return items.stream().filter(item -> item.getItemId() == itemId).findAny().orElse(null);
    }

    public List<Item> getItems(InventoryType type) {
        return getItems().stream().filter(item -> item.getInvType().equals(type)).collect(Collectors.toList());
    }

    abstract void sort(boolean proactive);
}
