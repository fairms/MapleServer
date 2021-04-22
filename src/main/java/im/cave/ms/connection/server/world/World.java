package im.cave.ms.connection.server.world;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.party.Party;
import im.cave.ms.client.multiplayer.party.PartyMember;
import im.cave.ms.client.multiplayer.party.PartyQuest;
import im.cave.ms.configs.Config;
import im.cave.ms.configs.WorldConfig;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.server.AbstractServer;
import im.cave.ms.connection.server.auction.Auction;
import im.cave.ms.connection.server.cashshop.CashShopServer;
import im.cave.ms.connection.server.channel.MapleChannel;
import im.cave.ms.enums.PartyQuestType;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer.worldId
 * @date 11/19 16:22
 */
public class World {
    private int id;
    private List<MapleChannel> channels = new ArrayList<>();
    private final Map<Integer, Party> parties = new HashMap<>();
    private final Map<Integer, Guild> guilds = new HashMap<>();
    private final Set<PartyQuest> partyQuests = new HashSet<>();
    private String title;
    private Integer partyCounter = 1;
    private CashShopServer cashShopServer;
    private Auction auction;
    private String eventMessage;
    private int dropRate;
    private int mesoRate;
    private int expRate;

    public World(int id, String eventMessage) {
        this.id = id;
        this.eventMessage = eventMessage;
        this.title = String.format("World-%d", id);
    }

    public World(int id, String eventMessage, String title) {
        this.id = id;
        this.eventMessage = eventMessage;
        this.title = title;
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

    public boolean init() {
        try {
            WorldConfig.WorldInfo info = Config.worldConfig.getWorldInfo(id);
            setDropRate(info.drop_rate);
            setExpRate(info.exp_rate);
            setMesoRate(info.meso_rate);
            setEventMessage(info.event_message);
            for (int i = 0; i < info.channels; i++) {
                MapleChannel channel = new MapleChannel(id, i);
                channels.add(channel);
            }
            cashShopServer = new CashShopServer(id);
            auction = new Auction(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        initGuilds();
        return channels.size() > 0;
    }

    private void initGuilds() {
        List<Guild> guilds = (List<Guild>) DataBaseManager.getObjListFromDB(Guild.class);
        for (Guild g : guilds) {
            addGuild(g);
        }
    }

    private void addGuild(Guild guild) {
        getGuilds().put(guild.getId(), guild);
    }

    public Map<Integer, Guild> getGuilds() {
        return guilds;
    }

    public Collection<Guild> getGuildsByString(int searchType, boolean exactWord, String searchTerm) {
        Collection<Guild> guilds = getGuilds().values();
        Set<Guild> res = new HashSet<>(guilds);
        for (Guild g : guilds) {
            if (searchType == 1) {
                String guildName = g.getName();
                String leaderName = g.getGuildLeader().getName();
                if ((exactWord && !guildName.equals(searchTerm) && !leaderName.equals(searchTerm)
                        || (!exactWord && !guildName.contains(searchTerm) && !leaderName.contains(searchTerm)))) {
                    res.remove(g);
                }
            } else {
                String name = searchType == 2
                        ? g.getName()
                        : searchType == 3
                        ? g.getGuildLeader().getName()
                        : "";
                if ((exactWord && !name.equals(searchTerm)) || (!exactWord && !name.contains(searchTerm))) {
                    res.remove(g);
                }
            }
        }
        return res;
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

    public MapleCharacter getCharById(int id) {
        MapleCharacter character = null;
        for (MapleChannel channel : channels) {
            character = channel.getCharById(id);
            if (character != null) {
                break;
            }
        }
        return character;
    }

    public boolean checkGuildName(String name) {
        int len = StringUtil.len(name);
        if (len < 4 || len > 13) {
            return false;
        }
        Guild guild = Util.findWithPred(guilds.values(), g -> g.getName().equalsIgnoreCase(name));
        return guild == null;
    }

    public Auction getAuction() {
        return auction;
    }

    public void addPartyQuest(PartyQuest partyQuest) {
        partyQuests.add(partyQuest);
    }

    public boolean hasInProgress(int channel, PartyQuestType type) {
        for (PartyQuest partyQuest : partyQuests) {
            if (partyQuest.getChannel() == channel &&
                    partyQuest.getType() == type) {
                List<PartyMember> memberInProgress = partyQuest.getParty().getOnlineMembers().stream().filter(m -> m.getPartyQuest() != null).collect(Collectors.toList());
                return memberInProgress.size() != 0;
            }
        }
        return false;
    }

    public void removePQ(PartyQuest partyQuest) {
        partyQuests.remove(partyQuest);
    }

    public int getDropRate() {
        return dropRate;
    }

    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    public int getMesoRate() {
        return mesoRate;
    }

    public void setMesoRate(int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public int getExpRate() {
        return expRate;
    }

    public void setExpRate(int expRate) {
        this.expRate = expRate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
