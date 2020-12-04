package im.cave.ms.enums;

/**
 * Created on 1/25/2018.
 */
public enum MessageType {
    DROP_PICKUP_MESSAGE(0),
    QUEST_RECORD_MESSAGE(1),// v200
    QUEST_RECORD_MESSAGE_ADD_VALID_CHECK(2),// v200
//    CASH_ITEM_EXPIRE_MESSAGE(3),// v200
    INC_EXP_MESSAGE(3), // √
    INC_SP_MESSAGE(5),// v200
    INC_POP_MESSAGE(6),// v200
    INC_MONEY_MESSAGE(7),// v
    INC_GP_MESSAGE(8),
    INC_COMMITMENT_MESSAGE(9),
    GIVE_BUFF_MESSAGE(10),
    GENERAL_ITEM_EXPIRE_MESSAGE(11),
    SYSTEM_MESSAGE(12),
    // 13
    QUEST_RECORD_EX_MESSAGE(13), //√
    WORLD_SHARE_RECORD_MESSAGE(15),
    ITEM_PROTECT_EXPIRE_MESSAGE(16),
    ITEM_EXPIRE_REPLACE_MESSAGE(17),
    ITEM_ABILITY_TIME_LIMITED_EXPIRE_MESSAGE(18),
    SKILL_EXPIRE_MESSAGE(19),
    INC_NON_COMBAT_STAT_EXP_MESSAGE(20),
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
    INC_WP_MESSAGE(37),
//    MAX_WP_MESSAGE(38),
    STYLISH_KILL_MESSAGE(38),
    BARRIER_EFFECT_IGNORE_MESSAGE(40),
    EXPIRED_CASH_ITEM_RESULT_MESSAGE(41),
    COLLECTION_RECORD_MESSAGE(42),
    RANDOM_CHANCE_MESSAGE(43),
    EXPIRED_QUEST_RESULT_MESSAGE(44),
    //45-54
    ;

    private byte val;

    MessageType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
