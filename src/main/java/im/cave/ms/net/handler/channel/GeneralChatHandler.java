package im.cave.ms.net.handler.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.net.db.DataBaseManager;
import im.cave.ms.net.handler.CommandHandler;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 13:40
 */
public class GeneralChatHandler {

    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int tick = slea.readInt();
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            c.close();
            return;
        }
        player.setTick(tick);
        String content = slea.readMapleAsciiString();

        if (content.startsWith("@")) {
            CommandHandler.handle(c, content);
            return;
        }

        player.getMap().broadcastMessage(player, MaplePacketCreator.getChatText(player, content), true);
    }
}
