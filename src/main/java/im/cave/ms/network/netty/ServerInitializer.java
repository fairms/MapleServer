package im.cave.ms.network.netty;

import im.cave.ms.enums.ServerType;
import im.cave.ms.network.server.AbstractServer;
import im.cave.ms.network.server.cashshop.CashShopHandler;
import im.cave.ms.network.server.channel.ChannelHandler;
import im.cave.ms.network.server.login.LoginServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.netty
 * @date 11/19 19:09
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final int channelId;
    private final int worldId;
    private final ServerType type;

    public ServerInitializer(AbstractServer abstractServer) {
        this.type = abstractServer.getType();
        this.channelId = abstractServer.getChannelId();
        this.worldId = abstractServer.getWorldId();
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleStateHandler", new IdleStateHandler(25, 25, 0));
        pipeline.addLast("decoder", new MaplePacketDecoder());
        pipeline.addLast("encoder", new MaplePacketEncoder());

        switch (type) {
            case LOGIN:
                pipeline.addLast(new LoginServerHandler());
                break;
            case CHAT:
            case WORLD:
            case AUCTION:
            case CHANNEL:
                pipeline.addLast(new ChannelHandler(channelId, worldId));
                break;
            case CASHSHOP:
                pipeline.addLast(new CashShopHandler(worldId));
                break;
        }
    }
}
