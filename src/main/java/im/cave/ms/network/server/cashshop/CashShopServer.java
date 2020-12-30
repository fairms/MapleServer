package im.cave.ms.network.server.cashshop;

import im.cave.ms.client.cashshop.CashShopItem;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.netty.ServerAcceptor;
import im.cave.ms.network.server.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public CashShopServer(int worldId) {
        super(worldId, -1);
        type = ServerType.CASHSHOP;
        port = 8480;
        acceptor = new ServerAcceptor(this);
        acceptor.server = this;
        new Thread(acceptor).start();
        log.info("CashShop listening on port {}", port);
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

}
