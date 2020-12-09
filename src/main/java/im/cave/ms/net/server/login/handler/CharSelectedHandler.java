package im.cave.ms.net.server.login.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.LoginType;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.world.World;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CharSelectedHandler {
    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int charId = slea.readInt();
        byte invisible = slea.readByte();
        if (c.getLoginStatus().equals(LoginStatus.LOGGEDIN) && c.getAccount().getCharacter(charId) != null) {
            MapleCharacter player = c.getAccount().getCharacter(charId);
            c.setPlayer(player);
            c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
            Server.getInstance().addClientInTransfer(c.getChannel(), charId, c);
            int port = Server.getInstance().getChannel(c.getWorld(), c.getChannel()).getPort();
            c.announce(LoginPacket.selectCharacterResult(LoginType.Success, (byte) 0, port, charId));
        } else {
            c.announce(LoginPacket.selectCharacterResult(LoginType.UnauthorizedUser, (byte) 0, 0, 0));
        }
    }
}
