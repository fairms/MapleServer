package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.net.netty.InPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.server.CommandHandler;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 13:40
 */
public class GeneralChatHandler {

    public static void handlePacket(InPacket inPacket, MapleClient c) {
        int tick = inPacket.readInt();
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            c.close();
            return;
        }
        player.setTick(tick);
        String content = inPacket.readMapleAsciiString();

        if (content.startsWith("@")) {
            CommandHandler.handle(c, content);
            return;
        }

        player.getMap().broadcastMessage(player, MaplePacketCreator.getChatText(player, content), true);
    }
}
