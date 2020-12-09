package im.cave.ms.net.server.login.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.config.ServerConfig;
import im.cave.ms.enums.LoginType;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:38
 */
public class PasswordLoginHandler {
    private static final Logger log = LoggerFactory.getLogger(PasswordLoginHandler.class);

    public static void handlePacket(MapleClient c, SeekableLittleEndianAccessor slea) {
        String username = "3378690678";
        String password = "3378690678!";
        c.setMachineID(new byte[16]);
        LoginType loginResult = c.login(username, password);
        if (loginResult == LoginType.Success) {
            c.announce(LoginPacket.loginResult(c, loginResult));
            c.announce(LoginPacket.serverListBg());
            for (MaplePacketLittleEndianWriter serverInfo : LoginPacket.serverList()) {
                c.announce(serverInfo);
            }
            c.announce(LoginPacket.serverListEnd());
        } else if (loginResult == LoginType.NotRegistered && ServerConfig.config.AUTOMATIC_REGISTER) {
            Account account = new Account(username, BCrypt.hashpw(password, BCrypt.gensalt(10)));
            account.saveToDb();
            c.announce(LoginPacket.loginResult(c, LoginType.Success));
            c.announce(LoginPacket.serverListBg());
            for (MaplePacketLittleEndianWriter serverInfo : LoginPacket.serverList()) {
                c.announce(serverInfo);
            }
            c.announce(LoginPacket.serverListEnd());
        } else {
            c.announce(LoginPacket.loginResult(c, loginResult));
        }
    }
}
