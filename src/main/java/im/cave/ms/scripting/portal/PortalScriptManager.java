package im.cave.ms.scripting.portal;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.Portal;
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
 * @Package im.cave.ms.scripting.portal
 * @date 11/28 14:16
 */
public class PortalScriptManager extends AbstractScriptManager {
    private static final PortalScriptManager instance = new PortalScriptManager();

    private Map<String, NashornScriptEngine> scripts = new HashMap<>();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    private ScriptEngineFactory sef;

    private PortalScriptManager() {
        ScriptEngineManager sem = new ScriptEngineManager();
        sef = sem.getEngineByName("javascript").getFactory();
    }

    private NashornScriptEngine getPortalScript(String scriptName) {
        String scriptPath = String.format("portal/%s.js", scriptName);
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

    public boolean executePortalScript(Portal portal, MapleClient client) {
        try {
            NashornScriptEngine nse = getPortalScript(portal.getScript());
            if (nse != null) {
                return (boolean) nse.invokeFunction("enter", new PortalPlayerInteraction(client, portal));
            }
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void reloadPortalScripts() {
        scripts.clear();
    }
}
