package im.cave.ms.network.server.login.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.LoginType;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.server.Server;
import im.cave.ms.network.server.login.LoginServer;
import im.cave.ms.tools.Pair;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CharlistRequestHandler {
    public static void handlePacket(InPacket inPacket, MapleClient c) {
        inPacket.skip(1);
        int worldId = inPacket.readByte();
        int channel = inPacket.readByte();
        c.setWorld(worldId);
        c.setChannel(channel);
        if (c.getLoginStatus() == LoginStatus.NOTLOGGEDIN && inPacket.available() == 284) {
            inPacket.skip(2);
            String key = inPacket.readMapleAsciiString();
            LoginServer instance = LoginServer.getInstance();
            if (instance.getLoginAuthKey().containsKey(key)) {
                Pair<String, Integer> pair = instance.getLoginAuthKey().get(key);
                Account account = (Account) DataBaseManager.getObjFromDB(Account.class, "account", pair.getLeft());
                c.setAccount(account);
                c.setChannel(pair.getRight());
                Server.getInstance().addAccount(account);
                c.setLoginStatus(LoginStatus.LOGGEDIN);
                c.announce(LoginPacket.authSuccess(c));
            }
        } else if (c.getLoginStatus() == LoginStatus.NOTLOGGEDIN) {
            c.announce(LoginPacket.loginResult(c, LoginType.AuthFail));
            return;
        }
        List<MapleCharacter> characters = c.loadCharacters(worldId, false);
        c.announce(LoginPacket.charList(c, characters, 0));
        c.announce(LoginPacket.account(c.getAccount()));
    }
}