package im.cave.ms.client;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.FriendFlag;
import im.cave.ms.network.netty.OutPacket;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int friendId;
    private String name;
    private byte flag; // 5 through 8 = account friend
    @Transient
    private int channelId;
    @Column(name = "groupName")
    private String group;
    private int friendAccountId;
    private String nickname;
    private String memo;
    @Transient
    private boolean inShop;
    @Transient
    private MapleCharacter character;

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getFriendId());
        outPacket.writeAsciiString(getName(), 13);
        outPacket.write(getFlag());
        outPacket.writeInt(getChannelId());
        outPacket.writeAsciiString(getGroup(), 17);
        outPacket.write(0);
        outPacket.writeInt(getFriendAccountId());
        outPacket.writeAsciiString(getNickname(), 13);
        outPacket.writeAsciiString(getMemo(), 256);
        outPacket.writeInt(isInShop() ? 1 : 0);
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public void setFlag(FriendFlag flag) {
        this.flag = (byte) flag.getVal();
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getFriendAccountId() {
        return friendAccountId;
    }

    public void setFriendAccountId(int friendAccountId) {
        this.friendAccountId = friendAccountId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean isInShop() {
        return inShop;
    }

    public void setInShop(boolean inShop) {
        this.inShop = inShop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAccount() {
        return getFlag() > 4;
    }

    public MapleCharacter getChar() {
        return character;
    }
}
