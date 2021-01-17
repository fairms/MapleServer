package im.cave.ms.client.multiplayer;

import im.cave.ms.client.character.items.Item;
import im.cave.ms.connection.netty.OutPacket;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.multiplayer
 * @date 1/17 16:22
 */
@Getter
@Setter
@Entity
@Table(name = "express")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Express {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int fromId;
    private String fromChar;
    private int toId;
    private String toChar;
    private long expiredDate;
    private String message;
    private byte status;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId")
    private Item item;
    private int meso;
    private long createdDate;


    public Express(int fromId, String fromChar, int toId, String toChar, long expiredDate, String message, byte type, Item item, int meso, long createdDate) {
        this.fromId = fromId;
        this.fromChar = fromChar;
        this.toId = toId;
        this.toChar = toChar;
        this.expiredDate = expiredDate;
        this.message = message;
        this.status = type;
        this.item = item;
        this.meso = meso;
        this.createdDate = createdDate;
    }

    public Express() {

    }

    public void encode(OutPacket out) {
        out.writeInt(getId());
        out.writeAsciiString(getFromChar(), 13);
        out.writeLong(0); //unk
        out.writeLong(getExpiredDate());
        out.write(getStatus());
        out.writeAsciiString(getMessage(), 100);
        out.writeZeroBytes(100);
        out.write(0);
        if (item != null) {
            item.encode(out);
        }
    }
}
