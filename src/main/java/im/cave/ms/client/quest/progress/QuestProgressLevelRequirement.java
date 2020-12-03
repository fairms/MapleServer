package im.cave.ms.client.quest.progress;

import im.cave.ms.client.character.MapleCharacter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created on 3/2/2018.
 */
@Entity
@DiscriminatorValue("level")
public class QuestProgressLevelRequirement extends QuestProgressRequirement {

    @Column(name = "requiredCount")
    private int level;

    public QuestProgressLevelRequirement() {
    }

    public QuestProgressLevelRequirement(int level) {
        this.level = level;
    }

    @Override
    public boolean isComplete(MapleCharacter chr) {
        return chr.getLevel() >= getLevel();
    }

    public QuestProgressRequirement deepCopy() {
        QuestProgressLevelRequirement qplr = new QuestProgressLevelRequirement();
        qplr.setLevel(getLevel());
        qplr.setOrder(getOrder());
        return qplr;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
