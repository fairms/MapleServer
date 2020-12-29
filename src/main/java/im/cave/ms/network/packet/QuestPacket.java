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
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(MessageType.QUEST_RECORD_MESSAGE.getVal());
        outPacket.writeInt(quest.getQrKey());
        QuestStatus state = quest.getStatus();
        outPacket.write(state.getVal());
        switch (state) {
            case NotStarted:
                outPacket.write(0); // If quest is completed, but should never be true?
                break;
            case Started:
                outPacket.writeMapleAsciiString(quest.getQRValue());
                break;
            case Completed:
                outPacket.writeLong(quest.getCompletedTime());
                break;
        }
        return outPacket;
    }

    public static OutPacket questResult(QuestType type, int questId, int npcTemplateId, int secondQuestID, boolean startNavigation) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.QUEST_RESULT.getValue());
        outPacket.write(type.getVal());
        outPacket.writeInt(questId);
        outPacket.writeInt(npcTemplateId);
        outPacket.writeInt(secondQuestID);
        outPacket.writeBool(startNavigation);
        return outPacket;
    }

    public static OutPacket updateQuestEx(int questId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.UPDATE_QUEST_EX.getValue());
        outPacket.writeInt(questId);
        return outPacket;
    }
}
