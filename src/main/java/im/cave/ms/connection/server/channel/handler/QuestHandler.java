package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.QuestPacket;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.constants.QuestConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.QuestType;
import im.cave.ms.provider.data.QuestData;
import im.cave.ms.provider.info.QuestInfo;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.server.channel.handler
 * @date 12/9 17:04
 */
public class QuestHandler {
    private static final Logger log = LoggerFactory.getLogger(QuestHandler.class);

    public static void handleQuestRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        QuestManager questManager = player.getQuestManager();
        QuestType type = QuestType.getType(in.readByte());
        int questId = 0;
        int npcTemplateId = 0;
        Position position = null;
        boolean success = false;
        if (type != null) {
            switch (type) {
                case QuestReq_AcceptQuest: // Quest start
                case QuestReq_CompleteQuest: // Quest end
                case QuestReq_OpeningScript: // Scripted quest start
                case QuestReq_CompleteScript: // Scripted quest end
                    questId = in.readInt();
                    npcTemplateId = in.readInt();
                    if (in.available() > 4) {
                        position = in.readPosition();
                    }
                    break;
                case QuestReq_ResignQuest: //Quest forfeit
                    questId = in.readInt();
                    player.getQuestManager().removeQuest(questId);
                    break;
                case QuestReq_LaterStep:
                    questId = in.readInt();
                    break;
                default:
                    log.error(String.format("Unhandled quest request %s!", type));
                    break;
            }
        }

        if (type == null || questId == 0) {
            player.chatMessage(ChatType.Tip, String.format("Could not find quest %d.", questId));
            return;
        }
        QuestInfo questInfo = QuestData.getQuestInfo(questId);
        switch (type) {
            case QuestReq_AcceptQuest:
                if (questManager.canStartQuest(questId)) {
                    questManager.addQuest(QuestData.createQuestFromId(questId));
                    success = true;
                }
                break;
            case QuestReq_CompleteQuest:
                if (questManager.hasQuestInProgress(questId)) {
                    Quest quest = questManager.getQuests().get(questId);
                    if (quest.isComplete(player)) {
                        questManager.completeQuest(questId);
                        success = true;
                    }
                }
                break;
            case QuestReq_OpeningScript:
                String scriptName = questInfo.getStartScript();
                if (scriptName == null || scriptName.equalsIgnoreCase("")) {
                    scriptName = String.format("%d%s", questId, QuestConstants.QUEST_START_SCRIPT_END_TAG);
                }
            {
                String finalScriptName = scriptName;
                int finalQuestId = questId;
                int finalNpcTemplateId = npcTemplateId;
                EventManager.addEvent(() -> QuestScriptManager.getInstance().start(c, finalScriptName, finalQuestId, finalNpcTemplateId), 0);
            }
            break;
            case QuestReq_CompleteScript:
                scriptName = questInfo.getEndScript();
                if (scriptName == null || scriptName.equalsIgnoreCase("")) {
                    scriptName = String.format("%d%s", questId, QuestConstants.QUEST_COMPLETE_SCRIPT_END_TAG);
                }
            {
                String finalScriptName = scriptName;
                int finalQuestId = questId;
                int finalNpcTemplateId = npcTemplateId;
                EventManager.addEvent(() -> QuestScriptManager.getInstance().start(c, finalScriptName, finalQuestId, finalNpcTemplateId), 0);
                break;
            }
            case QuestReq_LaterStep:
                if (questInfo != null && questInfo.getTransferField() != 0) {
                    player.changeMap(questInfo.getTransferField());
                }
                break;
        }
        if (success) {
            player.announce(QuestPacket.questResult(QuestType.QuestRes_Act_Success, questId, npcTemplateId, 0, false));
        }
    }
}
