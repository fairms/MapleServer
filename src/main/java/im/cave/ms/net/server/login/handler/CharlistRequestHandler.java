package im.cave.ms.net.server.login.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CharlistRequestHandler {
    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.skip(1);
        int worldId = slea.readByte();
        int channel = slea.readByte();
        c.setWorld(worldId);
        c.setChannel(channel);
        List<MapleCharacter> characters = c.loadCharacters(worldId, false);
        c.announce(LoginPacket.charList(c, characters, 0));
        c.announce(LoginPacket.account(c.getAccount()));
    }
}
