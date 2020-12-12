package im.cave.ms.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import im.cave.ms.MsApplication;
import im.cave.ms.client.MapleSignIn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.config
 * @date 11/19 23:30
 */
public class WorldConfig {

    public static WorldConfig config = loadConfig("world-config.yml");

    public List<WorldInfo> worlds;
    public List<MapleSignIn.SignInRewardInfo> signin;

    public static WorldConfig loadConfig(String filename) {
        try {
            URL resource = MsApplication.class.getClassLoader().getResource(filename);
            YamlReader reader = new YamlReader(new FileReader(resource.getFile()));
            WorldConfig config = reader.read(WorldConfig.class);
            reader.close();
            return config;
        } catch (FileNotFoundException e) {
            String message = "Could not read config file " + filename + ": " + e.getMessage();
            throw new RuntimeException(message);
        } catch (IOException e) {
            e.printStackTrace();
            String message = "Could not successfully parse config file " + filename + ": " + e.getMessage();
            throw new RuntimeException(message);
        }
    }

    public static void reload() {
        config = loadConfig("world-config.yml");
    }

    public WorldInfo getWorldInfo(int worldId) {
        return worlds.stream().filter(world -> world.id == worldId).findAny().orElse(null);
    }

    public List<MapleSignIn.SignInRewardInfo> getSignInRewards() {
        return signin;
    }

    public static class WorldInfo {
        public int id = 0;
        public int flag = 0;
        public String server_message = "Welcome!";
        public String event_message = "";
        public int channels = 1;
        public int exp_rate = 1;
        public int meso_rate = 1;
        public int drop_rate = 1;
        public int boss_drop_rate = 1;
        public int quest_rate = 1;
        public int travel_rate = 1;
        public int fishing_rate = 1;
    }
}
