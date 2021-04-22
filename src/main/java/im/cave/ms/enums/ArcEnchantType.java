package im.cave.ms.enums;

public enum ArcEnchantType {
    Upgrade(1),
    Absorbing(2),
    ;
    private final int val;

    ArcEnchantType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
