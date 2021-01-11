package im.cave.ms.scripting.npc;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.AbstractScriptManager;

import javax.script.Invocable;
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

    public synchronized static NpcScriptManager getInstance() {
        return instance;
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
            String scriptPath = String.format("npc/%s.js", script);
            Invocable iv = getInvocable(scriptPath, c);
            if (iv == null) {
                c.getPlayer().dropMessage("NPC: " + npcId + " " + script + " 脚本不存在 地图:" + c.getPlayer().getMapId());
                dispose(c);
                return;
            }
            NpcConversationManager cm = new NpcConversationManager(c, npcId, this, scriptPath);
            cms.put(c, cm);
            engine.put("cm", cm);
            c.getPlayer().setConversation(true);
            iv.invokeFunction("start");
        } catch (Exception e) {
            e.printStackTrace();
            dispose(c);
        }
    }

    public void dispose(MapleClient c) {
        NpcConversationManager cm = cms.get(c);
        if (cm != null) {
            cm.getNpcScriptInfo().reset();
//            resetContext(cm.getScript(), c);
            cms.remove(c);
        }
        c.getPlayer().setConversation(false);
    }

    public NpcConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }

    public void reloadNpcScripts() {
        cms.clear();
    }
}
