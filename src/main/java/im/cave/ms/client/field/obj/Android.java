package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.network.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long itemId;
    @Transient
    private MapleCharacter owner;
    private short skin;
    private short hair;
    private short face;
    private String name;
    private int type;

    public void encode(OutPacket outPacket) {
        outPacket.write(getType());
        outPacket.writePosition(getPosition());
        outPacket.write(getMoveAction());
        outPacket.writeShort(getFh());
        outPacket.writeInt(0);
        encodeAndroidInfo(outPacket);
        outPacket.writeZeroBytes(68);  //装备
    }


    public void encodeAndroidInfo(OutPacket outPacket) {
        outPacket.writeShort(getSkin());
        outPacket.writeShort(getHair());
        outPacket.writeShort(getFace());
        outPacket.writeMapleAsciiString(getName());
    }
}
