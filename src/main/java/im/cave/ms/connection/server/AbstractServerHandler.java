package im.cave.ms.connection.server;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.configs.Config;
import im.cave.ms.connection.crypto.AESCipher;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.LoginPacket;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.enums.ServerType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static im.cave.ms.client.MapleClient.AES_CIPHER;
import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.server
 * @date 1/5 10:07
 */
public abstract class AbstractServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LoggerFactory.getLogger("ServerHandler");


    public void connected(ChannelHandlerContext ctx, ServerType type) {
        int sendIv = (int) (Math.random() * Integer.MAX_VALUE);
        int recvIv = (int) (Math.random() * Integer.MAX_VALUE);
        Channel channel = ctx.channel();
        MapleClient client = new MapleClient(channel, sendIv, recvIv);
        client.announce(LoginPacket.getHello(client, type));
        channel.attr(CLIENT_KEY).set(client);
        ctx.channel().attr(AES_CIPHER).set(new AESCipher());
        EventManager.addFixedRateEvent(client::sendPing, 0, 10000);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        Account account = c.getAccount();
        MapleCharacter player = c.getPlayer();
        if (player != null && !player.isChangingChannel()) {
            player.logout();
        } else if (player != null && player.isChangingChannel()) {
            player.setChangingChannel(false);
        } else if (account != null) {
            account.logout();
        }
        MapleClient o = ctx.channel().attr(CLIENT_KEY).get();
        if (o != null) {
            c.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.info("Client forcibly closed the game.");
        } else {
            cause.printStackTrace();
        }
    }

    protected void handleUnknown(InPacket in, short op) {
        if (Config.serverConfig.SHOW_UNKNOWN_PACKET) {
            log.warn("Unhandled opcode {}, packet {}",
                    Integer.toHexString(op & 0xFFFF),
                    in);
        }
    }

}
