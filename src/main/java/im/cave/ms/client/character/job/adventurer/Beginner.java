package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.job.MapleJob;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.enums.JobEnum;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;

import java.util.Arrays;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Regen;
import static im.cave.ms.enums.SkillStat.time;
import static im.cave.ms.enums.SkillStat.x;


public class Beginner extends MapleJob {
    public static final int THREE_SNAILS = 1000;
    public static final int RECOVERY = 1001;
    public static final int NIMBLE_FEET = 1002;
    public static final int RETURN_MAPLESTORY = 1281;

    private final int[] buffs = new int[]{
            RECOVERY,
            NIMBLE_FEET,
    };

    private static final int[] addedSkills = new int[]{
            RECOVERY,
            NIMBLE_FEET,
            THREE_SNAILS
    };

    public Beginner(MapleCharacter chr) {
        super(chr);
        if (chr.getId() != 0 && isHandlerOfJob(chr.getJob())) {
            for (int id : addedSkills) {
                if (!chr.hasSkill(id)) {
                    Skill skill = chr.getSkill(id, true);
                    skill.setRootId(0);
                    skill.setMasterLevel(3);
                    skill.setMaxLevel(3);
                }
            }
        }
    }

    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        super.handleAttack(c, attackInfo);
    }

    @Override
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket in) throws Exception {
        super.handleSkill(c, skillId, slv, in);
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Skill skill = chr.getSkill(skillId);
        SkillInfo si = SkillData.getSkillInfo(skillId);
        if (!isBuff(skillId)) {
            switch (skillId) {
                case THREE_SNAILS:

                case RETURN_MAPLESTORY:

                    break;
            }
        }
    }

    @Override
    public void handleBuff(MapleClient c, InPacket in, int skillId, int slv) {
        super.handleBuff(c, in, skillId, slv);
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        SkillInfo si = SkillData.getSkillInfo(skillId);
        boolean sendStat = true;
        Option option = new Option();
        switch (skillId) {
            case RECOVERY:
                option.nOption = si.getValue(x, slv);
                option.rOption = skillId;
                option.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Regen, option);
                tsm.sendSetStatPacket();
                break;
            case NIMBLE_FEET:
                option.nOption = si.getValue(SkillStat.speed, slv);
                option.rOption = skillId;
                option.tOption = si.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.Speed, option);
                break;
            default:
                sendStat = false;
        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }


//    @Override
//    public void handleHit(Client c, InPacket in, HitInfo hitInfo) {
//        super.handleHit(c, in, hitInfo);
//    }

    @Override
    public boolean isHandlerOfJob(short id) {
        JobEnum job = JobEnum.getJobById(id);
        return job == JobEnum.BEGINNER;
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId) || Arrays.stream(buffs).anyMatch(b -> b == skillId);
    }
}
