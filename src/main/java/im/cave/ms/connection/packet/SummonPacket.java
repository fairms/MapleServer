package im.cave.ms.connection.packet;

import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 12/16 16:16
 */
public class SummonPacket {
    public static OutPacket spawnSummon(int charId, Summon summon) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SPAWN_SUMMON.getValue());
        out.writeInt(charId);
        out.writeInt(summon.getObjectId());
        out.writeInt(summon.getSkillID());
        out.writeInt(summon.getCharLevel());
        out.writeInt(summon.getSlv());
        // CSummoned::Init
        out.writePosition(summon.getPosition());
        out.write(summon.getMoveAction());
        out.writeShort(summon.getCurFoothold());
        out.write(summon.getMoveAbility().getVal()); // 1
        out.write(summon.getAssistType().getVal());  // 2
        out.write(summon.getEnterType().getVal());  // 1
        out.writeInt(summon.getObjectId()); // 00 00 00 00
        out.writeBool(summon.isFlyMob()); // 0
        out.writeBool(summon.isBeforeFirstAttack()); //0
        out.writeInt(summon.getTemplateId()); // 00 00 00 00
        out.writeInt(summon.getBulletID()); // 00 00 00 00
        out.write(0);
        out.writeBool(summon.isJaguarActive());
        out.writeInt(summon.getSummonTerm());
        out.writeBool(summon.isAttackActive());
        out.writeZeroBytes(13);
        return out;

    }

    public static OutPacket summonMove(int charId, int objId, MovementInfo movementInfo) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SUMMON_MOVE.getValue());
        out.writeInt(charId);
        out.writeInt(objId);
        movementInfo.encode(out);
        return out;
    }


    public static OutPacket summonedSkill(Summon summon, int summonSkillID) {
        OutPacket out = new OutPacket(SendOpcode.SUMMON_SKILL);

        out.writeInt(summon.getChr().getId());
        out.writeInt(summon.getObjectId());
        out.writeInt(summonSkillID);
        out.write(0);

        return out;
    }

    public static OutPacket summonRemoved(Summon summon, byte levelType) {
        OutPacket out = new OutPacket(SendOpcode.REMOVE_SUMMON);

        out.writeInt(summon.getChr().getId());
        out.writeInt(summon.getObjectId());
        out.write(levelType);

        return out;
    }
}
