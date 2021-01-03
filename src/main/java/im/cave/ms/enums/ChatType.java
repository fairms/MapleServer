package im.cave.ms.enums;

import java.util.Arrays;

public enum ChatType {
    Normal(0),
    Whisper(1),
    GroupParty(2),
    GroupFriend(3),
    GroupGuild(4),
    GroupAlliance(5),
    GameDesc(6),
    Tip(7),
    Notice(8),
    Notice2(9),
    AdminChat(10),
    SystemNotice(11),
    SpeakerChannel(12),
    SpeakerWorld(13),
    SpeakerWorldGuildSkill(14),
    ItemSpeaker(15),
    ItemSpeakerItem(16),
    SpeakerBridge(17),
    SpeakerWorldExPreview(18),
    Mob(19),
    Expedition(20),
    ItemMessage(21),
    MiracleTime(22),
    LotteryItemSpeaker(23),
    YellowBlue(24),  //[Lucid] : content
    AvatarMegaphone(25),
    PickupSpeakerWorld(26),
    WorldName(27),
    BossArenaNotice(28),
    Claim(29),
    Purple(30),
    BlackOnOrigin(33),
    Message(34),
    LoveSpeaker(36),
    MobileSpeaker(37),
    WhiteOnGreen(38),
    YellowOnRed(39),
    YellowOnRedChannel(39),
    CakeSpeaker(41),
    PieSpeaker(42),
    BlackOnGreen(43),
    BlackOnRed(44),
    BlackOnYellow(45),
    DarkBlue(46),
    BlackOnGreen2(47);

    private final short val;

    ChatType(short val) {
        this.val = val;
    }

    ChatType(int i) {
        this((short) i);
    }

    public short getVal() {
        return val;
    }

    public static ChatType getByVal(int type) {
        return Arrays.stream(values()).filter(bp -> bp.getVal() == type).findAny().orElse(null);
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
