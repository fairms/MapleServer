package im.cave.ms.constants;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.constant
 * @date 11/19 21:48
 */
public class GameConstants {


    public static long[] charExp = new long[276];
    public static int maxLevel = 275;
    public static final long MAX_MONEY = 3000000000L;
    public static final int BEGINNER_SP_MAX_LV = 7;
    public static final long DAMAGE_CAP = 50000000;
    public static final int RESISTANCE_SP_MAX_LV = 10;
    public static final int QUICKSLOT_SIZE = 32;
    //  default keymap
    public static final byte[] DEFAULT_KEY = {1, 2, 3, 4, 5, 6, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 29, 31, 34, 35, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 50, 56, 57, 59, 60, 61, 63, 64, 65, 66, 70};
    public static final byte[] DEFAULT_TYPE = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 4};
    public static final int[] DEFAULT_ACTION = {46, 10, 12, 13, 18, 23, 8, 5, 0, 4, 27, 30, 39, 1, 41, 19, 14, 15, 52, 2, 17, 11, 3, 20, 26, 16, 22, 9, 50, 51, 6, 31, 29, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106, 47};
    //  custom keymap
    public static final byte[] CUSTOM_KEY = {1, 20, 21, 22, 23, 25, 26, 27, 29, 34, 35, 36, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 49, 50, 52, 56, 57, 59, 60, 61, 63, 64, 65, 66, 70, 71, 73, 79, 82, 83};
    public static final byte[] CUSTOM_TYPE = {4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4};
    public static final int[] CUSTOM_ACTION = {46, 27, 30, 0, 1, 19, 14, 15, 52, 17, 11, 8, 3, 20, 26, 16, 22, 9, 50, 51, 2, 31, 29, 5, 7, 4, 53, 54, 100, 101, 102, 103, 104, 105, 106, 47, 12, 13, 23, 10, 18};

    //drop
    public static final int DROP_HEIGHT = 100; // was 20
    public static final int DROP_REMAIN_ON_GROUND_TIME = 120; // 2 minutes

    public static final int BASE_MOB_RESPAWN_RATE = 10000; // In milliseconds

    //map
    public static final int NO_MAP_ID = 999999999;


    public static int[][][] INC_HP_MP = {
            // first array = per job
            // then a list of tuples (minHP, maxHP, minMP, maxMP, randMP)
            // 1st value is for levelup, 2nd for assigning sp
            {{12, 16}, {0, 10}, {12, 0}, {8, 12}, {0, 6}, {8, 15}},// 0
            {{64, 68}, {0, 4}, {6, 0}, {50, 54}, {0, 2}, {4, 15}},// 1
            {{10, 14}, {0, 22}, {24, 0}, {6, 10}, {0, 18}, {20, 15}},// 2
            {{20, 24}, {0, 14}, {16, 0}, {16, 20}, {0, 10}, {12, 15}},// 3
            {{20, 24}, {0, 14}, {16, 0}, {16, 20}, {0, 10}, {12, 15}},// 4
            {{22, 26}, {0, 18}, {22, 0}, {18, 20}, {0, 14}, {16, 15}},// 5
            {{25, 29}, {0, 18}, {22, 0}, {28, 30}, {0, 14}, {16, 15}},// 6
            {{20, 24}, {0, 14}, {16, 20}, {16, 20}, {0, 10}, {12, 15}},// 7
            {{44, 48}, {0, 4}, {8, 0}, {30, 34}, {0, 2}, {4, 15}},// 8 - Aran
            {{16, 20}, {0, 35}, {39, 0}, {12, 16}, {0, 21}, {25, 15}},// 9 - Evan
            {{20, 24}, {0, 14}, {16, 0}, {16, 20}, {0, 10}, {12, 15}},// 10 - Mercedes
            {{16, 20}, {0, 198}, {200, 0}, {12, 16}, {0, 21}, {25, 15}},// 11 - Luminous
            {{34, 38}, {0, 22}, {24, 0}, {20, 24}, {0, 18}, {20, 15}},// 12 - Kinesis/BAM
            {{20, 24}, {0, 14}, {16, 0}, {16, 20}, {0, 10}, {12, 15}},// 13 - Phantom
            {{22, 26}, {0, 18}, {22, 0}, {18, 20}, {0, 14}, {16, 15}},// 14 - Mechanic
            {{52, 56}, {0, 0}, {0, 0}, {38, 40}, {0, 0}, {0, 0}},// 15 - Demon Slayer
            {{28, 32}, {0, 0}, {0, 0}, {24, 26}, {0, 0}, {0, 0}},// 16 - Angelic Buster
            {{30, 30}, {0, 0}, {0, 0}, {30, 30}, {0, 0}, {0, 0}},// 17 - Demon Avanger.
            {{20, 24}, {0, 14}, {16, 0}, {16, 20}, {0, 10}, {12, 15}},// 18 - Xenon
            {{64, 68}, {0, 0}, {0, 0}, {50, 54}, {0, 0}, {0, 0}},// 19 - Zero
            {{44, 48}, {0, 18}, {22, 0}, {30, 34}, {0, 14}, {16, 15}},// 20 - Jett
            {{37, 41}, {0, 22}, {24, 0}, {28, 30}, {0, 18}, {20, 0}},// 21 - Cannon
            {{44, 48}, {0, 4}, {8, 20}, {34, 38}, {0, 2}, {4, 15}},// 22 - Hayato
            {{40, 44}, {0, 0}, {0, 0}, {28, 32}, {0, 0}, {0, 0}},// 23 - Kanna
    };


    static {
        initCharExp();
    }

    private static void initCharExp() {
        charExp[1] = 15;
        charExp[2] = 34;
        charExp[3] = 57;
        charExp[4] = 92;
        charExp[5] = 135;
        charExp[6] = 372;
        charExp[7] = 560;
        charExp[8] = 840;
        charExp[9] = 1242;
        for (int i = 10; i <= 14; i++) {
            charExp[i] = charExp[i - 1];
        }
        for (int i = 15; i <= 29; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.2);
        }
        for (int i = 30; i <= 34; i++) {
            charExp[i] = charExp[i - 1];
        }
        for (int i = 35; i <= 39; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.2);
        }
        for (int i = 40; i <= 59; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.08);
        }
        for (int i = 60; i <= 64; i++) {
            charExp[i] = charExp[i - 1];
        }
        for (int i = 65; i <= 74; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.075);
        }
        for (int i = 75; i <= 89; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.07);
        }
        for (int i = 90; i <= 99; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.065);
        }
        for (int i = 100; i <= 104; i++) {
            charExp[i] = charExp[i - 1];
        }
        for (int i = 105; i <= 139; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.065);
        }
        for (int i = 140; i <= 179; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.0625);
        }
        for (int i = 180; i <= 199; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.06);
        }
        // level 200
        charExp[200] = 2207026470L;
        for (int i = 201; i <= 209; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.12);
        }

        // level 210
        charExp[210] = (long) (charExp[209] * 1.375 * 2);
        for (int i = 211; i <= 219; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.08);
        }

        // level 220
        charExp[220] = 84838062013L;
        for (int i = 221; i <= 229; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.04);
        }

        // level 230
        charExp[230] = (long) (charExp[229] * 1.02 * 2);
        for (int i = 231; i <= 239; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.02);
        }

        // level 240
        charExp[240] = (long) (charExp[239] * 1.01 * 2);
        for (int i = 241; i <= 249; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.01);
        }

        // level 250
        charExp[250] = (long) (charExp[249] * 1.01 * 2);
        for (int i = 251; i <= 259; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.01);
        }

        // level 260
        charExp[260] = (long) (charExp[259] * 1.01 * 2);
        for (int i = 261; i <= 269; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.01);
        }

        // level 260
        charExp[270] = (long) (charExp[269] * 1.01 * 2);
        for (int i = 271; i <= 274; i++) {
            charExp[i] = (long) (charExp[i - 1] * 1.01);
        }

    }

    public static int[][] getIncValArray(int job) {
        int jobRace = job / 1000;
        int jobCategory = JobConstants.getJobCategory((short) job);
        if (jobCategory <= 9) {
            if (job / 10 == 53 || job == 501) {
                return INC_HP_MP[21];
            }
            if (JobConstants.isLuminous((short) job)) {
                return INC_HP_MP[11];
            }
            if (jobRace == 2) {
                switch (jobCategory) {
                    case 1:// Aran
                        return INC_HP_MP[8];
                    case 2:// Evan
                        return INC_HP_MP[9];
                    case 3:// Mercedes
                        return INC_HP_MP[10];
                    case 4:// Phantom
                        return INC_HP_MP[13];
                }
            }
            if (JobConstants.isBattleMage((short) job) || JobConstants.isKinesis((short) job)) {
                return INC_HP_MP[12];
            } else if (JobConstants.isWildHunter((short) job)) {
                return INC_HP_MP[3];// can use default ? :/
            } else if (JobConstants.isMechanic((short) job)) {
                return INC_HP_MP[14];
            } else if (JobConstants.isDemonSlayer((short) job)) {
                return INC_HP_MP[15];
            } else if (JobConstants.isAngelicBuster((short) job)) {
                return INC_HP_MP[16];
            } else if (JobConstants.isDemonAvenger((short) job)) {
                return INC_HP_MP[17];
            } else if (JobConstants.isXenon((short) job)) {
                return INC_HP_MP[18];
            } else if (JobConstants.isZero((short) job) || JobConstants.isBlaster((short) job)) {
                return INC_HP_MP[19];
            } else if (JobConstants.isJett((short) job)) {
                return INC_HP_MP[20];
            } else if (JobConstants.isBeastTamer((short) job)) {
                return INC_HP_MP[21];
            } else if (JobConstants.isHayato((short) job)) {
                return INC_HP_MP[22];
            } else if (JobConstants.isKanna((short) job)) {
                return INC_HP_MP[23];
            }
            return INC_HP_MP[jobCategory];
        }
        return null;// something wrong.

    }
}
