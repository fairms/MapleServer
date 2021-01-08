package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.AssistType;
import im.cave.ms.enums.MoveAbility;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.Util;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.BasicStatUp;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Booster;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ComboCounter;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieDamR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieUnk1;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MaxHP;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MaxMP;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.PDD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.PowerGuard;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_SKILL_STATE;
import static im.cave.ms.enums.SkillStat.indieDamR;
import static im.cave.ms.enums.SkillStat.indiePad;
import static im.cave.ms.enums.SkillStat.indiePowerGuard;
import static im.cave.ms.enums.SkillStat.pdd;
import static im.cave.ms.enums.SkillStat.time;
import static im.cave.ms.enums.SkillStat.x;
import static im.cave.ms.enums.SkillStat.y;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.Job.adventurer
 * @date 12/7 19:55
 */
public class Warrior extends Beginner {
    public static final int IRON_BODY = 1000003;
    public static final int WARRIOR_MASTERY = 1000009;
    //HERO
    public static final int HERO_WEAPON_BOOSTER = 1101004;
    public static final int HERO_RAGE = 1101006;
    public static final int HERO_COMBO_ATTACK = 1101013;
    public static final int HERO_FINAL_ATTACK = 1100002;
    public static final int HERO_PHYSICAL_TRAINING = 1100009;
    public static final int HERO_MAPLE_WARRIOR = 1121000;
    public static final int HERO_LEGEND_ADVENTURER = 1121053;
    //KNIGHT
    public static final int KNIGHT_WEAPON_BOOSTER = 1301004;
    public static final int KNIGHT_IRON_WILL = 1301006;
    public static final int KNIGHT_HYPER_BODY = 1301007;
    public static final int KNIGHT_EVIL_EYE = 1301013;
    public static final int KNIGHT_EVIL_EYE_OF_DOMINATION = 1311013;
    public static final int KNIGHT_MAPLE_WARRIOR = 1321053;

    private final AtomicInteger comboCount = new AtomicInteger(1);
    private Summon evilEye;

    private static final int[] buffs = new int[]{
            HERO_WEAPON_BOOSTER,
            HERO_RAGE,
            HERO_COMBO_ATTACK,
            HERO_LEGEND_ADVENTURER,
            HERO_MAPLE_WARRIOR,
            KNIGHT_WEAPON_BOOSTER,
            KNIGHT_IRON_WILL,
            KNIGHT_HYPER_BODY,
            KNIGHT_EVIL_EYE,
            KNIGHT_EVIL_EYE_OF_DOMINATION,
            KNIGHT_MAPLE_WARRIOR
    };

    private static final int[] passive = new int[]{
            IRON_BODY,
            WARRIOR_MASTERY,
            HERO_PHYSICAL_TRAINING
    };

    public Warrior(MapleCharacter chr) {
        super(chr);
        if (chr != null && JobConstants.isHero(chr.getJob())) {
            if (!chr.hasSkill(HERO_COMBO_ATTACK)) {
                Skill skill = SkillData.getSkill(HERO_COMBO_ATTACK);
                Objects.requireNonNull(skill).setCurrentLevel(1);
                chr.addSkill(skill);
            }
        }
    }

