package im.cave.ms.enums;

import im.cave.ms.tools.Util;

public enum ArcEnchantType {
    Absorbing_Single(0),
    Upgrade(1),
    Absorbing_Multi(2),
    ;
    private final int val;


    public static ArcEnchantType getEnchantTypeByVal(int val) {
        return Util.findWithPred(values(), auctionAction -> auctionAction.getVal() == val);
    }

    ArcEnchantType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
