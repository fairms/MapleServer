package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;

public interface QuestStartRequirement {

    boolean hasRequirements(MapleCharacter chr);
}
