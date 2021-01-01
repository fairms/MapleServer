package im.cave.ms.client.field.obj;

import im.cave.ms.client.pet.PetItem;
import im.cave.ms.network.netty.OutPacket;
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
    private PetItem item;

    public Pet(int templateId, int ownerId) {
        super(templateId);
        this.ownerId = ownerId;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getTemplateId());
        outPacket.writeMapleAsciiString(name);
        outPacket.writeLong(getItem().getId());
        outPacket.writePosition(getPosition());
        outPacket.write(getMoveAction());
        outPacket.writeShort(getFh());
        outPacket.writeInt(hue);
        outPacket.writeShort(giantRate);
        outPacket.writeInt(0);
    }
}
