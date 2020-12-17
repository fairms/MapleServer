package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Portal;
import im.cave.ms.network.netty.InPacket;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 15:29
 */
public class EnterPortalHandler {
    public static void handlePacket(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        if (inPacket.available() != 0) {
            byte type = inPacket.readByte();
            int targetId = inPacket.readInt();
            String portalName = inPacket.readMapleAsciiString();
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
