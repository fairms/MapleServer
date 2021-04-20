package im.cave.ms.provider.data;

import im.cave.ms.constants.MatrixConstants;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.provider.info.VCore;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VCoreData {
    private static final MapleDataProvider etcData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Etc.wz"));

    private static final Map<Integer, VCore.EnforceOption> skillEnforce = new HashMap<>();
    private static final Map<Integer, VCore.EnforceOption> boostEnforce = new HashMap<>();
    private static final Map<Integer, VCore.EnforceOption> specialEnforce = new HashMap<>();
    private static final Map<Integer, VCore.EnforceOption> expCoreEnforce = new HashMap<>();
    private static final Map<Integer, VCore> vcore = new HashMap<>();
    public static final int SKILL = 0, BOOST = 1, SPECIAL = 2, EXP = 3;


    public static VCore getCore(int coreID) {
        return vcore.getOrDefault(coreID, null);
    }


    public static boolean isSkillNode(int coreID) {
        return getCore(coreID).getType() == SKILL;
    }

    public static boolean isBoostNode(int coreID) {
        return getCore(coreID).getType() == BOOST;
    }

    public static boolean isSpecialNode(int coreID) {
        return getCore(coreID).getType() == SPECIAL;
    }

    public static boolean isExpNode(int coreID) {
        return getCore(coreID).getType() == EXP;
    }

    public static int getMaxLevel(int type) {
        return type == SKILL || type == BOOST ? MatrixConstants.GRADE_MAX : 1;
    }


    public static Map<Integer, VCore.EnforceOption> getEnforceOption(int type) {
        if (type == SKILL) {
            return skillEnforce;
        } else if (type == BOOST) {
            return boostEnforce;
        } else if (type == SPECIAL) {
            return specialEnforce;
        } else if (type == EXP) {
            return expCoreEnforce;
        }
        return null;
    }

    public static List<VCore> getNodesForJob(int jobID) {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : vcore.values()) {
            if (core.isJobSkill(jobID)) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getSkillNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : vcore.values()) {
            if (core.getType() == SKILL) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getBoostNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : vcore.values()) {
            if (core.getType() == BOOST && !core.getJobs().contains("none")) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getSpecialNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : vcore.values()) {
            if (core.getType() == SPECIAL) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getExpNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : vcore.values()) {
            if (core.getType() == EXP) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getClassNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : getSkillNodes()) {
            if (core.getJobs().contains("warrior") || core.getJobs().contains("magician") || core.getJobs().contains("archer")
                    || core.getJobs().contains("rogue") || core.getJobs().contains("pirate")) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getJobNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : getSkillNodes()) {
            if (!core.getJobs().contains("all") && !core.getJobs().contains("none") && !core.getJobs().contains("warrior") && !core.getJobs().contains("magician")
                    && !core.getJobs().contains("archer") && !core.getJobs().contains("rogue") && !core.getJobs().contains("pirate")) {
                cores.add(core);
            }
        }
        return cores;
    }

    public static List<VCore> getDecentNodes() {
        List<VCore> cores = new ArrayList<>();
        for (VCore core : getSkillNodes()) {
            if (core.getName().contains("Decent")) {
                cores.add(core);
            }
        }
        return cores;
    }

    private static void loadVCoreDataFromWz() {
        MapleData data = etcData.getData("VCore.img");
        MapleData coreData = data.getChildByPath("CoreData");
        VCore core;
        for (MapleData coreNode : coreData) {
            core = new VCore();
            core.setCoreID(Integer.parseInt(coreNode.getName()));
            for (MapleData attr : coreNode) {
                String name = attr.getName();
                String value = MapleDataTool.getString(attr);
                switch (name) {
                    case "type":
                        core.setType(Byte.parseByte(value));
                        break;
                    case "maxLevel":
                        core.setMaxLevel(Short.parseShort(value));
                        break;
                    case "name":
                        core.setName(value);
                        break;
                    case "desc":
                        core.setDesc(value);
                        break;
                    case "expireAfter":
                        core.setExpireAfter(Integer.parseInt(value));
                        break;
                    case "connectSkill":
                        for (MapleData connectedSkill : attr) {
                            String skillId = MapleDataTool.getString(connectedSkill);
                            if (Util.isNumber(skillId)) {
                                core.addConnectedSkill(Integer.parseInt(skillId));
                            }
                        }
                        break;
                    case "job":
                        for (MapleData job : attr) {
                            core.addJob(MapleDataTool.getString(job));
                        }
                        break;
                    case "spCoreOption":
                        VCore.CoreOption coreOption = new VCore.CoreOption();
                        for (MapleData opt : attr) {
                            switch (opt.getName()) {
                                case "cond":
                                    for (MapleData cond : opt) {
                                        String condName = cond.getName();
                                        String condValue = MapleDataTool.getString(cond);
                                        switch (condName) {
                                            case "type":
                                                coreOption.setCondType(condValue);
                                                break;
                                            case "cooltime":
                                                coreOption.setCooltime(Integer.parseInt(condValue));
                                                break;
                                            case "validTime":
                                                coreOption.setValidTime(Integer.parseInt(condValue));
                                                break;
                                            case "count":
                                                coreOption.setCount(Integer.parseInt(condValue));
                                                break;
                                            case "prob":
                                                coreOption.setProb(Double.parseDouble(condValue));
                                                break;
                                            default:
//                                                log.debug(String.format("[VCore] Unknown core option value (Cond) [%s], [%s]", condName, condValue));
                                                break;
                                        }
                                    }
                                    break;
                                case "effect":
                                    for (MapleData effect : opt) {
                                        String effName = effect.getName();
                                        String effValue = MapleDataTool.getString(effect);
                                        switch (effName) {
                                            case "type":
                                                coreOption.setEffectType(effValue);
                                                break;
                                            case "skill_id":
                                                coreOption.setSkillID(Integer.parseInt(effValue));
                                                break;
                                            case "skill_level":
                                                coreOption.setSLV(Short.parseShort(effValue));
                                                break;
                                            case "heal_percent":
                                                coreOption.setHealPercent(Integer.parseInt(effValue));
                                                break;
                                            case "reducePercent":
                                                coreOption.setReducePercent(Integer.parseInt(effValue));
                                                break;
                                            default:
//                                                log.debug(String.format("[VCore] Unknown core option value (Effect) [%s], [%s]", effName, effValue));
                                                break;
                                        }
                                    }
                                    break;
                            }
                        }
                        core.setOption(coreOption);
                        break;
                    case "icon":
                    case "iconMouseOver":
                    case "iconDisabled":
                    case "NotDuringEquipRemove":
                    case "notAbleCraft":
                    case "nobAbleGemStone":
                    case "checkCooltimeSkill":
                    case "noDisassemble":
                    case "checkRemoveSkill":
                        break;
                    default:
//                        log.debug(String.format("[VCore] Unknown v core value [%s], [%s]", name, value));
                        break;
                }
            }
            vcore.put(core.getCoreID(), core);
        }
        loadVCoreEnforcementDataFromWz(etcData.getData("VCore.img").getChildByPath("Enforcement"));
    }

    private static void loadVCoreEnforcementDataFromWz(MapleData enforcementDataNode) {
        for (MapleData enforcementType : enforcementDataNode) {
            String type = enforcementType.getName();
            for (MapleData enforcementOptionNode : enforcementType) {
                VCore.EnforceOption option = new VCore.EnforceOption();
                int level = Integer.parseInt(enforcementOptionNode.getName());
                for (MapleData levelData : enforcementOptionNode) {
                    String name = levelData.getName();
                    String value = MapleDataTool.getString(levelData);
                    switch (name) {
                        case "expEnforce":
                            option.setEnforceExp(Integer.parseInt(value));
                            break;
                        case "nextExp":
                            option.setNextExp(Integer.parseInt(value));
                            break;
                        case "extract":
                            option.setExtract(Integer.parseInt(value));
                            break;
                        default:
//                            log.debug(String.format("[VCore] Unknown enforcement value [%s], [%s]", name, value));
                            break;
                    }
                }
                switch (type) {
                    case "Skill":
                        skillEnforce.put(level, option);
                        break;
                    case "Enforce":
                        boostEnforce.put(level, option);
                        break;
                    case "Special":
                        specialEnforce.put(level, option);
                        break;
                    case "expCore":
                        expCoreEnforce.put(level, option);
                        break;
                    default:
//                        log.debug(String.format("[VCore] Unknown enforcement type [%s]", type));
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        loadVCoreDataFromWz();
    }

}
