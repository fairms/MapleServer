package im.cave.ms.config;

import im.cave.ms.client.MapleSignIn;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.config
 * @date 11/19 23:30
 */
public class WorldConfig {

    public List<WorldInfo> worlds;
    public List<MapleSignIn.SignInRewardInfo> signin;

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