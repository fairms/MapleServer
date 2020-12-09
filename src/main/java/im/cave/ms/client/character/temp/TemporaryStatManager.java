package im.cave.ms.client.character.temp;

import im.cave.ms.client.Job.MapleJob;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.TSIndex;
import im.cave.ms.net.packet.PlayerPacket;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.service.EventManager;
import im.cave.ms.tools.Tuple;
import im.cave.ms.tools.Util;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.*;

/**
 * Created on 1/3/2018.
 */
public class TemporaryStatManager {
    private static final Logger log = LoggerFactory.getLogger(TemporaryStatManager.class);
    private final Map<CharacterTemporaryStat, List<Option>> currentStats = new HashMap<>();
    private final Map<CharacterTemporaryStat, List<Option>> newStats = new HashMap<>();
    private final Map<CharacterTemporaryStat, List<Option>> removedStats = new HashMap<>();
    private final Map<CharacterTemporaryStat, ScheduledFuture> schedules = new HashMap<>();
    private final HashMap<Tuple<CharacterTemporaryStat, Option>, ScheduledFuture> indieSchedules = new HashMap<>();
    private int pvpDamage;
    private byte defenseState;
    private byte defenseAtt;
    private int[] diceInfo = new int[22];
    private final int[] diceOption = new int[8];
    private List<Integer> mobZoneStates;
    private int viperEnergyCharge;
    //    private StopForceAtom stopForceAtom;
    private final MapleCharacter chr;
    private final List<TemporaryStatBase> twoStates = new ArrayList<>();
    //    private final Set<AffectedArea> affectedAreas = new HashSet<>();
    private final Map<BaseStat, Integer> baseStats = new HashMap<>();

    public TemporaryStatManager(MapleCharacter chr) {
        this.chr = chr;
        for (CharacterTemporaryStat cts : TSIndex.getAllCTS()) {
            switch (cts) {
                case PartyBooster:
//                    twoStates.add(new PartyBooster());
                    break;
                case GuidedBullet:
//                    twoStates.add(new GuidedBullet());
                    break;
                case EnergyCharged:
                    twoStates.add(new TemporaryStatBase(true));
                    break;
                case RideVehicle:
                    twoStates.add(new TwoStateTemporaryStat(false));
                    break;
                default:
                    twoStates.add(new TwoStateTemporaryStat(true));
                    break;
            }
        }
    }

    public List<TemporaryStatBase> getTwoStates() {
        return twoStates;
    }

    public TemporaryStatBase getTSBByTSIndex(TSIndex tsi) {
        return getTwoStates().get(tsi.getIndex());
    }

