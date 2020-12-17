package im.cave.ms.network.server.login.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.LoginPacket;

import static im.cave.ms.enums.LoginStatus.LOGGEDIN;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:50
 */
public class ServerListHandler {
    public static void serverStatus(InPacket inPacket, MapleClient c) {
        validate(c);
        inPacket.skip(1);
        int worldId = inPacket.readInt();
        c.announce(LoginPacket.serverStatus(worldId));
    }

    public static void serverList(InPacket inPacket, MapleClient c) {
        for (OutPacket server : LoginPacket.serverList()) {
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
