package im.cave.ms.client.quest.progress;

import im.cave.ms.client.character.MapleCharacter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("mob")
public class QuestProgressMobRequirement extends QuestProgressRequirement {

    @Column(name = "unitID")
    private int mobID;
    @Column(name = "requiredCount")
    private int requiredCount;
    @Column(name = "currentCount")
    private int currentCount;

    public QuestProgressMobRequirement() {
    }

    public void setMobID(int mobID) {
        this.mobID = mobID;
    }

    public int getMobID() {
        return mobID;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(int requiredCount) {
        this.requiredCount = requiredCount;
    }

    public void incCurrentCount(int amount) {
        currentCount += amount;
        if (currentCount < 0) {
            currentCount = 0;
        }
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    @Override
    public boolean isComplete(MapleCharacter chr) {
        return getCurrentCount() >= getRequiredCount();
    }

    public QuestProgressRequirement deepCopy() {
        QuestProgressMobRequirement qpmr = new QuestProgressMobRequirement();
        qpmr.setMobID(getMobID());
        qpmr.setRequiredCount(getRequiredCount());
        qpmr.setCurrentCount(getCurrentCount());
        qpmr.setOrder(getOrder());
        return qpmr;
    }


    public String getValue() {
        return String.valueOf(getCurrentCount());
    }
}