    public void putCharacterStatValue(CharacterTemporaryStat cts, Option option) {
        boolean indie = cts.isIndie();
        option.setTimeToMillis();
        SkillInfo skillinfo = SkillData.getSkillInfo(indie ? option.nReason : option.rOption);
        if (skillinfo != null && !skillinfo.isNotIncBuffDuration()) {
            int duration = (indie ? option.tTerm : option.tOption);
            long buffTimeR = getChr().getTotalStat(BaseStat.buffTimeR); // includes the 100% base
            if (indie) {
                option.tTerm = (int) ((buffTimeR * duration) / 100);
            } else {
                option.tOption = (int) ((buffTimeR * duration) / 100);
            }
        }
        //战斗指令
        if (cts == CombatOrders) {
            chr.setCombatOrders(option.nOption);
        }
        //独立：最大伤害
        if (cts == IndieMaxDamageOver || cts == IndieMaxDamageOverR) {
            long base = getTotalNOptionOfStat(IndieMaxDamageOver) + (cts == IndieMaxDamageOver ? option.nValue : 0);
            long rate = getTotalNOptionOfStat(IndieMaxDamageOverR) + (cts == IndieMaxDamageOverR ? option.nValue : 0);
            if ((GameConstants.DAMAGE_CAP + base) * rate > Integer.MAX_VALUE) {
                chr.chatMessage(ChatType.Tip, "Not adding max damage over, as it would bypass the hard cap of damage.");
                return;
            }
        }
        if (!indie) {
            List<Option> optList = new ArrayList<>();
            optList.add(option);
            if (hasStat(cts)) {
                Option oldOption = getCurrentStats().get(cts).get(0);
                // remove old base stats from map
                for (Map.Entry<BaseStat, Integer> stats : BaseStat.getFromCTS(cts, oldOption).entrySet()) {
                    removeBaseStat(stats.getKey(), stats.getValue());
                }
            }
            //新增加buff
            getNewStats().put(cts, optList);
            getCurrentStats().put(cts, optList);
            for (Map.Entry<BaseStat, Integer> stats : BaseStat.getFromCTS(cts, option).entrySet()) {
                //新增属性
                addBaseStat(stats.getKey(), stats.getValue());
            }
            //持续时间
            if (option.tOption > 0) {
                if (getSchedules().containsKey(cts)) {
                    getSchedules().get(cts).cancel(false);
                }
                ScheduledFuture sf = EventManager.addEvent(() -> removeStat(cts, true), option.tOption);
                getSchedules().put(cts, sf);
            }
        } else {
            List<Option> optList;
            if (hasStat(cts)) {
                optList = getCurrentStats().get(cts);
            } else {
                optList = new ArrayList<>();
            }
            if (optList.contains(option)) {
                // remove old option of the same skill
                Option oldOption = getOptByCTSAndSkill(cts, option.nReason);
                for (Map.Entry<BaseStat, Integer> stats : BaseStat.getFromCTS(cts, oldOption).entrySet()) {
                    removeBaseStat(stats.getKey(), stats.getValue());
                }
                optList.remove(oldOption);
            }
            optList.add(option);
            getNewStats().put(cts, optList);
            getCurrentStats().put(cts, optList);
            // Add stats to base stat
            for (Map.Entry<BaseStat, Integer> stats : BaseStat.getFromCTS(cts, option).entrySet()) {
                addBaseStat(stats.getKey(), stats.getValue());
            }
            if (option.tTerm > 0) {
                Tuple tuple = new Tuple(cts, option);
                if (getIndieSchedules().containsKey(tuple)) {
                    getIndieSchedules().get(tuple).cancel(false);
                }
                ScheduledFuture sf = EventManager.addEvent(() -> removeIndieStat(cts, option, true), option.tTerm);
                getIndieSchedules().put(tuple, sf);
            }
        }
        if (cts != LifeTidal && JobConstants.isDemonAvenger(chr.getJobId())) {
//            ((Demon) chr.getJobHandler()).sendHpUpdate();
        }
    }

    public Option getOptByCTSAndSkill(CharacterTemporaryStat cts, int skillID) {
        Option res = null;
        if (getCurrentStats().containsKey(cts)) {
            for (Option o : getCurrentStats().get(cts)) {
                if (o.rOption == skillID || o.nReason == skillID) {
                    res = o;
                    break;
                }
            }
        }
        return res;
    }

    public synchronized void removeStat(CharacterTemporaryStat cts, boolean fromSchedule) {
        if (cts == CombatOrders) {
            chr.setCombatOrders(0);
        }
        Option opt = getOption(cts);
        //去除属性
        for (Map.Entry<BaseStat, Integer> stats : BaseStat.getFromCTS(cts, opt).entrySet()) {
            removeBaseStat(stats.getKey(), stats.getValue());
        }
        getRemovedStats().put(cts, getCurrentStats().get(cts));
        getCurrentStats().remove(cts);
        sendResetStatPacket(cts == RideVehicle || cts == RideVehicleExpire);
        if (TSIndex.isTwoStat(cts)) {
            getTSBByTSIndex(TSIndex.getTSEFromCTS(cts)).reset();
        }
        if (!fromSchedule && getSchedules().containsKey(cts)) {
            getSchedules().get(cts).cancel(false);
        } else {
            getSchedules().remove(cts);
        }
        if (JobConstants.isDemonAvenger(chr.getJobId())) {
//            ((Demon) chr.getJobHandler()).sendHpUpdate();
        }
    }

