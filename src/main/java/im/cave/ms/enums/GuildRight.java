package im.cave.ms.enums;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 2/3 13:04
 * <p>
 * 家族成员的权限
 * </p>
 */
public enum GuildRight {
    ALL(0xFFFF),
    NULL(0),
    INVITE(0x1),
    EDIT_NOTICE(0x2),
    CHANGE_GRADE(0x4),
    EDIT_MARK(0x8),
    KICK(0x10),
    BULLETIN_BOARD_MANAGE(0x20),
    VERIFY_APPLY(0x40),
    GUILD_SKILL_MANAGE(0x100),
    USE_GUILD_SKILL(0x400),
    WATERWAY_MANAGE(0x4000),
    ;


    private int val;

    GuildRight(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
