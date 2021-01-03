package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;


public class QuestStartMarriageRequirement implements QuestStartRequirement {
    @Override
    public boolean hasRequirements(MapleCharacter chr) {
        return chr.isMarried();
    }

}