    public synchronized void removeIndieStat(CharacterTemporaryStat cts, Option option, boolean fromSchedule) {
        List<Option> optList = new ArrayList<>();
        optList.add(option);
        getRemovedStats().put(cts, optList);
        for (Map.Entry<BaseStat, Integer> stats : BaseStat.getFromCTS(cts, option).entrySet()) {
            removeBaseStat(stats.getKey(), stats.getValue());
        }
        if (getCurrentStats().containsKey(cts)) {
//            if (option.summon != null) {
//                getChr().getField().broadcastPacket(Summoned.summonedRemoved(option.summon, LeaveType.ANIMATION));
//                option.summon = null;
//            }
            getCurrentStats().get(cts).remove(option);
            if (getCurrentStats().get(cts).size() == 0) {
                getCurrentStats().remove(cts);
            }
        }
        sendResetStatPacket();
        Tuple tuple = new Tuple(cts, option);
        if (!fromSchedule && getIndieSchedules().containsKey(tuple)) {
            getIndieSchedules().get(tuple).cancel(false);
        } else {
            getIndieSchedules().remove(tuple);
        }
    }

    public boolean hasNewStat(CharacterTemporaryStat cts) {
        return newStats.containsKey(cts);
    }

    public boolean hasStat(CharacterTemporaryStat cts) {
        return getCurrentStats().containsKey(cts);
    }

    public Option getOption(CharacterTemporaryStat cts) {
        if (hasStat(cts)) {
            return getCurrentStats().get(cts).get(0);
        }
        return new Option();
    }

    public List<Option> getOptions(CharacterTemporaryStat cts) {
        if (hasStat(cts)) {
            return getCurrentStats().get(cts);
        }
        return new ArrayList<>();
    }

    public int[] getMaskByCollection(Map<CharacterTemporaryStat, List<Option>> map) {
        int[] res = new int[CharacterTemporaryStat.length];
        for (int i = 0; i < res.length; i++) {
            for (CharacterTemporaryStat cts : map.keySet()) {
                if (cts.getPos() == i) {
                    res[i] |= cts.getVal();
                }
            }
        }
        return res;
    }

    public int[] getTotalMask() {
        return getMaskByCollection(getCurrentStats());
    }

    public int[] getNewMask() {
        return getMaskByCollection(getNewStats());
    }

    public int[] getRemovedMask() {
        return getMaskByCollection(getRemovedStats());
    }

    public void encodeForLocal(MaplePacketLittleEndianWriter mplew) {
        int[] mask = getNewMask();
        for (int i = 0; i < getNewMask().length; i++) {
            mplew.writeInt(mask[i]);
        }
        List<CharacterTemporaryStat> orderedAndFilteredCtsList = new ArrayList<>(getNewStats().keySet()).stream()
                .filter(cts -> cts.getOrder() != -1)
                .sorted(Comparator.comparingInt(CharacterTemporaryStat::getOrder))
                .collect(Collectors.toList());
        for (CharacterTemporaryStat cts : orderedAndFilteredCtsList) {
            if (cts.getOrder() != -1) {
                Option o = getOption(cts);
                if (cts.isEncodeInt()) {
                    mplew.writeInt(o.nOption);
                } else {
                    mplew.writeShort(o.nOption);
                }
                mplew.writeInt(o.rOption);
                mplew.writeInt(o.tOption);
            }
        }

        if (hasNewStat(ComboCounter)) {
            mplew.writeInt(0);
        }

        mplew.writeZeroBytes(13);
        encodeIndieTempStat(mplew);
        getNewStats().clear();
    }

