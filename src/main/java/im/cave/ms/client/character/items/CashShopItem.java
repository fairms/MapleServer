package im.cave.ms.client.character.items;

import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cashshop_item")
@Getter
@Setter
public class CashShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int itemId;
    private int sn;
    @Enumerated(EnumType.ORDINAL)
    private CashShopItemFlag shopItemFlag = CashShopItemFlag.None;
    private int price;
    private int originalPrice;
    private int bundleQuantity;
    private int availableDays;
    private short pbCash;
    private short pbPoint;
    private short pbGift;
    private short meso;
    private int gender;
    private int likes;
    private int requiredLevel;
    private String category;
    private boolean showUp;
    private long flag = 0x400;

    public void encodeModified(OutPacket out) {
        out.writeInt(sn);
        out.writeLong(getMask());
        if ((flag & 0x01) != 0) {
            out.writeInt(getItemId());
        }
        if ((flag & 0x02) != 0) {
            out.writeShort(getBundleQuantity());
        }
        if ((flag & 0x10) != 0) {
//            out.write(cmi.priority);
        }
        if ((flag & 0x04) != 0) {
//            out.writeInt(cmi.discountPrice)
        }
        if ((flag & 0x8) != 0) {
//            out.write(cmi.csClass)
        }
        if ((flag & 0x20) != 0) {
//            out.writeShort(cmi.period)
        }
        if ((flag & 0x20000) != 0) {
//            out.writeShort(cmi.fameLimit)
        }
        if ((flag & 0x40000) != 0) {
//            out.writeShort(cmi.levelLimit)
        }
        //0x40 = ?
        if ((flag & 0x80) != 0) {
//            out.writeInt(cmi.meso)
        }
        if ((flag & 0x200) != 0) {
//            out.write(cmi.gender)
        }
        if ((flag & 0x400) != 0) {
            out.writeBool(isShowUp());
        }
        if ((flag & 0x800) != 0) {
//            out.write(cmi.mark)
        }
        //0x2000, 0x4000, 0x8000, 0x10000, 0x20000, 0x100000, 0x80000 - ?
        if ((flag & 0x80000) != 0) {
//            out.writeInt(cmi.termStart)
        }
        if ((flag & 0x100000) != 0) {
//            out.writeInt(cmi.termEnd)
        }
        if ((flag & 0x800000) != 0) {
//            out.writeShort(cmi.categories)
        }

    }

    private long getMask() {

//        if (this.itemId > 0) {
//            this.flag |= 0x1;
//        }
//        if (this.bundleQuantity > 0) {
//            this.flag |= 0x2;
//        }
//        if (this.discountPrice > 0) {
//            this.flag |= 0x4;
//        }
////            if (this.csClass > 0) {
////                this.flag |= 0x8;
////            }
//        if (this.priority >= 0) {
//            this.flag |= 0x10;
//        }
//        if (this.period > 0) {
//            this.flag |= 0x20;
//        }
//        //0x40 = ?
//        if (this.meso > 0) {
//            this.flag |= 0x80;
//        }
//        if (this.gender >= 0) {
//            this.flag |= 0x200;
//        }
////            if (this.showUp) {
//        this.flag |= 0x400;
////            }
//        if (this.mark >= -1 || this.mark <= 0xF) {
//            this.flag |= 0x800;
//        }
//        //0x2000, 0x4000, 0x8000, 0x10000, 0x20000, 0x100000, 0x80000 - ?
//        if (this.fameLimit > 0) {
//            this.flag |= 0x20000;
//        }
//        if (this.levelLimit > 0) {
//            this.flag |= 0x40000;
//        }
//        if (this.termStart > 0) {
//            this.flag |= 0x80000;
//        }
//        if (this.termEnd > 0) {
//            this.flag |= 0x100000;
//        }
//        if (this.categories > 0) {
//            this.flag |= 0x800000;
//        }
        return flag;
    }

    private enum CashShopItemFlag {
        None,
        Event,
        New,
        Sale,
        Hot,
        Limited,
        BlackFriday,
        AccountLimited,
        CharLimited
    }

}
