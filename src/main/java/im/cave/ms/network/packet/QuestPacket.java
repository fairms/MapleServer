package im.cave.ms.network.packet;

import im.cave.ms.client.quest.Quest;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.QuestStatus;
import im.cave.ms.enums.QuestType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 11/20 21:59
 */
public class QuestPacket {

    public static OutPacket questRecordMessage(Quest quest) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        out.write(MessageType.QUEST_RECORD_MESSAGE.getVal());
        out.writeInt(quest.getQrKey());
        QuestStatus state = quest.getStatus();
        out.write(state.getVal());
        switch (state) {
            case NotStarted:
                out.write(0); // If quest is completed, but should never be true?
                break;
            case Started:
                out.writeMapleAsciiString(quest.getQRValue());
                break;
            case Completed:
                out.writeLong(quest.getCompletedTime());
                break;
        }
        return out;
    }

    public static OutPacket questResult(QuestType type, int questId, int npcTemplateId, int secondQuestID, boolean startNavigation) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.QUEST_RESULT.getValue());
        out.write(type.getVal());
        out.writeInt(questId);
        out.writeInt(npcTemplateId);
        out.writeInt(secondQuestID);
        out.writeBool(startNavigation);
        return out;
    }

    public static OutPacket updateQuestEx(int questId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.UPDATE_QUEST_EX.getValue());
        out.writeInt(questId);
        return out;
    }
}
