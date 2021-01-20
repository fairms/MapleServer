package im.cave.ms.client.multiplayer;

import im.cave.ms.connection.netty.OutPacket;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
 * @Package im.cave.ms.client.social
 * @date 1/12 22:19
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MapleNotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private byte status; //02已读 01未读 //00发送
    private int fromId;
    private String fromChr;
    private int toId;
    private String toChr;
    private String msg;
    private long createdTime;

    public MapleNotes() {

    }

    public void encodeForIn(OutPacket out) {
        out.writeInt(getId());
        out.writeInt(getStatus());
        out.writeInt(getFromId());
        out.writeMapleAsciiString(getFromChr());
        out.writeMapleAsciiString(getMsg());
        out.writeLong(getCreatedTime());
        out.write(0);
        out.writeMapleAsciiString(getFromChr());
        out.writeMapleAsciiString(getMsg());
        out.writeZeroBytes(13);
    }

    public void encodeForOut(OutPacket out) {
        out.writeInt(getId());
        out.write(0);
        out.writeInt(getFromId());
        out.writeMapleAsciiString(getFromChr());
        out.writeInt(getToId());
        out.writeMapleAsciiString(getToChr());
        out.writeMapleAsciiString(getMsg());
        out.writeLong(getCreatedTime());
    }
}
