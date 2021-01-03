package im.cave.ms.enums;


public enum EnterType {
    NoAnimation(0),
    Animation(1);

    private final byte val;

    EnterType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
