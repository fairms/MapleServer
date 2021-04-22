package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/12 20:41
 */
public enum ChatRoomType {
    Req_Join(0),
    Req_Leave(2),
    Res_ChatInviteRequest(3),
    Res_ChatInviteTip(4),
    Res_ChatInviteRefuseTip(5),
    Req_Chat(6),

    ;

    private final byte val;

    ChatRoomType(int val) {
        this.val = (byte) val;
    }

    public static ChatRoomType getByVal(byte val) {
        return Arrays.stream(values()).filter(mrt -> mrt.getVal() == val).findAny().orElse(null);
    }

    public byte getVal() {
        return val;
    }

}
