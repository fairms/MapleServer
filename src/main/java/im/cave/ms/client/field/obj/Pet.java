package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.items.PetItem;
import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 1/1 21:48
 */
@Getter
@Setter
public class Pet extends MapleMapObj {
    private final int ownerId;
    private int id;
    private int idx;
    private String name;
    private long petLockerSN;
    private int hue = -1;
    private short giantRate = 100;
    private boolean transformed;
    private boolean reinforced;
    private PetItem petItem;

    public Pet(int templateId, int ownerId) {
        super(templateId);
        this.ownerId = ownerId;
    }

    public void encode(OutPacket out) {
        out.writeInt(getTemplateId());
        out.writeMapleAsciiString(name);
        out.writeLong(getPetItem().getCashItemSerialNumber());
        out.writePosition(getPosition());
        out.write(getMoveAction());
        out.writeShort(getFh());
        out.writeInt(hue);
        out.writeShort(giantRate);
        out.writeInt(0);
    }
}
