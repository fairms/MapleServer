package im.cave.ms.scripting.map;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.AbstractPlayerInteraction;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.map
 * @date 11/27 22:07
 */
public class MapScriptMethods extends AbstractPlayerInteraction {

    public MapScriptMethods(MapleClient c) {
        super(c);
    }


    public void setMobCapacity(int capacity) {
        c.getPlayer().getMap().setFixedMobCapacity(capacity);
    }

    public void generateMobs(boolean init) {
        c.getPlayer().getMap().generateMobs(init);
    }
}
