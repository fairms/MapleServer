package im.cave.ms.enums;


import im.cave.ms.tools.Util;

import java.util.Arrays;


public enum FlameStat {
    STR(0),
    DEX(1),
    INT(2),
    LUK(3),
    STR_DEX(4),
    STR_INT(5),
    STR_LUK(6),
    DEX_INT(7),
    DEX_LUK(8),
    INT_LUK(9),
    MaxHP(10),
    MaxMP(11),
    LevelReduction(12),
    Defense(13),
    Attack(17),
    MagicAttack(18),
    Speed(19),
    Jump(20),
    BossDamage(21),
    Damage(23),
    AllStats(24);

    private final int val;

    FlameStat(int val) {
        this.val = val;
    }

    public static FlameStat getByVal(int val) {
        return Util.findWithPred(Arrays.asList(values()), stat -> stat.getVal() == val);
    }

    public int getVal() {
        return val;
    }
}
