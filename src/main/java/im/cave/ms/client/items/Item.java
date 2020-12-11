package im.cave.ms.client.items;

import im.cave.ms.enums.InventoryType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.unit.DataUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static im.cave.ms.constants.GameConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character.items
 * @date 11/19 21:59
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    protected int itemId;
    protected int pos;
    @Column(name = "sn")
    protected long cashItemSerialNumber;
    protected long expireTime = ZERO_TIME;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "invType")
    protected InventoryType invType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    protected Type type;
    protected int quantity = 1;
    protected boolean isCash;
    private String owner = "";

    public void drop() {
        setPos(0);
    }

    public void removeQuantity(int quantity) {
        if (quantity > 0) {
            setQuantity(Math.max(0, getQuantity() - quantity));
        }
    }

    public void addQuantity(int quantity) {
        if (quantity > 0 && quantity + getQuantity() > 0) {
            setQuantity(getQuantity() + quantity);
        }

    }

    public Item deepCopy() {
        Item item = new Item();
        this.setItemId(item.getItemId());
        item.setPos(getPos());
        item.setCashItemSerialNumber(getCashItemSerialNumber());
        item.setExpireTime(getExpireTime());
        this.setInvType(item.getInvType());
        this.setCash(item.isCash());
        this.setType(item.getType());
        this.setOwner(item.getOwner());
        this.setQuantity(item.getQuantity());
        return item;

    }

    public boolean isTradable() {
        return !ItemData.getItemById(getItemId()).isTradeBlock();
    }


    public enum Type {
        EQUIP(1),
        ITEM(2),
        PET(3);

        private final byte val;

        Type(byte val) {
            this.val = val;
        }

        Type(int val) {
            this.val = (byte) val;

        }

        public byte getVal() {
            return val;
        }
    }

    public Item() {

    }

    public Item(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + ", ItemId: " + getItemId() + ", Qty: " + getQuantity() + ", InvType: " + getInvType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == (item.id) && item.id == item.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId);
    }


}
