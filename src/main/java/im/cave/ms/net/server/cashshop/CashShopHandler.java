package im.cave.ms.net.server.cashshop;


import im.cave.ms.net.netty.InPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.server.cashshop
 * @date 12/10 22:56
 */
public class CashShopHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger log = LoggerFactory.getLogger("Channel");

    private final int world;

    public CashShopHandler(int worldId) {
        this.world = worldId;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket msg) throws Exception {

    }
}
