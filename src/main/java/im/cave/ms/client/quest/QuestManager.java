package im.cave.ms.client.quest;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.quest.requirement.QuestStartCompletionRequirement;
import im.cave.ms.client.quest.requirement.QuestStartRequirement;
import im.cave.ms.client.quest.reward.QuestBuffItemReward;
import im.cave.ms.client.quest.reward.QuestItemReward;
import im.cave.ms.client.quest.reward.QuestReward;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.QuestStatus;
import im.cave.ms.network.packet.PlayerPacket;
import im.cave.ms.network.packet.QuestPacket;
import im.cave.ms.provider.data.QuestData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static im.cave.ms.enums.QuestStatus.Completed;
import static im.cave.ms.enums.QuestStatus.NotStarted;
import static im.cave.ms.enums.QuestStatus.Started;

/**
 * Created on 12/20/2017.
 */
@Entity
@Table(name = "questmanagers")
@Getter
@Setter
public class QuestManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @CollectionTable(name = "questlists")
    @MapKeyColumn(name = "questId")
    private Map<Integer, Quest> questList;

    @Transient
    private MapleCharacter chr;

    public QuestManager() {

    }

    public QuestManager(MapleCharacter chr) {
        questList = new HashMap<>();
        this.chr = chr;
    }

    public Set<Quest> getCompletedQuests() {
        return getQuests().values().stream().filter(quest -> quest.getStatus() == Completed).collect(Collectors.toSet());
    }

    public Set<Quest> getQuestsInProgress() {
        return getQuests().values().stream().filter(quest -> quest.getStatus() == Started).collect(Collectors.toSet());
    }

    public int getSize() {
        return questList.size();
    }

    public Map<Integer, Quest> getQuests() {
        return questList;
    }

    public boolean hasQuestInProgress(int questID) {
        Quest quest = getQuests().get(questID);
        return quest != null && quest.getStatus() == Started;
    }

    /**
     * Checks if a quest has been completed, i.e. the status is COMPLETE.
     *
     * @param questID the quest's id to check
     * @return quest completeness
     */
    public boolean hasQuestCompleted(int questID) {
        Quest quest = getQuests().get(questID);
        return quest != null && quest.getStatus() == Completed;
    }

    /**
     * Checks if a quest is complete. This means that a quest's status is STARTED, and that all the requirements to
     * complete it have been met.
     *
     * @param questID the quest's id to check
     * @return completeness
     */
    public boolean isComplete(int questID) {
        Quest quest = getQuests().get(questID);
        return hasQuestInProgress(questID) && quest.isComplete(chr);
    }

    public void addQuest(Quest quest) {
        addQuest(quest, true);
    }

    public void addCustomQuest(Quest quest) {
        addQuest(quest, false);
    }

    /**
     * Adds a new {@link Quest} to this QuestManager's quests. If it already exists, doesn't do anything.
     * Use {@link #replaceQuest(Quest)} if a given quest should be overridden.
     *
     * @param quest            The Quest to add.
     * @param addRewardsFromWz Whether or not to addRewards from the WzFiles
     */
    private void addQuest(Quest quest, boolean addRewardsFromWz) {
        if (!getQuests().containsKey(quest.getQrKey())) {
            getQuests().put(quest.getQrKey(), quest);
            chr.announce(QuestPacket.questRecordMessage(quest));
            if (quest.getStatus() == QuestStatus.Completed) {
                chr.chatMessage(ChatType.Tip, "[Info] Completed quest " + quest.getQrKey());
            } else {
                chr.chatMessage(ChatType.Tip, "[Info] Accepted quest " + quest.getQrKey());
                if (addRewardsFromWz) {
                    QuestInfo qi = QuestData.getQuestInfo(quest.getQrKey());
                    if (qi != null) {
                        for (QuestReward qr : qi.getQuestRewards()) {
                            if ((qr instanceof QuestItemReward && ((QuestItemReward) qr).getStatus() == 0) || (qr instanceof QuestBuffItemReward && ((QuestBuffItemReward) qr).getStatus() == 0)) {
                                qr.giveReward(getChr());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a new {@link Quest} to this QuestManager's quest. If it already exists, overrides the old one with the new one.
     *
     * @param quest The Quest to add/replace.
     */
    public void replaceQuest(Quest quest) {
        getQuests().put(quest.getQrKey(), quest);
//        chr.write(WvsContext.questRecordMessage(quest));
    }

    public boolean canStartQuest(int questID) {
        QuestInfo qi = QuestData.getQuestInfo(questID);
        if (qi == null) {
            return true;
        }
        Set<QuestStartRequirement> questReqs = qi.getQuestStartRequirements().stream()
                .filter(qsr -> qsr instanceof QuestStartCompletionRequirement)
                .collect(Collectors.toSet());
        boolean hasQuest = questReqs.size() == 0 ||
                questReqs.stream().anyMatch(q -> q.hasRequirements(chr));
        return hasQuest && qi.getQuestStartRequirements().stream()
                .filter(qsr -> !(qsr instanceof QuestStartCompletionRequirement))
                .allMatch(qsr -> qsr.hasRequirements(chr));
    }

    public MapleCharacter getChr() {
        return chr;
    }

    /**
     * Completes a quest. Assumes the check for in-progressness has already been done, so this method can be used
     * to complete quests that the Char does not actually have.
     *
     * @param questID The quest ID to finish.
     */
    public void completeQuest(int questID) {
        QuestInfo questInfo = QuestData.getQuestInfo(questID);
        Quest quest = getQuests().get(questID);
        if (quest == null) {
            quest = QuestData.createQuestFromId(questID);
            addQuest(quest);
        }
        quest.setStatus(QuestStatus.Completed);
        quest.setCompletedTime(System.currentTimeMillis());
        chr.chatMessage(ChatType.Tip, "[Info] Completed quest " + quest.getQrKey());
//        chr.getMap().broadcastMessage(UserRemote.effect(chr.getId(), Effect.questCompleteEffect()));
        chr.announce(PlayerPacket.effect(Effect.questCompleteEffect()));
        chr.announce(QuestPacket.questRecordMessage(quest));
        if (questInfo != null) {
            for (QuestReward qr : questInfo.getQuestRewards()) {
                if (!(qr instanceof QuestItemReward) || ((QuestItemReward) qr).getStatus() != 0) {
                    qr.giveReward(chr);
                }
            }
        }
    }

    public void handleMobKill(Mob mob) {
        Set<Quest> questsInProgress = chr.getQuestManager().getQuestsInProgress();
        for (Quest quest : questsInProgress) {
            if (quest.reqMob(mob.getTemplateId())) {
                quest.handleMobKill(mob.getTemplateId());
                chr.announce(QuestPacket.questRecordMessage(quest));
            }
        }
    }

    public void handleMoneyGain(int money) {
        for (Quest q : getQuestsInProgress()) {
            if (q.hasMoneyReq()) {
                q.addMoney(money);
                chr.announce(QuestPacket.questRecordMessage(q));
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChr(MapleCharacter chr) {
        this.chr = chr;
    }

    /**
     * Removes a given quest from this QuestManager, and notifies the client of this change. Does nothing if the Char
     * does not currently have the quest.
     *
     * @param questID the id of the quest that should be removed
     */
    public void removeQuest(int questID) {
        Quest q = getQuests().get(questID);
        if (q != null) {
            q.setStatus(NotStarted);
            getQuests().remove(questID);
            chr.announce(QuestPacket.questRecordMessage(q));
        }
    }

    /**
     * Adds a quest to this QuestManager with a given id. If there is no quest with that id, does nothing.
     *
     * @param id the quest's id to add
     */
    public void addQuest(int id) {
        Quest q = QuestData.createQuestFromId(id);
        addQuest(q);
    }

    public Quest getQuestById(int questID) {
        return getQuests().getOrDefault(questID, null);
    }
}
