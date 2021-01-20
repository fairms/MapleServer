package im.cave.ms.connection.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.multiplayer.MapleNotes;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.BroadcastMsgType;
import im.cave.ms.enums.MapleNotesType;
import im.cave.ms.enums.WhisperType;
import im.cave.ms.provider.data.StringData;

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
        out.writeShort(SendOpcode.CHAT.getValue());
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


    public static OutPacket broadcastMsg(String content, BroadcastMsgType type) {
        return broadcastMsg(null, content, type, null);
    }

    public static OutPacket broadcastMsgWithItem(MapleCharacter chr, String content, BroadcastMsgType type, Item item) {
        return broadcastMsg(chr, content, type, item);
    }

    public static OutPacket broadcastMsg(MapleCharacter chr, String content, BroadcastMsgType type, Item item) {
        OutPacket out = new OutPacket(SendOpcode.BROADCAST_MSG);
        out.write(type.getVal());
        switch (type) {
            case NOTICE:
                break;
            case ALERT:
            case EVENT:
                out.writeMapleAsciiString(content);
                break;
            case SLIDE:
                out.write(1);
                out.writeMapleAsciiString(content);
                break;
            case NOTICE_WITH_OUT_PREFIX:
                out.writeMapleAsciiString(content);
                out.writeInt(0);
                break;
            case PICKUP_ITEM_WORLD:
                out.writeMapleAsciiString(content);
                out.write(item.getItemId());
                item.encode(out);
                break;
            case ITEM_SPEAKER: //check  5076100 超级喇叭
                out.writeMapleAsciiString(String.format("%s: %s", chr.getName(), content));
                out.writeMapleAsciiString(chr.getName());
                out.writeMapleAsciiString(content);
                out.writeLong(0); //喇叭itemId
                out.write(chr.getWorld());
                out.writeInt(chr.getId()); //unk 8025765
                out.write(chr.getChannel());
                out.writeBool(true); //是否可以点击私聊
                out.writeInt(5076100);
                out.writeBool(item != null);
                if (item != null) {
                    item.encode(out);
                    out.writeMapleAsciiString(StringData.getEquipName(item.getItemId()));
                }
                break;
            case WITH_ITEM:// check
                out.writeMapleAsciiString(content);
                out.writeInt(item.getItemId());
                out.writeInt(chr.getChannel());
                out.writeInt(3); // color?
                out.writeBool(true);
                item.encode(out);
                break;
            case PINK:
            case YELLOW_BLACK:
            case LOVE:
                out.writeMapleAsciiString(String.format("%s: %s", chr.getName(), content));
                out.writeMapleAsciiString(chr.getName());
                out.writeMapleAsciiString(content);
                out.writeLong(0);
                out.write(chr.getWorld());
                out.writeInt(chr.getId());
                out.write(chr.getChannel());
                out.write(1);

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
        //todo
        out.writeMapleAsciiString(msg);
        out.writeInt(0); //00 00 00 00
        out.writeInt(0);//00 00 00 00
        out.write(chr.getWorld());  // 01
        out.writeInt(chr.getId()); // 01 00 00 00  unk
        out.writeInt(chr.getChannel()); // 02 00 00 00 channel
        out.writeBool(whisperIcon);
        chr.getCharLook().encode(out);
        return out;
    }

    public static OutPacket mapleNotesResult(MapleNotesType type, List<MapleNotes> notes, int param) {
        OutPacket out = new OutPacket(SendOpcode.MAPLE_NOTES);
        out.write(type.getVal());
        switch (type) {
            case Res_Send_Success:
                break;
            case Res_Send_Fail:
                out.write(param);
                break;
            case Res_Add_Sent:
                MapleNotes msg = notes.get(0);
                msg.encodeForOut(out);
                break;
            case Res_Inbox:
                out.write(notes.size());
                for (MapleNotes message : notes) {
                    message.encodeForIn(out);
                }
                break;
            case Res_Outbox:
                out.write(notes.size());
                for (MapleNotes message : notes) {
                    message.encodeForOut(out);
                }
                break;
            case Res_Delete_Received_Success:
            case Res_Delete_Sent_Success:
                out.writeInt(1);
                out.writeInt(param);
                break;
            case Res_InNotes_Read:
                out.write(2);
                out.writeInt(1);
                out.writeInt(param);
                break;
        }
        return out;
    }

}
