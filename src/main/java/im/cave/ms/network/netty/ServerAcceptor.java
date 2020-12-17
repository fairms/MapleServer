package im.cave.ms.network.netty;

import im.cave.ms.network.server.AbstractServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.netty
 * @date 11/19 17:00
 */
public class ServerAcceptor implements Runnable {
    public AbstractServer server;
    private static final Logger log = LoggerFactory.getLogger(ServerAcceptor.class);


    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup);
            sb.channel(NioServerSocketChannel.class);
            sb.childHandler(new ServerInitializer(server));
            sb.childOption(ChannelOption.TCP_NODELAY, true);
            sb.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = sb.bind(server.getPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("端口：{} 被占用", server.getPort());
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
