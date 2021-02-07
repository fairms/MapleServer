package im.cave.ms.connection.server.auction;

import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.server.AbstractServerHandler;
import im.cave.ms.enums.ServerType;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.server.auction
 * @date 2/5 10:53
 */
public class AuctionHandler extends AbstractServerHandler {
    private static final Logger log = LoggerFactory.getLogger("Auction");

    private final int world;

    public AuctionHandler(int world) {
        this.world = world;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InPacket inPacket) throws Exception {

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(" Join in World-{} CashShop", world);
        connected(ctx, ServerType.CASHSHOP);
    }
}
