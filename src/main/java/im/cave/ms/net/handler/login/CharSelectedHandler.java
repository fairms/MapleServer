package im.cave.ms.net.handler.login;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
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
        MapleCharacter player = c.getAccount().getCharacter(charId);
        player.setAccount(c.getAccount());
        player.setChannel(c.getChannel());
        c.setPlayer(player);
        c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
        c.getPlayer().setChangingChannel(true);
        World world = Server.getInstance().getWorldById(c.getWorld());
        MapleChannel channel = world.getChannel(c.getChannel());
        channel.addPlayer(c.getPlayer());
        int port = channel.getPort();
        c.announce(LoginPacket.getChannel(port, charId));
    }
}
