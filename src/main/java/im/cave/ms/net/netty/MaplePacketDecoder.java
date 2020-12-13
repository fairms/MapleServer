package im.cave.ms.net.netty;

import im.cave.ms.client.MapleClient;
import im.cave.ms.net.crypto.AESCipher;
import im.cave.ms.net.crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.netty
 * @date 11/19 19:32
 */
public class MaplePacketDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(MaplePacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        MapleClient client = ctx.channel().attr(CLIENT_KEY).get();
        if (client == null) {
            ctx.channel().disconnect();
            return;
        }
        int uSeqRcv = client.getRecvIv();
        if (client.getStoreLength() == -1) {
            if (in.readableBytes() < 4) {
                return;
            }
            int uRawSeq = in.readShortLE();
            int uDataLen = in.readShortLE();
            uDataLen ^= uRawSeq;
            if (uDataLen > 0x50000) {
                log.error("Recv packet length overflow");
                return;
            }
            short uSeqBase = (short) ((uSeqRcv >> 16) ^ uRawSeq);
            if (uSeqBase != AESCipher.nVersion) {
                log.error("[PacketDecoder] | Incorrect packet seq! Dropping client");
                client.close();
                return;
            }
            client.setStoreLength(uDataLen);
        }
        if (in.readableBytes() >= client.getStoreLength()) {
            byte[] dec = new byte[client.getStoreLength()];
            in.readBytes(dec);
            AESCipher.Crypt(dec, uSeqRcv);
            client.setRecvIv(CIGCipher.InnoHash(uSeqRcv, 4, 0));
            client.setStoreLength(-1);
            InPacket inPacket = new InPacket(dec);
            out.add(inPacket);
        }
    }
}
