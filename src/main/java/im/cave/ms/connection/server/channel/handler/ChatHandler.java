package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.friend.Friend;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.MessagePacket;
import im.cave.ms.connection.server.channel.CommandExecutor;
import im.cave.ms.enums.WhisperType;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.server.channel.handler
 * @date 1/9 23:14
 */
public class ChatHandler {
    public static void handleUserGeneralChat(InPacket in, MapleClient c) {
        int tick = in.readInt();
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            c.close();
            return;
        }
        player.setTick(tick);
        String content = in.readMapleAsciiString();

        if (content.startsWith("@") || content.startsWith("!")) {
            CommandExecutor.handle(c, content);
            return;
        }
        player.getMap().broadcastMessage(player, MessagePacket.getChatText(player, content), true);
    }

    public static void handleWhisper(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte val = in.readByte();
        WhisperType type = WhisperType.getByVal(val);
        if (type == null) {
            player.dropMessage("Whisper val " + val + "未处理");
            player.enableAction();
            return;
        }
        player.setTick(in.readInt());
        String destName = in.readMapleAsciiString();
        MapleCharacter dest = player.getMapleWorld().getCharByName(destName);
        switch (type) {
            case Req_Find_Friend:
                if (dest == null) {
                    return;
                } else if (dest.getChannel() != player.getChannel()) {
                    player.announce(MessagePacket.whisper(WhisperType.Res_Find_Friend, destName, 3, dest.getChannel()));
                    break;
                }
                player.announce(MessagePacket.whisper(WhisperType.Res_Find_Friend, destName, 1, dest.getMapId()));
                break;
            case Req_Find_GuildMember:
                if (dest == null) {
                    return;
                } else if (dest.getChannel() != player.getChannel()) {
                    player.announce(MessagePacket.whisper(WhisperType.Res_Find_GuildMember, destName, 3, dest.getChannel()));
                    break;
                }
                player.announce(MessagePacket.whisper(WhisperType.Res_Find_GuildMember, destName, 1, dest.getMapId()));
                break;
            case Req_Whisper:
                String msg = in.readMapleAsciiString();
                if (dest == null) {
                    player.announce(MessagePacket.whisper(WhisperType.Res_Whisper, destName, 0, 0));
                } else {
                    player.announce(MessagePacket.whisper(WhisperType.Res_Whisper, destName, 1, 0));
                    //todo
                }
                break;
        }
    }


    //todo
    public static void handleGroupMessage(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte type = in.readByte(); //0 好友
        in.readByte();
        int groupId = in.readInt();
        if (type == 0) {
            for (Friend friend : player.getFriends()) {
                MapleCharacter friendChar = player.getMapleWorld().getCharById(friend.getId());
                if (friendChar != null) {
//                    friendChar.announce();
                }
            }
        } else if (type == 1) {

        } else if (type == 2) {

        }
    }
}
