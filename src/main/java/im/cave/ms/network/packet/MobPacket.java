package im.cave.ms.network.packet;

import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.enums.RemoveMobType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/29 17:11
 */
public class MobPacket {

    public static OutPacket spawnMob(Mob mob, boolean hasBennInit) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_MOB.getValue());
        outPacket.writeBool(mob.isSealedInsteadDead());
        outPacket.writeInt(mob.getObjectId());
        outPacket.write(mob.getCalcDamageIndex());
        outPacket.writeInt(mob.getTemplateId());
        ////getTemporaryStat
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0x20);
        outPacket.writeShort(0);
        if (!hasBennInit) {
            mob.encodeInit(outPacket);
        }
        return outPacket;
    }


    public static OutPacket moveMob(int objId, int moveId, boolean useSkill, int currentMp, int skillId, short skillLevel) {
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

    public static OutPacket changeMobController(Mob mob, boolean hasBeenInit, boolean isController) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.MOB_CHANGE_CONTROLLER.getValue());
        outPacket.writeBool(isController);
        outPacket.writeInt(mob.getObjectId());
        if (isController) {
            outPacket.write(mob.getCalcDamageIndex());
            outPacket.writeInt(mob.getObjectId());
            //getTemporaryStat
            outPacket.writeInt(0);
            outPacket.writeInt(0);
            outPacket.writeInt(0);
            outPacket.writeInt(0);
            outPacket.writeInt(0x20);
            outPacket.writeShort(0);
            //mob.init
            if (!hasBeenInit) {
                mob.encodeInit(outPacket);
            }
        }
        return outPacket;
    }

}
