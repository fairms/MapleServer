package im.cave.ms.network.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.WhisperType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;

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
}
