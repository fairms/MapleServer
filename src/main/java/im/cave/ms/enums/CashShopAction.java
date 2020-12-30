package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 12/30 22:00
 */
public enum CashShopAction {
    CashTrunk(0x07),
    Gift(0x09),
    Cart(0x0B);
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
