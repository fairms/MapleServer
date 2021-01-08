package im.cave.ms.network.netty;

import im.cave.ms.client.MapleClient;
import im.cave.ms.network.crypto.AESCipher;
import im.cave.ms.network.crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static im.cave.ms.client.MapleClient.CLIENT_KEY;
import static im.cave.ms.constants.ServerConstants.LOGIN_PORT;
import static im.cave.ms.constants.ServerConstants.VERSION;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.netty
 * @date 11/19 19:32
 */
public class MaplePacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger log = LoggerFactory.getLogger(MaplePacketEncoder.class);
    private static final int uSeqBase = (short) ((((0xFFFF - VERSION) >> 8) & 0xFF) | (((0xFFFF - VERSION) << 8) & 0xFF00));

    @Override
    protected void encode(ChannelHandlerContext ctx, OutPacket out, ByteBuf byteBuf) {
        byte[] data = out.getData();
        MapleClient client = ctx.channel().attr(CLIENT_KEY).get();
        AESCipher ac = ctx.channel().attr(MapleClient.AES_CIPHER).get();
        if (client != null) {
            int uSeqSend = client.getSendIv();
            short uDataLen = (short) (((data.length << 8) & 0xFF00) | (data.length >>> 8));
            short uRawSeq = (short) ((((uSeqSend >> 24) & 0xFF) | (((uSeqSend >> 16) << 8) & 0xFF00)) ^ uSeqBase);
            client.acquireEncoderState();
            try {
                uDataLen ^= uRawSeq;
                if (client.getPort() == LOGIN_PORT) {
                    ac.Crypt(data, uSeqSend);
                } else {
                    CIGCipher.Crypt(data, uSeqSend);
                }
                client.setSendIv(CIGCipher.InnoHash(uSeqSend, 4, 0));
            } finally {
                client.releaseEncoderState();
            }
            byteBuf.writeShort(uRawSeq);
            byteBuf.writeShort(uDataLen);
            byteBuf.writeBytes(data);
        } else {
            byteBuf.writeBytes(data);
        }
    }
}
