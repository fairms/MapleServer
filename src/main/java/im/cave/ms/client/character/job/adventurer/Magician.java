package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.enums.MoveAbility;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.AntiMagicShell;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.BasicStatUp;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Booster;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ChillingStep;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ElementalReset;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Infinity;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MagicGuard;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Stance;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.TeleportMasteryOn;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.TeleportMasteryRange;
import static im.cave.ms.enums.SkillStat.indieMad;
import static im.cave.ms.enums.SkillStat.prop;
import static im.cave.ms.enums.SkillStat.time;
import static im.cave.ms.enums.SkillStat.x;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.job.adventurer
 * @date 12/28 14:26
 */
public class Magician extends Beginner {
    public static final int MAGIC_GUARD = 2001002; //魔法盾
    //IL
    public static final int IL_MAGIC_BOOSTER = 2201010; //魔法狂暴
    public static final int IL_MEDITATION = 2201001; //精神力
    public static final int IL_CHILLING_STEP = 2201009;

    public static final int IL_ELEMENTAL_DECREASE = 2211008; //自然力重置
    public static final int IL_TELEPORT_MASTERY = 2211007;
    public static final int IL_ELEMENTAL_ADAPTATION = 2211012;
    public static final int IL_THUNDER_STORM = 2211011;

    public static final int IL_MAPLE_WARRIOR = 2221000;
    public static final int IL_INFINITY = 2221004;
    public static final int IL_ELQUINES = 2221005;

    public static final int IL_TELEPORT_MASTERY_RANGE = 2221045;

    //FP

    private static final int[] buffs = new int[]{
            MAGIC_GUARD,
            IL_MAGIC_BOOSTER,
            IL_MEDITATION,
            IL_CHILLING_STEP,
            IL_ELEMENTAL_DECREASE,
            IL_TELEPORT_MASTERY,
            IL_TELEPORT_MASTERY_RANGE,
            IL_INFINITY,
            IL_MAPLE_WARRIOR
    };

    private int infinityStack;
    private ScheduledFuture infinityTimer;

    public Magician(MapleCharacter chr) {
        super(chr);
    }

    @Override
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket in) {
        try {
            super.handleSkill(c, skillId, slv, in);
        } catch (Exception e) {
            getMapleCharacter().chatMessage("skill not exist");
        }
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
            case MAGIC_GUARD:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = 0;
                tsm.putCharacterStatValue(MagicGuard, o);
                break;
            case IL_MAGIC_BOOSTER:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o);
                break;
            case IL_MEDITATION:
                o.nValue = si.getValue(indieMad, slv);
                o.nReason = skillId;
                o.tStart = (int) System.currentTimeMillis();
                o.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMAD, o);
                break;
            case IL_ELEMENTAL_DECREASE:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(ElementalReset, o);
                break;
            case IL_TELEPORT_MASTERY:
            case IL_TELEPORT_MASTERY_RANGE:
                CharacterTemporaryStat masteryStat = skillId == IL_TELEPORT_MASTERY ? TeleportMasteryRange : TeleportMasteryOn;
                if (tsm.hasStat(masteryStat)) {
                    tsm.removeStatsBySkill(skillId);
                    tsm.sendResetStatPacket();
                } else {
                    o.nOption = si.getValue(x, slv);
                    o.rOption = skillId;
                    o.tOption = 0;
                    tsm.putCharacterStatValue(masteryStat, o);
                }
                break;
            case IL_CHILLING_STEP:
                if (tsm.hasStat(ChillingStep)) {
                    tsm.removeStatsBySkill(skillId);
                    tsm.sendResetStatPacket();
                } else {
                    o.rOption = skillId;
                    tsm.putCharacterStatValue(ChillingStep, o);
                }
                break;
            case IL_ELEMENTAL_ADAPTATION:
                o.nOption = 1;
                o.rOption = skillId;
                tsm.putCharacterStatValue(AntiMagicShell, o);
                break;
            case IL_MAPLE_WARRIOR:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(BasicStatUp, o);
                break;
            case IL_INFINITY:
                o.nOption = 1;
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Infinity, o);
                oo.nOption = si.getValue(prop, slv);
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Stance, oo);
                infinityStack = 0;
                if (infinityTimer != null && !infinityTimer.isDone()) {
                    infinityTimer.cancel(true);
                }
                infinity();
                break;
            case IL_THUNDER_STORM:
                Summon summon = Summon.getSummonBy(c.getPlayer(), skillId, (byte) slv);
                MapleMap map = c.getPlayer().getMap();
                summon.setFlyMob(true);
                map.spawnSummon(summon);
                break;
            case IL_ELQUINES:
                summon = Summon.getSummonBy(c.getPlayer(), skillId, (byte) slv);
                map = c.getPlayer().getMap();
                summon.setFlyMob(true);
                summon.setMoveAbility(MoveAbility.Walk);
                map.spawnSummon(summon);
                break;
            default:
                sendStat = false;

        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }


    private int getInfinitySkill() {
        int skill = 0;
        if (chr.hasSkill(IL_INFINITY)) {
            skill = IL_INFINITY;
        }
        return skill;
    }


    private void infinity() {
        if (!chr.hasSkill(getInfinitySkill())) {
            return;
        }
        //todo
    }


    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId) || Arrays.stream(buffs).anyMatch(b -> b == skillId);
    }


}
