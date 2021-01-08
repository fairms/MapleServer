package im.cave.ms.client.character.items;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.provider.data.ItemData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.enums.InventoryOperationType.ADD;
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
    private short flag;
    //    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
//            orphanRemoval = true)
//    @PrimaryKeyJoinColumn
    @Transient
    private Familiar familiar;
    //    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
//            orphanRemoval = true)
//    @PrimaryKeyJoinColumn
    @Transient
    private Android android;
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
        player.announce(UserPacket.inventoryOperation(true, ADD,
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

    public void encode(OutPacket out) {
        out.write(getType().getVal());
        out.writeInt(getItemId());
        boolean hasSn = getCashItemSerialNumber() > 0;
        out.writeBool(hasSn);
        if (hasSn) {
            out.writeLong(getCashItemSerialNumber());
        }
        out.writeLong(getExpireTime());
        out.writeInt(-1);
        out.write(0);
        if (getType() == Type.ITEM) {
            out.writeShort(getQuantity());
            out.writeMapleAsciiString(getOwner());
            out.writeInt(getFlag());
            if (ItemConstants.isThrowingStar(getItemId()) || ItemConstants.isBullet(getItemId()) ||
                    ItemConstants.isFamiliar(getItemId()) || getItemId() == 4001886) {
                out.writeLong(getId());
            }
            out.writeInt(0);
            if (ItemConstants.isFamiliar(getItemId()) && getFamiliar() == null) {
                int familiarID = ItemData.getFamiliarId(getItemId());
                Familiar familiar = new Familiar(familiarID);
                setFamiliar(familiar);
            }
            Familiar familiar = getFamiliar();
            out.writeInt(familiar != null ? familiar.getFamiliarId() : 0);
            out.writeShort(familiar != null ? familiar.getLevel() : 0);
            out.writeShort(familiar != null ? familiar.getSkill() : 0);
            out.writeShort(familiar != null ? familiar.getLevel() : 0);
            out.writeShort(familiar != null ? familiar.getOption(0) : 0);
            out.writeShort(familiar != null ? familiar.getOption(1) : 0);
            out.writeShort(familiar != null ? familiar.getOption(2) : 0);
            out.write(familiar != null ? familiar.getGrade() : 0);     //品级 0=C 1=B 2=A 3=S 4=SS
        }
    }
}
