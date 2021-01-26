package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/6 22:37
 */
public enum CashShopCurrencyType {
    Cash(0),
    MaplePoint(1),
    Point(-1),
    Meso(-1);

    private final byte val;

    CashShopCurrencyType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public static CashShopCurrencyType getByVal(byte type) {
        return Arrays.stream(values()).filter(v -> v.getVal() == type).findFirst().orElse(null);
    }
}
