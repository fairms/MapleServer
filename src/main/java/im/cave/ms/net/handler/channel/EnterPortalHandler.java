package im.cave.ms.net.handler.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Portal;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 15:29
 */
public class EnterPortalHandler {
    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();

        if (slea.available() != 0) {
            byte type = slea.readByte();
            int targetId = slea.readInt();
            Portal portal = player.getMap().getPortal(slea.readMapleAsciiString());
            portal.enterPortal(c);
        }

    }
}
