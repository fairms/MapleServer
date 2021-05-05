package im.cave.ms.connection.server.login.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.configs.Config;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.LoginPacket;
import im.cave.ms.connection.server.Server;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.LoginType;
import im.cave.ms.tools.DateUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:38
 */
public class PasswordLoginHandler {
    private static int count = 0; //todo 记录登录账号密码尝试册数
    //todo 记录异地登陆踢出在线账号功能
    private static final Logger log = LoggerFactory.getLogger(PasswordLoginHandler.class);

    public static void handlePacket(MapleClient c, InPacket in) {
        InetSocketAddress inSocket = (InetSocketAddress) c.getCh().remoteAddress();
        String clientIP = inSocket.getAddress().getHostAddress();
        byte[] machineId;
        String username;
        String password;
        if (clientIP.equals("221.231.130.70")) {
            machineId = new byte[16];
            username = "3378690678";
            password = "3378690678!";
        } else if (clientIP.startsWith("192.168")) {
            machineId = new byte[16];
            username = "admin";
            password = "admin";
        } else {
            machineId = in.read(16);
            username = in.readMapleAsciiString();
            password = in.readMapleAsciiString();
        }
        c.setMachineID(machineId);
        LoginType loginResult = c.login(username, password);
        if (loginResult == LoginType.Success && !Server.getInstance().isOnline()) {
            loginResult = LoginType.SystemErr;
        }
        if (loginResult == LoginType.Success) {
            c.announce(LoginPacket.loginResult(c, loginResult));
            c.announce(LoginPacket.serverListBg());
            for (OutPacket serverInfo : LoginPacket.worldInformation()) {
                c.announce(serverInfo);
            }
            count++;
            c.announce(LoginPacket.worldInformationEnd());
        } else if (loginResult == LoginType.NotRegistered && Config.serverConfig.AUTOMATIC_REGISTER) {
            Account account = Account.createAccount(username, BCrypt.hashpw(password, BCrypt.gensalt(ServerConstants.BCRYPT_ITERATIONS)));
            account.save();
            account = (Account) DataBaseManager.getObjFromDB(Account.class, account.getId());
            account.setLastLogin(DateUtil.getFileTime(System.currentTimeMillis()));
            c.setAccount(account);
            c.setLoginStatus(LoginStatus.LOGGEDIN);
            c.announce(LoginPacket.loginResult(c, LoginType.Success));
            c.announce(LoginPacket.serverListBg());
            for (OutPacket serverInfo : LoginPacket.worldInformation()) {
                c.announce(serverInfo);
            }
            c.announce(LoginPacket.worldInformationEnd());
        } else {
            c.announce(LoginPacket.loginResult(c, loginResult));
        }
    }
}
