package im.cave.ms.network.packet;

import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 12/16 16:16
 */
public class SummonPacket {
    public static OutPacket spawnSummon(int charId, Summon summon) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_SUMMON.getValue());
        outPacket.writeInt(charId);
        outPacket.writeInt(summon.getObjectId());
        outPacket.writeInt(summon.getSkillID());
        outPacket.writeInt(summon.getCharLevel());
        outPacket.writeInt(summon.getSlv());
        // CSummoned::Init
        outPacket.writePosition(summon.getPosition());
        outPacket.write(summon.getMoveAction());
        outPacket.writeShort(summon.getCurFoothold());
        outPacket.write(summon.getMoveAbility().getVal()); // 1
        outPacket.write(summon.getAssistType().getVal());  // 2
        outPacket.write(summon.getEnterType().getVal());  // 1
        outPacket.writeInt(summon.getObjectId()); // 00 00 00 00
        outPacket.writeBool(summon.isFlyMob()); // 0
        outPacket.writeBool(summon.isBeforeFirstAttack()); //0
        outPacket.writeInt(summon.getTemplateId()); // 00 00 00 00
        outPacket.writeInt(summon.getBulletID()); // 00 00 00 00
        outPacket.writeBool(false);
        outPacket.writeBool(summon.isJaguarActive());
        outPacket.writeInt(summon.getSummonTerm());
        outPacket.writeBool(summon.isAttackActive());
        outPacket.writeZeroBytes(13);
        return outPacket;

    }

    public static OutPacket summonMove(int charId, int objId, MovementInfo movementInfo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SUMMON_MOVE.getValue());
        outPacket.writeInt(charId);
        outPacket.writeInt(objId);
        movementInfo.encode(outPacket);
        return outPacket;
    }
}
