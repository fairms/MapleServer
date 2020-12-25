package im.cave.ms.client.items;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Familiar;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.network.packet.PlayerPacket;
import im.cave.ms.provider.data.ItemData;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

import static im.cave.ms.constants.GameConstants.MAX_TIME;
import static im.cave.ms.enums.InventoryOperation.ADD;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

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
@Table(name = "item")
public class Item implements Serializable {
    protected long expireTime = MAX_TIME;

    protected int itemId;
    protected int pos;
    @Column(name = "sn")
    protected long cashItemSerialNumber;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "invType")
    protected InventoryType invType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    protected Type type;
    protected int quantity = 1;
    protected boolean isCash;
    @Transient
    private short flag;
    @JoinColumn(name = "familiarId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private Familiar familiar;
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
        item.setItemId(getItemId());
        item.setPos(getPos());
        item.setCashItemSerialNumber(getCashItemSerialNumber());
        item.setExpireTime(getExpireTime());
        item.setInvType(getInvType());
        item.setCash(isCash());
        item.setType(getType());
        item.setOwner(getOwner());
        item.setQuantity(getQuantity());
        return item;

    }

    public boolean isTradable() {
        return !ItemData.getItemInfoById(getItemId()).isTradeBlock();
    }

    public void updateToChar(MapleCharacter player) {
        short bagIndex = (short) (getInvType() == EQUIPPED ? -getPos() : getPos());
        player.announce(PlayerPacket.inventoryOperation(true, false, ADD,
                bagIndex, (short) 0, 0, this));
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
