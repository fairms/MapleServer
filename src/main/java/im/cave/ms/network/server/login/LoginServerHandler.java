package im.cave.ms.network.server.login;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.crypto.AESCipher;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.network.server.ErrorPacketHandler;
import im.cave.ms.network.server.login.handler.CharOperationHandler;
import im.cave.ms.network.server.login.handler.CharlistRequestHandler;
import im.cave.ms.network.server.login.handler.CreateCharHandler;
import im.cave.ms.network.server.login.handler.OfficialLoginHandler;
import im.cave.ms.network.server.login.handler.PasswordLoginHandler;
import im.cave.ms.network.server.login.handler.ServerListHandler;
import im.cave.ms.network.server.service.EventManager;
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
 * @Package im.cave.ms.handler
 * @date 11/19 19:40
 */
public class LoginServerHandler extends SimpleChannelInboundHandler<InPacket> {

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
        ctx.channel().attr(AES_CIPHER).set(new AESCipher());
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
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c == null || c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
            return;
        }
        short op = inPacket.readShort();
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(inPacket, op);
            return;
        }
        switch (opcode) {
            case CUSTOM_LOGIN:
            case BEFORE_LOGIN:
                PasswordLoginHandler.handlePacket(c, inPacket);
                break;
            case CLIENT_AUTH_REQUEST:
                c.announce(LoginPacket.clientAuth());
                break;
            case SDO_LOGIN_REQUEST:
                OfficialLoginHandler.handlePacket(c, inPacket);
                break;
            case SERVERSTATUS_REQUEST:
                ServerListHandler.serverStatus(inPacket, c);
                break;
            case CHARLIST_REQUEST:
                CharlistRequestHandler.handlePacket(inPacket, c);
                break;
            case CHAR_SELECTED:
                CharOperationHandler.handleSelectChar(inPacket, c);
                break;
            case CHECK_CHAR_NAME_REQUEST:
                CreateCharHandler.checkName(inPacket, c);
                break;
            case SERVERLIST_REQUEST2:
                ServerListHandler.serverList(inPacket, c);
                break;
            case CREATE_CHAR_REQUEST:
                CreateCharHandler.handleCreateCharRequest(inPacket, c);
                break;
            case DELETE_CHAR:
                CharOperationHandler.handleDeleteChar(inPacket, c);
                break;
            case DELETE_CHAR_CONFIRM:
                CharOperationHandler.handleDeleteCharConfirm(inPacket, c);
                break;
            case CANCEL_DELETE_CHAR:
                CharOperationHandler.handleCancelDelete(inPacket, c);
                break;
            case PONG:
                c.pongReceived();
                break;
            case ERROR_PACKET:
                ErrorPacketHandler.handlePacket(inPacket);
                break;
            case OPEN_CREATE_CHAR_LAYOUT:
                c.announce(LoginPacket.getOpenCreateChar());
                break;
            case USER_CASH_POINT_REQUEST:
                c.announce(WorldPacket.queryCashPointResult(c.getAccount()));
            case USER_SLOT_EXPAND_REQUEST:
                CharOperationHandler.handleAccountCharSlotsExpand(inPacket, c);
                break;
            default:
                handleUnknown(inPacket, op);
                break;
        }
//        inPacket.release();
    }

    private void handleUnknown(InPacket inPacket, short op) {
        log.warn("Unhandled opcode {}, content {}",
                Integer.toHexString(op & 0xFFFF),
                inPacket);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.info("Client forcibly closed the game");
        } else {
            cause.printStackTrace();
        }
    }
}
