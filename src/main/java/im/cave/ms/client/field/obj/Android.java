package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 1/1 16:54
 */
@Getter
@Setter
@Entity
@Table(name = "android")
public class Android extends MapleMapObj {
    @Id
    private long itemId;
    @Transient
    private MapleCharacter owner;
    private short skin;
    private short hair;
    private short face;
    private String name;
    private int type;

    public void encode(OutPacket out) {
        out.write(getType());
        out.writePosition(getPosition());
        out.write(getMoveAction());
        out.writeShort(getFh());
        out.writeInt(0);
        encodeAndroidInfo(out);
        out.writeZeroBytes(68);  //装备
    }


    public void encodeAndroidInfo(OutPacket out) {
        out.writeShort(getSkin() >= 2000 ? getSkin() - 2000 : getSkin());
        out.writeShort(getHair() - 30000);
        out.writeShort(getFace() - 20000);
        out.writeMapleAsciiString(getName());
    }
}
