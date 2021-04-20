package im.cave.ms.client.multiplayer.miniroom;

import im.cave.ms.client.character.MapleCharacter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.miniroom
 * @date 12/29 14:47
 */
public interface MiniRoom {
    //关闭
    void close();

    //邀请
    void invite(MapleCharacter inviter, MapleCharacter invitee);

    //邀请
    void invite(MapleCharacter invitee);

    //退出
    void exit(MapleCharacter chr);
}
