package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 12/30 22:00
 */
public enum CashShopAction {
    Res_LoadLocker_Done(7),
    Res_LoadLocker_Failed(8),
    Res_LoadGift_Done(9),
    Res_LoadGift_Failed(10),
    Res_SetCart_Done(11),
    Res_Buy_Done(15),
    Res_Buy_Failed(16),
    Res_EquipSlotExt_Done(32),
    Res_Get_Item_Done(34),
    Res_Get_Item_Failed(35),
    Res_Move_Item_Done(36),
    Res_Move_Item_Failed(37);
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
