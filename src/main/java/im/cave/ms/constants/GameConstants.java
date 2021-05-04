package im.cave.ms.constants;

import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.field.QuickMoveInfo;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.EnchantStat;
import im.cave.ms.enums.QuickMoveType;
import im.cave.ms.provider.data.ItemData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.constant
 * @date 11/19 21:48
 */
public class GameConstants {

    public static int getViewX(byte resolution) {
        switch (resolution) {
            case 0:
                return 800;
            case 1:
                return 1024;
            case 2:
                return 1280;
            case 3:
                return 1366;
        }
        return 0;
    }

    public static int getViewY(byte resolution) {
        switch (resolution) {
            case 0:
                return 600;
            case 1:
            case 3:
                return 768;
            case 2:
                return 720;
        }
        return 0;
    }


    public static int MAX_VIEW_X = 1366; //1366*768
    public static int MAX_VIEW_Y = 768;

    public static long[] charExp = new long[276];
    public static final int MAX_PET_AMOUNT = 3;
    public static int maxLevel = 275;
    public static final int MAX_HP = 500000;
    public static final int MAX_MP = 500000;
    public static final int MIN_HP = 50;
    public static final int MIN_MP = 10;
    public static final long MAX_MONEY = 3000000000L;
    public static final short DAMAGE_SKIN_MAX_SIZE = 20;
    public static final int BEGINNER_SP_MAX_LV = 7;
    public static final long DAMAGE_CAP = 50000000;
    public static final int RESISTANCE_SP_MAX_LV = 10;
    public static final int QUICKSLOT_SIZE = 32;
    public static final byte MAX_LOCKER_SIZE = 124;
    public static final int POTION_POT_MAX_LIMIT = 10000000;
    public static final int MAX_FLAME_BONUS_SAGAS = 6;
    public static final int FLAME_STAT_MULTIPLE = 1000;
    //  default keymap
    public static final byte[] DEFAULT_KEY = {1, 2, 3, 4, 5, 6, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 29, 31, 34, 35, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 50, 56, 57, 59, 60, 61, 63, 64, 65, 66, 70};
    public static final byte[] DEFAULT_TYPE = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 4};
    public static final int[] DEFAULT_ACTION = {46, 10, 12, 13, 18, 23, 8, 5, 0, 4, 27, 30, 39, 1, 41, 19, 14, 15, 52, 2, 17, 11, 3, 20, 26, 16, 22, 9, 50, 51, 6, 31, 29, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106, 47};
    //  custom keymap
    public static final byte[] CUSTOM_KEY = {1, 20, 21, 22, 23, 25, 26, 27, 29, 34, 35, 36, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 49, 50, 52, 56, 57, 59, 60, 61, 63, 64, 65, 66, 70, 71, 73, 79, 82, 83};
    public static final byte[] CUSTOM_TYPE = {4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4};
    public static final int[] CUSTOM_ACTION = {46, 27, 30, 0, 1, 19, 14, 15, 52, 17, 11, 8, 3, 20, 26, 16, 22, 9, 50, 51, 2, 31, 29, 5, 7, 4, 53, 54, 100, 101, 102, 103, 104, 105, 106, 47, 12, 13, 23, 10, 18};
    // Trading
    public static final int MAX_TRADE_ITEMS = 9;
    // Field
    public static final int NO_MAP_ID = 999999999;
    public static final int VIDEO_FIELD = 931050990; // Used for Effects and/or Videos
    public static final int ARDENTMILL = 910001000;
    public static final int FOREST_OF_TENACITY = 993001000;
    public static final int DEFAULT_FIELD_MOB_CAPACITY = 25;
    public static final double DEFAULT_FIELD_MOB_RATE_BY_MOBGEN_COUNT = 1.5;
    public static final int BASE_MOB_RESPAWN_RATE = 5000; // In milliseconds
    private static List<QuickMoveInfo> quickMoveInfos;
    //Guild
    public static final int MAX_DAY_COMMITMENT = 50000;
    public static final int SP_PER_GUILD_LEVEL = 2;
    public static final double GGP_PER_CONTRIBUTION = 0.1;
    public static final double IGP_PER_CONTRIBUTION = 0.7;
    public static final int GUILD_BBS_RECORDS_PER_PAGE = 10;
    public static final int GGP_FOR_SKILL_RESET = 50000;
    public static final int MAX_GUILD_LV = 30;
    public static final int MAX_GUILD_MEMBERS = 200;
    public static final int CREATE_GUILD_COST = 5000000;
    public static final int GUILD_MAX_MEMBERS_DEFAULT = 10;
    //drop
    public static final int DROP_HEIGHT = 100; // was 20
    public static final int DROP_REMAIN_ON_GROUND_TIME = 120; // 2 minutes
    public static final int DROP_REMOVE_OWNERSHIP_TIME = 30; // 30 sec
    public static final int DROP_DIFF = 25;
    public static final int MIN_MONEY_MULT = 6;
    public static final int MAX_MONEY_MULT = 9;
    public static final int MAX_DROP_CHANCE = 1000;
    public static final int MESO_DROP_CHANCE = MAX_DROP_CHANCE / 5;


    // Potential Chance on Drop Equips
    public static final int RANDOM_EQUIP_UNIQUE_CHANCE = 1; // out of a 100
    public static final int RANDOM_EQUIP_EPIC_CHANCE = 3; // out of a 100
    public static final int RANDOM_EQUIP_RARE_CHANCE = 8; // out of a 100

    // Hyper stat
    public static final long HYPER_STAT_RESET_COST = 10000000;
    public static final long HYPER_SKILL_RESET_COST = 1000000;

    //Party
    public static final String DEFAULT_PARTY_NAME = "快去组队游戏吧，GoGo";

    //Friend
    public static final String DEFAULT_FRIEND_GROUP = "未指定群组";
    // Default slots
    public static final int DEFAULT_BUDDY_CAPACITY = 20;
    public static final int DEFAULT_DAMAGE_SLOTS = 1;
    public static final short DEFAULT_EQUIP_INVENTORY_SLOTS = 32;
    public static final short DEFAULT_CONSUME_INVENTORY_SLOTS = 32;
    public static final short DEFAULT_INSTALL_INVENTORY_SLOTS = 32;
    public static final short DEFAULT_ETC_INVENTORY_SLOTS = 32;
    public static final short DEFAULT_CASH_INVENTORY_SLOTS = 64;
    public static final short DEFAULT_TRUNK_SLOTS = 4;
    public static final short DEFAULT_CHARACTER_SLOTS = 6;
    public static final short INVENTORY_MAX_SLOTS = 128;


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
        initQuickMove();
    }

    private static void initQuickMove() {
        quickMoveInfos = new ArrayList<>();
        quickMoveInfos.add(new QuickMoveInfo(0, 9010022, QuickMoveType.DimensionalPortal, 10, "使用可以移动到组队任务等各种地图的#c<次元之镜>#。", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9071003, QuickMoveType.MonsterPark, 100, "移动到可以和队员们一起消灭强大怪物的组队游戏区域\\n#c<怪物公园>#。\\n#c普通怪物公园：100级以上可以参加\\n网吧怪物竞技场：70级～200级", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9000086, QuickMoveType.Boat, 0, "移动到距离当前位置最近的#c<大陆移动码头>#。", true,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9000087, QuickMoveType.FreeMarket, 0, "移动到可以和其他玩家交易道具的#c<自由市场>#。", true,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9000088, QuickMoveType.Ardentmill, 35, "移动到专业技术村庄#c<匠人街>#。\\n#c35级以上可以移动", true,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9000089, QuickMoveType.Taxi, 0, "使用可以让角色移动到附近主要地区的#c<出租车>#。", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9010041, QuickMoveType.SpinningGlasses, 30, "获得了打工奖励。", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9000123, QuickMoveType.BigHeadward, 1, "在爱德华那里可以更换漂亮的发型。", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 9000124, QuickMoveType.Nurse, 1, "在塑料罗伊那里可以接受整容。", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 0, QuickMoveType.Hera, 10, "通过赫拉，可以访问婚礼村。", false,
                ZERO_TIME, MAX_TIME));
        quickMoveInfos.add(new QuickMoveInfo(0, 0, QuickMoveType.Party, 20, "可以移动到组队特殊地图。", true,
                ZERO_TIME, MAX_TIME));
    }

    public static int[] guildExp = new int[]{
            0, 15000, 60000, 135000, 240000,
            375000, 540000, 735000, 960000, 1215000,
            1500000, 1815000, 2160000, 2535000, 2940000,
            3375000, 3840000, 4335000, 4860000, 5415000,
            6000000, 6615000, 7260000, 7935000, 8640000,
            9375000, 10140000, 10935000, 11760000, 12615000
    };


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

    public static List<QuickMoveInfo> getQuickMoveInfos() {
        return quickMoveInfos;
    }


    public static int getEnchantmentValByChuc(Equip equip, EnchantStat es, short chuc, int curAmount) {
        if (equip.isCash() || (ItemData.getEquipById(equip.getItemId()).getTuc() <= 0 && !ItemConstants.isTucIgnoreItem(equip.getItemId()))) {
            return 0;
        }
        if (es == EnchantStat.PDD) {
            return (int) (equip.getIPDD() * (ItemConstants.isOverall(equip.getItemId()) ? 0.10 : 0.05));
        }
        if (es == EnchantStat.MDD) {
            return (int) (equip.getIMDD() * (ItemConstants.isOverall(equip.getItemId()) ? 0.10 : 0.05));
        }
        if (!equip.isSuperiorEqp()) {
            return getEquipStatBoost(equip, es, chuc);
        } else {
            if (es == EnchantStat.STR || es == EnchantStat.DEX || es == EnchantStat.INT || es == EnchantStat.LUK) {
                return getStatForSuperiorEnhancement(equip.getRLevel() + equip.getIIncReq(), chuc);
            }
            if (es == EnchantStat.PAD || es == EnchantStat.MAD) {
                return getAttForSuperiorEnhancement(equip.getRLevel() + equip.getIIncReq(), chuc);
            }
        }
        return 0;
    }


    public static int getStatForSuperiorEnhancement(int reqLevel, short chuc) {
        if (chuc == 0) {
            return reqLevel < 110 ? 2 : reqLevel < 149 ? 9 : 19;
        } else if (chuc == 1) {
            return reqLevel < 110 ? 3 : reqLevel < 149 ? 10 : 20;
        } else if (chuc == 2) {
            return reqLevel < 110 ? 5 : reqLevel < 149 ? 12 : 22;
        } else if (chuc == 3) {
            return reqLevel < 149 ? 15 : 25;
        } else if (chuc == 4) {
            return reqLevel < 149 ? 19 : 29;
        }
        return 0;
    }

    public static int getAttForSuperiorEnhancement(int reqLevel, short chuc) {
        if (chuc == 5) {
            return reqLevel < 150 ? 5 : 9;
        } else if (chuc == 6) {
            return reqLevel < 150 ? 6 : 10;
        } else if (chuc == 7) {
            return reqLevel < 150 ? 7 : 11;
        } else {
            return chuc == 8 ? 12 : chuc == 9 ? 13 : chuc == 10 ? 15 : chuc == 11 ? 17 : chuc == 12 ? 19 : chuc == 13 ? 21 : chuc == 14 ? 23 : 0;
        }
    }


    public static int getEquipStatBoost(Equip equip, EnchantStat es, short chuc) {
        int stat = 0;
        // hp/mp
        if (es == EnchantStat.MHP || es == EnchantStat.MMP) {
            stat += chuc <= 2 ? 5 : chuc <= 4 ? 10 : chuc <= 6 ? 15 : chuc <= 8 ? 20 : chuc <= 14 ? 25 : 0;
        }
        int reqLevel = equip.getRLevel() + equip.getIIncReq();
        // all stat
        if (es == EnchantStat.STR || es == EnchantStat.DEX || es == EnchantStat.INT || es == EnchantStat.LUK) {
            if (chuc <= 4) {
                stat += 2;
            } else if (chuc <= 14) {
                stat += 3;
            } else if (chuc <= 21) {
                stat += reqLevel <= 137 ? 7 : reqLevel <= 149 ? 9 : reqLevel <= 159 ? 11 : reqLevel <= 199 ? 13 : 15;
            }
        }
        // att for all equips
        if ((es == EnchantStat.PAD || es == EnchantStat.MAD) && chuc >= 15) {
            if (chuc == 15) {
                stat += reqLevel <= 137 ? 6 : reqLevel <= 149 ? 7 : reqLevel <= 159 ? 8 : reqLevel <= 199 ? 9 : 12;
            } else if (chuc == 16) {
                stat += reqLevel <= 137 ? 7 : reqLevel <= 149 ? 8 : reqLevel <= 159 ? 9 : reqLevel <= 199 ? 9 : 13;
            } else if (chuc == 17) {
                stat += reqLevel <= 137 ? 7 : reqLevel <= 149 ? 8 : reqLevel <= 159 ? 9 : reqLevel <= 199 ? 10 : 14;
            } else if (chuc == 18) {
                stat += reqLevel <= 137 ? 8 : reqLevel <= 149 ? 9 : reqLevel <= 159 ? 10 : reqLevel <= 199 ? 11 : 14;
            } else if (chuc == 19) {
                stat += reqLevel <= 137 ? 9 : reqLevel <= 149 ? 10 : reqLevel <= 159 ? 11 : reqLevel <= 199 ? 12 : 15;
            } else if (chuc == 20) {
                stat += reqLevel <= 149 ? 11 : reqLevel <= 159 ? 12 : reqLevel <= 199 ? 13 : 16;
            } else if (chuc == 21) {
                stat += reqLevel <= 149 ? 12 : reqLevel <= 159 ? 13 : reqLevel <= 199 ? 14 : 17;
            } else if (chuc == 22) {
                stat += reqLevel <= 149 ? 17 : reqLevel <= 159 ? 18 : reqLevel <= 199 ? 19 : 21;
            } else if (chuc == 23) {
                stat += reqLevel <= 149 ? 19 : reqLevel <= 159 ? 20 : reqLevel <= 199 ? 21 : 23;
            } else if (chuc == 24) {
                stat += reqLevel <= 149 ? 21 : reqLevel <= 159 ? 22 : reqLevel <= 199 ? 23 : 25;
            }
        }
        // att gains for weapons
        if (ItemConstants.isWeapon(equip.getItemId()) && !ItemConstants.isSecondary(equip.getItemId())) {
            if (chuc <= 14) {
                if (es == EnchantStat.PAD) {
                    stat += equip.getIPad() * 0.02;
                } else if (es == EnchantStat.MAD) {
                    stat += equip.getIMad() * 0.02;
                }
            } else if (es == EnchantStat.PAD || es == EnchantStat.MAD) {
                stat += chuc == 22 ? 13 : chuc == 23 ? 12 : chuc == 24 ? 11 : 0;
                if (reqLevel == 200 && chuc == 15) {
                    stat += 1;
                }
            }
        }
        // att gain for gloves, enhancements 4/6/8/10 and 12-14
        if (ItemConstants.isGlove(equip.getItemId()) && (es == EnchantStat.PAD || es == EnchantStat.MAD)) {
            if ((chuc <= 10 && chuc % 2 == 0) || (chuc >= 12 && chuc <= 14)) {
                stat += 1;
            }
        }
        // speed/jump for shoes
        if (ItemConstants.isShoe(equip.getItemId()) && (es == EnchantStat.SPEED || es == EnchantStat.JUMP) && chuc <= 4) {
            stat += 1;
        }
        return stat;
    }


    public static long applyTax(long money) {
        return Math.round(money * 0.95);
    }

    public static boolean isValidEmotion(int emotion) {
        return emotion >= 0 && emotion <= 10;
    }

    private static final int[] cumulativeTraitExp = {
            0, 20, 46, 80, 124, 181, 255, 351, 476, 639, 851, 1084,
            1340, 1622, 1932, 2273, 2648, 3061, 3515, 4014, 4563, 5128,
            5710, 6309, 6926, 7562, 8217, 8892, 9587, 10303, 11040, 11788,
            12547, 13307, 14089, 14883, 15689, 16507, 17337, 18179, 19034, 19902,
            20783, 21677, 22584, 23505, 24440, 25399, 26362, 27339, 28331, 29338,
            30360, 31397, 32450, 33519, 34604, 35705, 36823, 37958, 39110, 40279,
            41466, 32671, 43894, 45135, 46395, 47674, 48972, 50289, 51626, 52967,
            54312, 55661, 57014, 58371, 59732, 61097, 62466, 63839, 65216, 66597,
            67982, 69371, 70764, 72161, 73562, 74967, 76376, 77789, 79206, 80627,
            82052, 83481, 84914, 86351, 87792, 89237, 90686, 92139, 93596, 96000
    };

    public static int getTraitExpNeededForLevel(int level) {
        if (level < 0 || level >= cumulativeTraitExp.length) {
            return Integer.MAX_VALUE;
        }
        return cumulativeTraitExp[level];
    }


    private static final int[][] STAR_FORCE_LEVELS = {
            {Integer.MAX_VALUE, -1}, // per equip
            {137, 20},
            {127, 15},
            {117, 10},
            {107, 8},
            {95, 5},
    };

    private static final int[][] STAR_FORCE_LEVELS_SUPERIOR = {
            {Integer.MAX_VALUE, 15},
            {137, 12},
            {127, 10},
            {117, 8},
            {107, 5},
            {95, 3},
    };


    public static int getMaxStars(Equip equip) {
        int level = equip.getRLevel() - equip.getIIncReq();
        int stars = Arrays.stream(equip.isSuperiorEqp() ? STAR_FORCE_LEVELS_SUPERIOR : STAR_FORCE_LEVELS)
                .filter(lv -> level <= lv[0]).findFirst().orElse(new int[]{5})[1]; //很别扭
        return stars != -1 ? stars : 25;
    }

    public static int getExpRequiredForNextGuildLevel(int curLevel) {
        if (curLevel >= 25 || curLevel < 0) {
            return 0;
        }
        return guildExp[curLevel];
    }

    public static BaseStat getMainStatForJob(short job) {
        if (JobConstants.isBeginnerJob(job) || JobConstants.isBuccaneer(job) || JobConstants.isAdventurerPirate(job)
                || JobConstants.isPinkBean(job) || JobConstants.isDawnWarrior(job) || JobConstants.isKaiser(job)
                || JobConstants.isZero(job) || JobConstants.isDemon(job)
                || JobConstants.isDemonSlayer(job) || JobConstants.isAran(job) || JobConstants.isCannonShooter(job)
                || JobConstants.isDarkKnight(job) || JobConstants.isHero(job) || JobConstants.isPage(job)
                || JobConstants.isBlaster(job) || JobConstants.isHayato(job) || JobConstants.isMihile(job)
                || JobConstants.isShade(job) || JobConstants.isThunderBreaker(job) || JobConstants.isAdventurerWarrior(job)) {
            return BaseStat.str;
        } else if (JobConstants.isJett(job) || JobConstants.isCorsair(job) || JobConstants.isWildHunter(job)
                || JobConstants.isMercedes(job) || JobConstants.isAngelicBuster(job) || JobConstants.isWindArcher(job)
                || JobConstants.isAdventurerArcher(job)) {
            return BaseStat.dex;
        } else if (JobConstants.isBeastTamer(job) || JobConstants.isBlazeWizard(job) || JobConstants.isCleric(job)
                || JobConstants.isEvan(job) || JobConstants.isIceLightning(job) || JobConstants.isFirePoison(job)
                || JobConstants.isAdventurerMage(job) || JobConstants.isKanna(job) || JobConstants.isKinesis(job)
                || JobConstants.isLuminous(job)) {
            return BaseStat.inte;
        } else if (JobConstants.isAdventurerThief(job) || JobConstants.isNightLord(job) || JobConstants.isShadower(job)
                || JobConstants.isPhantom(job) || JobConstants.isNightWalker(job) || JobConstants.isDualBlade(job)) {
            return BaseStat.luk;
        } else if (JobConstants.isDemonAvenger(job)) {
            return BaseStat.mhp;
        }
        return null;
    }


    public static BaseStat getSecStatByMainStat(BaseStat mainStat) {
        if (mainStat == null) {
            return null;
        }
        switch (mainStat) {
            case str:
            case luk:
                return BaseStat.dex;
            case dex:
                return BaseStat.str;
            case inte:
                return BaseStat.luk;
        }
        return null;
    }


    /*
        内在能力
        内在等级=第一条潜能的等级
     */
    public static final int CHAR_POT_BASE_ID = 70000000;
    public static final int CHAR_POT_END_ID = 70000062;
    public static final int BASE_CHAR_POT_UP_RATE = 10; // 10%
    public static final int BASE_CHAR_POT_DOWN_RATE = 10; // 10%
    public static final int CHAR_POT_RESET_COST = 100;
    @Deprecated
    public static final int CHAR_POT_GRADE_LOCK_COST = 10000;
    public static final int CHAR_POT_LOCK_1_COST = 3000;
    public static final int CHAR_POT_LOCK_2_COST = 5000;

    public static final List<Integer> GRADE_B = List.of(
            70000000, 70000001, 70000002, 70000003, 70000004, 70000005,
            70000006, 70000008, 70000009, 70000015, 70000021,
            70000022, 70000023, 70000024, 70000033, 70000036,
            70000039, 70000048, 70000049, 70000052, 70000053,
            70000054, 70000055, 70000058, 70000059, 70000060, 70000061);

    public static final List<Integer> GRADE_A = List.of(70000012, 70000013, 70000014);

    public static final List<Integer> GRADE_S = List.of(70000027, 70000028, 70000029, 70000034,
            70000035, 70000041, 70000045);

    public static final List<Integer> GRADE_SS = List.of(70000016, 70000040, 70000042, 70000046, 70000047);

    public static int getCharPotGradeLockCost(int grade) {
        switch (grade) {
            case 0:
                return 0;
            case 1:
                return 490;
            case 2:
                return 5000;
            case 3:
                return 10000;
        }
        return 0;
    }

    public static int getBaseCharPotUpRate(byte grade) {
        switch (grade) {
            case 0:
                return 5;
            case 1:
                return 3;
            case 2:
                return 1;
            case 3:
                return 0;
        }
        return 0;
    }

    public static int getBaseCharPotDownRate(byte grade) {
        switch (grade) {
            case 0:
                return 0;
            case 1:
                return 3;
            case 2:
                return 15;
            case 3:
                return 50;
        }
        return 0;
    }

    public static List<Integer> getCharPotentialIDByGrade(byte grade) {
        List<Integer> potentials = new ArrayList<>();
        switch (grade) {
            case 3:
                potentials.addAll(GRADE_SS);
            case 2:
                potentials.addAll(GRADE_S);
            case 1:
                potentials.addAll(GRADE_A);
            case 0:
                potentials.addAll(GRADE_B);
        }
        return potentials;
    }

    public static byte getLeastReqGradeOfSkill(int skill) {
        if (GRADE_SS.contains(skill)) {
            return 3;
        } else if (GRADE_S.contains(skill)) {
            return 2;
        } else if (GRADE_A.contains(skill)) {
            return 1;
        } else {
            return 0;
        }
    }


    public static int getArcUpgradeCost(int arcId, int curLevel) {
        if (arcId == 1712001) {
            return 12440000 + 6600000 * curLevel;
        } else {
            return 2370000 + 7130000 * curLevel;
        }
    }

    public static int getArcUpgradeReqExp(int curLevel) {
        return (int) (Math.pow(curLevel, 2) + 11);
    }

}
