package im.cave.ms.enums;

import java.util.Arrays;

/**
 * Created on 3/21/2018.
 */
public enum GuildType {
    Req_RemoveGuild(3),
    Req_CheckGuildName(4),
    Req_SetGradeName(18),
    Req_SetGradeRight(19),
    Req_SetGradeNameAndRight(20),
    Req_SetMemberGrade(21),
    Req_SetMark(22),
    Req_SetNotice(23),
    Req_Setting(24),
    Req_SkillLevelSetUp(37),
    Req_Search(46),
    Req_Signin(49),
    Req_GuildsInApplication(50),
    Req_50(50),
    Req_Rank(53),

    Res_InputGuildNameRequest(3),
    Res_LoadGuild_Done(58),
    Res_Rank(60),
    Res_RemoveGuild_Done(96),
    Res_IncMaxMemberNum_Done(113),
    Res_SetGradeName_Done(119),
    Res_SetGradeRight_Done(121),
    Res_SetGradeNameAndRight_Done(123),
    Res_SetMemberGrade_Done(125),
    Res_SetMemberCommitment_Done(127),
    Res_SetMark_Done(129),
    Res_SetNotice_Done(136),
    Res_ChangeSetting_Done(138),
    Res_IncPoint_Done(146),
    Res_SetSkill_Done(154),

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
