package im.cave.ms.scripting.map;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.AbstractScriptManager;

import javax.script.Invocable;
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
    private final Map<String, Invocable> scripts = new HashMap<>();


    public static MapScriptManager getInstance() {
        return instance;
    }

    private MapScriptManager() {
        super();
    }


    public void reloadScripts() {
        scripts.clear();
    }

    public boolean runMapScript(MapleClient c, String mapScriptPath) {
        Invocable iv = scripts.get(mapScriptPath);

        if (iv != null) {
            try {
                iv.invokeFunction("start", new MapScriptMethods(c));
                return true;
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            iv = getInvocable("map/" + mapScriptPath + ".js", c);
            if (iv == null) {
                return false;
            }
            scripts.put(mapScriptPath, iv);
            engine.put("ms", new MapScriptMethods(c));
            iv.invokeFunction("start");
            return true;
        } catch (ScriptException | NoSuchMethodException e) {
            log.error("{} 发生错误", mapScriptPath);
            e.printStackTrace();
        }
        return false;
    }
}
