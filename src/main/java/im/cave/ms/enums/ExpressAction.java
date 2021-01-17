package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/17 15:52
 */
public enum ExpressAction {
    Req_Load(1),
    Req_Send_Normal(3),
    Req_PickUp(5),
    Req_Drop(6),
    Req_Close_Dialog(8),
    Res_Open_Dialog(9),
    Res_Init_Locker(10),
    Res_Send_Success(11),
    Res_Please_Check_Name(14),
    Res_Send_Done(19),
    Res_Remove_Done(24),
    Res_New_Msg(26);


    private final byte val;

    ExpressAction(int val) {
        this.val = (byte) val;
    }


    public static ExpressAction getByVal(byte val) {
        return Arrays.stream(values()).filter(mrt -> mrt.getVal() == val).findAny().orElse(null);
    }

    public byte getVal() {
        return val;
    }

}
