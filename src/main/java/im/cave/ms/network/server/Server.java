package im.cave.ms.network.server;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.config.Config;
import im.cave.ms.config.WorldConfig;
import im.cave.ms.network.server.cashshop.CashShopServer;
import im.cave.ms.network.server.channel.MapleChannel;
import im.cave.ms.network.server.login.LoginServer;
import im.cave.ms.network.server.world.World;
import im.cave.ms.scripting.map.MapScriptManager;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.scripting.portal.PortalScriptManager;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.server
 * @date 11/19 22:15
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static Server instance = null;

    public Server() {
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private final Map<Integer, Pair<Byte, MapleClient>> transfers = new HashMap<>();
    private final List<World> worlds = new ArrayList<>();
    private final Set<Integer> accounts = new HashSet<>();
    private LoginServer loginServer;
    private boolean online = false;
    private long serverCurrentTime = 0;
    private final long uptime = System.currentTimeMillis();

    public boolean isOnline() {
        return online;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public World getWorldById(int id) {
        return getWorlds().stream().filter(world -> world.getId() == id)
                .findAny().orElse(null);
    }

    public void init() {
        log.info("Starting server.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //todo
        }));

//        QuestData.loadQuests();

        loginServer = LoginServer.getInstance();
        for (WorldConfig.WorldInfo world : Config.worldConfig.worlds) {
            worlds.add(new World(world.id, world.event_message));
        }
        worldInit();
        online = true;
    }

    private void worldInit() {
        for (World world : worlds) {
            log.info("World-{} is starting... ", world.getId());
            world.init();
            log.info("World-{} is running... ", world.getId());
        }
    }

    public boolean isAccountLoggedIn(Account account) {
        return accounts.contains(account.getId());
    }

    public void addAccount(Account account) {
        accounts.add(account.getId());
    }

    public void removeAccount(Account account) {
        accounts.remove(account.getId());
    }

    public MapleChannel getChannel(byte world, byte channel) {
        return getWorldById(world).getChannel(channel);
    }

    public void addClientInTransfer(byte channel, int charId, MapleClient c) {
        transfers.put(charId, new Pair<>(channel, c));
    }

    public Pair<Byte, MapleClient> getClientTransInfo(int charId) {
        return transfers.getOrDefault(charId, null);
    }

    public void removeTransfer(int charId) {
        transfers.remove(charId);
    }


    public String onlinePlayer() {
        StringBuilder sb = new StringBuilder();
        for (World world : worlds) {
            for (MapleChannel channel : world.getChannels()) {
                sb.append("world-").append(world.getId()).append(" channel-").append(channel.getChannelId())
                        .append(" online:").append(channel.getPlayerCount()).append("        ");
            }
        }
        return sb.toString();
    }

    public CashShopServer getCashShop(byte world) {
        return getWorldById(world).getCashShop();
    }

    public void reloadScripts() {
        PortalScriptManager.getInstance().reloadPortalScripts();
        NpcScriptManager.getInstance().reloadNpcScripts();
        MapScriptManager.getInstance().reloadScripts();
        QuestScriptManager.getInstance().reloadQuestScripts();
    }

    public long getServerCurrentTime() {
        return serverCurrentTime;
    }

    public void setServerCurrentTime(long serverCurrentTime) {
        this.serverCurrentTime = serverCurrentTime;
    }

    public MapleCharacter getCharById(int charId, byte worldId) {
        World world = getWorldById(worldId);
        for (MapleChannel channel : world.getChannels()) {
            MapleCharacter player = channel.getPlayer(charId);
            if (player != null) {
                return player;
            }
        }
        return null;
    }
}
