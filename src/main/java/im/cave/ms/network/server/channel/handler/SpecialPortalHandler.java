package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.Portal;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.MaplePacketCreator;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler
 * @date 11/28 14:05
 */
public class SpecialPortalHandler {
    public static void handlePacket(InPacket inPacket, MapleClient c) {
        byte type = inPacket.readByte();
        String portalName = inPacket.readMapleAsciiString();
        Portal portal = c.getPlayer().getMap().getPortal(portalName);
        if (portal == null) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().isChangingChannel()) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }

        portal.enterPortal(c);
    }
}
