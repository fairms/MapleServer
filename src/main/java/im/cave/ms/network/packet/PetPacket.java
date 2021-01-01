package im.cave.ms.network.packet;

import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 1/1 22:15
 */
public class PetPacket {
    public static OutPacket petActivateChange(Pet pet, boolean active, byte removedReason) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.PET_ACTIVATED.getValue());
        outPacket.writeInt(pet.getOwnerId());
        outPacket.writeInt(pet.getIdx());
        outPacket.writeBool(active);
        outPacket.write(0);
        if (active) {
            pet.encode(outPacket);
        } else {
            outPacket.write(removedReason);
        }
        return outPacket;
    }
}
