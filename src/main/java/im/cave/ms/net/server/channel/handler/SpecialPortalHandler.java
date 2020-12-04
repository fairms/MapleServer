package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.Portal;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler
 * @date 11/28 14:05
 */
public class SpecialPortalHandler {
    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte type = slea.readByte();
        String portalName = slea.readMapleAsciiString();
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
