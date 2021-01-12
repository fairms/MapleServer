package im.cave.ms.client.storage;

import im.cave.ms.client.character.items.Item;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.constants.GameConstants;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.trunk
 * @date 1/6 23:59
 */
@Entity
@DiscriminatorValue("Locker")
public class Locker extends Storage {

    public Locker() {
        super(GameConstants.MAX_LOCKER_SIZE);
    }

    @Override
    public void putItem(Item item, int quantity) {
        getItems().add(item);
        if (item.getId() == 0) {
            DataBaseManager.saveToDB(this);
        }
    }

    @Override
    public void sort(boolean proactive) {
        //todo
    }

    public Item getItemBySerialNumber(long serialNumber) {
        return getItems().stream().filter(item -> item.getCashItemSerialNumber() == serialNumber).findAny().orElse(null);
    }

    public void removeItemBySerialNumber(long serialNumber) {
        getItems().removeIf(item -> item.getCashItemSerialNumber() == serialNumber);
    }
}
