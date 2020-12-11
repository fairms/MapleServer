package im.cave.ms.net.server.login.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import static im.cave.ms.enums.LoginStatus.LOGGEDIN;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:50
 */
public class ServerListHandler {
    public static void serverStatus(SeekableLittleEndianAccessor slea, MapleClient c) {
        validate(c);
        slea.skip(1);
        int worldId = slea.readInt();
        c.announce(LoginPacket.serverStatus(worldId));
    }

    public static void serverList(SeekableLittleEndianAccessor slea, MapleClient c) {
        for (MaplePacketLittleEndianWriter server : LoginPacket.serverList()) {
            c.announce(server);
        }
        c.announce(LoginPacket.serverListEnd());
    }

    private static void validate(MapleClient c) {
        if (c.getLoginStatus() == LOGGEDIN) {
            return;
        }
        c.close();
    }
}
