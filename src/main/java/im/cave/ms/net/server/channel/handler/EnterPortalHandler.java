package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Portal;
import im.cave.ms.scripting.portal.PortalScriptManager;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 15:29
 */
public class EnterPortalHandler {
    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        if (slea.available() != 0) {
            byte type = slea.readByte();
            int targetId = slea.readInt();
            String portalName = slea.readMapleAsciiString();
            if (portalName != null && !"".equals(portalName)) {
                Portal portal = player.getMap().getPortal(portalName);
                portal.enterPortal(c);
            } else if (player.getHp() <= 0) {
                int returnMap = player.getMap().getReturnMap();
                player.changeMap(returnMap);
                player.heal(50);
            }
        }
    }
}
