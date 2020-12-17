package im.cave.ms.network.server.cashshop;

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
    private static CashShopServer instance;

    public CashShopServer(int worldId) {
        super(worldId, -1);
    }

}
