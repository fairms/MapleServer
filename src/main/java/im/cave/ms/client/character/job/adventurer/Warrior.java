package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.MobAttackInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobStat;
import im.cave.ms.client.field.obj.mob.MobTemporaryStat;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.MobPacket;
import im.cave.ms.connection.packet.SummonPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.AssistType;
import im.cave.ms.enums.JobType;
import im.cave.ms.enums.MoveAbility;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.Util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.*;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_SKILL_STATE;
import static im.cave.ms.enums.SkillStat.*;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.Job.adventurer
 * @date 12/7 19:55
 */
public class Warrior extends Beginner {
    //WARRIOR 战士一转
    public static final int SLASH_BLAST = 1001005; //群体攻击
    public static final int WAR_LEAP = 1001008; //战士飞叶
    public static final int LEAP_ATTACK = 1001010; //飞跃一击
    public static final int IRON_BODY = 1000003; //圣甲术
    public static final int WARRIOR_MASTERY = 1000009; //战士精通
    //HERO 英雄
    //  二转
    public static final int BRANDISH = 1101011; //轻舞飞扬
    public static final int HERO_COMBO_FURY = 1101012; //狂澜之力
    public static final int HERO_COMBO_FURY_DOWN = 1100012; //狂澜之力_下
    public static final int HERO_COMBO_ATTACK = 1101013; //斗气集中
    public static final int HERO_WEAPON_BOOSTER = 1101004; //快速武器
    public static final int HERO_RAGE = 1101006; //愤怒之火
    public static final int HERO_WEAPON_MASTERY = 1100000; //武器精通
    public static final int HERO_FINAL_ATTACK = 1100002; //终极剑斧
    public static final int HERO_PHYSICAL_TRAINING = 1100009; //物理训练
    // 三转
    public static final int HERO_INTREPID_SLASH = 1111010; //勇猛劈砍
    public static final int HERO_RUSH = 1111012; //突进
    public static final int HERO_PANIC = 1111003; //恐慌
    public static final int HERO_SHOUT = 1111008; //虎咆啸
    public static final int HERO_UPWARD_CHARGE = 1111015; //冲天击
    public static final int HERO_COMBO_SYNERGY = 1110013; //斗气协和
    public static final int HERO_SELF_RECOVERY = 1110000; //自我恢复
    public static final int HERO_CHANGE_ATTACK = 1110009; //乘胜追击
    public static final int HERO_ENDURE = 1110011; //抵抗力
    //  四转
    public static final int HERO_RAGING_BLOW = 1121008; //终极打击
    public static final int HERO_RAGING_BLOW_ENHANCED = 1120017; //强化终极打击
    public static final int HERO_PUNCTURE = 1121015; //烈焰冲斩
    public static final int HERO_MAGIC_CRASH = 1121016; //魔击无效
    public static final int HERO_POWER_STANCE = 1120014; //稳如泰山
    public static final int HERO_ENRAGE = 1120010; //葵花宝典
    public static final int HERO_MAPLE_WARRIOR = 1121000; //冒险岛勇士
    public static final int HERO_HERO_WILL = 1120011; //勇士的意志
    public static final int HERO_ADVANCED_COMBO = 1120003; //进阶斗气
    public static final int HERO_COMBAT_MASTERY = 1120012; //战斗精通
    public static final int HERO_ADVANCED_FINAL_ATTACK = 1120013; //进阶终极攻击
    //  超级技能
    public static final int HERO_LEGEND_ADVENTURER = 1121053; //传说冒险家
    public static final int HERO_CRY_VALHALLA = 1121054; //战灵附体
    //Paladin 圣骑士
    //  二转
    public static final int PALADIN_FLAME_CHARGE = 1201011; //火焰冲击
    public static final int PALADIN_BLIZZARD_CHARGE = 1201012; //寒冰冲击
    public static final int PALADIN_ELEMENTAL_CHARGE = 1200014; //元素冲击
    public static final int PALADIN_CLOSE_COMBAT = 1201013; //准骑士密令
    public static final int PALADIN_WEAPON_BOOSTER = 1201004; //快速武器
    public static final int PALADIN_WEAPON_MASTERY = 1200000; //精准武器
    public static final int PALADIN_FINAL_ATTACK = 1200002; //终极剑钝器
    public static final int PALADIN_PHYSICAL_TRAINING = 1200009; //物理训练
    //  三转
    public static final int PALADIN_LIGHTNING_CHARGE = 1211008; //雷鸣冲击
    public static final int PALADIN_HP_RECOVERY = 1211010; //元气恢复
    public static final int PALADIN_RUSH = 1211012; //突进
    public static final int PALADIN_THREATEN = 1211013; //压制术
    public static final int PALADIN_UPWARD_CHARGE = 1211017; //冲天击
    public static final int PALADIN_PARASHOCK_GUARD = 1211014; //抗震防御
    public static final int PALADIN_COMBAT_ORDERS = 1211011; //战斗命令
    public static final int PALADIN_SHIELD_MASTERY = 1210001; //盾牌精通
    public static final int PALADIN_ACHILLES = 1210015; //阿基里斯
    public static final int PALADIN_DIVINE_SHIELD = 1210016; //祝福盔甲
    //  四转
    public static final int PALADIN_BLAST = 1221009; //连环环坡
    public static final int PALADIN_DIVINE_CHARGE = 1221004; //神圣冲击
    public static final int PALADIN_MAGIC_CRASH = 1221014; //魔击无效
    public static final int PALADIN_HEAVEN_HAMMER = 1221011; //圣域
    public static final int PALADIN_ELEMENTAL_FORCE = 1221015; //元素之力
    public static final int PALADIN_POWER_STANCE = 1220017; //稳如泰山
    public static final int PALADIN_MAPLE_WARRIOR = 1221000; //冒险岛勇士
    public static final int PALADIN_HERO_WILL = 1221012; //勇士的意志
    public static final int PALADIN_GUARDIAN = 1221016; //守护之神
    public static final int PALADIN_HIGH_PALADIN = 1220018; //圣骑士专家
    public static final int PALADIN_ADVANCED_CHARGE = 1220010; //万佛归一破
    //DARK KNIGHT 黑骑士
    //  二转
    public static final int KNIGHT_PIERCING_DRIVE = 1301011; //贯穿刺透
    public static final int KNIGHT_EVIL_EYE = 1301013; //灵魂助力
    public static final int KNIGHT_SPEAR_SWEEP = 1301012; //神矛天引
    public static final int KNIGHT_WEAPON_BOOSTER = 1301004; //快速武器
    public static final int KNIGHT_IRON_WILL = 1301006; //极限防御
    public static final int KNIGHT_HYPER_BODY = 1301007; //神圣之火
    public static final int KNIGHT_WEAPON_MASTERY = 1300000; //精准武器
    public static final int KNIGHT_FINAL_ATTACK = 1300004; //终极枪矛
    public static final int KNIGHT_PHYSICAL_TRAINING = 1300009; //物理训练
    //  三转
    public static final int KNIGHT_LA_MANCHA_SPEAR = 1311011; //拉曼查之枪
    public static final int KNIGHT_RUSH = 1311012; //突进
    public static final int KNIGHT_EVIL_EYE_SHOCK = 1311014; //灵魂助力震惊
    public static final int KNIGHT_UPWARD_CHARGE = 1311017; //冲天击
    public static final int KNIGHT_CROSS_CHAIN = 1311015; //交叉锁链
    public static final int KNIGHT_EVIL_EYE_OF_DOMINATION = 1310013; //灵魂助力统治
    public static final int KNIGHT_LORD_OF_DARKNESS = 1310009; //黑暗至尊
    public static final int KNIGHT_ENDURE = 1310010; //抵抗力
    public static final int KNIGHT_HEX_OF_THE_EVIL_EYE = 1310016; //灵魂祝福
    //  四转
    public static final int KNIGHT_GUNGNIRS_DESCENT = 1321013; //神枪降临
    public static final int KNIGHT_MAGIC_CRASH = 1321014; //魔击无效
    public static final int KNIGHT_DARK_IMPALE = 1321012; //黑暗穿刺
    public static final int KNIGHT_POWER_STANCE = 1320017; //稳如泰山
    public static final int KNIGHT_SACRIFICE = 1321015; //龙之献祭
    public static final int KNIGHT_MAPLE_WARRIOR = 1321000; //冒险岛勇士
    public static final int KNIGHT_HERO_WILL = 1321010; //勇士的意志
    public static final int KNIGHT_FINAL_PACT = 1320016; //重生契约
    public static final int KNIGHT_BARRICADE_MASTERY = 1320018; //进阶精准武器
    public static final int KNIGHT_REVENGE_OF_THE_EVIL_EYE = 1320011; //灵魂复仇
    //  超级技能
    public static final int KNIGHT_DARK_THIRST = 1321054; //黑暗饥渴

