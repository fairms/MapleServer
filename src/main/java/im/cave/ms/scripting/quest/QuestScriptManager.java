package im.cave.ms.scripting.quest;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.provider.data.QuestData;
import im.cave.ms.scripting.AbstractScriptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;


public class QuestScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(QuestScriptManager.class);
    private static final QuestScriptManager instance = new QuestScriptManager();
    private final Map<MapleClient, QuestActionManager> qms = new HashMap<>();

    public static QuestScriptManager getInstance() {
        return instance;
    }

    public void start(MapleClient c, String questName, int questId, int npc) {
        Quest quest = QuestData.createQuestFromId(questId);
        try {
            c.acquireScriptState();
            QuestActionManager qm = new QuestActionManager(c, questId, npc, true);
            if (qms.containsKey(c)) {
                return;
            }
            qms.put(c, qm);
            String scriptPath = String.format("quest/%s.js", questName);
            Invocable iv = getInvocable(scriptPath, c);
            if (iv == null) {
                c.getPlayer().dropMessage("任务:" + questId + "脚本不存在, NPC:" + npc + ", 地图:" + c.getPlayer().getMapId());
                qm.dispose();
                return;
            }
            engine.put("qm", qm);
            iv.invokeFunction("start");
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        } finally {
            dispose(c);
            c.releaseScriptState();
        }
    }


    public void dispose(MapleClient c) {
        if (c == null || c.getPlayer() == null) {
            return;
        }
        QuestActionManager qm = qms.get(c);
        if (qm != null) {
            qm.getNpcScriptInfo().reset();
            resetContext("quest/" + qm.getQuest() + ".js", c);
            qms.remove(c);
        }
        c.getPlayer().setConversation(false);
    }


    public QuestActionManager getQM(MapleClient c) {
        return qms.get(c);
    }

    public void reloadQuestScripts() {
        qms.clear();
    }
}
