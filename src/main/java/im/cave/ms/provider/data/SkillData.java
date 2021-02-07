package im.cave.ms.provider.data;

import im.cave.ms.client.character.skill.MobSkillInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.MobSkillStat;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.info.MakingSkillRecipe;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataFileEntry;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.provider.wz.MapleDataType;
import im.cave.ms.tools.Position;
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
    private static final MapleDataProvider mobSkillData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Skill.wz/MobSkill"));

    private static final Map<Integer, SkillInfo> skills = new HashMap<>();
    private static final Map<Integer, Map<Integer, Integer>> eliteMobSkills = new HashMap<>();
    private static final Map<Short, Map<Short, MobSkillInfo>> mobSkillInfos = new HashMap<>();
    private static Map<Integer, MakingSkillRecipe> makingSkillRecipes = new HashMap<>();

    public static SkillInfo getSkillInfo(int skillId) {
        if (!skills.containsKey(skillId)) {
            return getSkillFromWz(skillId);
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

    public static SkillInfo getSkillFromWz(int skillId) {
        String sId = String.valueOf(skillId);
        sId = StringUtil.getLeftPaddedStr(sId, '0', 7);
        String rootId;
        String rootPath;
        if (skillId > 80000000 && skillId < 89000000) {  // 特殊的
            rootId = sId.substring(0, sId.length() - 2);
        } else {
            rootId = sId.substring(0, sId.length() - 4);
        }
        rootPath = rootId + ".img";
        MapleData data;
        data = skillData.getData(rootPath);
        if (data == null) {
            return null;
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
//                                    log.warn("Unknown SkillStat " + attrName);
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

    public static void loadMobSkills() {
        for (MapleDataFileEntry file : mobSkillData.getRoot().getFiles()) {
            MapleData skillMapleData = mobSkillData.getData(file.getName());
            String strId = skillMapleData.getName().replace(".img", "");
            for (MapleData levelMapleData : skillMapleData.getChildByPath("level")) {
                short level = Short.parseShort(levelMapleData.getName());
                MobSkillInfo msi = new MobSkillInfo();
                msi.setId(Short.parseShort(strId));
                msi.setLevel(level);
                for (MapleData skillStat : levelMapleData) {
                    String name = skillStat.getName();
                    String value = MapleDataTool.getString(skillStat);
                    String x = MapleDataTool.getString("x", skillStat, "0");
                    String y = MapleDataTool.getString("y", skillStat, "0");
                    if (Util.isNumber(name)) {
                        msi.addIntToList(Integer.parseInt(value));
                        continue;
                    }
                    switch (name) {
                        case "x":
                            msi.putMobSkillStat(MobSkillStat.x, value);
                            break;
                        case "mpCon":
                            msi.putMobSkillStat(MobSkillStat.mpCon, value);
                            break;
                        case "interval":
                        case "inteval":
                            msi.putMobSkillStat(MobSkillStat.interval, value);
                            break;
                        case "hp":
                        case "HP":
                            msi.putMobSkillStat(MobSkillStat.hp, value);
                            break;
                        case "info":
                            msi.putMobSkillStat(MobSkillStat.info, value);
                            break;
                        case "y":
                            msi.putMobSkillStat(MobSkillStat.y, value);
                            break;
                        case "lt":
                            msi.setLt(new Position(Integer.parseInt(x), Integer.parseInt(y)));
                            break;
                        case "rb":
                            msi.setRb(new Position(Integer.parseInt(x), Integer.parseInt(y)));
                            break;
                        case "lt2":
                            msi.setLt2(new Position(Integer.parseInt(x), Integer.parseInt(y)));
                            break;
                        case "rb2":
                            msi.setRb2(new Position(Integer.parseInt(x), Integer.parseInt(y)));
                            break;
                        case "lt3":
                            msi.setLt3(new Position(Integer.parseInt(x), Integer.parseInt(y)));
                            break;
                        case "rb3":
                            msi.setRb3(new Position(Integer.parseInt(x), Integer.parseInt(y)));
                            break;
                        case "limit":
                            msi.putMobSkillStat(MobSkillStat.limit, value);
                            break;
                        case "broadCastScreenMsg":
                            msi.putMobSkillStat(MobSkillStat.broadCastScreenMsg, value);
                            break;
                        case "w":
                            msi.putMobSkillStat(MobSkillStat.w, value);
                            break;
                        case "z":
                            msi.putMobSkillStat(MobSkillStat.z, value);
                            break;
                        case "parsing":
                            msi.putMobSkillStat(MobSkillStat.parsing, value);
                            break;
                        case "prop":
                            msi.putMobSkillStat(MobSkillStat.prop, value);
                            break;
                        case "ignoreResist":
                            msi.putMobSkillStat(MobSkillStat.ignoreResist, value);
                            break;
                        case "count":
                            msi.putMobSkillStat(MobSkillStat.count, value);
                            break;
                        case "time":
                            msi.putMobSkillStat(MobSkillStat.time, value);
                            break;
                        case "targetAggro":
                            msi.putMobSkillStat(MobSkillStat.targetAggro, value);
                            break;
                        case "fieldScript":
                            msi.putMobSkillStat(MobSkillStat.fieldScript, value);
                            break;
                        case "elemAttr":
                            msi.putMobSkillStat(MobSkillStat.elemAttr, value);
                            break;
                        case "delay":
                            msi.putMobSkillStat(MobSkillStat.delay, value);
                            break;
                        case "rank":
                            msi.putMobSkillStat(MobSkillStat.rank, value);
                            break;
                        case "HPDeltaR":
                            msi.putMobSkillStat(MobSkillStat.HPDeltaR, value);
                            break;
                        case "summonEffect":
                            msi.putMobSkillStat(MobSkillStat.summonEffect, value);
                            break;
                        case "y2":
                            msi.putMobSkillStat(MobSkillStat.y2, value);
                            break;
                        case "q":
                            msi.putMobSkillStat(MobSkillStat.q, value);
                            break;
                        case "q2":
                            msi.putMobSkillStat(MobSkillStat.q2, value);
                            break;
                        case "s2":
                            msi.putMobSkillStat(MobSkillStat.s2, value);
                            break;
                        case "u":
                            msi.putMobSkillStat(MobSkillStat.u, value);
                            break;
                        case "u2":
                            msi.putMobSkillStat(MobSkillStat.u2, value);
                            break;
                        case "v":
                            msi.putMobSkillStat(MobSkillStat.v, value);
                            break;
                        case "z2":
                            msi.putMobSkillStat(MobSkillStat.z2, value);
                            break;
                        case "w2":
                            msi.putMobSkillStat(MobSkillStat.w2, value);
                            break;
                        case "skillAfter":
                            msi.putMobSkillStat(MobSkillStat.skillAfter, value);
                            break;
                        case "x2":
                            msi.putMobSkillStat(MobSkillStat.x2, value);
                            break;
                        case "script":
                            msi.putMobSkillStat(MobSkillStat.script, value);
                            break;
                        case "attackSuccessProp":
                            msi.putMobSkillStat(MobSkillStat.attackSuccessProp, value);
                            break;
                        case "bossHeal":
                            msi.putMobSkillStat(MobSkillStat.bossHeal, value);
                            break;
                        case "face":
                            msi.putMobSkillStat(MobSkillStat.face, value);
                            break;
                        case "callSkill":
                            msi.putMobSkillStat(MobSkillStat.callSkill, value);
                            break;
                        case "level":
                            msi.putMobSkillStat(MobSkillStat.level, value);
                            break;
                        case "linkHP":
                            msi.putMobSkillStat(MobSkillStat.linkHP, value);
                            break;
                        case "timeLimitedExchange":
                            msi.putMobSkillStat(MobSkillStat.timeLimitedExchange, value);
                            break;
                        case "summonDir":
                            msi.putMobSkillStat(MobSkillStat.summonDir, value);
                            break;
                        case "summonTerm":
                            msi.putMobSkillStat(MobSkillStat.summonTerm, value);
                            break;
                        case "castingTime":
                            msi.putMobSkillStat(MobSkillStat.castingTime, value);
                            break;
                        case "subTime":
                            msi.putMobSkillStat(MobSkillStat.subTime, value);
                            break;
                        case "reduceCasting":
                            msi.putMobSkillStat(MobSkillStat.reduceCasting, value);
                            break;
                        case "additionalTime":
                            msi.putMobSkillStat(MobSkillStat.additionalTime, value);
                            break;
                        case "force":
                            msi.putMobSkillStat(MobSkillStat.force, value);
                            break;
                        case "targetType":
                            msi.putMobSkillStat(MobSkillStat.targetType, value);
                            break;
                        case "forcex":
                            msi.putMobSkillStat(MobSkillStat.forcex, value);
                            break;
                        case "sideAttack":
                            msi.putMobSkillStat(MobSkillStat.sideAttack, value);
                            break;
                        case "afterEffect":
                        case "rangeGap":
                            msi.putMobSkillStat(MobSkillStat.rangeGap, value);
                            break;
                        case "noGravity":
                            msi.putMobSkillStat(MobSkillStat.noGravity, value);
                            break;
                        case "notDestroyByCollide":
                            msi.putMobSkillStat(MobSkillStat.notDestroyByCollide, value);
                            break;
                        case "effect":
                        case "mob":
                        case "mob0":
                        case "hit":
                        case "affected":
                        case "affectedOtherSkill":
                        case "crash":
                        case "effectToUser":
                        case "affected_after":
                        case "fixDamR":
                        case "limitMoveSkill":
                        case "tile":
                        case "footholdRect":
                        case "targetMobType":
                        case "areaWarning":
                        case "arType":
                        case "tremble":
                        case "otherSkill":
                        case "etcEffect":
                        case "etcEffect1":
                        case "etcEffect2":
                        case "etcEffect3":
                        case "bombInfo":
                        case "affected_pre":
                        case "fixDamR_BT":
                        case "affectedPhase":
                        case "screen":
                        case "notMissAttack":
                        case "ignoreEvasion":
                        case "fadeinfo":
                        case "randomTarget":
                        case "option_linkedMob":
                        case "affected0":
                        case "summonOnce":
                        case "head":
                        case "mobGroup":
                        case "exceptRange":
                        case "exchangeAttack":
                        case "range":
                        case "addDam":
                        case "special":
                        case "target":
                        case "fixedPos":
                        case "fixedDir":
                        case "i52":
                        case "start":
                        case "cancleType":
                        case "succeed":
                        case "failed":
                        case "during":
                        case "castingBarHide":
                        case "skillCancelAlways":
                        case "cancleDamage":
                        case "cancleDamageMultiplier":
                        case "bounceBall":
                        case "info2":
                        case "regen":
                        case "kockBackD":
                        case "areaSequenceDelay":
                        case "areaSequenceRandomSplit":
                        case "accelerationEffect":
                        case "repeatEffect":
                        case "brightness":
                        case "brightnessDuration":
                        case "success":
                        case "fail":
                        case "affected_S":
                        case "appear":
                        case "affected_XS":
                        case "disappear":
                        case "command":
                        case "damIncPos": // May be useful
                        case "option_poison": // ?
                        case "phaseUserCount": // I think this is done client side (users hit mapped to phase?)
                            break;
                        default:
//                            log.warn(String.format("Unknown MobSkillStat %s with value %s (skill %s level %d)", name, value, strId, level));
                            break;
                    }

                }
                addMobSkillInfo(msi);
            }
        }
    }

    private static void addMobSkillInfo(MobSkillInfo msi) {
        getMobSkillInfos().computeIfAbsent(msi.getId(), k -> new HashMap<>());
        Map<Short, MobSkillInfo> msiLevelMap = getMobSkillInfos().get(msi.getId());
        msiLevelMap.put(msi.getLevel(), msi);
        getMobSkillInfos().put(msi.getId(), msiLevelMap);

    }

    public static Map<Short, Map<Short, MobSkillInfo>> getMobSkillInfos() {
        return mobSkillInfos;
    }


    private static void loadEliteMobSkillsFromWZ() {
        MapleData eliteMobSkillData = mobSkillData.getData("EliteMobSkill.img");
        for (MapleData gradeMapleData : eliteMobSkillData.getChildren()) {
            int grade = Integer.parseInt(gradeMapleData.getName());
            for (MapleData skillMapleData : gradeMapleData.getChildren()) {
                int skill = Integer.parseInt(MapleDataTool.getString("skill", skillMapleData));
                int level = Integer.parseInt(MapleDataTool.getString("level", skillMapleData));
                addEliteMobSkill(grade, skill, level);
            }
        }
    }

    private static void addEliteMobSkill(int grade, int skill, int level) {
        if (!eliteMobSkills.containsKey(grade)) {
            eliteMobSkills.put(grade, new HashMap<>());
        }
        eliteMobSkills.get(grade).put(skill, level);
    }


    public static void loadMakingRecipeSkills() {
        int[] recipes = {9200, 9201, 9202, 9203, 9204};
        for (Integer recipeCategory : recipes) {
            MapleData recipeMapleData = skillData.getData(String.format("Recipe_%d.img", recipeCategory));
            for (MapleData node : recipeMapleData) {
                MakingSkillRecipe msr = new MakingSkillRecipe();
                int recipeId = Integer.parseInt(node.getName());
                msr.setRecipeID(recipeId);
                msr.setReqSkillID(10000 * (recipeId / 10000));
                for (MapleData recipe : node) {
                    String name = recipe.getName();
                    String value = MapleDataTool.getString(recipe);
                    switch (name) {
                        case "target":
                            for (MapleData targets : recipe) {
                                MakingSkillRecipe.TargetElem tar = new MakingSkillRecipe.TargetElem();
                                for (MapleData target : targets) {
                                    String targetName = target.getName();
                                    int targetValue = MapleDataTool.getInt(target);
                                    switch (targetName) {
                                        case "item":
                                            tar.setItemID(targetValue);
                                            break;
                                        case "count":
                                            tar.setCount(targetValue);
                                            break;
                                        case "probWeight":
                                            tar.setProbWeight(targetValue);
                                            break;
                                        default:
                                            log.warn("Unknown target value " + targetName);
                                            break;
                                    }
                                }
                                msr.addTarget(tar);
                            }
                            break;
                        case "weatherItem":
                            msr.setWeatherItemID(Integer.parseInt(value));
                            break;
                        case "incSkillProficiency":
                            msr.setIncSkillProficiency(Integer.parseInt(value));
                            break;
                        case "incSkillProficiencyOnFailure":
                            msr.setIncSkillProficiencyOnFailure(Integer.parseInt(value));
                            break;
                        case "incSkillMasterProficiency":
                            msr.setIncSkillMasterProficiency(Integer.parseInt(value));
                            break;
                        case "incSkillMasterProficiencyOnFailure":
                            msr.setIncSkillMasterProficiencyOnFailure(Integer.parseInt(value));
                            break;
                        case "incFatigability":
                            msr.setIncFatigability(Integer.parseInt(value));
                            break;
                        case "addedCoolProb":
                            msr.setAddedCoolProb(Integer.parseInt(value));
                            break;
                        case "coolTimeSec":
                            msr.setCoolTimeSec(Integer.parseInt(value));
                            break;
                        case "addedTimeTaken":
                            msr.setAddedSecForMaxGauge(Integer.parseInt(value));
                            break;
                        case "period":
                            msr.setExpiredPeriod(Integer.parseInt(value));
                            break;
                        case "premium":
                            msr.setPremiumItem(Integer.parseInt(value) != 0);
                            break;
                        case "needOpenItem":
                            msr.setNeedOpenItem(Integer.parseInt(value) != 0);
                            break;
                        case "reqSkillLevel":
                            msr.setRecommendedSkillLevel(Integer.parseInt(value));
                            break;
                        case "reqSkillProficiency":
                            msr.setReqSkillProficiency(Integer.parseInt(value));
                            break;
                        case "reqMeso":
                            msr.setReqMeso(Integer.parseInt(value));
                            break;
                        case "reqMapObjectTag":
                            msr.setReqMapObjectTag(value);
                            break;
                        case "recipe":
                            for (MapleData ingredients : recipe) {
                                int itemID = -1, count = -1;
                                for (MapleData ingredient : ingredients) {
                                    String ingredientName = ingredient.getName();
                                    int ingredientValue = MapleDataTool.getInt(ingredient);
                                    switch (ingredientName) {
                                        case "item":
                                            itemID = ingredientValue;
                                            break;
                                        case "count":
                                            count = ingredientValue;
                                            break;
                                        default:
                                            log.warn("Unknown ingredient value " + ingredientName);
                                            break;
                                    }
                                }
                                if (itemID != -1 && count != -1) {
                                    msr.addIngredient(itemID, count);
                                }
                            }
                            break;
                        default:
                            log.warn("Unknown recipe value " + name);
                            break;
                    }

                }
                makingSkillRecipes.put(recipeId, msr);
            }

        }

    }
}
