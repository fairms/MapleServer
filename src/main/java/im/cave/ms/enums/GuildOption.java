package im.cave.ms.enums;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/29 15:20
 */
public enum GuildOption {
    Trend_Harmony(0x01),
    Trend_DressUp(0x02),
    Trend_Hunting(0x04),
    Trend_Business(0x08),
    Trend_Collect(0x10),
    Trend_Professional(0x20),
    Trend_TeamGame(0x40),
    Trend_Boss(0x80),

    Time_Non_Weekend(0x01),
    Time_Weekend(0x02),
    Time_00_06(0x04),
    Time_06_12(0x08),
    Time_12_18(0x10),
    Time_18_24(0x20),

    Age_10th_Gen(0x01),
    Age_20th_Gen(0x02),
    Age_30th_Gen(0x04),
    Age_Other(0x08);


    private final int val;

    private GuildOption(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
