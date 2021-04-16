package im.cave.ms.connection.server;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.configs.Config;
import im.cave.ms.configs.WorldConfig;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.server.cashshop.CashShopServer;
import im.cave.ms.connection.server.channel.MapleChannel;
import im.cave.ms.connection.server.login.LoginServer;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.connection.server.world.World;
import im.cave.ms.provider.data.*;
import im.cave.ms.tools.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

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

    public static void main(String[] args) {
        getInstance().init();
        Runtime rt = Runtime.getRuntime();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                rt.exec("gsudo netsh int i add addr 1 221.231.130.70");
            } catch (IOException ignored) {

            }
        }
    }

    private void initDataProvider() {
        System.out.println("Begin DataProvider Init");
        StringData.init();
        MobData.init();
        ItemData.init();
        NpcData.loadNpcDataFromWz();
        QuestData.loadQuests();
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

    public CashShopServer getCashShop(byte world) {
        return getWorldById(world).getCashShop();
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

    public MapleCharacter findCharByName(String name, byte worldId) {
        World world = getWorldById(worldId);
        for (MapleChannel channel : world.getChannels()) {
            MapleCharacter player = channel.getPlayer(name);
            if (player != null) {
                return player;
            }
        }
        return null;
    }


    public LoginServer getLoginServer() {
        return loginServer;
    }

    public void setLoginServer(LoginServer loginServer) {
        this.loginServer = loginServer;
    }

    public void setOnline(boolean on) {
        online = on;
    }

    public void init() {
        log.info("开始启动服务器.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

        }));
        DataBaseManager.init();
        loginServer = LoginServer.getInstance();
        for (WorldConfig.WorldInfo worldInfo : Config.worldConfig.worlds) {
            log.info("世界-{} 开始启动", worldInfo.id);
            World world = new World(worldInfo.id, worldInfo.event_message);
            if (world.init()) {
                worlds.add(world);
                for (MapleChannel channel : world.getChannels()) {
                    log.info("频道-{} 监听端口：{}", channel.getChannelId(), channel.getPort());
                }
                log.info("拍卖行 监听端口:{}", world.getAuction().getPort());
                log.info("商城 监听端口:{}", world.getCashShop().getPort());
            } else {
                log.info("世界-{} 启动失败", world.getId());
                return;
            }
        }
        setOnline(true);
        //加载WZ
        EventManager.addEvent(this::initDataProvider, 0);
    }
}
