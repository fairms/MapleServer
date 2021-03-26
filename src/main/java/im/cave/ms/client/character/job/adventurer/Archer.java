package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.EPAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.NoBulletConsume;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.SharpEyes;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.SoulArrow;
import static im.cave.ms.enums.SkillStat.delay;
import static im.cave.ms.enums.SkillStat.epad;
import static im.cave.ms.enums.SkillStat.time;
import static im.cave.ms.enums.SkillStat.x;
import static im.cave.ms.enums.SkillStat.y;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.job.adventurer
 * @date 12/28 14:25
 */
public class Archer extends Beginner {
    public static final int BOW_SOUL_ARROW = 3101004;

    public static final int BOW_ILLUSION_STEP = 3121007;
    public static final int BOW_SHARP_EYE = 3121002;

    public Archer(MapleCharacter chr) {
        super(chr);
    }


    @Override
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket in) throws Exception {
        super.handleSkill(c, skillId, slv, in);

        switch (skillId) {


        }
    }


    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        super.handleAttack(c, attackInfo);
    }

    @Override
    public void handleBuff(MapleClient c, InPacket in, int skillId, int slv) {
        super.handleBuff(c, in, skillId, slv);
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        SkillInfo si = SkillData.getSkillInfo(skillId);
        boolean sendStat = true;
        Option o = new Option();
        Option oo = new Option();
        Option ooo = new Option();
        switch (skillId) {
            case BOW_ILLUSION_STEP:
                o.nOption = si.getValue(SkillStat.indieDex, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.DEX, o);
                oo.nOption = si.getValue(x, slv);
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.IllusionStep, oo);
                break;
            case BOW_SOUL_ARROW:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(SoulArrow, o);
                oo.nOption = si.getValue(epad, slv);
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(EPAD, oo);
                ooo.nOption = si.getValue(x, slv);
                ooo.rOption = skillId;
                ooo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(NoBulletConsume, ooo);
                break;
            case BOW_SHARP_EYE:
                int cr = si.getValue(x, slv);
                int cd = si.getValue(y, slv);
                o.nOption = (cr << 8) + cd;
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(SharpEyes, oo);
                //todo deal hyper passive
                break;
            default:
                sendStat = false;
        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return super.isHandlerOfJob(id);
    }

    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId);
    }

    @Override
    public int getFinalAttackSkill() {
        return super.getFinalAttackSkill();
    }
}
