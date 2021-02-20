package im.cave.ms.client.field.obj.mob;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.skill.BurnedInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.MobPacket;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.Tuple;
import im.cave.ms.tools.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static im.cave.ms.client.field.obj.mob.MobStat.ACC;
import static im.cave.ms.client.field.obj.mob.MobStat.AddDamParty;
import static im.cave.ms.client.field.obj.mob.MobStat.BMageDebuff;
import static im.cave.ms.client.field.obj.mob.MobStat.BahamutLightElemAddDam;
import static im.cave.ms.client.field.obj.mob.MobStat.BattlePvPHelenaMark;
import static im.cave.ms.client.field.obj.mob.MobStat.DarkLightning;
import static im.cave.ms.client.field.obj.mob.MobStat.DeadlyCharge;
import static im.cave.ms.client.field.obj.mob.MobStat.EVA;
import static im.cave.ms.client.field.obj.mob.MobStat.ElementResetBySummon;
import static im.cave.ms.client.field.obj.mob.MobStat.Ember;
import static im.cave.ms.client.field.obj.mob.MobStat.ExchangeAttack;
import static im.cave.ms.client.field.obj.mob.MobStat.Explosion;
import static im.cave.ms.client.field.obj.mob.MobStat.ExtraBuffStat;
import static im.cave.ms.client.field.obj.mob.MobStat.Fatality;
import static im.cave.ms.client.field.obj.mob.MobStat.Freeze;
import static im.cave.ms.client.field.obj.mob.MobStat.Incizing;
import static im.cave.ms.client.field.obj.mob.MobStat.InvincibleBalog;
import static im.cave.ms.client.field.obj.mob.MobStat.Laser;
import static im.cave.ms.client.field.obj.mob.MobStat.LinkTeam;
import static im.cave.ms.client.field.obj.mob.MobStat.MCounter;
import static im.cave.ms.client.field.obj.mob.MobStat.MDR;
import static im.cave.ms.client.field.obj.mob.MobStat.MGuardUp;
import static im.cave.ms.client.field.obj.mob.MobStat.MImmune;
import static im.cave.ms.client.field.obj.mob.MobStat.MagicUp;
import static im.cave.ms.client.field.obj.mob.MobStat.MultiDamSkill;
import static im.cave.ms.client.field.obj.mob.MobStat.MultiPMDR;
import static im.cave.ms.client.field.obj.mob.MobStat.PCounter;
import static im.cave.ms.client.field.obj.mob.MobStat.PDR;
import static im.cave.ms.client.field.obj.mob.MobStat.PGuardUp;
import static im.cave.ms.client.field.obj.mob.MobStat.PImmune;
import static im.cave.ms.client.field.obj.mob.MobStat.PowerUp;
import static im.cave.ms.client.field.obj.mob.MobStat.SeperateSoulC;
import static im.cave.ms.client.field.obj.mob.MobStat.SeperateSoulP;
import static im.cave.ms.client.field.obj.mob.MobStat.SoulExplosion;
import static im.cave.ms.client.field.obj.mob.MobStat.Speed;
import static im.cave.ms.client.field.obj.mob.MobStat.TrueSight;
import static im.cave.ms.enums.SkillStat.dotInterval;
import static im.cave.ms.enums.SkillStat.dotSuperpos;
import static im.cave.ms.enums.SkillStat.dotTime;


public class MobTemporaryStat {
    private List<BurnedInfo> burnedInfos = new ArrayList<>();
    private Map<Tuple<Integer, Integer>, ScheduledFuture> burnCancelSchedules = new HashMap<>();
    private Map<Tuple<Integer, Integer>, ScheduledFuture> burnSchedules = new HashMap<>();
    private String linkTeam;
    private Comparator<MobStat> mobStatComparator = (o1, o2) -> {
        int res = 0;
        if (o1.getPos() < o2.getPos()) {
            res = -1;
        } else if (o1.getPos() > o2.getPos()) {
            res = 1;
        } else {
            if (o1.getVal() < o2.getVal()) {
                res = -1;
            } else if (o1.getVal() > o2.getVal()) {
                res = 1;
            }
        }
        return res;
    };
    private final TreeMap<MobStat, Option> currentStatVals = new TreeMap<>(mobStatComparator);
    private TreeMap<MobStat, Option> newStatVals = new TreeMap<>(mobStatComparator);
    private TreeMap<MobStat, Option> removedStatVals = new TreeMap<>(mobStatComparator);
    private Map<MobStat, ScheduledFuture> schedules = new HashMap<>();
    private Mob mob;