    private void encodeIndieTempStat(MaplePacketLittleEndianWriter mplew) {
        Map<CharacterTemporaryStat, List<Option>> stats = getCurrentStats().entrySet().stream()
                .filter(stat -> stat.getKey().isIndie() && getNewStats().containsKey(stat.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        TreeMap<CharacterTemporaryStat, List<Option>> sortedStats = new TreeMap<>(stats);
        if (sortedStats.size() == 0) {
            mplew.writeInt(0);
        }
        for (Map.Entry<CharacterTemporaryStat, List<Option>> stat : sortedStats.entrySet()) {
            int curTime = (int) System.currentTimeMillis();
            List<Option> options = stat.getValue();
            if (options == null) {
                mplew.writeInt(0);
                continue;
            }
            mplew.writeInt(options.size());
            for (Option option : options) {
                mplew.writeInt(option.nReason);
                mplew.writeInt(option.nValue);
                mplew.writeInt(curTime);
                mplew.writeInt(0);
                mplew.writeInt(option.tTerm);
                mplew.writeZeroBytes(16);
            }
        }
    }


//    public void encodeForRemote(OutPacket outPacket, Map<CharacterTemporaryStat, List<Option>> collection) {
//        int[] mask = getMaskByCollection(collection);
//        for (int maskElem : mask) {
//            outPacket.encodeInt(maskElem);
//        }
//        List<CharacterTemporaryStat> orderedAndFilteredCtsList = new ArrayList<>(collection.keySet()).stream()
//                .filter(cts -> cts.getOrder() != -1)
//                .sorted(Comparator.comparingInt(CharacterTemporaryStat::getOrder))
//                .collect(Collectors.toList());
//        for (CharacterTemporaryStat cts : orderedAndFilteredCtsList) {
//            if (cts.getRemoteOrder() != -1) {
//                Option o = getOption(cts);
//                switch (cts) {
//                    case Unk82: // Why does this get encoded, then immediately overwritten?
//                        outPacket.encodeShort(o.nOption);
//                        break;
//                }
//                if (!cts.isNotEncodeAnything()) {
//                    if (cts.isRemoteEncode1()) {
//                        outPacket.encodeByte(o.nOption);
//                    } else if (cts.isRemoteEncode4()) {
//                        outPacket.encodeInt(o.nOption);
//                    } else {
//                        outPacket.encodeShort(o.nOption);
//                    }
//                    if (!cts.isNotEncodeReason()) {
//                        outPacket.encodeInt(o.rOption);
//                    }
//                }
//                switch (cts) {
//                    case Contagion:
//                        outPacket.encodeInt(o.tOption);
//                        break;
//                    case BladeStance:
//                    case ImmuneBarrier:
//                    case Unk530:
//                    case Unk531:
//                    case Unk586:
//                        outPacket.encodeInt(o.xOption);
//                        break;
//                    case FullSoulMP:
//                        outPacket.encodeInt(o.rOption);
//                        outPacket.encodeInt(o.xOption);
//                        break;
//                    case AntiMagicShellBool:
//                    case PoseTypeBool:
//                        outPacket.encodeByte(o.bOption);
//                        break;
//                }
//            }
//        }
//        outPacket.encodeByte(getDefenseAtt());
//        outPacket.encodeByte(getDefenseState());
//        outPacket.encodeByte(getPvpDamage());
//        outPacket.encodeInt(0);// unknown
//        outPacket.encodeInt(0);
//        Set<CharacterTemporaryStat> ctsSet = collection.keySet();
//        if (ctsSet.contains(Unk526)) {
//            outPacket.encodeInt(collection.get(Unk526).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk527)) {
//            outPacket.encodeInt(collection.get(Unk527).get(0).xOption);
//        }
//        if (ctsSet.contains(ZeroAuraStr)) {
//            outPacket.encodeByte(collection.get(ZeroAuraStr).get(0).bOption);
//        }
//        if (ctsSet.contains(ZeroAuraSpd)) {
//            outPacket.encodeByte(collection.get(ZeroAuraSpd).get(0).bOption);
//        }
//        if (ctsSet.contains(BMageAura)) {
//            outPacket.encodeByte(collection.get(BMageAura).get(0).bOption);
//        }
//        if (ctsSet.contains(BattlePvPHelenaMark)) {
//            outPacket.encodeInt(collection.get(BattlePvPHelenaMark).get(0).nOption);
//            outPacket.encodeInt(collection.get(BattlePvPHelenaMark).get(0).rOption);
//            outPacket.encodeInt(collection.get(BattlePvPHelenaMark).get(0).cOption);
//        }
//        if (ctsSet.contains(BattlePvPLangEProtection)) {
//            outPacket.encodeInt(collection.get(BattlePvPLangEProtection).get(0).nOption);
//            outPacket.encodeInt(collection.get(BattlePvPLangEProtection).get(0).rOption);
//        }
//        if (ctsSet.contains(MichaelSoulLink)) {
//            outPacket.encodeInt(collection.get(MichaelSoulLink).get(0).xOption);
//            outPacket.encodeByte(collection.get(MichaelSoulLink).get(0).bOption);
//            outPacket.encodeInt(collection.get(MichaelSoulLink).get(0).cOption);
//            outPacket.encodeInt(collection.get(MichaelSoulLink).get(0).yOption);
//        }
//        if (ctsSet.contains(AdrenalinBoost)) {
//            outPacket.encodeByte(collection.get(AdrenalinBoost).get(0).cOption);
//        }
//        if (ctsSet.contains(Stigma)) {
//            outPacket.encodeInt(collection.get(Stigma).get(0).bOption);
//        }
//        if (ctsSet.contains(DivineEcho)) {
//            outPacket.encodeShort(collection.get(DivineEcho).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk423)) {
//            outPacket.encodeShort(collection.get(Unk423).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk424)) {
//            outPacket.encodeShort(collection.get(Unk424).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk503)) {
//            outPacket.encodeInt(collection.get(Unk503).get(0).xOption);
//            outPacket.encodeInt(collection.get(Unk503).get(0).bOption);
//            outPacket.encodeInt(collection.get(Unk503).get(0).cOption);
//            outPacket.encodeInt(collection.get(Unk503).get(0).yOption);
//        }
//        if (ctsSet.contains(VampDeath)) {
//            outPacket.encodeInt(collection.get(VampDeath).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk520)) {
//            outPacket.encodeInt(collection.get(Unk520).get(0).bOption);
//            outPacket.encodeInt(collection.get(Unk520).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk255)) {
//            outPacket.encodeInt(collection.get(Unk255).get(0).bOption);
//            outPacket.encodeInt(collection.get(Unk255).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk538)) {
//            outPacket.encodeInt(collection.get(Unk538).get(0).xOption);
//            outPacket.encodeInt(collection.get(Unk538).get(0).bOption);
//            outPacket.encodeInt(collection.get(Unk538).get(0).cOption);
//        }
//        if (getStopForceAtom() != null) {
//            getStopForceAtom().encode(outPacket);
//        } else {
//            new StopForceAtom().encode(outPacket);
//        }
//        outPacket.encodeInt(getViperEnergyCharge());
//        for (int i = 0; i < 7; i++) {// 7=>8 v202 maybe more ts index ?
//            if (hasNewStat(TSIndex.getCTSFromTwoStatIndex(i))) {
//                getTwoStates().get(i).encode(outPacket);
//            }
//        }
//        if (ctsSet.contains(NewFlying)) {
//            outPacket.encodeInt(collection.get(NewFlying).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk517)) {
//            outPacket.encodeInt(collection.get(Unk517).get(0).xOption);
//        }
//        if (ctsSet.contains(KeyDownMoving)) {
//            outPacket.encodeInt(collection.get(KeyDownMoving).get(0).xOption);
//        }
//        if (ctsSet.contains(Unk556)) {
//            outPacket.encodeInt(collection.get(Unk556).get(0).xOption);
//        }
//        outPacket.encodeInt(0);
//    }
//
//    private void encodeIndieTempStat(OutPacket outPacket) {
//        Map<CharacterTemporaryStat, List<Option>> stats = getCurrentStats().entrySet().stream()
//                .filter(stat -> stat.getKey().isIndie() && getNewStats().containsKey(stat.getKey()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        TreeMap<CharacterTemporaryStat, List<Option>> sortedStats = new TreeMap<>(stats);
//        for (Map.Entry<CharacterTemporaryStat, List<Option>> stat : sortedStats.entrySet()) {
//            int curTime = (int) System.currentTimeMillis();
//            List<Option> options = stat.getValue();
//            if (options == null) {
//                outPacket.encodeInt(0);
//                continue;
//            }
//            outPacket.encodeInt(options.size());
//            for (Option option : options) {
//                outPacket.encodeInt(option.nReason);
//                outPacket.encodeInt(option.nValue);
//                outPacket.encodeInt(option.nKey);
//                outPacket.encodeInt(curTime - option.tStart); // elapsedTime
//                outPacket.encodeInt(option.tTerm);
//                int size = 0;
//                outPacket.encodeInt(size);
//                for (int i = 0; i < size; i++) {
//                    outPacket.encodeInt(0); // MValueKey
//                    outPacket.encodeInt(0); // MValue
//                }
//            }
//        }
//    }

//    public void encodeRemovedIndieTempStat(OutPacket outPacket) {
//        Map<CharacterTemporaryStat, List<Option>> stats = getRemovedStats().entrySet().stream()
//                .filter(stat -> stat.getKey().isIndie())
//                .sorted(Comparator.comparingInt(stat -> stat.getKey().getVal()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        for (Map.Entry<CharacterTemporaryStat, List<Option>> stat : stats.entrySet()) {
//            int curTime = (int) System.currentTimeMillis();
//            // encode remaining stats
//            CharacterTemporaryStat key = stat.getKey();
//            List<Option> options = getOptions(key);
//            if (options == null) {
//                outPacket.encodeInt(0);
//                continue;
//            }
//            outPacket.encodeInt(options.size());
//            for (Option option : options) {
//                outPacket.encodeInt(option.nReason);
//                outPacket.encodeInt(option.nValue);
//                outPacket.encodeInt(option.nKey); // nKey
//                outPacket.encodeInt(curTime - option.tStart);
//                outPacket.encodeInt(option.tTerm); // tTerm
//                outPacket.encodeInt(0); // size
//                // pw.writeInt(0); // nMValueKey
//                // pw.writeInt(0); // nMValue
//            }
//        }
//    }

    public boolean hasNewMovingEffectingStat() {
        return getNewStats().keySet().stream().anyMatch(CharacterTemporaryStat::isMovingEffectingStat);
    }

    public boolean hasMovingEffectingStat() {
        return getCurrentStats().keySet().stream().anyMatch(CharacterTemporaryStat::isMovingEffectingStat);
    }

    public boolean hasRemovedMovingEffectingStat() {
        return getRemovedStats().keySet().stream().anyMatch(CharacterTemporaryStat::isMovingEffectingStat);
    }

    public Map<CharacterTemporaryStat, List<Option>> getCurrentStats() {
        return currentStats;
    }

    public Map<CharacterTemporaryStat, List<Option>> getNewStats() {
        return newStats;
    }

    public Map<CharacterTemporaryStat, List<Option>> getRemovedStats() {
        return removedStats;
    }

    public int getPvpDamage() {
        return pvpDamage;
    }

    public void setPvpDamage(int pvpDamage) {
        this.pvpDamage = pvpDamage;
    }

    public byte getDefenseState() {
        return defenseState;
    }

    public void setDefenseState(byte defenseState) {
        this.defenseState = defenseState;
    }

    public byte getDefenseAtt() {
        return defenseAtt;
    }

    public void setDefenseAtt(byte defenseAtt) {
        this.defenseAtt = defenseAtt;
    }

    public int[] getDiceInfo() {
        return diceInfo;
    }

    public void setDiceInfo(int[] diceInfo) {
        this.diceInfo = diceInfo;
    }

    public void throwDice(int roll) {
        throwDice(roll, 0);
    }

    public void throwDice(int roll, int secondRoll) {
        int[] array = {0, 0, 30, 20, 15, 20, 30, 20}; // Stats for Normal Rolls
        int[] arrayDD = {0, 0, 40, 30, 25, 30, 40, 30}; // Stats for Double Down Rolls
        for (int i = 0; i < diceOption.length; i++) {
            diceOption[i] = 0;
        }
        if (roll == secondRoll) {
            diceOption[roll] = arrayDD[roll];
        } else {
            diceOption[roll] = array[roll];
            diceOption[secondRoll] = array[secondRoll];
        }
        int[] diceinfo = new int[]{
                diceOption[3],  //nOption 3 (MHPR)
                diceOption[3],  //nOption 3 (MMPR)
                diceOption[4],  //nOption 4 (Cr)
                0,  // CritDamage Min
                0,  // ???  ( CritDamage Max (?) )
                0,  // EVAR
                0,  // AR
                0,  // ER
                diceOption[2],  //nOption 2 (PDDR)
                diceOption[2],  //nOption 2 (MDDR)
                0,  // PDR
                0,  // MDR
                diceOption[5],  //nOption 5 (PIDR)
                0,  // PDamR
                0,  // MDamR
                0,  // PADR
                0,  // MADR
                diceOption[6], //nOption 6 (EXP)
                diceOption[7], //nOption 7 (IED)
                0,  // ASRR
                0,  // TERR
                0,  // MesoRate
                0,
        };
        setDiceInfo(diceinfo);
    }

    public List<Integer> getMobZoneStates() {
        return mobZoneStates;
    }

    public void setMobZoneStates(List<Integer> mobZoneStates) {
        this.mobZoneStates = mobZoneStates;
    }

    public int getViperEnergyCharge() {
        return viperEnergyCharge;
    }

    public void setViperEnergyCharge(int viperEnergyCharge) {
        this.viperEnergyCharge = viperEnergyCharge;
    }

//    public StopForceAtom getStopForceAtom() {
//        return stopForceAtom;
//    }

//    public void setStopForceAtom(StopForceAtom stopForceAtom) {
//        this.stopForceAtom = stopForceAtom;
//    }

    public MapleCharacter getChr() {
        return chr;
    }

    public Map<CharacterTemporaryStat, ScheduledFuture> getSchedules() {
        return schedules;
    }

    public Map<Tuple<CharacterTemporaryStat, Option>, ScheduledFuture> getIndieSchedules() {
        return indieSchedules;
    }

    public void sendSetStatPacket() {
//        getChr().getField().broadcastPacket(UserRemote.setTemporaryStat(getChr(), (short) 0), getChr());
        getChr().announce(PlayerPacket.giveBuff(this));
    }

    public void sendResetStatPacket() {
        sendResetStatPacket(false);
    }

    public void sendResetStatPacket(boolean demount) {
        for (CharacterTemporaryStat characterTemporaryStat : getRemovedStats().keySet()) {
            if (characterTemporaryStat == Speed) {
                demount = true;
                break;
            }
        }
        getChr().announce(PlayerPacket.removeBuff(this, demount));
        getRemovedStats().clear();
    }

    public void removeAllDebuffs() {
        removeStat(CharacterTemporaryStat.Stun, false);
        removeStat(CharacterTemporaryStat.Poison, false);
        removeStat(CharacterTemporaryStat.Seal, false);
        removeStat(CharacterTemporaryStat.Darkness, false);
        removeStat(CharacterTemporaryStat.Thaw, false);
        removeStat(CharacterTemporaryStat.Weakness, false);
        removeStat(CharacterTemporaryStat.Curse, false);
        removeStat(CharacterTemporaryStat.Slow, false);
        removeStat(CharacterTemporaryStat.Blind, false);
        sendResetStatPacket();
    }

//    public Set<AffectedArea> getAffectedAreas() {
//        return affectedAreas;
//    }

//    public void addAffectedArea(AffectedArea aa) {
//        getAffectedAreas().add(aa);
//    }
//
//    public void removeAffectedArea(AffectedArea aa) {
//        getAffectedAreas().remove(aa);
//
//        if (aa.getRemoveSkill()) {
//            removeStatsBySkill(aa.getSkillID());
//        }
//    }
//
//    public boolean hasAffectedArea(AffectedArea affectedArea) {
//        return getAffectedAreas().contains(affectedArea);
//    }

    public boolean hasStatBySkillId(int skillId) {
        for (CharacterTemporaryStat cts : getCurrentStats().keySet()) {
            if (getOption(cts).rOption == skillId || getOption(cts).nReason == skillId) {
                return true;
            }
        }
        return false;
    }

    public void removeStatsBySkill(int skillId) {
        Map<CharacterTemporaryStat, Option> removedMap = new HashMap<>();
        for (CharacterTemporaryStat cts : getCurrentStats().keySet()) {
            Option checkOpt = new Option();
            checkOpt.nReason = skillId;
            if (cts.isIndie() && getOptions(cts) != null && getOptions(cts).contains(checkOpt)) {
                Option o = Util.findWithPred(getOptions(cts), opt -> opt.equals(checkOpt));
                if (o == null) {
                    log.error("Found option null, yet the options contained it?");
                } else {
                    removedMap.put(cts, o);
                }
            } else if (getOption(cts).rOption == skillId || getOption(cts).nReason == skillId) {
                removedMap.put(cts, getOption(cts));
            }
        }
        removedMap.forEach((cts, opt) -> {
            if (cts.isIndie()) {
                removeIndieStat(cts, opt, false);
            } else {
                removeStat(cts, false);
            }
        });
    }

    public void addSoulMPFromMobDeath() {
//        if (hasStat(SoulMP)) {
//            Option o = getOption(SoulMP);
//            o.nOption = Math.min(ItemConstants.MAX_SOUL_CAPACITY, o.nOption + ItemConstants.MOB_DEATH_SOUL_MP_COUNT);
//            putCharacterStatValue(SoulMP, o);
//            if (o.nOption >= ItemConstants.MAX_SOUL_CAPACITY && !hasStat(FullSoulMP)) {
//                Option o2 = new Option();
//                o2.rOption = ItemConstants.getSoulSkillFromSoulID(((Equip) chr.getEquippedItemByBodyPart(BodyPart.Weapon)).getSoulOptionId());
//                if (o2.rOption == 0) {
//                    chr.chatMessage(String.format("Unknown corresponding skill for soul socket id %d!",
//                            ((Equip) chr.getEquippedItemByBodyPart(BodyPart.Weapon)).getSoulOptionId()));
//                }
//                o2.nOption = ItemConstants.MAX_SOUL_CAPACITY;
//                o2.xOption = ItemConstants.MAX_SOUL_CAPACITY;
//                putCharacterStatValue(FullSoulMP, o2);
//            }
//            sendSetStatPacket();
//        }
    }

    public void putCharacterStatValueFromMobSkill(CharacterTemporaryStat cts, Option o) {
        o.rOption |= o.slv << 16; // mob skills are encoded differently: not an int, but short (skill ID), then short (slv).
        putCharacterStatValue(cts, o);
        MapleJob sourceJobHandler = chr.getJobHandler();
        sourceJobHandler.handleMobDebuffSkill(chr);
    }

    public void removeAllStats() {
        Set<CharacterTemporaryStat> currentStats = new HashSet<>();
        currentStats.addAll(getNewStats().keySet());
        currentStats.addAll(getCurrentStats().keySet());
        currentStats.forEach(stat -> removeStat(stat, false));
    }

    public Map<BaseStat, Integer> getBaseStats() {
        return baseStats;
    }

    public void addBaseStat(BaseStat bs, int value) {
        getBaseStats().put(bs, getBaseStats().getOrDefault(bs, 0) + value);
    }

    public void removeBaseStat(BaseStat bs, int value) {
        addBaseStat(bs, -value);
    }

    public long getTotalNOptionOfStat(CharacterTemporaryStat cts) {
        if (cts.isIndie()) {
            return getOptions(cts).stream().mapToLong(o -> o.nValue).sum();
        } else {
            return getOptions(cts).stream().mapToLong(o -> o.nOption).sum();
        }
    }
}
