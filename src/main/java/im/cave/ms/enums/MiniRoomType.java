package im.cave.ms.enums;

import java.util.Arrays;

public enum MiniRoomType {
    PlaceItem(0),
    PlaceItem_2(1),
    PlaceItem_3(2),
    PlaceItem_4(3),

    SetMesos(4),
    SetMesos_2(5),
    SetMesos_3(6),
    SetMesos_4(7),
    Trade(8),
    TradeConfirm(9),
    TradeConfirm2(10),
    TradeConfirm3(11), // 3...?
    TradeConfirmRemoteResponse(14), // what is this even used for

    TradeRestraintItem(15),
    Create(16),
    Accept(19),
    EnterTrade(20),
    TradeInviteRequest(21),
    InviteResultStatic(22),

    Chat(24),
    五子棋和及一大考验(26), //01
    Avatar(27),
    ExitTrade(28),

    CheckSSN2(30),
    Ready(94),
    UnReady(95),
    Start(97), //发起方 1bit 对方2bit 01
    RPSSelect(98), //2剪刀 0 拳头 1布
    RPSInvite(113),
    RPSResult(114), //0 赢 1 平 2 输  + 对方的选择
    ;

    private final byte val;

    MiniRoomType(int val) {
        this.val = (byte) val;
    }

    public static MiniRoomType getByVal(byte val) {
        return Arrays.stream(values()).filter(mrt -> mrt.getVal() == val).findAny().orElse(null);
    }

    public byte getVal() {
        return val;
    }
}