    private final AtomicInteger comboCount = new AtomicInteger(1);
    private Summon evilEye;
    private int lastCharge;

    public static final int[] buffs = new int[]{
            HERO_WEAPON_BOOSTER,
            HERO_RAGE,
            HERO_COMBO_ATTACK,
            HERO_LEGEND_ADVENTURER,
            HERO_MAPLE_WARRIOR,
            HERO_CRY_VALHALLA,

            PALADIN_WEAPON_BOOSTER,
            PALADIN_COMBAT_ORDERS,
            PALADIN_PARASHOCK_GUARD,
            PALADIN_MAPLE_WARRIOR,
            PALADIN_GUARDIAN,

            KNIGHT_WEAPON_BOOSTER,
            KNIGHT_IRON_WILL,
            KNIGHT_HYPER_BODY,
            KNIGHT_EVIL_EYE,
            KNIGHT_EVIL_EYE_OF_DOMINATION,
            KNIGHT_MAPLE_WARRIOR,
            KNIGHT_SACRIFICE
    };

    public static final int[] passive = new int[]{
            IRON_BODY,
            WARRIOR_MASTERY,
            HERO_PHYSICAL_TRAINING
    };

    public Warrior(MapleCharacter chr) {
        super(chr);
        if (chr != null) {
            short jobId = chr.getJob();
            if (JobConstants.isHero(jobId)) {
                JobType job = JobType.getJobById(jobId);
                if (job.isAdvancedJobOf(JobType.FIGHTER)) {
                    if (!chr.hasSkill(HERO_COMBO_ATTACK)) {
                        Skill skill = SkillData.getSkill(HERO_COMBO_ATTACK);
                        Objects.requireNonNull(skill).setCurrentLevel(1);
                        chr.addSkill(skill);
                    }
                }
                if (job.isAdvancedJobOf(JobType.CRUSADER)) {
                    if (!chr.hasSkill(HERO_UPWARD_CHARGE)) {
                        Skill skill = SkillData.getSkill(HERO_UPWARD_CHARGE);
                        Objects.requireNonNull(skill).setCurrentLevel(1);
                        chr.addSkill(skill);
                    }
                }
            }
        }
    }

