package im.cave.ms.network.packet;

import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.client.movement.MovementInfo;
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
        outPacket.write(0); //unk
        if (active) {
            pet.encode(outPacket);
        } else {
            outPacket.write(removedReason);
        }
        return outPacket;
    }

    public static OutPacket petActionSpeak(int charId, int index, int op, String msg) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.PET_ACTION_SPEAK.getValue());
        outPacket.writeInt(charId);
        outPacket.writeInt(index);
        outPacket.writeShort(op);
        outPacket.writeMapleAsciiString(msg);
        return outPacket;
    }

    public static OutPacket petMove(int charId, int index, MovementInfo movementInfo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.PET_MOVE.getValue());
        outPacket.writeInt(charId);
        outPacket.writeInt(index);
        movementInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket petActionCommand(int charId, int index) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.PET_ACTION_COMMAND.getValue());
        outPacket.writeInt(charId);
        outPacket.writeInt(index);
        return outPacket;
    }
}
