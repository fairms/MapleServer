package im.cave.ms.net.packet;

import im.cave.ms.client.quest.Quest;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.QuestStatus;
import im.cave.ms.enums.QuestType;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 11/20 21:59
 */
public class QuestPacket {
    public static MaplePacketLittleEndianWriter questRecordMessage(Quest quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(MessageType.QUEST_RECORD_MESSAGE.getVal());
        mplew.writeInt(quest.getQrKey());
        QuestStatus state = quest.getStatus();
        mplew.write(state.getVal());
        switch (state) {
            case NotStarted:
                mplew.write(0); // If quest is completed, but should never be true?
                break;
            case Started:
                mplew.writeMapleAsciiString(quest.getQrValue());
                break;
            case Completed:
                mplew.writeLong(quest.getCompletedTime());
                break;
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter questResult(QuestType type, int questId, int npcTemplateId, int secondQuestID, boolean startNavigation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.QUEST_RESULT.getValue());
        mplew.write(type.getVal());
        mplew.writeInt(questId);
        mplew.writeInt(npcTemplateId);
        mplew.writeInt(secondQuestID);
        mplew.writeBool(startNavigation);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter showEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.EFFECT.getValue());
        mplew.write(effect);
        return mplew;
    }
}
