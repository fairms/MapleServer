package im.cave.ms.network.server.cashshop;

import im.cave.ms.client.cashshop.CashShopItem;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.network.netty.ServerAcceptor;
import im.cave.ms.network.server.AbstractServer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.cashshop
 * @date 11/19 16:23
 */
public class CashShopServer extends AbstractServer {
    private static final Logger log = LoggerFactory.getLogger(CashShopServer.class);
    private List<MapleCharacter> characters = new ArrayList<>();
    private Map<Integer, CashShopItem> modifiedItems = new HashMap<>();
    private Map<Integer, Byte> hotItems = new HashMap<>();
    private AtomicInteger cashItemCounter;

    public CashShopServer(int worldId) {
        super(worldId, -1);
        type = ServerType.CASHSHOP;
        port = 8480;
        acceptor = new ServerAcceptor(this);
        acceptor.server = this;
        new Thread(acceptor).start();
        if (acceptor.isOnline()) {
            log.info("CashShop listening on port {}", port);
            loadCashItemCounter();
        } else {
            log.error("CashShop 运行失败");
        }
    }

    private void loadCashItemCounter() {
        try (Session session = DataBaseManager.getSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger count = (BigInteger) session.createSQLQuery("SELECT MAX(sn) FROM maple.items").uniqueResult();
            transaction.commit();
            session.close();
            if (count != null) {
                cashItemCounter = new AtomicInteger(count.intValue());
            } else {
                cashItemCounter = new AtomicInteger();
            }
            log.info("Current cash item sn : {}", cashItemCounter.get());
        }

    }

    public void addChar(MapleCharacter character) {
        characters.add(character);
    }

    public Map<Integer, CashShopItem> getModifiedItems() {
        return modifiedItems;
    }

    public void setModifiedItems(Map<Integer, CashShopItem> modifiedItems) {
        this.modifiedItems = modifiedItems;
    }

    public void setCharacters(List<MapleCharacter> characters) {
        this.characters = characters;
    }

    public Map<Integer, Byte> getHotItems() {
        return hotItems;
    }

    public void setHotItems(Map<Integer, Byte> hotItems) {
        this.hotItems = hotItems;
    }

    public long getNextSerialNumber() {
        return cashItemCounter.getAndIncrement();
    }
}
