package im.cave.ms.connection.server.auction;

import im.cave.ms.connection.netty.ServerAcceptor;
import im.cave.ms.connection.server.AbstractServer;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.ServerType;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.auction
 * @date 11/19 16:23
 */
public class Auction extends AbstractServer {


    public Auction(int worldId) {
        super(worldId, 21);
        type = ServerType.AUCTION;
        port = ServerConstants.AUCTION_PORT;
        acceptor = new ServerAcceptor(this);
        acceptor.server = this;
        new Thread(acceptor).start();
    }
}
