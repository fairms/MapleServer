package im.cave.ms.connection.server.login;

import im.cave.ms.client.MapleClient;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.LoginPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.packet.opcode.RecvOpcode;
import im.cave.ms.connection.server.AbstractServerHandler;
import im.cave.ms.connection.server.ErrorPacketHandler;
import im.cave.ms.connection.server.login.handler.CharOperationHandler;
import im.cave.ms.connection.server.login.handler.CharlistRequestHandler;
import im.cave.ms.connection.server.login.handler.OfficialLoginHandler;
import im.cave.ms.connection.server.login.handler.PasswordLoginHandler;
import im.cave.ms.connection.server.login.handler.ServerListHandler;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.ServerType;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.handler
 * @date 11/19 19:40
 */
public class LoginServerHandler extends AbstractServerHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Connect with {}", ctx.channel().remoteAddress());
        connected(ctx, ServerType.LOGIN);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket in) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c == null || c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
            return;
        }
        short op = in.readShort();
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(in, op);
            return;
        }
        switch (opcode) {
            case CUSTOM_LOGIN:
            case BEFORE_LOGIN:
                PasswordLoginHandler.handlePacket(c, in);
                break;
            case CLIENT_START:
                c.announce(LoginPacket.sendStart());
                break;
            case AFTER_CREATE_CHAR:
                CharOperationHandler.handleAfterCreateChar(in, c);
                break;
            case SDO_LOGIN_REQUEST:
                OfficialLoginHandler.handlePacket(c, in);
                break;
            case SERVERSTATUS_REQUEST:
                ServerListHandler.serverStatus(in, c);
                break;
            case CHARLIST_REQUEST:
                CharlistRequestHandler.handlePacket(in, c);
                break;
            case CHAR_SELECTED:
                CharOperationHandler.handleSelectChar(in, c);
                break;
            case CHECK_CHAR_NAME_REQUEST:
                CharOperationHandler.handleCheckDuplicatedId(in, c);
                break;
            case SERVERLIST_REQUEST2:
                ServerListHandler.serverList(in, c);
                break;
            case CREATE_CHAR_REQUEST:
                CharOperationHandler.handleCreateCharRequest(in, c);
                break;
            case DELETE_CHAR:
                CharOperationHandler.handleUserDeleteChar(in, c);
                break;
            case DELETE_CHAR_CONFIRM:
                CharOperationHandler.handleUserConfirmDeleteChar(in, c);
                break;
            case CANCEL_DELETE_CHAR:
                CharOperationHandler.handleCancelDelete(in, c);
                break;
            case PONG:
                c.pongReceived();
                break;
            case ERROR_PACKET:
                ErrorPacketHandler.handlePacket(in);
                break;
            case OPEN_CREATE_CHAR_LAYOUT:
                c.announce(LoginPacket.getOpenCreateChar());
                break;
            case USER_CASH_POINT_REQUEST:
                c.announce(WorldPacket.queryCashPointResult(c.getAccount()));
            case USER_SLOT_EXPAND_REQUEST:
                CharOperationHandler.handleAccountCharSlotsExpand(in, c);
                break;
            default:
                handleUnknown(in, op);
                break;
        }
    }
}
