package im.cave.ms.enums;


public enum RemoveMobType {
    STAY(0),
    ANIMATION_DEATH(1),
    NO_ANIMATION_DEATH(2),
    INSTA_DEATH(3),
    NO_ANIMATION_DEATH_2(4),
    ANIMATION_DEATH_2(5),
    ANIMATION_DEATH_3(6),

    ;
    private final byte val;

    RemoveMobType(byte val) {
        this.val = val;
    }

    RemoveMobType(int val) {
        this((byte) val);
    }

    public byte getVal() {
        return val;
    }
}
