package im.cave.ms.provider.data;

import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestInfo;
import im.cave.ms.client.quest.progress.QuestProgressItemRequirement;
import im.cave.ms.client.quest.progress.QuestProgressLevelRequirement;
import im.cave.ms.client.quest.progress.QuestProgressMobRequirement;
import im.cave.ms.client.quest.progress.QuestProgressMoneyRequirement;
import im.cave.ms.client.quest.progress.QuestProgressRequirement;
import im.cave.ms.client.quest.requirement.QuestStartCompletionRequirement;
import im.cave.ms.client.quest.requirement.QuestStartItemRequirement;
import im.cave.ms.client.quest.requirement.QuestStartJobRequirement;
import im.cave.ms.client.quest.requirement.QuestStartMarriageRequirement;
import im.cave.ms.client.quest.requirement.QuestStartMaxLevelRequirement;
import im.cave.ms.client.quest.requirement.QuestStartMinStatRequirement;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.QuestStatus;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 11/21 23:00
 */
public class QuestData {
    private static final Logger log = LoggerFactory.getLogger(QuestData.class);
    private static final MapleDataProvider questData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Quest.wz"));

    private static final Map<Integer, QuestInfo> quests = new HashMap<>();


    public static QuestInfo getQuestInfo(int questId) {
        if (!quests.containsKey(questId)) {
            return getQuestInfoFromWz(questId);
        }
        return quests.get(questId);
    }

