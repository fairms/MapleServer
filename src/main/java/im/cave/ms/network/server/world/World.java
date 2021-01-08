package im.cave.ms.network.server.world;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.social.party.Party;
import im.cave.ms.configs.Config;
import im.cave.ms.configs.WorldConfig;
import im.cave.ms.network.server.cashshop.CashShopServer;
import im.cave.ms.network.server.channel.MapleChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.world
 * @date 11/19 16:22
 */
public class World {
    private int id;
    private List<MapleChannel> channels = new ArrayList<>();
    private final Map<Integer, Party> parties = new HashMap<>();
    private Integer partyCounter = 1;
    private CashShopServer cashShopServer;
    private String eventMessage;

    public World(int id, String eventMessage) {
        this.id = id;
        this.eventMessage = eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MapleChannel> getChannels() {
        return channels;
    }

    public MapleChannel getChannel(int id) {
        return channels.stream().filter(channel -> channel.getChannelId() == id).findAny().orElse(channels.get(0));
    }

    public void setChannels(List<MapleChannel> channels) {
        this.channels = channels;
    }

    public int getChannelsSize() {
        return channels.size();
    }

    public int getPartyIdAndIncrement() {
        return partyCounter++;
    }

    public void addParty(Party party) {
        int id = getPartyIdAndIncrement();
        parties.put(id, party);
        party.setId(id);
        if (party.getWorld() == null) {
            party.setWorld(this);
        }
    }

    //todo fix
    public boolean init() {
        try {
            WorldConfig.WorldInfo info = Config.worldConfig.getWorldInfo(id);
            for (int i = 0; i < info.channels; i++) {
                MapleChannel channel = new MapleChannel(id, i);
                channels.add(channel);
            }
            cashShopServer = new CashShopServer(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return channels.size() > 0;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public CashShopServer getCashShop() {
        return cashShopServer;
    }

    public void removeParty(Party party) {
        parties.remove(party.getId(), party);
    }

    public Party getPartyById(int id) {
        return parties.getOrDefault(id, null);
    }

    public MapleCharacter getCharByName(String charName) {
        MapleCharacter character = null;
        for (MapleChannel channel : channels) {
            character = channel.getCharByName(charName);
            if (character != null) {
                break;
            }
        }
        return character;
    }
}
