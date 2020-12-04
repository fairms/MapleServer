package im.cave.ms.net.server;

import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler
 * @date 11/20 21:42
 */
public class ErrorPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorPacketHandler.class);

    public static void handlePacket(SeekableLittleEndianAccessor slea) {
        slea.skip(6);
        short packetLength = slea.readShort();
        slea.skip(4);
        int op = slea.readShort();
        byte[] packet = slea.read(packetLength - 6);
        log.error("Send error opcode {} packet {} ", Integer.toHexString(op), packet);
    }
}
