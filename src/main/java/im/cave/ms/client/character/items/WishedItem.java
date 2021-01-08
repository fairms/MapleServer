package im.cave.ms.client.character.items;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.cashshop
 * @date 1/4 11:17
 */
@Entity
@Setter
@Getter
@Table(name = "wished_item")
public class WishedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int itemId;

    public WishedItem(int itemId) {
        this.itemId = itemId;
    }

    public WishedItem() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishedItem item = (WishedItem) o;

        return itemId == item.itemId;
    }

    @Override
    public int hashCode() {
        return itemId;
    }
}
