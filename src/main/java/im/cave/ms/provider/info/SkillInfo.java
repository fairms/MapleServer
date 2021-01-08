package im.cave.ms.provider.info;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Tuple;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SkillInfo {
    private static final Logger log = LoggerFactory.getLogger(SkillInfo.class);

    private int skillId;
    private int rootId;
    private int maxLevel;
    private int currentLevel;
    private Map<SkillStat, String> skillStatInfo = new HashMap<>();
    private boolean invisible;
    private int masterLevel;
    private int fixLevel;
    private List<Rect> rects = new ArrayList<>();
    private boolean massSpell;
    private int type;
    private Set<Integer> psdSkills = new HashSet<>();
    private String elemAttr = "";
    private int hyper;
    private int hyperstat;
    private int vehicleId;
    private int reqTierPoint;
    private final Map<Integer, Integer> reqSkills = new HashMap<>();
    private boolean notCooltimeReset;
    private boolean notIncBuffDuration;
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    private boolean psd;
    private Set<Integer> addAttackSkills = new HashSet<>();
    private Map<Integer, Integer> extraSkillInfo = new HashMap<>();
    private boolean ignoreCounter;
    private boolean areaAttack;
    public List<Map<SkillStat, String>> levels;

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Map<SkillStat, String> getSkillStatInfo() {
        return skillStatInfo;
    }

    public void setSkillStatInfo(Map<SkillStat, String> skillStatInfo) {
        this.skillStatInfo = skillStatInfo;
    }

    public void addSkillStatInfo(SkillStat sc, String value) {
        getSkillStatInfo().put(sc, value);
    }

    public int getValue(SkillStat skillStat, int slv) {
        int result = 0;
        if (levels != null) {
            Map<SkillStat, String> stats = levels.get(slv - 1);
            String value = stats.get(skillStat);
            if (value == null) {
                return 0;
            } else {
                return Integer.parseInt(value);
            }
        }
        String value = getSkillStatInfo().get(skillStat);
        if (value == null || slv == 0) {
            return 0;
        }
        // Sometimes newlines get taken, just remove those
        value = value.replace("\n", "").replace("\r", "");
        value = value.replace("\\n", "").replace("\\r", ""); // unluko
        String original = value;
        if (Util.isNumber(value)) {
            result = Integer.parseInt(value);
        } else {
            try {
                value = value.replace("u", "Math.ceil");
                value = value.replace("d", "Math.floor");
                String toReplace = value.contains("y") ? "y"
                        : value.contains("X") ? "X"
                        : "x";
                Object res = engine.eval(value.replace(toReplace, slv + ""));
                if (res instanceof Integer) {
                    result = (Integer) res;
                } else if (res instanceof Double) {
                    result = ((Double) res).intValue();
                }
            } catch (ScriptException e) {
                log.error(String.format("Error when parsing: skill %d, level %d, skill stat %s, tried to eval %s.",
                        getSkillId(), slv, skillStat, original));
                e.printStackTrace();
            }
        }
        return result;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public void setMasterLevel(int masterLevel) {
        this.masterLevel = masterLevel;
    }

    public int getFixLevel() {
        return fixLevel;
    }

    public void setFixLevel(int fixLevel) {
        this.fixLevel = fixLevel;
    }

    public void addRect(Rect rect) {
        getRects().add(rect);
    }

    public List<Rect> getRects() {
        return rects;
    }

    public Rect getLastRect() {
        return rects != null && rects.size() > 0 ? rects.get(rects.size() - 1) : null;
    }

    public Rect getFirstRect() {
        return rects != null && rects.size() > 0 ? rects.get(0) : null;
    }

    public boolean isMassSpell() {
        return massSpell;
    }

    public void setMassSpell(boolean massSpell) {
        this.massSpell = massSpell;
    }

    public boolean hasCooltime() {
        return getValue(SkillStat.cooltime, 1) > 0 || getValue(SkillStat.cooltimeMS, 1) > 0;
    }

    public Map<BaseStat, Integer> getBaseStatValues(MapleCharacter chr, int slv, int skillID) {
        Map<BaseStat, Integer> stats = new HashMap<>();
        for (SkillStat ss : getSkillStatInfo().keySet()) {
            Tuple<BaseStat, Integer> bs = getBaseStatValue(ss, slv, chr);
            if (bs == null) {
                continue;
            }
            stats.put(bs.getLeft(), bs.getRight());
        }
        if (skillID == 20010194) {
            stats.put(BaseStat.mhpR, 15);
        }
        return stats;
    }

    private Tuple<BaseStat, Integer> getBaseStatValue(SkillStat ss, int slv, MapleCharacter chr) {
        BaseStat bs = ss.getBaseStat();
        if (bs == null) {
            return null;
        }
        int value = getValue(ss, slv);
        switch (ss) {
            case lv2damX:
                value *= chr.getLevel();
                break;
            case str2dex:
                value *= chr.getStat(MapleStat.STR);
                break;
            case dex2luk:
            case dex2str:
                value *= chr.getStat(MapleStat.DEX);
                break;
            case int2luk:
                value *= chr.getStat(MapleStat.INT);
                break;
            case luk2dex:
            case luk2int:
                value *= chr.getStat(MapleStat.LUK);
                break;
            case mhp2damX:
                value *= chr.getStat(MapleStat.MAXHP);
                break;
            case mmp2damX:
                value *= chr.getStat(MapleStat.MAXMP);
                break;
        }
        return new Tuple<>(bs, value);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setRects(List<Rect> rects) {
        this.rects = rects;
    }

    public void addPsdSkill(int skillID) {
        getPsdSkills().add(skillID);
    }

    public Set<Integer> getPsdSkills() {
        return psdSkills;
    }

    public void setPsdSkills(Set<Integer> psdSkills) {
        this.psdSkills = psdSkills;
    }

    public String getElemAttr() {
        return elemAttr;
    }

    public void setElemAttr(String elemAttr) {
        this.elemAttr = elemAttr;
    }

    public void setHyper(int hyper) {
        this.hyper = hyper;
    }

    public int getHyper() {
        return hyper;
    }

    public void setHyperStat(int hyperstat) {
        this.hyperstat = hyperstat;
    }

    public int getHyperStat() {
        return hyperstat;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setReqTierPoint(int reqTierPoint) {
        this.reqTierPoint = reqTierPoint;
    }

    public int getReqTierPoint() {
        return reqTierPoint;
    }

    public void addReqSkill(int skillID, int slv) {
        getReqSkills().put(skillID, slv);
    }

    public Map<Integer, Integer> getReqSkills() {
        return reqSkills;
    }

    public void setNotCooltimeReset(boolean notCooltimeReset) {
        this.notCooltimeReset = notCooltimeReset;
    }

    public boolean isNotCooltimeReset() {
        return notCooltimeReset;
    }

    public void setNotIncBuffDuration(boolean notIncBuffDuration) {
        this.notIncBuffDuration = notIncBuffDuration;
    }

    public boolean isNotIncBuffDuration() {
        return notIncBuffDuration;
    }

    public void setPsd(boolean psd) {
        this.psd = psd;
    }

    public boolean isPsd() {
        return psd;
    }

    public Set<Integer> getAddAttackSkills() {
        return addAttackSkills;
    }

    public void setAddAttackSkills(Set<Integer> addAttackSkills) {
        this.addAttackSkills = addAttackSkills;
    }

    public void addAddAttackSkills(int skillId) {
        getAddAttackSkills().add(skillId);
    }

    public Map<Integer, Integer> getExtraSkillInfo() {
        return extraSkillInfo;
    }

    public void setExtraSkillInfo(Map<Integer, Integer> extraSkillInfo) {
        this.extraSkillInfo = extraSkillInfo;
    }

    public void addExtraSkillInfo(int skillid, int delay) {
        getExtraSkillInfo().put(skillid, delay);
    }

    public boolean isIgnoreCounter() {
        return getValue(SkillStat.ignoreCounter, 1) != 0;
    }

    public void setAreaAttack(boolean b) {
        areaAttack = b;
    }
}
