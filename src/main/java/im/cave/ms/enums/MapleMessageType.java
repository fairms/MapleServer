package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/12 22:06
 */
public enum MapleMessageType {
    Req_Send(0),
    Req_Delete_Received_Message(2),
    Req_Delete_Sent_Message(3),
    Req_Read(5),
    Res_Inbox(6),
    Res_Outbox(7),
    Res_Send_Success(8),
    Res_Send_Fail(9),
    Res_Delete_Received_Success(11),
    Res_Delete_Sent_Success(13),
    Res_InMessage_Read(14),
    Res_Add_Sent(15),
    ;

    private final byte val;

    MapleMessageType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public static MapleMessageType getByVal(byte val) {
        return Arrays.stream(values()).filter(mrt -> mrt.getVal() == val).findAny().orElse(null);
    }

}
