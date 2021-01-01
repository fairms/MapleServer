package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 12/30 22:00
 */
public enum CashShopAction {
    Res_LoadLocker_Done(0x07),
    Res_LoadLocker_Failed(0x08),
    Res_LoadGift_Done(0x09),
    Res_LoadGift_Failed(0x10),
    Res_SetCart_Done(0x0B),
    Res_Buy_Done(0x15),
    Res_Buy_Failed(0x16),
    Res_Get_Item_Done(0x22),
    Res_Get_Item_Failed(0x23),
    Res_Move_Item_Done(0x24),
    Res_Move_Item_Failed(0x25);
    private final short val;

    CashShopAction(short val) {
        this.val = val;
    }

    CashShopAction(int i) {
        this((short) i);
    }

    public static CashShopAction getByVal(int type) {
        return Arrays.stream(values()).filter(bp -> bp.getVal() == type).findAny().orElse(null);
    }

    public short getVal() {
        return val;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
