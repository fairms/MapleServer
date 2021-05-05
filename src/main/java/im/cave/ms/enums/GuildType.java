package im.cave.ms.enums;

import java.util.Arrays;

public enum GuildType {
    Req_FindGuildByCid(1),
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
    Req_Skill_Use(40),
    Req_Search(46),
    Req_CheckIn(49),
    Req_GuildsInApplication(50),
    Req_50(50),
    Req_Rank(53),

    Res_InputGuildNameRequest(3),
    Res_LoadGuild_Done(58),
    Res_FindGuild_Done(59),
    Res_Rank(60),
    Res_RemoveGuild_Done(96),
    Res_IncMaxMemberNum_Done(113),
    Res_ChangeLevelOrJob(117),
    Res_NotifyLoginOrLogout(118),
    Res_SetGradeName_Done(119),
    Res_SetGradeName_Unknown(120),
    Res_SetGradeRight_Done(121),
    Res_SetGradeRight_Unknown(122),
    Res_SetGradeNameAndRight_Done(123),
    Res_SetGradeNameAndRight_Unknown(124),
    Res_SetMemberGrade_Done(125),
    Res_SetMemberGrade_Unknown(126),
    Res_SetMemberCommitment_Done(127),
    Res_SetMemberCommitment_Unknown(128),
    Res_SetMark_Done(129),
    Res_SetMark_Unknown(130),

    Res_SetNotice_Done(136),
    Res_ChangeSetting_Done(138),
    Res_IncPoint_Done(146),

    Res_SetSkill_Done(154),
    Res_UseSkill_Done(164),
    Res_176(176), // guildID + 0000 + dateInt

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
