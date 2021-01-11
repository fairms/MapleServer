package im.cave.ms.network.packet.opcode;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.WhisperType;
import im.cave.ms.network.netty.OutPacket;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet.opcode
 * @date 1/9 23:18
 */
public class ChatPacket {
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
}
