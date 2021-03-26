package im.cave.ms.enums;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 3/25 9:01
 */
public enum FameAction {
    Add(0),
    AlreadyAddInThisMonth(4),
    Receive(5);


    private final byte val;

    FameAction(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
