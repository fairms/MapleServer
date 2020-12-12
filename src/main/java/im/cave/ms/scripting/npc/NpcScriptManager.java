package im.cave.ms.scripting.npc;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.AbstractScriptManager;
import jdk.nashorn.api.scripting.NashornScriptEngine;

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
    private final Map<String, NashornScriptEngine> scripts = new HashMap<>();

    public synchronized static NpcScriptManager getInstance() {
        return instance;
    }


    public boolean isNpcScriptAvailable(MapleClient c, String fileName) {
        NashornScriptEngine iv = null;
        if (fileName != null) {
            iv = getScriptEngine("npc/" + fileName + ".js");
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
            String scriptPath = String.format("npc/%s.js", script);
            if (scripts.containsKey(scriptPath)) {
                nse = scripts.get(scriptPath);
            } else {
                nse = getScriptEngine("npc/" + script + ".js");
                scripts.put(scriptPath, nse);
            }
            if (nse == null) {
                c.getPlayer().dropMessage("NPC: " + npcId + " " + script + " 脚本不存在 地图:" + c.getPlayer().getMapId());
                dispose(c);
                return;
            }
            NpcConversationManager ncm = new NpcConversationManager(c, npcId, this);
            cms.put(c, ncm);
            nse.put("cm", ncm);
            c.getPlayer().setConversation(true);
            nse.invokeFunction("start");
        } catch (Exception e) {
            e.printStackTrace();
            dispose(c);
        }
    }

    public void dispose(MapleClient c) {
        NpcConversationManager cm = cms.get(c);
        if (cm != null) {
            cm.getNpcScriptInfo().reset();
            cms.remove(c);
        }
        c.getPlayer().setConversation(false);
    }

    public NpcConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }

    public void reloadNpcScripts() {
        scripts.clear();
        cms.clear();
    }
}
