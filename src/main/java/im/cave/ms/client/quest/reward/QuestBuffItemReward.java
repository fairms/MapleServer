package im.cave.ms.client.quest.reward;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.ItemBuffs;

public class QuestBuffItemReward implements QuestReward {
    private int buffItemID;
    private int status;

    public QuestBuffItemReward(int buffItemID, int status) {
        this.buffItemID = buffItemID;
        this.status = status;
    }

    public QuestBuffItemReward() {
    }

    public int getBuffItemID() {
        return buffItemID;
    }

    public void setBuffItemID(int buffItemID) {
        this.buffItemID = buffItemID;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void giveReward(MapleCharacter chr) {
        ItemBuffs.giveItemBuffsFromItemID(chr, getBuffItemID());
    }

}
