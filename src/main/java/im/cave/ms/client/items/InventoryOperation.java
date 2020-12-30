package im.cave.ms.client.items;

import im.cave.ms.enums.InventoryOperationType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.items
 * @date 12/29 16:37
 */
@Getter
@Setter
public class InventoryOperation {
    private InventoryOperationType type;
    private Item item;
    private short oldPos;
    private short newPos;
    private int bagPos;

    public InventoryOperation(InventoryOperationType type) {
        this.type = type;
    }

    public InventoryOperation(short oldPos, short newPos, Item item) {
        type = InventoryOperationType.MOVE;
        this.oldPos = oldPos;
        this.newPos = newPos;
        this.item = item;
    }
}