    public MobTemporaryStat(Mob mob) {
        this.mob = mob;
    }

    public MobTemporaryStat deepCopy() {
        MobTemporaryStat copy = new MobTemporaryStat(getMob());
        copy.setBurnedInfos(new ArrayList<>());
        for (BurnedInfo bi : getBurnedInfos()) {
            copy.getBurnedInfos().add(bi.deepCopy());
        }
        copy.setLinkTeam(getLinkTeam());
        copy.mobStatComparator = getMobStatComparator();
        for (MobStat ms : getCurrentStatVals().keySet()) {
            copy.addStatOptions(ms, getCurrentStatVals().get(ms).deepCopy());
        }
        return copy;
    }

    public Option getNewOptionsByMobStat(MobStat mobStat) {
        return getNewStatVals().getOrDefault(mobStat, null);
    }

    public Option getCurrentOptionsByMobStat(MobStat mobStat) {
        return getCurrentStatVals().getOrDefault(mobStat, null);
    }

    public Option getRemovedOptionsByMobStat(MobStat mobStat) {
        return getRemovedStatVals().getOrDefault(mobStat, null);
    }

    public void encode(OutPacket out) {
        synchronized (currentStatVals) {
            int[] mask = getNewMask();
            for (int j : mask) {
                out.writeInt(j);
            }

            for (Map.Entry<MobStat, Option> entry : getNewStatVals().entrySet()) {
                MobStat mobStat = entry.getKey();
                Option option = entry.getValue();
                switch (mobStat) {
                    case PAD:
                    case PDR:
                    case MAD:
                    case MDR:
                    case ACC:
                    case EVA:
                    case Speed:
                    case Stun:
                    case Freeze:
                    case Poison:
                    case Seal:
                    case Darkness:
                    case PowerUp:
                    case MagicUp:
                    case PGuardUp:
                    case MGuardUp:
                    case PImmune:
                    case MImmune:
                    case Web:
                    case HardSkin:
                    case Ambush:
                    case Venom:
                    case Blind:
                    case SealSkill:
                    case Dazzle:
                    case PCounter:
                    case MCounter:
                    case RiseByToss:
                    case BodyPressure:
                    case Weakness:
                    case Showdown:
                    case MagicCrash:
                    case DamagedElemAttr:
                    case Dark:
                    case Mystery:
                    case AddDamParty:
                    case HitCriDamR:
                    case Fatality:
                    case Lifting:
                    case DeadlyCharge:
                    case LucidNightmare:
                    case Smite:
                    case AddDamSkill:
                    case Incizing:
                    case DodgeBodyAttack:
                    case DebuffHealing:
                    case AddDamSkill2:
                    case BodyAttack:
                    case TempMoveAbility:
                    case FixDamRBuff:
                    case ElementDarkness:
                    case AreaInstallByHit:
                    case BMageDebuff:
                    case JaguarProvoke:
                    case JaguarBleeding:
                    case DarkLightning:
                    case PinkBeanFlowerPot:
                    case BattlePvPHelenaMark:
                    case PsychicLock:
                    case PsychicLockCoolTime:
                    case PsychicGroundMark:
                    case PowerImmune:
                    case PsychicForce:
                    case MultiPMDR:
                    case ElementResetBySummon:
                    case BahamutLightElemAddDam:
                    case BossPropPlus:
                    case MultiDamSkill:
                    case RWLiftPress:
                    case RWChoppingHammer:
                    case TimeBomb:
                    case Treasure:
                    case AddEffect:
                    case Invincible:
                    case Explosion:
                    case HangOver:
                        out.writeInt(getNewOptionsByMobStat(mobStat).nOption);
                        out.writeInt(getNewOptionsByMobStat(mobStat).rOption);
                        out.writeShort(getNewOptionsByMobStat(mobStat).tOption / 500);
                }
            }
            if (hasNewMobStat(PDR)) {
                out.writeInt(getNewOptionsByMobStat(PDR).cOption);
            }
            if (hasNewMobStat(MDR)) {
                out.writeInt(getNewOptionsByMobStat(MDR).cOption);
            }
            if (hasNewMobStat(PCounter)) {
                out.writeInt(getNewOptionsByMobStat(PCounter).wOption);
            }
            if (hasNewMobStat(MCounter)) {
                out.writeInt(getNewOptionsByMobStat(MCounter).wOption);
            }
            if (hasNewMobStat(PCounter)) {
                out.writeInt(getNewOptionsByMobStat(PCounter).mOption); // nCounterProb
                out.write(getNewOptionsByMobStat(PCounter).bOption); // bCounterDelay
                out.writeInt(getNewOptionsByMobStat(PCounter).nReason); // nAggroRank
            } else if (hasNewMobStat(MCounter)) {
                out.writeInt(getNewOptionsByMobStat(MCounter).mOption); // nCounterProb
                out.write(getNewOptionsByMobStat(MCounter).bOption); // bCounterDelay
                out.writeInt(getNewOptionsByMobStat(MCounter).nReason); // nAggroRank
            }
            if (hasNewMobStat(Fatality)) {
                out.writeInt(getNewOptionsByMobStat(Fatality).wOption);
                out.writeInt(getNewOptionsByMobStat(Fatality).uOption);
                out.writeInt(getNewOptionsByMobStat(Fatality).pOption);
                out.writeInt(getNewOptionsByMobStat(Fatality).yOption);
                out.writeInt(getNewOptionsByMobStat(Fatality).mOption);
            }
            if (hasNewMobStat(Explosion)) {
                out.writeInt(getNewOptionsByMobStat(Explosion).wOption);
            }
            if (hasNewMobStat(ExtraBuffStat)) {
                List<Option> values = getNewOptionsByMobStat(ExtraBuffStat).extraOpts;
                out.writeBool(values.size() > 0);
                if (values.size() > 0) {
                    out.writeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).nOption); // nPAD
                    out.writeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).mOption); // nMAD
                    out.writeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).xOption); // nPDR
                    out.writeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).yOption); // nMDR
                }
            }
            if (hasNewMobStat(DeadlyCharge)) {
                out.writeInt(getNewOptionsByMobStat(DeadlyCharge).pOption);
                out.writeInt(getNewOptionsByMobStat(DeadlyCharge).pOption);
            }
            if (hasNewMobStat(Incizing)) {
                out.writeInt(getNewOptionsByMobStat(Incizing).wOption);
                out.writeInt(getNewOptionsByMobStat(Incizing).uOption);
                out.writeInt(getNewOptionsByMobStat(Incizing).pOption);
            }
            if (hasNewMobStat(Speed)) {
                out.write(getNewOptionsByMobStat(Speed).mOption);
            }
            if (hasNewMobStat(BMageDebuff)) {
                out.writeInt(getNewOptionsByMobStat(BMageDebuff).cOption);
            }
            if (hasNewMobStat(DarkLightning)) {
                out.writeInt(getNewOptionsByMobStat(DarkLightning).cOption);
            }
            if (hasNewMobStat(BattlePvPHelenaMark)) {
                out.writeInt(getNewOptionsByMobStat(BattlePvPHelenaMark).cOption);
            }
            if (hasNewMobStat(MultiPMDR)) {
                out.writeInt(getNewOptionsByMobStat(MultiPMDR).cOption);
            }
            if (hasNewMobStat(Freeze)) {
                out.writeInt(getNewOptionsByMobStat(Freeze).cOption);
            }
            if (hasNewMobStat(MobStat.BurnedInfo)) {
                out.write(getBurnedInfos().size());
                for (BurnedInfo bi : getBurnedInfos()) {
                    out.writeInt(0);
                    bi.encode(out);
                }
            }
            if (hasNewMobStat(InvincibleBalog)) {
                out.write(getNewOptionsByMobStat(InvincibleBalog).nOption);
                out.write(getNewOptionsByMobStat(InvincibleBalog).bOption);
            }
            if (hasNewMobStat(ExchangeAttack)) {
                out.write(getNewOptionsByMobStat(ExchangeAttack).bOption);
            }
            if (hasNewMobStat(AddDamParty)) {
                out.writeInt(getNewOptionsByMobStat(AddDamParty).wOption);
                out.writeInt(getNewOptionsByMobStat(AddDamParty).pOption);
                out.writeInt(getNewOptionsByMobStat(AddDamParty).cOption);
            }
            if (hasNewMobStat(LinkTeam)) {
                out.writeMapleAsciiString(getLinkTeam());
            }
            if (hasNewMobStat(SoulExplosion)) {
                out.writeInt(getNewOptionsByMobStat(SoulExplosion).nOption);
                out.writeInt(getNewOptionsByMobStat(SoulExplosion).rOption);
                out.writeInt(getNewOptionsByMobStat(SoulExplosion).wOption);
            }
            if (hasNewMobStat(SeperateSoulP)) {
                out.writeInt(getNewOptionsByMobStat(SeperateSoulP).nOption);
                out.writeInt(getNewOptionsByMobStat(SeperateSoulP).rOption);
                out.writeShort(getNewOptionsByMobStat(SeperateSoulP).tOption / 500);
                out.writeInt(getNewOptionsByMobStat(SeperateSoulP).wOption);
                out.writeInt(getNewOptionsByMobStat(SeperateSoulP).uOption);
            }
            if (hasNewMobStat(SeperateSoulC)) {
                out.writeInt(getNewOptionsByMobStat(SeperateSoulC).nOption);
                out.writeInt(getNewOptionsByMobStat(SeperateSoulC).rOption);
                out.writeShort(getNewOptionsByMobStat(SeperateSoulC).tOption / 500);
                out.writeInt(getNewOptionsByMobStat(SeperateSoulC).wOption);
            }
            if (hasNewMobStat(Ember)) {
                out.writeInt(getNewOptionsByMobStat(Ember).nOption);
                out.writeInt(getNewOptionsByMobStat(Ember).rOption);
                out.writeInt(getNewOptionsByMobStat(Ember).wOption);
                out.writeInt(getNewOptionsByMobStat(Ember).tOption / 500);
                out.writeInt(getNewOptionsByMobStat(Ember).uOption);
            }
            if (hasNewMobStat(TrueSight)) {
                out.writeInt(getNewOptionsByMobStat(TrueSight).nOption);
                out.writeInt(getNewOptionsByMobStat(TrueSight).rOption);
                out.writeInt(getNewOptionsByMobStat(TrueSight).tOption / 500);
                out.writeInt(getNewOptionsByMobStat(TrueSight).cOption);
                out.writeInt(getNewOptionsByMobStat(TrueSight).pOption);
                out.writeInt(getNewOptionsByMobStat(TrueSight).uOption);
                out.writeInt(getNewOptionsByMobStat(TrueSight).wOption);
            }
            if (hasNewMobStat(MultiDamSkill)) {
                out.writeInt(getNewOptionsByMobStat(MultiDamSkill).cOption);
            }
            if (hasNewMobStat(Laser)) {
                out.writeInt(getNewOptionsByMobStat(Laser).nOption);
                out.writeInt(getNewOptionsByMobStat(Laser).rOption);
                out.writeInt(getNewOptionsByMobStat(Laser).tOption / 500);
                out.writeInt(getNewOptionsByMobStat(Laser).wOption);
                out.writeInt(getNewOptionsByMobStat(Laser).uOption);
            }
            if (hasNewMobStat(ElementResetBySummon)) {
                out.writeInt(getNewOptionsByMobStat(ElementResetBySummon).cOption);
                out.writeInt(getNewOptionsByMobStat(ElementResetBySummon).pOption);
                out.writeInt(getNewOptionsByMobStat(ElementResetBySummon).uOption);
                out.writeInt(getNewOptionsByMobStat(ElementResetBySummon).wOption);
            }
            if (hasNewMobStat(BahamutLightElemAddDam)) {
                out.writeInt(getNewOptionsByMobStat(BahamutLightElemAddDam).pOption);
                out.writeInt(getNewOptionsByMobStat(BahamutLightElemAddDam).cOption);
            }
            getNewStatVals().clear();
        }
    }

    private int[] getMaskByCollection(Map<MobStat, Option> map) {
        int[] res = new int[5];
        for (MobStat mobStat : map.keySet()) {
            res[mobStat.getPos()] |= mobStat.getVal();
        }
        OutPacket out = new OutPacket();
        for (int re : res) {
            out.writeInt(re);
        }
        return res;
    }

    public int[] getNewMask() {
        return getMaskByCollection(getNewStatVals());
    }

    public int[] getCurrentMask() {
        return getMaskByCollection(getCurrentStatVals());
    }

    public int[] getRemovedMask() {
        return getMaskByCollection(getRemovedStatVals());
    }

    public boolean hasNewMobStat(MobStat mobStat) {
        return getNewStatVals().keySet().contains(mobStat);
    }

    public boolean hasCurrentMobStat(MobStat mobStat) {
        return getCurrentStatVals().keySet().contains(mobStat);
    }

    public boolean hasCurrentMobStatBySkillId(int skillId) {
        return getCurrentStatVals().entrySet().stream().anyMatch(map -> map.getValue().rOption == skillId);
    }

    public boolean hasBurnFromSkillAndOwner(int skillID, int ownerCID) {
        return getBurnBySkillAndOwner(skillID, ownerCID) != null;
    }

    /**
     * Checks if this MTS has a burn skill by a given skillID and owner character id. If the given owner's id is 0,
     * ignores the given owner id.
     * <p>
     * //     * @param skillID  the skill id of which the burn should exist
     * //     * @param ownerCID the burn skill's owner's id that should match (or 0 if ignored)
     *
     * @return the BurnedInfo of the burn on this MTS, or null if there is none
     */
    public BurnedInfo getBurnBySkillAndOwner(int skillID, int ownerCID) {
        BurnedInfo res = null;
        for (BurnedInfo bi : getBurnedInfos()) {
            if (bi.getSkillId() == skillID && (bi.getCharacterId() == ownerCID || ownerCID == 0)) {
                res = bi;
            }
        }
        return res; // wow no lambda for once
    }

    public boolean hasRemovedMobStat(MobStat mobStat) {
        return getRemovedStatVals().containsKey(mobStat);
    }

    public Map<MobStat, Option> getCurrentStatVals() {
        return currentStatVals;
    }

    public TreeMap<MobStat, Option> getNewStatVals() {
        return newStatVals;
    }

    public TreeMap<MobStat, Option> getRemovedStatVals() {
        return removedStatVals;
    }

    public void removeMobStat(MobStat mobStat, boolean fromSchedule) {
        synchronized (currentStatVals) {
            getRemovedStatVals().put(mobStat, getCurrentStatVals().get(mobStat));
            getCurrentStatVals().remove(mobStat);
            getMob().getMap().broadcastMessage(MobPacket.statReset(getMob(), (byte) 1, false));
            getSchedules().remove(mobStat);
            if (!fromSchedule && getSchedules().containsKey(mobStat)) {
                getSchedules().get(mobStat).cancel(true);
                getSchedules().remove(mobStat);
            } else {
                getSchedules().remove(mobStat);
            }
        }
    }

    public void removeBurnedInfo(MapleCharacter chr, boolean fromSchedule) {
        synchronized (burnedInfos) {
            int charId = chr.getId();
            List<BurnedInfo> biList = getBurnedInfos().stream().filter(bi -> bi.getCharacterId() == charId).collect(Collectors.toList());
            getBurnedInfos().removeAll(biList);
            getRemovedStatVals().put(MobStat.BurnedInfo, getCurrentOptionsByMobStat(MobStat.BurnedInfo));
            if (getBurnedInfos().size() == 0) {
                getCurrentStatVals().remove(MobStat.BurnedInfo);
            }
            getMob().getMap().broadcastMessage(MobPacket.statReset(getMob(), (byte) 1, false, biList));
            if (chr.isBattleRecordOn()) {
                for (BurnedInfo bi : biList) {
                    int count = Math.min(bi.getDotCount(), (Util.getCurrentTime() - bi.getStartTime()) / bi.getInterval());
//                    chr.write(BattleRecordMan.dotDamageInfo(bi, count)); //添加dot伤害到战斗统计中
                }
            }
            if (!fromSchedule) {
                getBurnCancelSchedules().get(charId).cancel(true);
                getBurnCancelSchedules().remove(charId);
                getBurnSchedules().get(charId).cancel(true);
                getBurnSchedules().remove(charId);
            } else {
                getBurnCancelSchedules().remove(charId);
                getBurnSchedules().remove(charId);
            }
        }
    }

    /**
     * Adds a new MobStat to this MobTemporaryStat. Will immediately broadcast the reaction to all
     * clients.
     * Only works for user skills, not mob skills. For the latter, use {@link
     * #addMobSkillOptionsAndBroadCast(MobStat, Option)}.
     *
     * @param mobStat The MobStat to add.
     * @param option  The Option that contains the values of the stat.
     */
    public void addStatOptionsAndBroadcast(MobStat mobStat, Option option) {
        addStatOptions(mobStat, option);
        mob.getMap().broadcastMessage(MobPacket.statSet(getMob(), (short) 0));
    }

    /**
     * Adds a new MobStat to this MobTemporary stat. Will immediately broadcast the reaction to all
     * clients.
     * Only works for mob skills, not user skills. For the latter, use {@link
     * //     * #addStatOptionsAndBroadcast(MobStat, Option)}.
     *
     * @param mobStat The MobStat to add.
     * @param o       The option that contains the values of the stat.
     */
    public void addMobSkillOptionsAndBroadCast(MobStat mobStat, Option o) {
        o.rOption |= o.slv << 16; // mob skills are encoded differently: not an int, but short (skill ID), then short (slv).
        addStatOptionsAndBroadcast(mobStat, o);
    }

    public void addStatOptions(MobStat mobStat, Option option) {
        option.tTerm *= 1000;
        option.tOption *= 1000;
        int tAct = option.tOption > 0 ? option.tOption : option.tTerm;
        getNewStatVals().put(mobStat, option);
        getCurrentStatVals().put(mobStat, option);
        if (tAct > 0 && mobStat != MobStat.BurnedInfo) {
            if (getSchedules().containsKey(mobStat)) {
                getSchedules().get(mobStat).cancel(true);
            }
            ScheduledFuture sf = EventManager.addEvent(() -> removeMobStat(mobStat, true), tAct);
            getSchedules().put(mobStat, sf);
        }
    }


    public List<BurnedInfo> getBurnedInfos() {
        return burnedInfos;
    }

    public void setBurnedInfos(List<BurnedInfo> burnedInfos) {
        this.burnedInfos = burnedInfos;
    }

    public Comparator getMobStatComparator() {
        return mobStatComparator;
    }

    public String getLinkTeam() {
        return linkTeam;
    }

    public void setLinkTeam(String linkTeam) {
        this.linkTeam = linkTeam;
    }

    public boolean hasNewMovementAffectingStat() {
        return getNewStatVals().keySet().stream().anyMatch(MobStat::isMovementAffectingStat);
    }

    public boolean hasCurrentMovementAffectingStat() {
        return getCurrentStatVals().keySet().stream().anyMatch(MobStat::isMovementAffectingStat);
    }

    public boolean hasRemovedMovementAffectingStat() {
        return getRemovedStatVals().keySet().stream().anyMatch(MobStat::isMovementAffectingStat);
    }

    public Map<MobStat, ScheduledFuture> getSchedules() {
        if (schedules == null) {
            schedules = new HashMap<>();
        }
        return schedules;
    }

    public Mob getMob() {
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }

    public void clear() {
        for (ScheduledFuture t : getBurnSchedules().values()) {
            t.cancel(true);
        }
        getBurnSchedules().clear();
        for (ScheduledFuture t : getBurnCancelSchedules().values()) {
            t.cancel(true);
        }
        getBurnCancelSchedules().clear();
        for (ScheduledFuture t : getSchedules().values()) {
            t.cancel(true);
        }
        getSchedules().clear();
        getCurrentStatVals().forEach((ms, o) -> removeMobStat(ms, false));
    }

    public void createAndAddBurnedInfo(MapleCharacter chr, Skill skill) {
        int charId = chr.getId();
        SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
        int slv = skill.getCurrentLevel();
        Tuple<Integer, Integer> timerKey = new Tuple<>(charId, skill.getSkillId());
        BurnedInfo bi = getBurnBySkillAndOwner(skill.getSkillId(), chr.getId());
        int time = si.getValue(dotTime, slv) * 1000;
        if (bi == null) {
            // create a new burned info, as one didn't exist yet (taking skill + char id combination as key)
            bi = new BurnedInfo();
            bi.setCharacterId(charId);
            bi.setChr(chr);
            bi.setSkillId(skill.getSkillId());
            bi.setDamage((int) chr.getDamageCalc().calcPDamageForPvM(skill.getSkillId(), (byte) skill.getCurrentLevel()));
            bi.setInterval(si.getValue(dotInterval, slv) * 1000);
            bi.setDotCount(time / bi.getInterval());
            bi.setSuperPos(si.getValue(dotSuperpos, slv));
            bi.setAttackDelay(0);
            bi.setDotTickIdx(0);
            bi.setDotTickDamR(0); //damage added for every tick
            bi.setDotAnimation(bi.getAttackDelay() + bi.getInterval() + time);
            synchronized (burnedInfos) {
                getBurnedInfos().add(bi);
            }
        } else {
            // extend the timer if it does exist
            getBurnCancelSchedules().get(timerKey).cancel(true);
            getBurnSchedules().get(timerKey).cancel(true);
        }
        long damage = bi.getDamage();
        bi.setStartTime(Util.getCurrentTime());
        bi.setLastUpdate(Util.getCurrentTime());
        bi.setEnd((int) (System.currentTimeMillis() + time));
        Option o = new Option();
        o.nOption = 0;
        o.rOption = skill.getSkillId();
        addStatOptionsAndBroadcast(MobStat.BurnedInfo, o);
        ScheduledFuture sf = EventManager.addEvent(() -> removeBurnedInfo(chr, true), time);
        ScheduledFuture burn = EventManager.addFixedRateEvent(
                () -> getMob().damage(chr, damage), bi.getAttackDelay() + bi.getInterval(), bi.getInterval(), bi.getDotCount());
        getBurnCancelSchedules().put(timerKey, sf);
        getBurnSchedules().put(timerKey, burn);
    }

    public Map<Tuple<Integer, Integer>, ScheduledFuture> getBurnCancelSchedules() {
        return burnCancelSchedules;
    }

    public Map<Tuple<Integer, Integer>, ScheduledFuture> getBurnSchedules() {
        return burnSchedules;
    }

    public void removeBuffs() {
        removeMobStat(PowerUp, false);
        removeMobStat(MagicUp, false);
        removeMobStat(PGuardUp, false);
        removeMobStat(MGuardUp, false);
        removeMobStat(PImmune, false);
        removeMobStat(MImmune, false);
        removeMobStat(PCounter, false);
        removeMobStat(MCounter, false);
        if (hasCurrentMobStat(ACC) && getCurrentOptionsByMobStat(ACC).nOption > 0) {
            removeMobStat(ACC, false);
        }
        if (hasCurrentMobStat(EVA) && getCurrentOptionsByMobStat(EVA).nOption > 0) {
            removeMobStat(EVA, false);
        }
        getMob().getMap().broadcastMessage(MobPacket.statReset(getMob(), (byte) 0, false));
    }

    public void removeEverything() {
        Set<MobStat> mobStats = new HashSet<>(getCurrentStatVals().keySet());
        mobStats.stream().filter(ms -> ms != MobStat.BurnedInfo).forEach(ms -> removeMobStat(ms, false));
        Set<BurnedInfo> burnedInfos = new HashSet<>(getBurnedInfos());
        burnedInfos.forEach(bi -> removeBurnedInfo(bi.getChr(), false));
    }
}
