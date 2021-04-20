package im.cave.ms.client.multiplayer.miniroom;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.netty.OutPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.social.miniroom
 * @date 1/12 20:43
 */
public class ChatRoom implements MiniRoom {
    public static final AtomicInteger counter = new AtomicInteger(1);
    private int chatRoomId;
    private final List<MapleCharacter> chars = new ArrayList<>(6);

    public ChatRoom(MapleCharacter chr) {
        chatRoomId = counter.getAndIncrement();
        chars.add(chr);
    }

    public void broadcast(OutPacket chatRoomInviteTip, MapleCharacter player) {
        for (MapleCharacter chr : chars) {
            if (player != chr) {
                chr.announce(chatRoomInviteTip);
            }
        }
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    @Override
    public void close() {

    }

    @Override
    public void invite(MapleCharacter inviter, MapleCharacter invitee) {

    }

    @Override
    public void invite(MapleCharacter invitee) {

    }

    @Override
    public void exit(MapleCharacter chr) {

    }

}
