package im.cave.ms.enums;


import im.cave.ms.tools.Util;

import java.util.Arrays;


public enum FlameStat {
    STR(0),
    DEX(10),
    INT(20),
    LUK(30),
    STR_DEX(40),
    STR_INT(50),
    STR_LUK(60),
    DEX_INT(70),
    DEX_LUK(80),
    INT_LUK(90),
    MaxHP(100),
    MaxMP(110),
    LevelReduction(120),
    Defense(130),
    Attack(170),
    MagicAttack(180),
    Speed(190),
    Jump(200),
    BossDamage(210),
    Damage(230),
    AllStats(240);

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
