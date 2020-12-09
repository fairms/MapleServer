package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;

/**
 * Created on 3/2/2018.
 */
public class QuestStartMaxLevelRequirement implements QuestStartRequirement {

    private short level;

    public QuestStartMaxLevelRequirement(short level) {
        this.level = level;
    }

    public QuestStartMaxLevelRequirement() {

    }

    private short getLevel() {
        return level;
    }

    @Override
    public boolean hasRequirements(MapleCharacter chr) {
        return chr.getLevel() <= getLevel();
    }

}
