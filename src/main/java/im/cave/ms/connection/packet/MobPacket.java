package im.cave.ms.connection.packet;

import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.mob.ForcedMobStat;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobSkillAttackInfo;
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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SPAWN_MOB.getValue());
        out.writeBool(mob.isSealedInsteadDead());
        out.writeInt(mob.getObjectId());
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
        if (!hasBennInit) {
            mob.encodeInit(out);
        }
        return out;
    }


    public static OutPacket mobCtrlAck(int objId, int moveId, boolean useSkill, int currentMp, int skillId, short skillLevel) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MOB_CONTROL_ACK.getValue());
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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.HP_INDICATOR.getValue());
        out.writeInt(objectId);
        out.writeInt(percentage);
        out.write(0);
        return out;
    }


    public static OutPacket removeMob(int objectId, RemoveMobType type) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOVE_MOB.getValue());
        out.writeInt(objectId);
        out.write(type.getVal());
        out.writeZeroBytes(8);
        return out;
    }

    public static OutPacket changeMobController(Mob mob, boolean hasBeenInit, boolean isController) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MOB_CHANGE_CONTROLLER.getValue());
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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MOB_MOVE.getValue());
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
}
