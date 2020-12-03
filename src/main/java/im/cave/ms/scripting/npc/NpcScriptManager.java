package im.cave.ms.scripting.npc;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.AbstractScriptManager;
import jdk.nashorn.api.scripting.NashornScriptEngine;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.npc
 * @date 11/30 17:30
 */
public class NpcScriptManager extends AbstractScriptManager {
    private static final NpcScriptManager instance = new NpcScriptManager();
    private final Map<MapleClient, NpcConversationManager> cms = new WeakHashMap<>();
    private final Map<MapleClient, NashornScriptEngine> scripts = new HashMap<>();

    public synchronized static NpcScriptManager getInstance() {
        return instance;
    }


    public boolean isNpcScriptAvailable(MapleClient c, String fileName) {
        NashornScriptEngine iv = null;
        if (fileName != null) {
            iv = getScriptEngine("npc/" + fileName + ".js", c);
        }

        return iv != null;
    }


    public void start(MapleClient c, int npcId, String script) {
        if (c.getPlayer().isConversation()) {
            return;
        }
        try {
            if (cms.containsKey(c)) {
                dispose(c);
                return;
            }
            NashornScriptEngine nse;
            if (scripts.containsKey(c)) {
                nse = scripts.get(c);
            } else {
                nse = getScriptEngine("npc/" + script + ".js");
            }
            if (nse == null) {
                dispose(c);
                return;
            }
            NpcConversationManager ncm = new NpcConversationManager(c, npcId, this);
            cms.put(c, ncm);
            nse.put("cm", ncm);
            c.getPlayer().setConversation(true);
            scripts.put(c, nse);
            try {
                nse.invokeFunction("start");
            } catch (NoSuchMethodException e) {
                nse.invokeFunction("action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dispose(c);
        }
    }


    public void action(MapleClient c, byte mode, byte type, int selection) {
        NashornScriptEngine iv = scripts.get(c);
        if (iv != null) {
            try {
                iv.invokeFunction("action", mode, type, selection);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
                dispose(c);
            }
        }
    }


    public void dispose(MapleClient c) {
        NpcConversationManager cm = cms.get(c);
        scripts.remove(c);
        if (cm != null) {
            cm.getNpcScriptInfo().reset();
            cms.remove(c);
        }
        c.getPlayer().setConversation(false);
    }

    public NpcConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }

}
