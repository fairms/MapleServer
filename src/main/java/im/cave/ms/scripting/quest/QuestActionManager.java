
package im.cave.ms.scripting.quest;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.npc.NpcConversationManager;

/**
 * @author RMZero213
 */
public class QuestActionManager extends NpcConversationManager {
    private final boolean start; // this is if the script in question is start or end
    private final int quest;
    private String script;

    public QuestActionManager(MapleClient c, int quest, int npc, boolean start, String script) {
        super(c, npc, null, script);
        this.quest = quest;
        this.start = start;
        this.script = script;
    }

    public int getQuest() {
        return quest;
    }

    public boolean isStart() {
        return start;
    }

    @Override
    public void dispose() {
        QuestScriptManager.getInstance().dispose(getClient());
    }

    public boolean forceStartQuest() {
        return forceStartQuest(quest);
    }

    public boolean forceCompleteQuest() {
        return forceCompleteQuest(quest);
    }

    // For compatibility with some older scripts...
    public void startQuest() {
        forceStartQuest();
    }

    // For compatibility with some older scripts...
    public void completeQuest() {
        forceCompleteQuest();
    }

    @Override
    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