    @Override
    public void handleSkill(MapleClient c, int skillId, int skillLevel, InPacket in) {
        super.handleSkill(c, skillId, skillLevel, in);
        if (isBuff(skillId)) {
            handleBuff(c, in, skillId, skillLevel);
        }
    }

    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        super.handleAttack(c, attackInfo);
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        Skill skill = player.getSkill(attackInfo.skillId);
        SkillInfo si = null;
        if (skill != null) {
            si = SkillData.getSkillInfo(attackInfo.skillId);
        }
        boolean hasHitMobs = attackInfo.mobCount > 0;
        if (JobConstants.isHero(player.getJob())) {
            if (hasHitMobs) {
                int comboProp = getComboProp(chr);
                if (Util.succeedProp(comboProp)) {
                    incCombo(chr);
                }
            }
        }
    }

    private void incCombo(MapleCharacter chr) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if (!tsm.hasStat(ComboCounter)) {
            return;
        }
        int combo = comboCount.get();
        int maxCombo = getMaxCombo(chr);
        if (combo < maxCombo) {
            combo = comboCount.getAndIncrement();
        }
        Option option = new Option();
        option.nOption = combo;
        option.rOption = HERO_COMBO_ATTACK;
        if (chr.getQuestEx().containsKey(QUEST_EX_SKILL_STATE) && chr.getQuestEx().containsKey(HERO_COMBO_ATTACK)) {
            option.bOption = Integer.parseInt(chr.getQuestEx().get(QUEST_EX_SKILL_STATE).get(String.valueOf(HERO_COMBO_ATTACK)));
        }
        tsm.putCharacterStatValue(ComboCounter, option);
        tsm.sendSetStatPacket();
    }

    @Override
    public void handleBuff(MapleClient c, InPacket in, int skillId, int slv) {
        super.handleBuff(c, in, skillId, slv);
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
        boolean sendStat = true;
        Option o = new Option();
        Option oo = new Option();
        switch (skillId) {
            case HERO_WEAPON_BOOSTER:
            case KNIGHT_WEAPON_BOOSTER:
                o.nOption = skillInfo.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = skillInfo.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o);
                break;
            case HERO_RAGE:
                o.nReason = skillId;
                o.nValue = skillInfo.getValue(indiePad, slv);
                o.tTerm = skillInfo.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o);
                oo.nOption = skillInfo.getValue(indiePowerGuard, slv); //减伤百分比
                oo.rOption = skillId;
                oo.tOption = skillInfo.getValue(time, slv);
                tsm.putCharacterStatValue(PowerGuard, oo);
                break;
            case HERO_COMBO_ATTACK:
                comboCount.set(1);
                o.nOption = comboCount.get();
                o.rOption = skillId;
                o.tOption = 0;
                if (chr.getQuestEx().containsKey(QUEST_EX_SKILL_STATE) && chr.getQuestEx().containsKey(HERO_COMBO_ATTACK)) {
                    o.bOption = Integer.parseInt(chr.getQuestEx().get(QUEST_EX_SKILL_STATE).get(String.valueOf(HERO_COMBO_ATTACK)));
                }
                tsm.putCharacterStatValue(ComboCounter, o);
                break;
            case HERO_MAPLE_WARRIOR:
            case KNIGHT_MAPLE_WARRIOR:
                o.nOption = skillInfo.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = skillInfo.getValue(time, slv);
                tsm.putCharacterStatValue(BasicStatUp, o);
                break;
            case HERO_LEGEND_ADVENTURER:
                o.nReason = skillId;
                o.nValue = skillInfo.getValue(indieDamR, slv);
                o.tTerm = skillInfo.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o);
                break;
            case KNIGHT_IRON_WILL:
                o.nOption = skillInfo.getValue(pdd, slv);
                o.rOption = skillId;
                o.tOption = skillInfo.getValue(time, slv);
                tsm.putCharacterStatValue(PDD, o);
                break;
            case KNIGHT_HYPER_BODY:
                o.nOption = skillInfo.getValue(x, slv);
                o.rOption = skillId;
                int duration = skillInfo.getValue(time, slv);
                o.tOption = duration;
                tsm.putCharacterStatValue(MaxHP, o);
                oo.nOption = skillInfo.getValue(y, slv);
                oo.rOption = skillId;
                oo.tOption = duration;
                tsm.putCharacterStatValue(MaxMP, oo);
                break;
            case KNIGHT_EVIL_EYE:
                spawnEvilEye(KNIGHT_EVIL_EYE, (byte) slv);
                sendStat = false;
                break;
            default:
                sendStat = false;
        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }

    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId) || Arrays.stream(buffs).anyMatch(b -> b == skillId);
    }


    private int getComboProp(MapleCharacter chr) {
        Skill skill = null;
        if (chr.hasSkill(1110013)) {    //Combo Synergy
            skill = chr.getSkill(1110013);
        } else if (chr.hasSkill(HERO_COMBO_ATTACK)) {
            skill = chr.getSkill(HERO_COMBO_ATTACK);
        }
        if (skill == null) {
            return 0;
        }
        return SkillData.getSkillInfo(skill.getSkillId()).getValue(SkillStat.prop, skill.getCurrentLevel());
    }


    private int getMaxCombo(MapleCharacter chr) {
        int num = 0;
        if (chr.hasSkill(HERO_COMBO_ATTACK)) {
            num = 6;
        }
//        if (chr.hasSkill(ADVANCED_COMBO)) {
//            num = 11;
//        }
        return num;
    }


    public void spawnEvilEye(int skillID, byte slv) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        SkillInfo si = SkillData.getSkillInfo(skillID);

        MapleMap map;
        evilEye = Summon.getSummonBy(chr, skillID, slv);
        map = chr.getMap();
        evilEye.setFlyMob(true);
        evilEye.setMoveAbility(MoveAbility.Fly);
        evilEye.setAssistType(AssistType.Heal);
        evilEye.setAttackActive(true);
        map.spawnSummon(evilEye);
        o.nReason = skillID;
        o.nValue = 1;
        o.summon = evilEye;
        o.tStart = (int) System.currentTimeMillis();
        o.tTerm = si.getValue(time, slv);
        tsm.putCharacterStatValue(IndieUnk1, o);
        tsm.sendSetStatPacket();
    }
}
