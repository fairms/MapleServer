package im.cave.ms.network.packet.opcode;

public enum RecvOpcode {
    CUSTOM_LOGIN(0x7777),

    // Login Server
    BEFORE_LOGIN(0x67),
    SDO_LOGIN_REQUEST(0x69),
    SERVERSTATUS_REQUEST(0x6A),
    CHARLIST_REQUEST(0x6B),
    CHAR_SELECTED(0x72),
    CHECK_CHAR_NAME_REQUEST(0x77),
    SERVERLIST_REQUEST2(0x75),
    CREATE_CHAR_REQUEST(0x80),
    DELETE_CHAR(0x84),
    CANCEL_DELETE_CHAR(0x86),
    PONG(0x9C),
    ERROR_PACKET(0x9F),
    CLIENT_AUTH_REQUEST(0xA2),
    AFTER_CHAR_CREATED(0xAB),
    OPEN_CREATE_CHAR_LAYOUT(0XCF),

    //Channel Server
    UNK1(0x66),
    PLAYER_LOGIN(0x71),
    CPONG(0x9D),
    ENTER_PORTAL(0xCB),
    CHANGE_CHANNEL(0xCC),
    MIGRATE_TO_CASH_SHOP_REQUEST(0xD0),
    PLAYER_MOVE(0XD6),
    CANCEL_CHAIR(0xD7),
    USE_CHAIR(0xD8),
    CLOSE_RANGE_ATTACK(0xDB),
    RANGED_ATTACK(0xDC),
    MAGIC_ATTACK(0xDD),
    PLAYER_BODY_ATTACK(0xDE),
    USER_AREA_DOT_ATTACK(0xDF),
    USER_MOVING_SHOOT_ATTACK_PREPARE(0xE0),
    USER_ATTACK_USER(0xE1),
    CHAR_HIT(0xE2),
    //0xe3
    GENERAL_CHAT(0xE4),
    //e5
    //e6
    CHAR_EMOTION(0xE7),
    SELECT_NPC(0xF7),
    TALK_ACTION(0xF8),
    TRUNK_OPERATION(0xFA),
    ITEM_MOVE(0x109),
    USE_ITEM(0x10E),
    USE_SCRIPT_ITEM(0x114),
    PET_AUTO_EAT_FOOD(0x119),
    RETURN_SCROLL(0x127),
    EQUIP_ENCHANT_REQUEST(0x139),
    USER_ABILITY_UP_REQUEST(0x148),
    USER_ABILITY_MASS_UP_REQUEST(0x149),
    CHANGE_STAT_REQUEST(0x14b),
    SKILL_UP(0x150),
    USE_SKILL(0x151),
    CANCEL_BUFF(0x153),
    CHAR_INFO_REQUEST(0x15c),
    PORTAL_SPECIAL(0x160),
    USER_QUEST_REQUEST(0x169),
    REQUEST_INSTANCE_TABLE(0x180),
    CHANGE_KEYMAP(0x1D4),
    CHANGE_CHAR_REQUEST(0x21B),
    COMBO_KILL_CHECK(0x233),
    SUMMON_MOVE(0x291),
    CHANGE_QUICKSLOT(0x2AE),
    UPDATE_TICK(0x330),
    SIGN_IN(0x34A),
    UNITY_PORTAL_SELECT(0x355),
    SKILL_OPT(0x34D),
    WORLD_MAP_TRANSFER(0x3F1),
    OPEN_WORLD_MAP(0x3F9),
    MOB_MOVE(0x424),
    NPC_ANIMATION(0x44a),
    PICK_UP_ITEM(0x450),
    AFTER_CHANGE_MAP(0x45F),
    QUICK_MOVE_SELECT(0x48D),
    BATTLE_ANALYSIS(0x5e2),
    EQUIP_EFFECT_OPT(0x60A),


    // OPCODE ENCRYPT BEGIN
    BEGIN(0xCA),

    //OPCODE ENCRYPT END
    END(0xA7F);

    private final int code;

    RecvOpcode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }

    public static RecvOpcode getOpcode(int op) {
        for (RecvOpcode inHeader : RecvOpcode.values()) {
            if (inHeader.getValue() == op) {
                return inHeader;
            }
        }
        return null;
    }

}