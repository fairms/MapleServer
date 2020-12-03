package im.cave.ms.enums;

/**
 * Created on 2/21/2018.
 */
public enum DropEnterType {
    Default(0),
    Floating(1),
    Instant(2),
    FadeAway(3);

    private final byte val;

    DropEnterType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
