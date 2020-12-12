package im.cave.ms.net.server.login;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.net.server.ErrorPacketHandler;
import im.cave.ms.net.server.login.handler.CharOperationHandler;
import im.cave.ms.net.server.login.handler.CharlistRequestHandler;
import im.cave.ms.net.server.login.handler.CreateCharHandler;
import im.cave.ms.net.server.login.handler.OfficialLoginHandler;
import im.cave.ms.net.server.login.handler.PasswordLoginHandler;
import im.cave.ms.net.server.login.handler.ServerListHandler;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.packet.opcode.RecvOpcode;
import im.cave.ms.net.server.Server;
import im.cave.ms.enums.ServerType;
import im.cave.ms.provider.service.EventManager;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.handler
 * @date 11/19 19:40
 */
public class LoginServerHandler extends SimpleChannelInboundHandler<SeekableLittleEndianAccessor> {

    private static final Logger log = LoggerFactory.getLogger(LoginServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Connect with {}", ctx.channel().remoteAddress());
        int sendIv = (int) (Math.random() * Integer.MAX_VALUE);
        int recvIv = (int) (Math.random() * Integer.MAX_VALUE);
        Channel channel = ctx.channel();
        MapleClient client = new MapleClient(channel, sendIv, recvIv);
        client.announce(LoginPacket.getHello(sendIv, recvIv, ServerType.LOGIN));
        channel.attr(CLIENT_KEY).set(client);
        EventManager.addFixedRateEvent(client::sendPing, 0, 10000);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("[ChannelHandler] | Channel inactive.");
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c != null) {
            Account account = c.getAccount();
            if (account != null && c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
                c.close();
            } else if (account != null) {
                account.logout();
            }
            c.close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SeekableLittleEndianAccessor slea) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        short op = slea.readShort();
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(slea, op);
            return;
        }
        switch (opcode) {
            case BEFORE_LOGIN:
                PasswordLoginHandler.handlePacket(c, slea);
                break;
            case CLIENT_AUTH_REQUEST:
                c.announce(LoginPacket.clientAuth());
                break;
            case SDO_LOGIN_REQUEST:
                OfficialLoginHandler.handlePacket(c, slea);
                break;
            case SERVERSTATUS_REQUEST:
                ServerListHandler.serverStatus(slea, c);
                break;
            case CHARLIST_REQUEST:
                CharlistRequestHandler.handlePacket(slea, c);
                break;
            case CHAR_SELECTED:
                CharOperationHandler.handleSelectChar(slea, c);
                break;
            case AFTER_CHAR_CREATED:
                CreateCharHandler.afterCreate(slea, c);
                break;
            case CHECK_CHAR_NAME_REQUEST:
                CreateCharHandler.checkName(slea, c);
                break;
            case SERVERLIST_REQUEST2:
                ServerListHandler.serverList(slea, c);
                break;
            case CREATE_CHAR_REQUEST:
                CreateCharHandler.createChar(slea, c);
                break;
            case DELETE_CHAR:
                CharOperationHandler.handleDeleteChar(slea, c);
                break;
            case CANCEL_DELETE_CHAR:
                CharOperationHandler.handleCancelDelete(slea, c);
                break;
            case PONG:
                c.pongReceived();
                break;
            case ERROR_PACKET:
                ErrorPacketHandler.handlePacket(slea);
                break;
            case OPEN_CREATE_CHAR_LAYOUT:
                c.announce(LoginPacket.getOpenCreateChar());
                break;
            default:
                handleUnknown(slea, op);
        }
    }

    private void handleUnknown(SeekableLittleEndianAccessor slea, short op) {
        log.warn("Unhandled opcode {}, packet {}",
                Integer.toHexString(op & 0xFFFF),
                slea);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.info("Client forcibly closed the game");
            MapleClient client = ctx.channel().attr(CLIENT_KEY).get();
            Account account = client.getAccount();
            if (account != null) {
                Server.getInstance().addAccount(account);
            }
        } else {
            cause.printStackTrace();
        }
    }
}