    private static QuestInfo getQuestInfoFromWz(int questId) {
        MapleData data = questData.getData("/Check.img");
        MapleData questData = data.getChildByPath(String.valueOf(questId));
        if (questData == null) {
            return null;
        }
        QuestInfo quest = new QuestInfo();
        quest.setQuestID(questId);
        for (MapleData questAttr : questData.getChildren()) {
            byte status = Byte.parseByte(questAttr.getName());
            for (MapleData info : questAttr.getChildren()) {
                String name = info.getName();
                String value = MapleDataTool.getString(info, "0");
                switch (name) {
                    case "npc":
                        if (status == 0) {
                            quest.setStartNpc(Integer.parseInt(value));
                        } else {
                            quest.setEndNpc(Integer.parseInt(value));
                        }
                        break;
                    case "infoNumber":
                        quest.setInfoNumber(Integer.parseInt(value));
                        break;
                    case "fieldsetkeeptime":
                        quest.setFieldSetKeepTime(Integer.parseInt(value));
                        break;
                    case "subJobFlags":
                        quest.setSubJobFlags(Integer.parseInt(value));
                        break;
                    case "deathCount":
                        quest.setDeathCount(Integer.parseInt(value));
                        break;
                    case "mobDropMeso":
                        quest.setMobDropMeso(Integer.parseInt(value));
                        break;
                    case "morph":
                        quest.setMorph(Integer.parseInt(value));
                        break;
                    case "start":
                        quest.setStart(Long.parseLong(value));
                        break;
                    case "start_t":
                        quest.setStartT(Long.parseLong(value));
                        break;
                    case "end":
                        quest.setEnd(Long.parseLong(value));
                        break;
                    case "end_t":
                        quest.setEndT(Long.parseLong(value));
                        break;
                    case "startscript":
                        quest.setStartScript(value);
                        break;
                    case "endscript":
                        quest.setEndScript(value);
                        break;
                    case "fieldset":
                        quest.setFieldSet(value);
                        break;
                    case "normalAutoStart":
                        quest.setNormalAutoStart(Integer.parseInt(value) != 0);
                        break;
                    case "completeNpcAutoGuide":
                        quest.setCompleteNpcAutoGuide(Integer.parseInt(value) != 0);
                        break;
                    case "autoStart":
                        quest.setAutoStart(Integer.parseInt(value) != 0);
                        break;
                    case "scenarioQuest":
                        quest.setAutoStart(Integer.parseInt(value) != 0);
                        break;
                    case "secret":
                        quest.setSecret(Integer.parseInt(value) != 0);
                        break;
                    case "marriaged":
                        quest.addRequirement(new QuestStartMarriageRequirement());
                        break;
                    case "lvmin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.LEVEL, Short.parseShort(value)));
                        break;
                    case "pop":
                    case "fameGradeReq":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.FAME, Short.parseShort(value)));
                        break;
                    case "charismaMin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.CHARISMA, Short.parseShort(value)));
                        break;
                    case "insightMin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.INSIGHT, Short.parseShort(value)));
                        break;
                    case "willMin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.WILL, Short.parseShort(value)));
                        break;
                    case "craftMin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.CRAFT, Short.parseShort(value)));
                        break;
                    case "senseMin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.SENSE, Short.parseShort(value)));
                        break;
                    case "charm":
                    case "charmMin":
                        quest.addRequirement(new QuestStartMinStatRequirement(MapleStat.CHARM, Short.parseShort(value)));
                        break;
                    case "level":
                        quest.addProgressRequirement(new QuestProgressLevelRequirement(Short.parseShort(value)));
                        break;
                    case "lvmax":
                        quest.addRequirement(new QuestStartMaxLevelRequirement(Short.parseShort(value)));
                        break;
                    case "endmeso":
                        quest.addProgressRequirement(new QuestProgressMoneyRequirement(Integer.parseInt(value)));
                        break;
                    case "order":
                    case "notInTeleportItemLimitedField":
                    case "anotherUserORCheck":
                    case "damageOnFalling":
                    case "hpR":
                    case "dayByDay":
                    case "QuestRecordAndOption":
                    case "infoex":
                    case "equipAllNeed":
                    case "interval":
                    case "interval_t":
                    case "dayOfWeek":
                    case "QuestOrOption":
                    case "ItemOrOption":
                    case "dayN":
                    case "anotherUserCheckType":
                    case "anotherUserCheck":
                    case "userInteract":
                    case "petRecallLimit":
                    case "pettamenessmin":
                    case "dayN_t":
                    case "worldmin":
                    case "worldmax":
                    case "petAutoSpeakingLimit":
                    case "name":
                    case "multiKill":
                    case "comboKill":
                    case "job_JP":
                    case "job_TW":
                    case "dayByDay_t":
                    case "runeAct":
                    case "weeklyRepeatResetDayOfWeek":
                    case "weeklyRepeat":
                    case "dressChanged":
                    case "equipSelectNeed":
                    case "infoAccount":
                    case "infoAccountExt":
                    case "breakTimeField":
                    case "multiKillCount":
                    case "randomGroupList":
                    case "randomGroup":
                    case "mbmin":
                    case "duo":
                    case "duoAssistPoint":
                    case "wsrInfo":
                    case "premium":
                    case "dayOfWeek_t":
                    case "nxInfo":
                    case "episodeQuest":
                    case "pvpGrade":
                    case "vipStartGradeMin":
                    case "vipStartGradeMax":
                    case "vipStartAccount":
                    case "dailyCommitment":
                    case "purchasePeriodAbove":
                    case "charisma": // Maybe implement later
                    case "craft": // Maybe implement later
                    case "gender": // it's 2018, so equal opportunity
                    case "buff": // Maybe implement later
                    case "exceptbuff": // Maybe implement later
                        break;
                    case "quest":
                        for (MapleData reqQuestInfo : info.getChildren()) {
                            QuestStartCompletionRequirement questRequirement = new QuestStartCompletionRequirement();
                            if (reqQuestInfo.getChildByPath("state") == null) {
                                continue;
                            }
                            for (MapleData reqQuestAttr : reqQuestInfo.getChildren()) {
                                String questName = reqQuestAttr.getName();
                                String questValue = MapleDataTool.getString(reqQuestAttr, "0");
                                switch (questName) {
                                    case "id":
                                        questRequirement.setQuestID(Integer.parseInt(questValue));
                                        break;
                                    case "order":
                                        break; //todo
                                    case "state":
                                        questRequirement.setQuestStatus(Byte.parseByte(questValue));
                                        break;
                                    default:
                                        break;
                                }
                            }
                            quest.addRequirement(questRequirement);
                        }
                        break;
                    case "pet":
                        for (MapleData reqPetInfo : info.getChildren()) {
                            QuestStartItemRequirement questItemRequirement = new QuestStartItemRequirement();
                            for (MapleData reqPetAttr : reqPetInfo.getChildren()) {
                                String reqName = reqPetAttr.getName();
                                String reqValue = MapleDataTool.getString(reqPetAttr);
                                switch (reqName) {
                                    case "id":
                                        questItemRequirement.setId(Integer.parseInt(reqValue));
                                        break;
                                    case "order":
                                        break;
                                    default:
                                        break;
                                }
                            }
                            quest.addRequirement(questItemRequirement);
                        }
                        break;
                    case "job":
                    case "job ":
                        QuestStartJobRequirement qjr = new QuestStartJobRequirement();
                        for (MapleData reqInfo : info.getChildren()) {
                            qjr.addJobReq(MapleDataTool.getShort(reqInfo));
                        }
                        quest.addRequirement(qjr);
                        break;
                    case "scenarioQuestList":
                        for (MapleData child : info.getChildren()) {
                            quest.addScenario(MapleDataTool.getInt(child));
                        }
                        break;
                    case "fieldEnter":
                        for (MapleData child : info.getChildren()) {
                            quest.addFieldEnter(MapleDataTool.getInt(child));
                        }
                        break;
                    case "mob":
                        for (MapleData mobReqInfo : info.getChildren()) {
                            QuestProgressMobRequirement mobRequirement = new QuestProgressMobRequirement();
                            mobRequirement.setOrder(Integer.parseInt(mobReqInfo.getName()));
                            for (MapleData mobReqAttr : mobReqInfo.getChildren()) {
                                String reqName = mobReqAttr.getName();
                                String reqValue = MapleDataTool.getString(mobReqAttr);
                                switch (reqName) {
                                    case "id":
                                        mobRequirement.setMobID(Integer.parseInt(reqValue));
                                        break;
                                    case "count":
                                        mobRequirement.setRequiredCount(Integer.parseInt(reqValue));
                                        break;
                                    case "order":
                                        break;
                                    default:
                                        break;
                                }
                            }
                            quest.addProgressRequirement(mobRequirement);
                        }
                        break;
                    case "item":
                        for (MapleData itemReqInfo : info.getChildren()) {
                            QuestStartItemRequirement questStartItemRequirement = new QuestStartItemRequirement();
                            QuestProgressItemRequirement questProgressItemRequirement = new QuestProgressItemRequirement();
                            questProgressItemRequirement.setOrder(Integer.parseInt(itemReqInfo.getName()));
                            for (MapleData itemReqAttr : itemReqInfo) {
                                String reqName = itemReqAttr.getName();
                                String reqValue = MapleDataTool.getString(itemReqAttr);
                                switch (reqName) {
                                    case "id":
                                        if (status == 0) {
                                            questStartItemRequirement.setId(Integer.parseInt(reqValue));
                                        } else {
                                            questProgressItemRequirement.setItemID(Integer.parseInt(reqValue));
                                        }
                                        break;
                                    case "count":
                                        if (status == 0) {
                                            questStartItemRequirement.setQuantity(Integer.parseInt(reqValue));
                                        } else {
                                            questProgressItemRequirement.setRequiredCount(Integer.parseInt(reqValue));
                                        }
                                        break;
                                    case "order":
                                    case "secret":
                                        break;
                                    default:
                                        break;
                                }
                            }
                            if (status == 0) {
                                quest.addRequirement(questStartItemRequirement);
                            } else {
                                quest.addProgressRequirement(questProgressItemRequirement);
                            }
                        }
                        break;
                    case "skill":
                        for (MapleData skillReq : info.getChildren()) {
                            for (MapleData skillAttr : skillReq.getChildren()) {
                                String reqName = skillAttr.getName();
                                String reqValue = MapleDataTool.getString(skillAttr);
                                switch (reqName) {
                                    case "id":
                                        quest.setSkill(Integer.parseInt(reqValue));
                                        break;
                                    case "order":
                                    case "acquire":
                                    case "level":
                                    case "levelCondition":
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                    case "npcSpeech":
                        String speechValue = "NpcSpeech=";
                        for (MapleData speechInfo : info.getChildren()) {
                            boolean hasSpeech = false;
                            int templateID = 0, order = 0;
                            for (MapleData speechAttr : speechInfo) {
                                String reqName = speechAttr.getName();
                                String reqValue = MapleDataTool.getString(speechAttr);
                                switch (reqName) {
                                    case "script":
                                        quest.addSpeech(reqValue);
                                        break;
                                    case "speech":
                                        hasSpeech = true;
                                        break;
                                    case "id":
                                        templateID = Integer.parseInt(reqValue);
                                        break;
                                    case "order":
                                        order = Integer.parseInt(reqValue);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            if (hasSpeech && templateID != 0) {
                                speechValue += templateID + "" + order + "/";
                                quest.addSpeech(speechValue);
                            }
                        }
                        break;
                    default:
                        log.warn(String.format("(%d) Unk name %s with value %s", questId, name, value));
                        break;
                }
            }
        }
        quests.put(questId, quest);
        return quest;
    }

    public static Quest createQuestFromId(int questID) {
        QuestInfo qi = getQuestInfo(questID);
        Quest quest = new Quest();
        quest.setQrKey(questID);
        if (qi != null) {
            if (qi.isAutoComplete()) {
                quest.setStatus(QuestStatus.Started);
//                quest.completeQuest(); // TODO check what autocomplete actually means
            } else {
                quest.setStatus(QuestStatus.Started);
            }
            for (QuestProgressRequirement qpr : qi.getQuestProgressRequirements()) {
                quest.addQuestProgressRequirement(qpr.deepCopy());
            }
        } else {
            quest.setStatus(QuestStatus.Started);
        }
        return quest;

    }
}
