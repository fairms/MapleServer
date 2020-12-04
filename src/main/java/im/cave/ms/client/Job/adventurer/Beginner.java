package im.cave.ms.client.Job.adventurer;

import im.cave.ms.client.Job.MapleJob;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.provider.data.SkillData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 12/14/2017.
 */
public class Beginner extends MapleJob {
    public static final int RECOVERY = 1000;
    public static final int NIMBLE_FEET = 1001;
    public static final int THREE_SNAILS = 1002;

    private int[] addedSkills = new int[]{
            RECOVERY,
            NIMBLE_FEET,
            THREE_SNAILS
    };

    public Beginner(MapleCharacter chr) {
        super(chr);
    }

//    @Override
//    public void handleAttack(Client c, AttackInfo attackInfo) {
//        super.handleAttack(c, attackInfo);
//    }
//
//    @Override
//    public void handleSkill(Client c, int skillID, byte slv, InPacket inPacket) {
//        super.handleSkill(c, skillID, slv, inPacket);
//    }
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
    public boolean isBuff(int skillID) {
        return super.isBuff(skillID);
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
