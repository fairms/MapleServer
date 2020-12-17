package im.cave.ms.client.Job;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.network.netty.InPacket;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.Job
 * @date 12/8 19:06
 */
public class GM extends MapleJob {
    private static final int Haste = 9001000;
    private static final int Admin_Blessing = 9001003;

    public GM(MapleCharacter chr) {
        super(chr);
    }

    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        super.handleAttack(c, attackInfo);
    }

    @Override
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket inPacket) {
        super.handleSkill(c, skillId, slv, inPacket);
    }

    @Override
    public void handleJobLessBuff(MapleClient c, InPacket inPacket, int skillId, int slv) {
        super.handleJobLessBuff(c, inPacket, skillId, slv);
    }

    @Override
    public void handleMobDebuffSkill(MapleCharacter chr) {
        super.handleMobDebuffSkill(chr);
    }

    @Override
    public void handleCancelTimer(MapleCharacter chr) {
        super.handleCancelTimer(chr);
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return false;
    }

    @Override
    protected MapleCharacter getMapleCharacter() {
        return super.getMapleCharacter();
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    @Override
    public void handleSkillRemove(MapleClient c, int skillID) {
        super.handleSkillRemove(c, skillID);
    }

    @Override
    public void handleLevelUp() {
        super.handleLevelUp();
    }

    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId);
    }

    @Override
    public void setMapleCharacterCreationStats(MapleCharacter chr) {
        super.setMapleCharacterCreationStats(chr);
    }
}
