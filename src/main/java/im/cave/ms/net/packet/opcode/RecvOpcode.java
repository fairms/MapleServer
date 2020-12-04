package im.cave.ms.net.packet.opcode;

public enum RecvOpcode {
    CUSTOM_PACKET(0x3713),//13 37 lol

    // Login Server
    SDO_LOGIN_REQUEST(0x69),
    BEFORE_LOGIN(0x67),
    SERVERSTATUS_REQUEST(0x6A),
    CHARLIST_REQUEST(0x6B),
    CHAR_SELECTED(0x72),
    AFTER_CHAR_CREATED(0xAB),
    CHECK_CHAR_NAME_REQUEST(0x77),
    SERVERLIST_REQUEST2(0x75),
    CREATE_CHAR_REQUEST(0x80),
    PONG(0x9C),
    ERROR_PACKET(0x9F),
    CLIENT_AUTH_REQUEST(0xA2),
    OPEN_CREATE_CHAR_LAYOUT(0XCF),

    //Channel Server
    UNK1(0x66),
    PLAYER_LOGIN(0x71),
    CPONG(0x9D),
    ENTER_PORTAL(0xCB),
    CHANGE_CHANNEL(0xCC),
    PLAYER_MOVE(0XD6),
    CANCEL_CHAIR(0xd7),
    USE_CHAIR(0xd8),
    EMOTION(0xE7),
    CLOSE_RANGE_ATTACK(0xDB),

    CHAR_HIT(0xE2),
    GENERAL_CHAT(0xE4),
    SELECT_NPC(0xF7),
    TALK_ACTION(0xF8),
    ITEM_MOVE(0x109),
    USE_ITEM(0x10E),
    USE_SCRIPT_ITEM(0x114),
    PET_AUTO_EAT_FOOD(0x119),
    CHANGE_STAT_REQUEST(0x14b),
    SKILL_UP_REQUEST(0x150),
    CHAR_INFO_REQUEST(0x15c),
    PORTAL_SPECIAL(0x160),
    QUEST_ACTION(0x169),
    CHANGE_KEYMAP(0x1D4),
    CHANGE_QUICKSLOT(0x2AE),
    UPDATE_TICK(0x330),
    WORLD_MAP_TRANSFER(0x3F1),
    OPEN_WORLD_MAP(0x3F9),
    MOB_MOVE(0x424),
    NPC_ANIMATION(0x44a),
    PICK_UP(0x450),
    AFTER_CHANGE_MAP(0x45F),
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
