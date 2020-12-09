package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;

/**
 * Created on 3/2/2018.
 */
public class QuestStartCompletionRequirement implements QuestStartRequirement {
    private int questID;
    private byte questStatus;

    public QuestStartCompletionRequirement() {
    }

    public QuestStartCompletionRequirement(int questID, byte questStatus) {
        this.questID = questID;
        this.questStatus = questStatus;
    }

    public int getQuestID() {
        return questID;
    }

    public byte getQuestStatus() {
        return questStatus;
    }

    public void setQuestID(int questID) {
        this.questID = questID;
    }

    public void setQuestStatus(byte questStatus) {
        this.questStatus = questStatus;
    }

    @Override
    public boolean hasRequirements(MapleCharacter chr) {
//        QuestManager qm = chr.getQuestManager();
//        switch (getQuestStatus()) {
////            case 0: // Not started
////                return !qm.hasQuestInProgress(getQuestID()) && !qm.hasQuestCompleted(getQuestID());
////            case 1: // In progress
////                return qm.hasQuestInProgress(getQuestID());
//            case 0: // Completed
//                return qm.hasQuestCompleted(getQuestID());
//            default:
//                log.error(String.format("Unknown status %d.", getQuestStatus()));
//                return true;
//        }
        return true;
    }


}
