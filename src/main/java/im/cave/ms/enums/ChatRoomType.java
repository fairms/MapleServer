package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/12 20:41
 */
public enum ChatRoomType {
    Join(0),
    ChatInviteRequest(3),
    ChatInviteTip(4),
    ChatInviteRefuseTip(5),
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
