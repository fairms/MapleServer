package im.cave.ms.network.packet;

import im.cave.ms.client.field.movement.MovementInfo;
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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.PET_ACTIVATED.getValue());
        out.writeInt(pet.getOwnerId());
        out.writeInt(pet.getIdx());
        out.writeBool(active);
        out.write(0); //unk
        if (active) {
            pet.encode(out);
        } else {
            out.write(removedReason);
        }
        return out;
    }

    public static OutPacket petActionSpeak(int charId, int index, int op, String msg) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.PET_ACTION_SPEAK.getValue());
        out.writeInt(charId);
        out.writeInt(index);
        out.writeShort(op);
        out.writeMapleAsciiString(msg);
        return out;
    }

    public static OutPacket petMove(int charId, int index, MovementInfo movementInfo) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.PET_MOVE.getValue());
        out.writeInt(charId);
        out.writeInt(index);
        movementInfo.encode(out);
        return out;
    }

    public static OutPacket petActionCommand(int charId, int index) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.PET_ACTION_COMMAND.getValue());
        out.writeInt(charId);
        out.writeInt(index);
        return out;
    }
}
