package im.cave.ms.connection.packet;

import im.cave.ms.client.character.items.ExceptionItem;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.PetSkill;

import java.util.List;

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

    public static OutPacket petActionCommand(int charId, int index, int action, int status, int param) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.PET_ACTION_COMMAND.getValue());
        out.writeInt(charId);
        out.writeInt(index);
        out.write(action);
        out.write(status);
        out.writeInt(param);
        return out;
    }

    public static OutPacket petSkillChanged(long sn, boolean add, PetSkill skill) {
        OutPacket out = new OutPacket(SendOpcode.PET_SKILL_CHANGED);
        out.writeLong(sn);
        out.writeBool(add);
        out.write(skill.getVal());
        return out;
    }

    public static OutPacket cashPetPickUpOnOffResult(boolean changed, boolean on) {
        OutPacket out = new OutPacket(SendOpcode.CASH_PET_PICK_UP_ON_OFF_RESULT);
        out.writeBool(changed);
        out.writeBool(on);
        return out;
    }

    public static OutPacket initPetExceptionList(Pet pet) {
        OutPacket out = new OutPacket(SendOpcode.PET_LOAD_EXCEPTION_LIST);
        out.writeInt(pet.getOwnerId());
        out.writeInt(pet.getIdx());
        out.writeLong(pet.getPetItem().getCashItemSerialNumber());
        List<ExceptionItem> exceptionList = pet.getPetItem().getExceptionList();
        out.write(exceptionList.size());
        for (ExceptionItem exceptionItem : exceptionList) {
            out.writeInt(exceptionItem.getItemId());
        }
        return out;
    }
}
