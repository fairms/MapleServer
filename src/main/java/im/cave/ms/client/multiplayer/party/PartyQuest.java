package im.cave.ms.client.multiplayer.party;

import im.cave.ms.client.character.MapleCharacter;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.multiplayer.party
 * @date 2/23 16:16
 */
public class PartyQuest {
    private int id;
    private int channel;
    private int pqType;
    private List<MapleCharacter> chars;
    private boolean[] stages;
}
