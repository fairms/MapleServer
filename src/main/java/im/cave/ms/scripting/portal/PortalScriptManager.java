package im.cave.ms.scripting.portal;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Portal;
import im.cave.ms.scripting.AbstractScriptManager;

import javax.script.Invocable;
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

    private final Map<String, Invocable> scripts = new HashMap<>();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    private PortalScriptManager() {
        super();
    }

    private Invocable getPortalScript(String scriptName, MapleClient c) {
        String scriptPath = String.format("portal/%s.js", scriptName);
        Invocable nse = scripts.get(scriptPath);
        if (nse != null) {
            return nse;
        }
        nse = getInvocable(scriptPath, c);
        if (nse == null) {
            return null;
        }
        scripts.put(scriptPath, nse);
        return nse;
    }

    public boolean executePortalScript(Portal portal, MapleClient client) {
        try {
            Invocable nse = getPortalScript(portal.getScript(), client);
            if (nse != null) {
                return (boolean) nse.invokeFunction("enter", new PortalPlayerInteraction(client, portal));
            } else {
                MapleCharacter player = client.getPlayer();
                client.getPlayer().dropMessage("地图:" + player.getMapId() + " 传送口:" + portal.getScript());
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
