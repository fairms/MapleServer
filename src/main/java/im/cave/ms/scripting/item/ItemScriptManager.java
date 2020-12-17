package im.cave.ms.scripting.item;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.scripting.AbstractScriptManager;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.item
 * @date 12/13 22:42
 */
public class ItemScriptManager extends AbstractScriptManager {

    private static final Logger log = LoggerFactory.getLogger(ItemScriptManager.class);

    private static final ItemScriptManager instance = new ItemScriptManager();
    private final Map<String, NashornScriptEngine> scripts = new HashMap<>();

    public synchronized static ItemScriptManager getInstance() {
        return instance;
    }

    private NashornScriptEngine getItemScript(String scriptName) {
        String script = scriptName.replace("_", "/");
        String scriptPath = String.format("item/%s.js", script);
        NashornScriptEngine nse = scripts.get(scriptPath);
        if (nse != null) {
            return nse;
        }
        nse = getScriptEngine(scriptPath);
        if (nse == null) {
            return null;
        }
        scripts.put(scriptPath, nse);
        return nse;
    }

    public boolean startScript(int itemId, String script, int npcId, MapleClient c) {
        try {
            NashornScriptEngine nse = getItemScript(script);
            if (nse != null) {
                return (boolean) nse.invokeFunction("start", new ItemScriptAction(c, itemId, npcId));
            } else {
                MapleCharacter player = c.getPlayer();
                c.getPlayer().dropMessage("道具:" + itemId + " 脚本:" + script);
            }
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        }
        return false;
    }
}
