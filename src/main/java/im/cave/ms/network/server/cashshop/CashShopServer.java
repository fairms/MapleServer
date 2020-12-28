package im.cave.ms.network.server.cashshop;

import im.cave.ms.enums.ServerType;
import im.cave.ms.network.netty.ServerAcceptor;
import im.cave.ms.network.server.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.cashshop
 * @date 11/19 16:23
 */
public class CashShopServer extends AbstractServer {
    private static final Logger log = LoggerFactory.getLogger(CashShopServer.class);
    private List<Integer> accounts;

    public CashShopServer(int worldId) {
        super(worldId, -1);
        type = ServerType.CASHSHOP;
        port = 8480;
        acceptor = new ServerAcceptor();
        acceptor.server = this;
        new Thread(acceptor).start();
        log.info("CashShop listening on port {}", port);
    }
    
}
