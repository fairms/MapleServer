package im.cave.ms.enums;

public enum MessageType {
    DROP_PICKUP_MESSAGE(0),
    QUEST_RECORD_MESSAGE(1),// v200
    QUEST_RECORD_MESSAGE_ADD_VALID_CHECK(2),// v200
    CASH_ITEM_EXPIRE_MESSAGE(2),// v200
    INC_EXP_MESSAGE(3), // 白色字体经验 3
    INC_SP_MESSAGE(4),// 增加SP   // 04  short:jobId  + amount
    INC_POP_MESSAGE(5),// v200
    INC_MONEY_MESSAGE(6),// v
    INC_GP_MESSAGE(7), // 07 1E 00 00 00
    INC_COMMITMENT_MESSAGE(8),
    GIVE_BUFF_MESSAGE(9),
    GENERAL_ITEM_EXPIRE_MESSAGE(10),  //道具过期 10
    SYSTEM_MESSAGE(11),
    // 13
    QUEST_RECORD_EX_MESSAGE(13), // QUEST_EX
    WORLD_SHARE_RECORD_MESSAGE(14),
    ITEM_PROTECT_EXPIRE_MESSAGE(15),
    ITEM_EXPIRE_REPLACE_MESSAGE(16),
    ITEM_ABILITY_TIME_LIMITED_EXPIRE_MESSAGE(17),
    SKILL_EXPIRE_MESSAGE(18),
    INC_NON_COMBAT_STAT_EXP_MESSAGE(19), //提升倾向 //13  mask: 00 00 20 00 00 00 00 00 amount : 64 00 00 00
    //21
    LIMIT_NON_COMBAT_STAT_EXP_MESSAGE(22),
    // 23
    ANDROID_MACHINE_HEART_ALSET_MESSAGE(24),
    INC_FATIGUE_BY_REST_MESSAGE(25),
    INC_PVP_POINT_MESSAGE(26),
    PVP_ITEM_USE_MESSAGE(27),
    WEDDING_PORTAL_ERROR(28),
    INC_HARDCORE_EXP_MESSAGE(29),
    NOTICE_AUTO_LINE_CHANGED(30),
    ENTRY_RECORD_MESSAGE(31),
    EVOLVING_SYSTEM_MESSAGE(32),
    EVOLVING_SYSTEM_MESSAGE_WITH_NAME(33),
    CORE_INVEN_OPERATION_MESSAGE(34),
    NX_RECORD_MESSAGE(35),
    BLOCKED_BEHAVIOR_MESSAGE(36),
    INC_WP_MESSAGE(36),
    MAX_WP_MESSAGE(37),
    STYLISH_KILL_MESSAGE(38), //连杀奖励经验 / 黄色
    BARRIER_EFFECT_IGNORE_MESSAGE(39),
    EXPIRED_CASH_ITEM_RESULT_MESSAGE(40),
    COLLECTION_RECORD_MESSAGE(41),
    RANDOM_CHANCE_MESSAGE(42),
    EXPIRED_QUEST_RESULT_MESSAGE(43),
    MULTI_QUEST_EX(46), //maybe
    MAKE_ACHIEVEMENT(47), //2F 01 00 00 00 9F 00 00 00
    ;

    private final byte val;

    MessageType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}