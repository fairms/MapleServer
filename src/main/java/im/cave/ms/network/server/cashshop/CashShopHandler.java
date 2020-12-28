package im.cave.ms.network.server.cashshop;


import im.cave.ms.client.MapleClient;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.crypto.AESCipher;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.server.service.EventManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static im.cave.ms.client.MapleClient.AES_CIPHER;
import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.server.cashshop
 * @date 12/10 22:56
 */
public class CashShopHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger log = LoggerFactory.getLogger("CashShop");

    private final int world;

    public CashShopHandler(int worldId) {
        this.world = worldId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket msg) throws Exception {
        log.info(" Join in World-{} CashShop", world);
        int sendIv = (int) (Math.random() * Integer.MAX_VALUE);
        int recvIv = (int) (Math.random() * Integer.MAX_VALUE);
        MapleClient client = new MapleClient(ctx.channel(), sendIv, recvIv);
        client.announce(LoginPacket.getHello(sendIv, recvIv, ServerType.CASHSHOP));
        ctx.channel().attr(CLIENT_KEY).set(client);
        ctx.channel().attr(AES_CIPHER).set(new AESCipher());
        EventManager.addFixedRateEvent(client::sendPing, 0, 10000);
    }

}
