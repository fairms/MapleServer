package im.cave.ms.network.server.cashshop;


import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.crypto.AESCipher;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.CashShopPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.network.server.channel.handler.UserHandler;
import im.cave.ms.network.server.channel.handler.WorldHandler;
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
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(" Join in World-{} CashShop", world);
        int sendIv = (int) (Math.random() * Integer.MAX_VALUE);
        int recvIv = (int) (Math.random() * Integer.MAX_VALUE);
        MapleClient client = new MapleClient(ctx.channel(), sendIv, recvIv);
        client.announce(LoginPacket.getHello(sendIv, recvIv, ServerType.CASHSHOP));
        ctx.channel().attr(CLIENT_KEY).set(client);
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
        c.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c == null || c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
            return;
        }
        int op = inPacket.readShort();
        if (c.mEncryptedOpcode.containsKey(op)) {
            op = c.mEncryptedOpcode.get(op);
        }
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(inPacket, (short) op);
            return;
        }

        switch (opcode) {
            case USER_ENTER_SERVER:
                WorldHandler.handleUserEnterServer(inPacket, c, ServerType.CASHSHOP);
                break;
            case PONG:
                c.setLastPong(System.currentTimeMillis());
                break;
            case USER_TRANSFER_FIELD_REQUEST:
                UserHandler.handleChangeMapRequest(inPacket, c);
                break;
            case CASH_SHOP_POINT_REQUEST:
                c.announce(CashShopPacket.queryCashResult(c.getAccount()));
                break;
            case CASH_SHOP_CASH_ITEM_REQUEST:
                UserHandler.handleCashShopCashItemRequest(inPacket, c);
                break;

        }
    }


    private void handleUnknown(InPacket inPacket, short op) {
        log.warn("Unhandled opcode {}, packet {}",
                Integer.toHexString(op & 0xFFFF),
                inPacket);
    }


}
