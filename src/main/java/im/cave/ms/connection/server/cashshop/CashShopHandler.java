package im.cave.ms.connection.server.cashshop;


import im.cave.ms.client.MapleClient;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.CashShopPacket;
import im.cave.ms.connection.packet.opcode.RecvOpcode;
import im.cave.ms.connection.server.AbstractServerHandler;
import im.cave.ms.connection.server.channel.handler.UserHandler;
import im.cave.ms.connection.server.channel.handler.WorldHandler;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.ServerType;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.server.cashshop
 * @date 12/10 22:56
 */
public class CashShopHandler extends AbstractServerHandler {

    private static final Logger log = LoggerFactory.getLogger("CashShop");

    private final int world;

    public CashShopHandler(int worldId) {
        this.world = worldId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(" Join in World-{} CashShop", world);
        connected(ctx, ServerType.CASHSHOP);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket in) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c == null || c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
            return;
        }
        int op = in.readShort();
        if (c.mEncryptedOpcode.containsKey(op)) {
            op = c.mEncryptedOpcode.get(op);
        }
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(in, (short) op);
            return;
        }

        switch (opcode) {
            case USER_ENTER_SERVER:
                WorldHandler.handleUserEnterServer(in, c, ServerType.CASHSHOP);
                break;
            case PONG:
                c.setLastPong(System.currentTimeMillis());
                break;
            case USER_TRANSFER_FIELD_REQUEST:
                UserHandler.handleChangeMapRequest(in, c);
                break;
            case CASH_SHOP_POINT_REQUEST:
                c.announce(CashShopPacket.queryCashResult(c.getAccount()));
                break;
            case CASH_SHOP_CASH_ITEM_REQUEST:
                UserHandler.handleCashShopCashItemRequest(in, c);
                break;
            case CASH_SHOP_SAVE_COLLOCATION:
                c.getPlayer().enableAction();
                break;
        }
    }
}
