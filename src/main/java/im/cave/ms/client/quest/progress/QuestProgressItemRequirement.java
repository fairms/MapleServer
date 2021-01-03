package im.cave.ms.client.quest.progress;


import im.cave.ms.client.character.MapleCharacter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("item")
public class QuestProgressItemRequirement extends QuestProgressRequirement {


    @Column(name = "unitID")
    private int itemID;
    @Column(name = "requiredCount")
    private int requiredCount;

    public QuestProgressItemRequirement() {
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(int requiredCount) {
        this.requiredCount = requiredCount;
    }

    @Override
    public boolean isComplete(MapleCharacter chr) {
        return chr.hasItemCount(getItemID(), getRequiredCount());
    }

    public QuestProgressRequirement deepCopy() {
        QuestProgressItemRequirement qpir = new QuestProgressItemRequirement();
        qpir.setItemID(getItemID());
        qpir.setRequiredCount(getRequiredCount());
        qpir.setOrder(getOrder());
        return qpir;
    }
}
