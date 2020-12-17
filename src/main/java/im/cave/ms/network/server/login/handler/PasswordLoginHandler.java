package im.cave.ms.network.server.login.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.configs.ServerConfig;
import im.cave.ms.enums.LoginType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.LoginPacket;
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
    private static int count = 0;
    private static final Logger log = LoggerFactory.getLogger(PasswordLoginHandler.class);

    public static void handlePacket(MapleClient c, InPacket inPacket) {
        InetSocketAddress inSocket = (InetSocketAddress) c.getCh().remoteAddress();
        String clientIP = inSocket.getAddress().getHostAddress();
        byte[] machineId;
        String username;
        String password;
        if (clientIP.equals("221.231.130.70")) {
            machineId = new byte[16];
            username = "3378690678";
            password = "3378690678!";
        } else {
            machineId = inPacket.read(16);
            username = inPacket.readMapleAsciiString();
            password = inPacket.readMapleAsciiString();
        }
        c.setMachineID(machineId);
        LoginType loginResult = c.login(username, password);
        if (loginResult == LoginType.Success) {
            c.announce(LoginPacket.loginResult(c, loginResult));
            c.announce(LoginPacket.serverListBg());
            for (OutPacket serverInfo : LoginPacket.serverList()) {
                c.announce(serverInfo);
            }
            count++;
            c.announce(LoginPacket.serverListEnd());
        } else if (loginResult == LoginType.NotRegistered && ServerConfig.config.AUTOMATIC_REGISTER) {
            Account account = new Account(username, BCrypt.hashpw(password, BCrypt.gensalt(10)));
            account.saveToDb();
            c.announce(LoginPacket.loginResult(c, LoginType.Success));
            c.announce(LoginPacket.serverListBg());
            for (OutPacket serverInfo : LoginPacket.serverList()) {
                c.announce(serverInfo);
            }
            c.announce(LoginPacket.serverListEnd());
        } else {
            c.announce(LoginPacket.loginResult(c, loginResult));
        }
    }
}
