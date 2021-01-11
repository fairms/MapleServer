package im.cave.ms.network.server.channel;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.netty.ServerAcceptor;
import im.cave.ms.network.server.AbstractServer;
import im.cave.ms.provider.data.MapData;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.channel
 * @date 11/19 16:22
 */
public class MapleChannel extends AbstractServer {
    private static final Logger log = LoggerFactory.getLogger(MapleChannel.class);
    private final List<MapleCharacter> players = new ArrayList<>();
    private final List<MapleMap> maps;


    public MapleChannel(int worldId, int channelId) {
        super(worldId, channelId);
        type = ServerType.CHANNEL;
        port = 7575 + worldId * 100 + channelId;
        acceptor = new ServerAcceptor(this);
        new Thread(acceptor).start();
        maps = new CopyOnWriteArrayList<>();
    }

    public void addPlayer(MapleCharacter player) {
        if (players.contains(player)) {
            return;
        }
        players.add(player);
    }

    public MapleCharacter getPlayer(int charId) {
        return players.stream().filter(character -> character.getId() == charId).findAny().orElse(null);
    }

    public MapleCharacter getPlayer(String name) {
        return players.stream().filter(character -> character.getName().equals(name)).findAny().orElse(null);
    }

    public Integer getPlayerCount() {
        return players.size();
    }

    public int getChannelCapacity() {
        return (int) (Math.ceil(((float) players.size() / 100) * 500));
    }

    public void removePlayer(MapleCharacter player) {
        players.removeIf(character -> character.getAccId() == player.getAccId());
    }

    public MapleMap getMap(int mapId) {
        MapleMap map = maps.stream().filter(m -> m.getId() == mapId).findAny().orElse(null);
        if (map == null) {
            map = MapData.loadMapDataFromWz(mapId, worldId, channelId);
            if (map == null) {
                return getMap(100000000);
            }
            maps.add(map);
        }
        return map;
    }

    public List<MapleMap> getMaps() {
        return maps;
    }

    public void broadcast(OutPacket out) {
        for (MapleMap map : maps) {
            map.broadcastMessage(out);
        }
    }

    public MapleCharacter getCharByName(String charName) {
        return Util.findWithPred(players, character -> character.getName().equals(charName));
    }

    public MapleCharacter getCharById(int id) {
        return Util.findWithPred(players, character -> character.getId() == id);
    }
}
