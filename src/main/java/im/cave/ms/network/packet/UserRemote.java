package im.cave.ms.network.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.client.skill.HitInfo;
import im.cave.ms.client.skill.MobAttackInfo;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 12/29 12:42
 */
public class UserRemote {
    public static OutPacket hit(MapleCharacter player, HitInfo hitInfo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.REMOTE_HIT.getValue());
        outPacket.writeInt(player.getId());
        outPacket.write(hitInfo.type);
        outPacket.writeInt(hitInfo.hpDamage);
        outPacket.writeBool(hitInfo.isCrit);
        outPacket.writeBool(hitInfo.hpDamage == 0);
        outPacket.write(0);
        outPacket.writeInt(hitInfo.templateID);
        outPacket.write(hitInfo.action);
        outPacket.writeInt(hitInfo.mobID);

        outPacket.writeInt(0); // ignored
        outPacket.writeInt(hitInfo.reflectDamage);
        outPacket.writeBool(hitInfo.hpDamage == 0); // bGuard

        outPacket.write(hitInfo.specialEffectSkill);
        if ((hitInfo.specialEffectSkill & 1) != 0) {
            outPacket.writeInt(hitInfo.curStanceSkill);
        }
        outPacket.writeInt(hitInfo.hpDamage);
        return outPacket;
    }

    public static OutPacket effect(int id, Effect effect) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.REMOTE_EFFECT.getValue());
        outPacket.writeInt(id);
        effect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket attack(MapleCharacter player, AttackInfo attackInfo) {
        OutPacket outPacket = new OutPacket();
        switch (attackInfo.attackHeader) {
            case CLOSE_RANGE_ATTACK:
                outPacket.writeShort(SendOpcode.REMOTE_CLOSE_RANGE_ATTACK.getValue());
                break;
            case RANGED_ATTACK:
                outPacket.writeShort(SendOpcode.REMOTE_RANGED_ATTACK.getValue());
                break;
            case MAGIC_ATTACK:
                outPacket.writeShort(SendOpcode.REMOTE_MAGIC_ATTACK.getValue());
                break;
        }
        outPacket.writeInt(player.getId());
        outPacket.write(attackInfo.fieldKey);
        outPacket.write(attackInfo.mobCount << 4 | attackInfo.hits);
        outPacket.writeInt(player.getLevel());
        outPacket.writeInt(attackInfo.skillLevel);
        outPacket.writeInt(attackInfo.skillId);
        outPacket.writeZeroBytes(10);
        outPacket.write(attackInfo.attackAction);
        outPacket.write(attackInfo.direction);

        outPacket.writeShort(1);
        outPacket.writeInt(0);
        outPacket.writeShort(1);
        outPacket.write(0);
        outPacket.writeInt(0);

        for (MobAttackInfo mobAttackInfo : attackInfo.mobAttackInfo) {
            outPacket.writeInt(mobAttackInfo.objectId);
            outPacket.writeZeroBytes(13);
            for (long damage : mobAttackInfo.damages) {
                outPacket.writeLong(damage);
            }
        }
        return outPacket;
    }

    public static OutPacket charInfo(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHAR_INFO.getValue());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getLevel());
        outPacket.writeShort(chr.getJobId());
        outPacket.writeShort(0);//sub job
        outPacket.write(0x0A); //pvp grade
        outPacket.writeInt(chr.getFame());
        outPacket.writeBool(false); //marriage
        //todo marriage = true
        outPacket.write(0); //making skill size
        outPacket.writeMapleAsciiString("-"); //party name
        outPacket.writeMapleAsciiString(""); // 联盟
        outPacket.write(-1); //unk
        outPacket.write(0);  //unk
        outPacket.write(0); //pet size
        //todo pet info
        outPacket.write(0);
        outPacket.writeInt(0); //装备的的勋章
        outPacket.writeShort(0); //收藏数目
        //todo 收藏任务id+完成时间
        chr.encodeDamageSkins(outPacket);
        //倾向
        outPacket.write(1);
        outPacket.write(2);
        outPacket.write(3);
        outPacket.write(4);
        outPacket.write(5);
        outPacket.write(6);
        //
        outPacket.write(0);
        outPacket.writeZeroBytes(8);

        outPacket.writeInt(0);//椅子数
        outPacket.writeInt(0);//勋章数

        return outPacket;
    }

    public static OutPacket showBlackboard(MapleCharacter chr, boolean show, String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeInt(chr.getId());
        outPacket.writeBool(show);
        if (show) {
            outPacket.writeMapleAsciiString(content);
        }
        return outPacket;
    }

    public static OutPacket getChatText(MapleCharacter player, String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHATTEXT.getValue());
        outPacket.writeInt(player.getId());
        outPacket.writeBool(player.isGm());
        outPacket.writeMapleAsciiString(content);
        outPacket.writeMapleAsciiString(player.getName());
        outPacket.writeMapleAsciiString(content);
        outPacket.writeLong(0);
        outPacket.write(player.getWorld());
        outPacket.writeInt(player.getId());
        outPacket.write(3);
        outPacket.write(1);
        outPacket.write(-1);
        return outPacket;
    }
}
