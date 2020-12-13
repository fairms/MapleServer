package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.net.netty.InPacket;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 15:32
 */
public class ChangeChannelHandler {
    public static void handlePacket(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        byte channel = inPacket.readByte();
        inPacket.readInt();
        if (c.getChannel() == channel) {
            c.close(); //hack
            return;
        }
        //todo
        player.changeChannel(channel);
    }
}
