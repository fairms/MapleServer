package im.cave.ms.scripting.map;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.scripting.AbstractScriptManager;
import jdk.nashorn.api.scripting.NashornScriptEngine;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.map
 * @date 11/27 21:54
 */
public class MapScriptManager extends AbstractScriptManager {
    private static final MapScriptManager instance = new MapScriptManager();

    public static MapScriptManager getInstance() {
        return instance;
    }

    private final Map<String, NashornScriptEngine> scripts = new HashMap<>();
    private ScriptEngineFactory sef;

    private MapScriptManager() {
        ScriptEngineManager sem = new ScriptEngineManager();
        sef = sem.getEngineByName("javascript").getFactory();
    }

    public void reloadScripts() {
        scripts.clear();
    }

    public boolean runMapScript(MapleClient c, String mapScriptPath, boolean firstUser) {
        if (firstUser) {
            MapleCharacter player = c.getPlayer();
            int mapId = player.getMapId();
            if (player.hasEntered(mapScriptPath, mapId)) {
                return false;
            } else {
                player.enteredScript(mapScriptPath, mapId);
            }
        }
        NashornScriptEngine nse = scripts.get(mapScriptPath);

        if (nse != null) {
            try {
                nse.invokeFunction("start", new MapScriptMethods(c));
                return true;
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            nse = getScriptEngine("map/" + mapScriptPath + ".js", c);
            if (nse == null) {
                return false;
            }
            scripts.put(mapScriptPath, nse);
            nse.invokeFunction("start", new MapScriptMethods(c));
            return true;
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }
}
