package im.cave.ms.network.server.login;

import im.cave.ms.enums.ServerType;
import im.cave.ms.network.netty.ServerAcceptor;
import im.cave.ms.network.server.AbstractServer;
import im.cave.ms.tools.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.login
 * @date 11/19 16:22
 */
public class LoginServer extends AbstractServer {
    private static final Logger log = LoggerFactory.getLogger(LoginServer.class);
    private List<Integer> accounts;
    private static LoginServer instance;
    private HashMap<String, Pair<String, Integer>> loginAuthKey = new HashMap<>();

    private LoginServer() {
        super(-1, -1);
        type = ServerType.LOGIN;
        port = 8484;
        acceptor = new ServerAcceptor(this);
        new Thread(acceptor).start();
        log.info("Login server listening on port {}", port);
    }

    public static LoginServer getInstance() {
        if (instance == null) {
            instance = new LoginServer();
        }
        return instance;
    }

    public HashMap<String, Pair<String, Integer>> getLoginAuthKey() {
        return loginAuthKey;
    }


    public void setLoginAuthKey(HashMap<String, Pair<String, Integer>> loginAuthKey) {
        this.loginAuthKey = loginAuthKey;
    }

    public void putLoginAuthKey(String key, String account, int channel) {
        loginAuthKey.put(key, new Pair<>(account, channel));
    }
}