    @Override
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket in) {
        try {
            super.handleSkill(c, skillId, slv, in);
        } catch (Exception e) {
            getMapleCharacter().chatMessage("技能未处理 R:技能未学习");
            return;
        }
        switch (skillId) {
            case PALADIN_HP_RECOVERY:
                hpRecovery();
                break;
            case HERO_MAGIC_CRASH:
            case KNIGHT_MAGIC_CRASH:
            case PALADIN_MAGIC_CRASH:
                chr.chatMessage("魔击无效未处理");
                break;
            case HERO_HERO_WILL:
            case KNIGHT_HERO_WILL:
            case PALADIN_HERO_WILL:
                chr.chatMessage("勇士意志未处理");
                break;
        }
    }

    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        super.handleAttack(c, attackInfo);
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        Skill skill = player.getSkill(attackInfo.skillId);
        SkillInfo si;
        int slv = 0;
        int skillId;
        if (skill != null) {
            si = SkillData.getSkillInfo(attackInfo.skillId);
            slv = skill.getCurrentLevel();
            skillId = skill.getSkillId();
        } else {
            return;
        }
        boolean hasHitMobs = attackInfo.mobCount > 0;
        if (JobConstants.isHero(player.getJob())) {
            if (hasHitMobs) {
                int comboProp = getComboProp(chr);
                if (Util.succeedProp(comboProp)) {
                    incCombo();
                }
            }
        }
        Option o = new Option();
        Option oo = new Option();
        Option ooo = new Option();
        switch (attackInfo.skillId) {
            case HERO_PANIC:
                for (MobAttackInfo mobAttackInfo : attackInfo.mobAttackInfo) {
                    int objectId = mobAttackInfo.objectId;
                    MapleMap map = chr.getMap();
                    Mob mob = (Mob) map.getObj(objectId);
                    if (mob == null) {
                        continue;
                    }
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o.nOption = si.getValue(z, slv);
                    o.rOption = skill.getSkillId();
                    o.tOption = 0;
                    mts.addStatOptions(MobStat.PAD, o);
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        oo.nOption = -si.getValue(x, slv); // minus?
                        oo.rOption = skill.getSkillId();
                        oo.tOption = si.getValue(time, slv);
                        mts.addStatOptions(MobStat.Blind, oo);
                    }
                    c.write(MobPacket.statSet(mob, (short) 0));
                }
                break;
            case HERO_SHOUT:
                removeCombo(chr, 1);
                break;
            case HERO_PUNCTURE:
                removeCombo(chr, 1);
                for (MobAttackInfo mobAttackInfo : attackInfo.mobAttackInfo) {
                    int objectId = mobAttackInfo.objectId;
                    MapleMap map = chr.getMap();
                    Mob mob = (Mob) map.getObj(objectId);
                    if (mob == null) {
                        continue;
                    }
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o.nOption = si.getValue(y, slv);
                    o.rOption = skillId;
                    o.tOption = si.getValue(time, slv);
                    mts.addStatOptions(MobStat.AddDamParty, o);
                    if (Util.succeedProp(si.getValue(prop, slv))) {
                        mts.createAndAddBurnedInfo(chr, skill);
                    }
                    c.write(MobPacket.statSet(mob, (short) 0));
                }
                break;
            case PALADIN_FLAME_CHARGE:
            case PALADIN_BLIZZARD_CHARGE:
            case PALADIN_LIGHTNING_CHARGE:
            case PALADIN_DIVINE_CHARGE:
                Option option = new Option();
                option.nOption = skillId == PALADIN_DIVINE_CHARGE ? 1 : 0;
                option.rOption = skillId;
                option.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(WeaponCharge, option);
                tsm.sendSetStatPacket();
                giveChargeBuff(skillId, tsm);
                break;
        }
    }


    public void incCombo() {
        MapleCharacter chr = getMapleCharacter();
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
        if (chr.getQuestEx().containsKey(QUEST_EX_SKILL_STATE) &&
                chr.getQuestEx().get(QUEST_EX_SKILL_STATE).containsKey(String.valueOf(HERO_COMBO_ATTACK))) {
            option.bOption = Integer.parseInt(chr.getQuestEx().get(QUEST_EX_SKILL_STATE).get(String.valueOf(HERO_COMBO_ATTACK)));
        }
        tsm.putCharacterStatValue(ComboCounter, option);
        tsm.sendSetStatPacket();
    }

    private void removeCombo(MapleCharacter chr, int count) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        int currentCount = getComboCount(chr);
        Option o = new Option();
        if (currentCount > count + 1) {
            o.nOption = currentCount - count;
        } else {
            o.nOption = 0;
        }
        comboCount.set(o.nOption);
        o.rOption = HERO_COMBO_ATTACK;
        tsm.putCharacterStatValue(ComboCounter, o);
        tsm.sendSetStatPacket();
    }

    public int getComboCount(MapleCharacter c) {
        TemporaryStatManager tsm = c.getTemporaryStatManager();
        if (tsm.hasStat(ComboCounter)) {
            return tsm.getOption(ComboCounter).nOption;
        }
        return -1;
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
            case HERO_WEAPON_BOOSTER:
            case KNIGHT_WEAPON_BOOSTER:
            case PALADIN_WEAPON_BOOSTER:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o);
                break;
            case HERO_RAGE:
                o.nReason = skillId;
                o.nValue = si.getValue(indiePad, slv);
                o.tStart = ((int) System.currentTimeMillis());
                o.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o);
                oo.nOption = si.getValue(indiePowerGuard, slv); //减伤百分比
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PowerGuard, oo);
                break;
            case HERO_COMBO_ATTACK:
                comboCount.set(1);
                o.nOption = comboCount.get();
                o.rOption = skillId;
                o.tOption = 0;
                if (chr.getQuestEx().containsKey(QUEST_EX_SKILL_STATE) &&
                        chr.getQuestEx().get(QUEST_EX_SKILL_STATE).containsKey(String.valueOf(HERO_COMBO_ATTACK))) {
                    o.bOption = Integer.parseInt(chr.getQuestEx().get(QUEST_EX_SKILL_STATE).get(String.valueOf(HERO_COMBO_ATTACK)));
                }
                tsm.putCharacterStatValue(ComboCounter, o);
                break;
            case HERO_ENRAGE:
                o.nOption = 2501; //todo 可能是Mask
                o.rOption = skillId;
                tsm.putCharacterStatValue(Enrage, o);
                oo.nOption = si.getValue(y, slv);
                oo.rOption = skillId;
                tsm.putCharacterStatValue(EnrageCrDam, oo); //暴击伤害
                break;
            case HERO_CRY_VALHALLA:
                o.nReason = skillId;
                o.nValue = si.getValue(indieCr, slv);
                o.tStart = ((int) System.currentTimeMillis());
                o.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieCr, o);
                oo.nReason = skillId;
                oo.nValue = si.getValue(indiePad, slv);
                oo.tStart = ((int) System.currentTimeMillis());
                oo.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o);
                ooo.nOption = 100;
                ooo.rOption = skillId;
                ooo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Stance, ooo);
                Option oooo = new Option();
                oooo.nOption = si.getValue(x, slv);
                oooo.rOption = skillId;
                oooo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AsrR, oooo);
                tsm.putCharacterStatValue(TerR, oooo);
                break;
            case HERO_LEGEND_ADVENTURER:
                o.nReason = skillId;
                o.nValue = si.getValue(indieDamR, slv);
                o.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o);
                break;
            case PALADIN_PARASHOCK_GUARD:
                if (tsm.getOptByCTSAndSkill(Guard, skillId) != null) {
                    tsm.removeStatsBySkill(PALADIN_PARASHOCK_GUARD);
                    tsm.sendResetStatPacket();
                    break;
                }
                o.nReason = skillId;
                o.nValue = si.getValue(indiePad, slv);
                o.tStart = ((int) System.currentTimeMillis());
                tsm.putCharacterStatValue(IndiePAD, o);
                oo.nReason = skillId;
                oo.nValue = si.getValue(z, slv);
                oo.tStart = ((int) System.currentTimeMillis());
                tsm.putCharacterStatValue(IndiePDDR, oo);
                ooo.nOption = -si.getValue(x, slv);
                ooo.rOption = si.getSkillId();
                tsm.putCharacterStatValue(Guard, ooo); //todo check
                break;
            case PALADIN_COMBAT_ORDERS:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(CombatOrders, o);
                break;
            case PALADIN_ELEMENTAL_FORCE:
                o.nReason = skillId;
                o.nValue = si.getValue(indiePMdR, slv);
                o.tStart = ((int) System.currentTimeMillis());
                o.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePMdR, o);
                break;
            case HERO_MAPLE_WARRIOR:
            case KNIGHT_MAPLE_WARRIOR:
            case PALADIN_MAPLE_WARRIOR:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(BasicStatUp, o);
                break;
            case KNIGHT_IRON_WILL:
                o.nOption = si.getValue(pdd, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(PDD, o);
                break;
            case KNIGHT_HYPER_BODY:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                int duration = si.getValue(time, slv);
                o.tOption = duration;
                tsm.putCharacterStatValue(MaxHP, o);
                oo.nOption = si.getValue(y, slv);
                oo.rOption = skillId;
                oo.tOption = duration;
                tsm.putCharacterStatValue(MaxMP, oo);
                break;
            case KNIGHT_EVIL_EYE:
                spawnEvilEye(KNIGHT_EVIL_EYE, (byte) slv);
                sendStat = false;
                break;
            case KNIGHT_CROSS_CHAIN:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(CrossOverChain, o);
                break;
            case KNIGHT_SACRIFICE:
                if (tsm.hasStatBySkillId(KNIGHT_EVIL_EYE)) {
                    o.nReason = skillId;
                    o.nValue = si.getValue(x, slv);
                    o.tStart = (int) System.currentTimeMillis();
                    o.tTerm = si.getValue(time, slv);
                    tsm.putCharacterStatValue(IndieIgnoreMobpdpR, o);

                    oo.nReason = skillId;
                    oo.nValue = si.getValue(indieBDR, slv);
                    oo.tStart = (int) System.currentTimeMillis();
                    oo.tTerm = si.getValue(time, slv);
                    tsm.putCharacterStatValue(IndieBDR, oo);

                    tsm.removeStatsBySkill(KNIGHT_EVIL_EYE_OF_DOMINATION);
                    tsm.removeStatsBySkill(KNIGHT_EVIL_EYE_SHOCK);
                    removeEvilEye(tsm, c);

                    chr.heal((int) (chr.getMaxHP() / ((double) 100 / si.getValue(y, slv))));
                    chr.announce(UserPacket.skillCoolDown(1321013));
                }
            case KNIGHT_DARK_THIRST:
                o.nReason = skillId;
                o.nValue = si.getValue(indiePad, 1);
                o.tStart = ((int) System.currentTimeMillis());
                o.tTerm = si.getValue(time, 1);
                tsm.putCharacterStatValue(IndiePAD, o);

                oo.nOption = si.getValue(x, 1);
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, 1);
                tsm.putCharacterStatValue(AttackRecovery, oo);
            default:
                sendStat = false;
        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }

    private void removeEvilEye(TemporaryStatManager tsm, MapleClient c) {
        tsm.removeStatsBySkill(KNIGHT_EVIL_EYE);
        tsm.sendResetStatPacket();
        c.getPlayer().getMap().broadcastMessage(SummonPacket.summonRemoved(evilEye, (byte) 15));
        //level type 15:龙之献祭
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
        if (chr.hasSkill(HERO_ADVANCED_COMBO)) {
            num = 11;
        }
        return num;
    }


    public void spawnEvilEye(int skillID, byte slv) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        Option oo = new Option();
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
        tsm.putCharacterStatValue(IndieEmpty, o);

        oo.nOption = 1;
        oo.rOption = skillID;
        oo.tOption = si.getValue(time, slv);
        tsm.putCharacterStatValue(Beholder, oo);
        tsm.sendSetStatPacket();
    }

    private void giveChargeBuff(int skillId, TemporaryStatManager tsm) {
        Option o = new Option();
        SkillInfo chargeInfo = SkillData.getSkillInfo(PALADIN_ELEMENTAL_CHARGE);
        int amount = 1;
        if (tsm.hasStat(ElementalCharge)) {
            amount = tsm.getOption(ElementalCharge).mOption;
            if (lastCharge == skillId) {
                return;
            }
            if (amount < chargeInfo.getValue(z, 1)) {
                amount++;
            }
        }
        lastCharge = skillId;
        o.nOption = 1;
        o.rOption = PALADIN_ELEMENTAL_CHARGE;
        o.tOption = (10 * chargeInfo.getValue(time, 1)); // elemental charge  // 10x actual duration
        o.mOption = amount;
        o.wOption = amount * chargeInfo.getValue(w, 1); // elemental charge
        o.uOption = amount * chargeInfo.getValue(u, 1);
        o.zOption = amount * chargeInfo.getValue(z, 1);
        tsm.putCharacterStatValue(ElementalCharge, o);
        tsm.sendSetStatPacket();
    }


    public void hpRecovery() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        if (chr.hasSkill(PALADIN_HP_RECOVERY)) {
            Skill skill = chr.getSkill(PALADIN_HP_RECOVERY);
            byte slv = (byte) skill.getCurrentLevel();
            SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
            int recovery = si.getValue(x, slv);
            int amount = 10;

            if (tsm.hasStat(Restoration)) {
                amount = tsm.getOption(Restoration).nOption;
                if (amount < 300) {
                    amount = amount + 10;
                }
            }

            o.nOption = amount;
            o.rOption = skill.getSkillId();
            o.tOption = si.getValue(time, slv);
            int heal = Math.max((recovery + 10) - amount, 10);
            chr.heal((int) (chr.getMaxHP() / ((double) 100 / heal)));
            tsm.putCharacterStatValue(Restoration, o);
            tsm.sendSetStatPacket();
        }
    }

    public void healByEvilEye() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if (chr.hasSkill(KNIGHT_EVIL_EYE) && tsm.hasStatBySkillId(KNIGHT_EVIL_EYE)) {
            Skill skill = chr.getSkill(KNIGHT_EVIL_EYE);
            SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
            byte slv = (byte) skill.getCurrentLevel();
            int healHp = si.getValue(hp, slv);
            chr.heal(healHp);
            chr.announce(UserPacket.effect(Effect.hpRecovery(healHp)));
            chr.getMap().broadcastMessage(SummonPacket.summonedSkill(evilEye, skill.getSkillId()));
        }
    }

    public void giveHexOfTheEvilEyeBuffs() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        Option oo = new Option();
        Option ooo = new Option();
        Option oooo = new Option();
        Skill skill = chr.getSkill(KNIGHT_HEX_OF_THE_EVIL_EYE);
        byte slv = (byte) skill.getCurrentLevel();
        SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
        if (tsm.getOptByCTSAndSkill(EPDD, KNIGHT_HEX_OF_THE_EVIL_EYE) == null) {
            o.nOption = si.getValue(epad, slv);
            o.rOption = skill.getSkillId();
            o.tOption = si.getValue(time, slv);
            tsm.putCharacterStatValue(EPAD, o);

            oo.nOption = si.getValue(epdd, slv);
            oo.rOption = skill.getSkillId();
            oo.tOption = si.getValue(time, slv);
            tsm.putCharacterStatValue(EPDD, ooo);

            ooo.nReason = skill.getSkillId();
            ooo.nValue = si.getValue(indieCr, slv);
            ooo.tStart = (int) System.currentTimeMillis();
            ooo.tTerm = si.getValue(time, slv);
            tsm.putCharacterStatValue(IndieCr, ooo);

            tsm.sendSetStatPacket();
        }

    }


    @Override
    public boolean isHandlerOfJob(short id) {
        Set<JobType> jobs = JobType.getAllAdvancedJobs(JobType.WARRIOR.getJob());
        JobType job = JobType.getJobById(id);
        return job == JobType.WARRIOR || jobs.contains(job);
    }

    @Override
    public int getFinalAttackSkill() {
        return super.getFinalAttackSkill();
    }

    public AtomicInteger getComboCount() {
        return comboCount;
    }
}
