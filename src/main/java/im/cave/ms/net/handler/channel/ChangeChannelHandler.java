package im.cave.ms.net.handler.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/28 15:32
 */
public class ChangeChannelHandler {
    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int channel = slea.readByte();
        slea.readInt();
        if (c.getChannel() == channel) {
            c.close();
            return;
        }
        c.changeChannel(channel);
    }
}
