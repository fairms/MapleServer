package im.cave.ms.enums;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/20 11:15
 */
public enum HotTimeRewardResultType {
    LIST(0xD5),
    GET_MAPLE_POINT(0xD7),
    GET_ITEM(0xD8);

    private final byte val;

    HotTimeRewardResultType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

}
