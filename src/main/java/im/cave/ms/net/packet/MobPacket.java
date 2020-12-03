package im.cave.ms.net.packet;

import im.cave.ms.enums.RemoveMobType;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import static im.cave.ms.enums.RemoveMobType.ANIMATION_DEATH;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/29 17:11
 */
public class MobPacket {
    public static MaplePacketLittleEndianWriter mobMoveResponse(int objId, int moveId, boolean useSkill, int currentMp, int skillId, short skillLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.MOVE_MONSTER_RESPONSE.getValue());
        mplew.writeInt(objId);
        mplew.writeShort(moveId);
        mplew.writeBool(useSkill);
        mplew.writeInt(currentMp);
        mplew.writeInt(skillId);
        mplew.writeShort(skillLevel);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter hpIndicator(int objectId, byte percentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.HP_INDICATOR.getValue());
        mplew.writeInt(objectId);
        mplew.writeInt(percentage);
        mplew.write(0);
        return mplew;
    }


    public static MaplePacketLittleEndianWriter removeMob(int objectId, RemoveMobType type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.REMOVE_MONSTER.getValue());
        mplew.writeInt(objectId);
        mplew.write(type.getVal());
        mplew.writeZeroBytes(8);
        return mplew;
    }
}
