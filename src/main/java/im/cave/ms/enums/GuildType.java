package im.cave.ms.enums;

import java.util.Arrays;

/**
 * Created on 3/21/2018.
 */
public enum GuildType {
    Req_RemoveGuild(3),
    Req_CheckGuildName(4),
    Req_Setting(24),
    Req_Search(46),
    Req_Signin(49),
    Req_Rank(53),

    Res_InputGuildName(3),
    Res_LoadGuild_Done(58),
    Res_Rank(60),
    Res_RemoveGuild_Done(96),
    Res_SetMemberCommitment_Done(0x127),
    Res_ChangeSettings(138),//
    Res_IncPoint_Done(0x146),

    ;

    private final byte val;

    GuildType(int val) {
        this.val = (byte) val;
    }

    public static GuildType getTypeByVal(byte val) {
        return Arrays.stream(values()).filter(grt -> grt.getVal() == val).findAny().orElse(null);
    }

    public byte getVal() {
        return val;
    }
}
