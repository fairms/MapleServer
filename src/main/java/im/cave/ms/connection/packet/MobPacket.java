package im.cave.ms.connection.packet;

import im.cave.ms.client.character.skill.BurnedInfo;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.mob.EscortDest;
import im.cave.ms.client.field.obj.mob.ForcedMobStat;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobSkillAttackInfo;
import im.cave.ms.client.field.obj.mob.MobStat;
import im.cave.ms.client.field.obj.mob.MobTemporaryStat;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.RemoveMobType;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;

import java.util.List;


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

    public static OutPacket damaged(int mobId, long damage, int templateId, byte type, long hp, long maxHp) {
        OutPacket out = new OutPacket(SendOpcode.MOB_DAMAGED);

        out.writeInt(mobId);
        out.write(type);
        out.writeLong(damage);
        out.writeLong(hp);
        out.writeLong(maxHp);

        return out;
    }


    public static OutPacket hpIndicator(int objectId, byte percentage) {
        OutPacket out = new OutPacket(SendOpcode.HP_INDICATOR);
        out.writeInt(objectId);
        out.writeInt(percentage);
        out.write(0);
        return out;
    }

    public static OutPacket mobSkillDelay(int mobId, int skillAfter, int skillId, int slv, int sequenceDelay, Rect rect) {
        OutPacket out = new OutPacket(SendOpcode.MOB_SKILL_DELAY);

        out.writeInt(mobId);
        out.writeInt(skillAfter);
        out.writeInt(skillId);
        out.writeInt(slv);
        out.writeInt(sequenceDelay);
        if (rect != null) {
            out.writeRect(rect);
        } else {
            out.write(new byte[8]); // (0,0),(0,0)
        }

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
        out.write(0);
        if (hasMovementStat) {
            out.write(0); // ?
        }

        return out;
    }

    public static OutPacket statReset(Mob mob, byte calcDamageStatIndex, boolean sn) {
        return statReset(mob, calcDamageStatIndex, sn, null);
    }


    public static OutPacket statReset(Mob mob, byte calcDamageStatIndex, boolean sn, List<BurnedInfo> biList) {
        OutPacket out = new OutPacket(SendOpcode.MOB_STAT_RESET);
        MobTemporaryStat resetStats = mob.getTemporaryStat();
        int[] mask = resetStats.getRemovedMask();
        out.writeInt(mob.getObjectId());
        for (int i = 0; i < mob.getTemporaryStat().getNewMask().length; i++) {
            out.writeInt(mask[i]);
        }
        if (resetStats.hasRemovedMobStat(MobStat.BurnedInfo)) {
            if (biList == null) {
                out.writeInt(0);
                out.writeInt(0);
            } else {
                int dotCount = biList.stream().mapToInt(BurnedInfo::getDotCount).sum();
                out.writeInt(dotCount);
                out.writeInt(biList.size());
                for (BurnedInfo bi : biList) {
                    out.writeInt(bi.getCharacterId());
                    out.writeInt(bi.getSuperPos());
                }
            }
            resetStats.getBurnedInfos().clear();
        }
        out.write(calcDamageStatIndex);
        if (resetStats.hasRemovedMovementAffectingStat()) {
            out.writeBool(sn);
        }
        resetStats.getRemovedStatVals().clear();
        return out;
    }


    public static OutPacket escortFullPath(Mob mob, int oldAttr, boolean stopEscort) {
        OutPacket out = new OutPacket(SendOpcode.ESCORT_FULL_PATH);

        out.writeInt(mob.getObjectId());
        out.writeInt(mob.getEscortDest().size());
        out.writeShort(mob.getPosition().getX());
        out.writeShort(oldAttr);
        out.writeInt(mob.getPosition().getY());

        for (EscortDest escortDest : mob.getEscortDest()) {
            out.writeShort(escortDest.getDestPos().getX());
            out.writeShort(escortDest.getAttr());
            out.writeInt(escortDest.getDestPos().getY());
            out.writeInt(escortDest.getMass());
            if (escortDest.getMass() == 2) {
                out.writeInt(escortDest.getStopDuration());
            }
        }
        out.writeInt(mob.getCurrentDestIndex());
        int stopDuration = mob.getEscortStopDuration();
        out.writeBool(stopDuration > 0);
        if (stopDuration > 0) {
            out.writeInt(stopDuration);
        }
        out.writeBool(stopEscort);

        return out;
    }
}
