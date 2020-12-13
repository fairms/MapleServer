package im.cave.ms.net.packet.opcode;

import im.cave.ms.tools.Util;

public enum SendOpcode {


    //Login Server
    LOGIN_STATUS(0x00),
    SERVERLIST(0x01),
    SERVERSTATUS(0x05),//CHECK_USER_LIMIT_RESULT
    CHARLIST(0x06),
    SERVER_IP(0x07),
    AUTH_SUCCESS(0x08),
    PING(0x11),
    CHAR_NAME_RESPONSE(0x0A),
    ADD_NEW_CHAR_ENTRY(0x0B),
    DELETE_CHAR_TIME(0x0D),
    CANCEL_DELETE_CHAR(0x0E),
    OPEN_CREATE_CHAR(0x56),
    SERVER_LIST_BG(0x5D),
    DELETE_CHAR(0x84),


    //Channel Server
    CHANGE_CHANNEL(0x10),
    CPING(0x12),
    OPCODE_TABLE(0x2D),
    INVENTORY_OPERATION(0x6F),
    UPDATE_STATS(0x71),
    GIVE_BUFF(0x72),
    REMOVE_BUFF(0x73),
    CHANGE_SKILL_RESULT(0x77),
    SHOW_STATUS_INFO(0x83),
    CHAR_INFO(0x9f),
    SERVER_MSG(0xB0),
    PET_AUTO_EAT_MSG(0xBA),
    SERVER_NOTICE(0xd6),
    DEBUG_MSG(0xdd),
    RESULT_INSTANCE_TABLE(0xe7),
    RANK(0xF7),
    CHANGE_CHAR_KEY(0x12E),
    ACCOUNT(0x13C),
    UPDATE_QUEST_EX(0x13B),
    CANCEL_TITLE_EFFECT(0x145),
    EQUIP_ENCHANT(0x16A),
    UPDATE_VOUCHER(0x201),
    SET_MAP(0x236),
    SET_CASH_SHOP(0x239),
    FIELD_EFFECT(0x244),
    FIELD_MESSAGE(0x245),
    CLOCK(0x24A), //时钟
    QUICKSLOT_INIT(0x258),
    QUICK_MOVE(0x26b),
    SIT_RESULT(0x29b), //sit
    CHATTEXT(0x2B7),
    BLACK_BOARD(0x2B9),
    CHECK_ACCOUNT(0x2DC),
    CANCEL_CHAIR(0x2E1),
    HIDDEN_EFFECT_EQUIP(0x316),
    MOVE_PLAYER(0x345),
    EFFECT(0x383),
    QUEST_RESULT(0x388),
    DISABLE_UI(0x394),
    LOCK_UI(0x395),
    NOTICE(0x3A2),
    CHAT_MSG(0x3A3),
    FULLSCREEN_MSG(0x3b2),
    DEATH_CONFIRM(0x3cc),
    SKILL_COOLTIME(0x40f),
    OPEN_WORLDMAP(0x48F),
    SPAWN_MOB(0x4D5),
    REMOVE_MOB(0x4D6),
    SPAWN_MONSTER_CONTROL(0x4D7),
    MOVE_MONSTER_RESPONSE(0x4DF),
    HP_INDICATOR(0x4EC),
    SPAWN_NPC(0x542),
    REMOVE_NPC(0x543),
    SPAWN_NPC_REQUEST_CONTROLLER(0x545),
    NPC_ANIMATION(0x549),
    DROP_ENTER_FIELD(0x569),
    PICK_UP_DROP(0x56b),
    NPC_TALK(0x741),
    TRUNK_OPERATION(0x75A),
    SIGNIN_REWARDS(0x7EF),
    OPEN_UNITY_PORTAL(0x7f3),
    KEYMAP_INIT(0x84A),
    BATTLE_ANALYSIS(0x879),
    ;
    private final int code;

    SendOpcode(int code) {
        this.code = code;
    }

    public short getValue() {
        return (short) code;
    }

    public static Object getByValue(short op) {
        return Util.findWithPred(values(), sendOpcode -> sendOpcode.code == op);
    }

}
