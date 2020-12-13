package im.cave.ms.net.packet;

import im.cave.ms.enums.RemoveMobType;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.net.packet.opcode.SendOpcode;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/29 17:11
 */
public class MobPacket {
    public static OutPacket mobMoveResponse(int objId, int moveId, boolean useSkill, int currentMp, int skillId, short skillLevel) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.MOVE_MONSTER_RESPONSE.getValue());
        outPacket.writeInt(objId);
        outPacket.writeShort(moveId);
        outPacket.writeBool(useSkill);
        outPacket.writeInt(currentMp);
        outPacket.writeInt(skillId);
        outPacket.writeShort(skillLevel);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        return outPacket;
    }

    public static OutPacket hpIndicator(int objectId, byte percentage) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.HP_INDICATOR.getValue());
        outPacket.writeInt(objectId);
        outPacket.writeInt(percentage);
        outPacket.write(0);
        return outPacket;
    }


    public static OutPacket removeMob(int objectId, RemoveMobType type) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.REMOVE_MOB.getValue());
        outPacket.writeInt(objectId);
        outPacket.write(type.getVal());
        outPacket.writeZeroBytes(8);
        return outPacket;
    }

    public static OutPacket removeController(int objectId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
        outPacket.write(0);
        outPacket.writeInt(objectId);
        return outPacket;
    }
}
