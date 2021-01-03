package im.cave.ms.enums;

import im.cave.ms.tools.Util;

import java.util.Arrays;


public enum ItemOptionType {
    AnyEquip(0),
    Weapon(10),
    AnyExceptWeapon(11),
    ArmorExceptGlove(20),
    Accessory(40),
    Hat(51),
    Top(52),
    Bottom(53),
    Glove(54),
    Shoes(55);

    private int val;

    ItemOptionType(int val) {
        this.val = val;
    }

    public static ItemOptionType getByVal(int val) {
        return Util.findWithPred(Arrays.asList(values()), stat -> stat.getVal() == val);
    }

    public int getVal() {
        return val;
    }
}