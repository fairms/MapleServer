package im.cave.ms.provider.data;

import im.cave.ms.client.skill.MobSkillInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.provider.wz.MapleDataType;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 11/21 23:00
 */
public class SkillData {
    private static final Logger log = LoggerFactory.getLogger(SkillData.class);
    private static final MapleDataProvider skillData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Skill.wz"));
    private static final MapleDataProvider skill001Data = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Skill001.wz"));
    private static final MapleDataProvider skill002Data = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Skill002.wz"));

    private static final Map<Integer, SkillInfo> skills = new HashMap<>();
    private static final Map<Integer, Map<Integer, Integer>> eliteMobSkills = new HashMap<>();
    private static final Map<Short, Map<Short, MobSkillInfo>> mobSkillInfos = new HashMap<>();
//    private static Map<Integer, MakingSkillRecipe> makingSkillRecipes = new HashMap<>();

    public static SkillInfo getSkillInfo(int skillId) {
        if (!skills.containsKey(skillId)) {
            return loadSkillFromWz(skillId);
        }
        return skills.get(skillId);
    }


    public static Skill getSkill(int skillId) {
        SkillInfo si = getSkillInfo(skillId);
        if (si == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setSkillId(si.getSkillId());
        skill.setRootId(si.getRootId());
        skill.setMasterLevel(si.getMaxLevel());
        skill.setMaxLevel(si.getMaxLevel());
        if (si.getMasterLevel() <= 0) {
            skill.setMasterLevel(skill.getMaxLevel());
        }
        skill.setCurrentLevel(Math.max(si.getFixLevel(), 0));
        return skill;
    }

    public static SkillInfo loadSkillFromWz(int skillId) {
        String sId = String.valueOf(skillId);
        sId = StringUtil.getLeftPaddedStr(sId, '0', 7);
        String rootId;
        String rootPath;
        if (skillId > 80000000 && skillId < 89000000) {  // 特殊的
            rootId = sId.substring(0, sId.length() - 2);
            rootPath = rootId + ".img";
        } else {
            rootId = sId.substring(0, sId.length() - 4);
            rootPath = rootId + ".img";
        }
        MapleData data;
        data = skillData.getData(rootPath);
        if (data == null) {
            data = skill001Data.getData(rootPath);
            if (data == null) {
                data = skill002Data.getData(rootPath);
            }
        }
        MapleData skillData = data.getChildByPath("skill/" + sId);
        if (skillData != null) {
            SkillInfo skill = new SkillInfo();
            skill.setSkillId(skillId);
            skill.setRootId(Integer.parseInt(rootId));
            for (MapleData skillAttr : skillData.getChildren()) {
                if (skillAttr.getType().equals(MapleDataType.CANVAS)) {
                    continue;
                }
                String name = skillAttr.getName();
                String value = MapleDataTool.getString(skillAttr);
                switch (name) {
                    case "skillType":
                        skill.setType(Integer.parseInt(value));
                        break;
                    case "elemAttr":
                        skill.setElemAttr(value);
                        break;
                    case "masterLevel":
                        skill.setMasterLevel(Integer.parseInt(value));
                        break;
                    case "hyper":
                        skill.setHyper(Integer.parseInt(value));
                        break;
                    case "hyperStat":
                        skill.setHyperStat(Integer.parseInt(value));
                        break;
                    case "vehicleID":
                        skill.setVehicleId(Integer.parseInt(value));
                        break;
                    case "fixLevel":
                        skill.setFixLevel(Integer.parseInt(value));
                        break;
                    case "invisible":
                        skill.setInvisible(Integer.parseInt(value) != 0);
                        break;
                    case "massSpell":
                        skill.setMassSpell(Integer.parseInt(value) != 0);
                        break;
                    case "psd":
                        skill.setPsd(Integer.parseInt(value) != 0);
                        break;
                    case "psdSkill":
                        for (MapleData psdSkill : skillAttr.getChildren()) {
                            skill.addPsdSkill(Integer.parseInt(psdSkill.getName()));
                        }
                        break;
                    case "notCooltimeReset":
                        skill.setNotCooltimeReset(Integer.parseInt(value) != 0);
                        break;
                    case "notIncBuffDuration":
                        skill.setNotIncBuffDuration(Integer.parseInt(value) != 0);
                        break;
                    case "weapon":
                    case "weapon2":
                    case "weapon3":
                    case "weapon4":
                    case "action":
                    case "PVPcommon":
                    case "effect":
                    case "effect0":
                    case "affected":
                    case "skillList":
                    case "finalAttack":
                    case "cancleType":
                    case "mob":
                    case "hit":
                    case "number":
                    case "notRemoved":
                    case "state":
                        break;
                    case "req":
                        for (MapleData reqInfo : skillAttr.getChildren()) {
                            String reqInfoName = reqInfo.getName();
                            String reqInfoValue = MapleDataTool.getString(reqInfo);
                            if ("reqTierPoint".equalsIgnoreCase(reqInfoName)) {
                                skill.setReqTierPoint(Integer.parseInt(reqInfoValue));
                            } else if (Util.isNumber(reqInfoName)) {
                                skill.addReqSkill(Integer.parseInt(reqInfoName), Integer.parseInt(reqInfoValue));
                            }
                        }
                        break;
                    case "common":
                    case "info":
                    case "info2":
                        for (MapleData attr : skillAttr.getChildren()) {
                            String attrName = attr.getName();
                            if (attrName.equals("maxLevel")) {
                                skill.setMaxLevel(MapleDataTool.getInt(attr));
                            } else if (attrName.contains("lt") && attrName.length() <= 3) {
                                MapleData rbData = skillAttr.getChildByPath(attrName.replace("lt", "rb"));
                                Point ltPoint = MapleDataTool.getPoint(attr);
                                Point rbPoint = MapleDataTool.getPoint(rbData);
                                skill.addRect(new Rect(ltPoint.x, ltPoint.y, rbPoint.x, rbPoint.y));
                            } else {
                                SkillStat skillStat = SkillStat.getSkillStatByString(attrName);
                                if (skillStat != null) {
                                    skill.addSkillStatInfo(skillStat, MapleDataTool.getString(attr));
                                } else {
                                    log.warn("Unknown SkillStat " + attrName);
                                }
                            }
                        }
                        break;
                    case "addAttack":
                        for (MapleData addAttackInfo : skillAttr.getChildren()) {
                            String attackInfoName = addAttackInfo.getName();
                            if (attackInfoName.equals("skillPlus")) {
                                for (MapleData skillPlusInfo : addAttackInfo.getChildren()) {
                                    String skillPlusInfoName = skillPlusInfo.getName();
                                    String skillPlusInfoValue = MapleDataTool.getString(skillPlusInfo);
                                    skill.addAddAttackSkills(Integer.parseInt(skillPlusInfoValue));
                                }
                            }
                        }
                        break;
                    case "extraSkillInfo":
                        for (MapleData extraSkillInfo : skillAttr.getChildren()) {
                            String extraSkillInfoName = extraSkillInfo.getName();
                            int extraSkillDelay = 0;
                            int extraSkillId = -1;
                            for (MapleData extraSkillInfoIndividual : extraSkillInfo.getChildren()) {
                                String extraSkillName = extraSkillInfoIndividual.getName();
                                String extraSkillValue = MapleDataTool.getString(extraSkillInfoIndividual);
                                switch (extraSkillName) {
                                    case "delay":
                                        extraSkillDelay = Integer.parseInt(extraSkillValue);
                                        break;
                                    case "skill":
                                        extraSkillId = Integer.parseInt(extraSkillValue);
                                        break;
                                    default:
                                        log.warn(String.format("Unknown Extra Skill Info Name: %s", extraSkillName));
                                        break;
                                }
                                if (extraSkillId > 0) {
                                    skill.addExtraSkillInfo(extraSkillId, extraSkillDelay);
                                }
                            }
                        }
                        break;
                    case "level":
                        List<MapleData> levels = skillAttr.getChildren();
                        skill.setMaxLevel(levels.size());
                        skill.levels = new ArrayList<>();
                        for (MapleData level : levels) {
                            HashMap<SkillStat, String> skillLevel = new HashMap<>();
                            for (MapleData levelAttr : level.getChildren()) {
                                String levelAttrName = levelAttr.getName();
                                SkillStat skillStat = SkillStat.getSkillStatByString(levelAttrName);
                                if (skillStat != null) {
                                    skillLevel.put(skillStat, MapleDataTool.getString(levelAttr));
                                }
                            }
                            skill.levels.add(skillLevel);
                        }
                        break;
                    default:
                        log.warn("unknown skill attr : {} of skill:{}", name, skillId);
                }
            }
            SkillData.skills.put(skillId, skill);
            return skill;
        } else {
            return null;
        }

    }
}
