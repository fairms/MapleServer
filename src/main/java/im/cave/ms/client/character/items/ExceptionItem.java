package im.cave.ms.client.character.items;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character.items
 * @date 1/10 0:24
 */
@Entity
@Table(name = "pet_exception_item")
public class ExceptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long petId;
    private int itemId;

    public ExceptionItem(int itemId) {
        this.itemId = itemId;
    }

    public ExceptionItem() {
    }

    public int getItemId() {
        return itemId;
    }
}
