package im.cave.ms.connection.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.MapleMessage;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.MapleMessageType;
import im.cave.ms.enums.WhisperType;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet.opcode
 * @date 1/9 23:18
 */
public class MessagePacket {
    public static OutPacket getChatText(MapleCharacter player, String content) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHATTEXT.getValue());
        out.writeInt(player.getId());
        out.writeBool(player.isGm());
        out.writeMapleAsciiString(content);
        out.writeMapleAsciiString(player.getName());
        out.writeMapleAsciiString(content);
        out.writeLong(0);
        out.write(player.getWorld());
        out.writeInt(player.getId());
        out.write(3);
        out.write(1);
        out.write(-1);
        return out;
    }

    public static OutPacket whisper(WhisperType type, String destName, int status, int param) {
        OutPacket out = new OutPacket(SendOpcode.WHISPER);
        out.write(type.getVal());
        switch (type) {
            case Res_Find_Friend:
                out.writeMapleAsciiString(destName);
                out.write(status);
                out.writeInt(param);
                break;
            case Res_Whisper:
                out.writeMapleAsciiString(destName);
                out.write(status);
                break;
        }
        return out;
    }

    public static OutPacket setAvatarMegaphone(MapleCharacter chr, int itemId, List<String> lines, boolean whisperIcon) {
        OutPacket out = new OutPacket(SendOpcode.SET_AVATAR_MEGAPHONE);
        out.writeInt(itemId);
        out.writeMapleAsciiString(chr.getName());
        for (String line : lines) {
            out.writeMapleAsciiString(line);
        }
        String msg = String.join("", lines);
        out.writeMapleAsciiString(msg);
        out.writeInt(chr.getChannel()); //0
        out.writeInt(0);//0
        out.writeInt(0); // 01 01 00 00
        out.writeInt(0); // 0
        out.writeBool(whisperIcon);
        chr.getCharLook().encode(out);
        return out;
    }


    public static OutPacket mapleMessageResult(MapleMessageType type, List<MapleMessage> messages, int param) {
        OutPacket out = new OutPacket(SendOpcode.MAPLE_MESSAGE);
        out.write(type.getVal());
        switch (type) {
            case Res_Send_Success:
                break;
            case Res_Send_Fail:
                out.write(param);
                break;
            case Res_Add_Sent:
                MapleMessage msg = messages.get(0);
                msg.encode(out);
                break;
            case Res_Inbox:
            case Res_Outbox:
                out.write(messages.size());
                for (MapleMessage message : messages) {
                    message.encode(out);
                }
                break;
            case Res_Delete_Received_Success:
            case Res_Delete_Sent_Success:
                out.writeInt(1);
                out.writeInt(param);
                break;
            case Res_InMessage_Read:
                out.write(2);
                out.writeInt(1);
                out.writeInt(param);
                break;
        }
        return out;
    }
}
