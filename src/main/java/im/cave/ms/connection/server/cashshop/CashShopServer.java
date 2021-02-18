package im.cave.ms.connection.server.cashshop;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.CashShopItem;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.netty.ServerAcceptor;
import im.cave.ms.connection.server.AbstractServer;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.ServerType;
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

    {
        initCashItemCounter();
    }


    public CashShopServer(int worldId) {
        super(worldId, 20);
        type = ServerType.CASHSHOP;
        port = ServerConstants.CASH_SHOP_PORT;
        acceptor = new ServerAcceptor(this);
        acceptor.server = this;
        new Thread(acceptor).start();
    }

    private void initCashItemCounter() {
        try (Session session = DataBaseManager.getSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger count = (BigInteger) session.createSQLQuery("SELECT MAX(sn) FROM maple.item").uniqueResult();
            transaction.commit();
            session.close();
            if (count != null) {
                cashItemCounter = new AtomicInteger(count.intValue() + 1);
            } else {
                cashItemCounter = new AtomicInteger();
            }
        }
    }

    public void addChar(MapleCharacter character) {
        characters.add(character);
    }

    public void removeChar(MapleCharacter character) {
        characters.remove(character);
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
