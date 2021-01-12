package im.cave.ms.connection.server;


import im.cave.ms.connection.netty.InPacket;
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

    public static void handlePacket(InPacket in) {
        in.skip(6);
        short packetLength = in.readShort();
        in.skip(4);
        int op = in.readShort();
        byte[] packet = in.read(packetLength - 6);
        log.error("Send error opcode {} packet {} ", Integer.toHexString(op), packet);
    }
}
