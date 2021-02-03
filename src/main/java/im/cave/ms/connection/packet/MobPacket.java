package im.cave.ms.connection.packet;

import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.mob.ForcedMobStat;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobSkillAttackInfo;
import im.cave.ms.client.field.obj.mob.MobTemporaryStat;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.RemoveMobType;
import im.cave.ms.tools.Position;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/29 17:11
 */
public class MobPacket {

    public static OutPacket spawnMob(Mob mob, boolean hasBennInit) {
        OutPacket out = new OutPacket(SendOpcode.SPAWN_MOB);
        out.writeBool(mob.isSealedInsteadDead());
        out.writeInt(mob.getObjectId());
        out.write(mob.getCalcDamageIndex());
        out.writeInt(mob.getTemplateId());
        ForcedMobStat forcedMobStat = mob.getForcedMobStat();
        out.writeBool(forcedMobStat != null); //modified stat
        if (forcedMobStat != null) {
            forcedMobStat.encode(out);
        }

        MobTemporaryStat temporaryStat = mob.getTemporaryStat();
        temporaryStat.encode(out);
//        out.writeInt(0);
//        out.writeInt(0);
//        out.writeInt(0);
//        out.writeInt(0);
//        out.writeInt(0);  //mobTempStatMast

        if (!hasBennInit) {
            mob.encodeInit(out);
        }
        return out;
    }


    public static OutPacket mobCtrlAck(int objId, int moveId, boolean useSkill, int currentMp, int skillId, short skillLevel) {
        OutPacket out = new OutPacket(SendOpcode.MOB_CONTROL_ACK);
        out.writeInt(objId);
        out.writeShort(moveId);
        out.writeBool(useSkill);
        out.writeInt(currentMp);
        out.writeInt(skillId);
        out.writeShort(skillLevel);
        out.writeInt(0);
        out.writeInt(0);
        return out;
    }

    public static OutPacket hpIndicator(int objectId, byte percentage) {
        OutPacket out = new OutPacket(SendOpcode.HP_INDICATOR);
        out.writeInt(objectId);
        out.writeInt(percentage);
        out.write(0);
        return out;
    }


    public static OutPacket removeMob(int objectId, RemoveMobType type) {
        OutPacket out = new OutPacket(SendOpcode.REMOVE_MOB);
        out.writeInt(objectId);
        out.write(type.getVal());
        out.writeZeroBytes(8);
        return out;
    }

    public static OutPacket changeMobController(Mob mob, boolean hasBeenInit, boolean isController) {
        OutPacket out = new OutPacket(SendOpcode.MOB_CHANGE_CONTROLLER);
        out.writeBool(isController);
        out.writeInt(mob.getObjectId());
        if (isController) {
            out.write(mob.getCalcDamageIndex());
            out.writeInt(mob.getTemplateId());
            ForcedMobStat forcedMobStat = mob.getForcedMobStat();
            out.writeBool(forcedMobStat != null);
            if (forcedMobStat != null) {
                forcedMobStat.encode(out);
            }
            out.writeInt(0);
            out.writeInt(0);
            out.writeInt(0);
            out.writeInt(0);
            out.writeInt(0);
            if (!hasBeenInit) {
                mob.encodeInit(out);
            }
        }
        return out;
    }

    public static OutPacket moveMobRemote(Mob mob, MobSkillAttackInfo msai, MovementInfo movementInfo) {
        OutPacket out = new OutPacket(SendOpcode.MOB_MOVE);
        out.writeInt(mob.getObjectId());
        out.write(msai.actionAndDirMask);
        out.write(msai.action);
        out.writeLong(msai.targetInfo);
        out.writeZeroBytes(6);
        out.write(msai.multiTargetForBalls.size());
        for (Position pos : msai.multiTargetForBalls) {
            out.writePosition(pos);
        }
        movementInfo.encode(out);
        out.write(0);
        return out;
    }

    public static OutPacket statSet(Mob mob, short delay) {
        OutPacket out = new OutPacket(SendOpcode.MOB_STAT_SET);
        MobTemporaryStat mts = mob.getTemporaryStat();
        boolean hasMovementStat = mts.hasNewMovementAffectingStat();
        out.writeInt(mob.getObjectId());
        mts.encode(out);
        out.writeShort(delay);
        out.write(1); // nCalcDamageStatIndex
        if (hasMovementStat) {
            out.write(0); // ?
        }

        return out;
    }
}
