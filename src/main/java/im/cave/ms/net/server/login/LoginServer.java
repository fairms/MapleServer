package im.cave.ms.net.server.login;

import im.cave.ms.net.netty.ServerAcceptor;
import im.cave.ms.net.server.AbstractServer;
import im.cave.ms.enums.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private LoginServer() {
        super(-1, -1);
        type = ServerType.LOGIN;
        port = 8484;
        acceptor = new ServerAcceptor();
        acceptor.server = this;
        new Thread(acceptor).start();
        log.info("Login server listening on port {}", port);
    }

    public static LoginServer getInstance() {
        if (instance == null) {
            instance = new LoginServer();
        }
        return instance;
    }
}
