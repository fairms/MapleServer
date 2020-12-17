package im.cave.ms.network.server.login.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.LoginType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.server.Server;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CharOperationHandler {
    public static void handleSelectChar(InPacket inPacket, MapleClient c) {
        int charId = inPacket.readInt();
        byte invisible = inPacket.readByte();
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

    public static void handleDeleteChar(InPacket inPacket, MapleClient c) {
        inPacket.readByte();
        inPacket.readByte();
        int charId = inPacket.readInt();
        MapleCharacter character = c.getAccount().getCharacter(charId);
        character.setDeleted(true);
        long deleteTime = LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        character.setDeleteTime(deleteTime);
        c.announce(LoginPacket.deleteTime(charId));
    }

    public static void handleCancelDelete(InPacket inPacket, MapleClient c) {
        int charId = inPacket.readInt();
        MapleCharacter character = c.getAccount().getCharacter(charId);
        character.setDeleted(false);
        character.setDeleteTime(0L);
        c.announce(LoginPacket.cancelDeleteChar(charId));
    }
}
