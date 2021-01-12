package im.cave.ms.client.multiplayer;

import im.cave.ms.connection.netty.OutPacket;
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
@Table(name = "message")
@Getter
@Setter
@Builder
public class MapleMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private byte status;
    private int fromId;
    private String fromChr;
    private int toId;
    private String toChr;
    private String msg;
    private long createdTime;

    public MapleMessage() {

    }

    public MapleMessage(int id, byte status, int fromId, String fromChr, int toId, String toChr, String msg, long createdTime) {
        this.id = id;
        this.status = status;
        this.fromId = fromId;
        this.fromChr = fromChr;
        this.toId = toId;
        this.toChr = toChr;
        this.msg = msg;
        this.createdTime = createdTime;
    }

    public void encode(OutPacket out) {
        out.writeInt(getId());
        out.write(getStatus());
        out.writeInt(getFromId());
        out.writeMapleAsciiString(getFromChr());
        out.writeInt(getToId());
        out.writeMapleAsciiString(getToChr());
        out.writeMapleAsciiString(getMsg());
        out.writeLong(getCreatedTime());
    }
}
