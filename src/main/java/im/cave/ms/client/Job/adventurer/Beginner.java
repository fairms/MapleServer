package im.cave.ms.client.Job.adventurer;

import im.cave.ms.client.Job.MapleJob;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.client.skill.SkillStat;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

import java.util.Arrays;

/**
 * Created on 12/14/2017.
 */
public class Beginner extends MapleJob {
    public static final int RECOVERY = 1001;
    public static final int NIMBLE_FEET = 1002;
    public static final int THREE_SNAILS = 1000;

    private final int[] buffs = new int[]{
            RECOVERY,
            NIMBLE_FEET,
    };

    public Beginner(MapleCharacter chr) {
        super(chr);
    }

    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        super.handleAttack(c, attackInfo);
    }

    @Override
    public void handleSkill(MapleClient c, int skillId, int skillLevel, SeekableLittleEndianAccessor slea) {
        super.handleSkill(c, skillId, skillLevel, slea);
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Skill skill = chr.getSkill(skillId);
        SkillInfo si = null;
        if (skill != null) {
            si = SkillData.getSkillInfo(skillId);
        }
        if (isBuff(skillId)) {
            return;
        } else {
        }
    }

    public void handleBuff(MapleClient c, SeekableLittleEndianAccessor slea, int skillId, int slv) {
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
        boolean sendStat = true;
        switch (skillId) {
            case RECOVERY:
                sendStat = false;
                break;
            case NIMBLE_FEET:
                Option option = new Option();
                option.nOption = skillInfo.getValue(SkillStat.speed, slv);
                option.rOption = skillId;
                option.tOption = skillInfo.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.Speed, option);
                break;
            default:
                sendStat = false;
        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }


    //
//    @Override
//    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {
//        super.handleHit(c, inPacket, hitInfo);
//    }

    @Override
    public boolean isHandlerOfJob(short id) {
        JobConstants.JobEnum job = JobConstants.JobEnum.getJobById(id);
        return job == JobConstants.JobEnum.BEGINNER;
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId) || Arrays.stream(buffs).anyMatch(b -> b == skillId);
    }

//    @Override
//    public void setCharCreationStats(Char chr) {
//        super.setCharCreationStats(chr);
//        CharacterStat cs = chr.getAvatarData().getCharacterStat();
//        if (chr.getSubJob() == 1) {
//            cs.setPosMap(103050900);
//        } else if (chr.getSubJob() == 2) {
//            cs.setPosMap(3000600);
//        } else {
//            cs.setPosMap(4000011);
//        }
//    }
}
