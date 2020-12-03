package im.cave.ms.net.server;

import im.cave.ms.client.Account;
import im.cave.ms.client.items.Equip;
import im.cave.ms.config.WorldConfig;
import im.cave.ms.net.server.login.LoginServer;
import im.cave.ms.net.server.world.World;
import im.cave.ms.provider.data.ItemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private List<World> worlds = new ArrayList<>();
    private Set<Integer> accounts = new HashSet<>();
    private LoginServer loginServer;
    private boolean online = false;
    private long serverCurrentTime = 0;
    private long uptime = System.currentTimeMillis();

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

        }));

        Map<Integer, Equip> equips = ItemData.getEquips();
        LoginServer.getInstance();

        for (WorldConfig.WorldInfo world : WorldConfig.config.worlds) {
            worlds.add(new World(world.id, world.event_message));
        }
        worldInit();
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
}
