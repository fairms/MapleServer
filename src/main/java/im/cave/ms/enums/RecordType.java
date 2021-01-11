package im.cave.ms.enums;

import im.cave.ms.tools.Util;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/7 17:04
 */
public enum RecordType {
    MAP_TRANSFER_COUPON_FREE,
    MAP_TRANSFER_COUPON_CASH,
    BOSS_LOG,
    RETURN_MAP,
    MAP_ENTER,
    ;

    public static RecordType getByName(String name) {
        return Util.findWithPred(values(), type -> type.name().equals(name));
    }
}
