package im.cave.ms.connection.packet.result;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.guilds.GuildMember;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.GuildType;

import static im.cave.ms.enums.GuildType.Req_InviteGuild;
import static im.cave.ms.enums.GuildType.Res_BattleSkillOpen;
import static im.cave.ms.enums.GuildType.Res_ChangeLevelOrJob;
import static im.cave.ms.enums.GuildType.Res_CreateNewGuild_Done;
import static im.cave.ms.enums.GuildType.Res_IncMaxMemberNum_Done;
import static im.cave.ms.enums.GuildType.Res_IncPoint_Done;
import static im.cave.ms.enums.GuildType.Res_JoinGuild_Done;
import static im.cave.ms.enums.GuildType.Res_KickGuild_Done;
import static im.cave.ms.enums.GuildType.Res_LoadGuild_Done;
import static im.cave.ms.enums.GuildType.Res_NotifyLoginOrLogout;
import static im.cave.ms.enums.GuildType.Res_Rank_Reflash;
import static im.cave.ms.enums.GuildType.Res_SetGGP_Done;
import static im.cave.ms.enums.GuildType.Res_SetGradeName_Done;
import static im.cave.ms.enums.GuildType.Res_SetIGP_Done;
import static im.cave.ms.enums.GuildType.Res_SetMark_Done;
import static im.cave.ms.enums.GuildType.Res_SetMemberCommitment_Done;
import static im.cave.ms.enums.GuildType.Res_SetMemberGrade_Done;
import static im.cave.ms.enums.GuildType.Res_WithdrawGuild_Done;

public class GuildResult {

    private GuildType type;
    private Guild guild;
    private GuildMember member;
    private String[] gradeNames;
    private boolean online;
    private boolean showBox;
    private MapleCharacter chr;
    private int intArg;
    private String stringArg;
//    private GuildSkill skill;

    private GuildResult(GuildType type) {
        this.type = type;
    }

    public void encode(OutPacket out) {
        out.write(type.getVal());
        switch (type) {
            case Res_LoadGuild_Done:
                out.writeBool(guild != null); // ???
                out.writeBool(guild != null);
                if (guild != null) {
                    guild.encode(out);
                    out.writeInt(0); // aGuildNeedPoint
                }
                break;
            case Res_SetGradeName_Done:
                out.writeInt(guild.getId());
                for (String name : gradeNames) {
                    out.writeMapleAsciiString(name);
                }
                break;
            case Res_CreateNewGuild_Done:
                guild.encode(out);
                break;
            case Req_InviteGuild:
                out.writeInt(chr.getGuild().getId());
                out.writeMapleAsciiString(chr.getName());
                out.writeInt(chr.getLevel());
                out.writeInt(chr.getJob());
                out.writeInt(chr.getSubJob());
                break;
            case Res_JoinGuild_Done:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                member.encode(out);
                break;
            case Res_KickGuild_Done:
            case Res_WithdrawGuild_Done:
                out.writeInt(guild.getId());
                out.writeInt(intArg); // expelledID
                out.writeMapleAsciiString(stringArg); // expelledName
                break;
            case Res_SetMark_Done:
                out.writeInt(guild.getId());
                out.writeShort(guild.getMarkBg());
                out.write(guild.getMarkBgColor());
                out.writeShort(guild.getMark());
                out.write(guild.getMarkColor());
                break;
            case Res_IncMaxMemberNum_Done:
                out.writeInt(guild.getId());
                out.writeInt(guild.getMaxMembers());
                break;
            case Res_SetMemberGrade_Done:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.write(member.getGrade());
                break;
            case Res_ChangeLevelOrJob:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.writeInt(member.getLevel());
                out.writeInt(member.getJob());
                break;
            case Res_NotifyLoginOrLogout:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.writeBool(online);
                out.writeBool(showBox);
                break;
            case Res_SetMemberCommitment_Done:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.writeInt(member.getCommitment());
                out.writeInt(member.getDayCommitment());
                out.writeInt(member.getIgp());
                out.writeLong(member.getCommitmentIncTime());
                break;
            case Res_SetGGP_Done:
                out.writeInt(guild.getId());
                out.writeInt(guild.getGgp());
                break;
            case Res_SetIGP_Done:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.writeInt(member.getIgp());
                break;
            case Res_IncPoint_Done:
                out.writeInt(guild.getId());
                out.writeInt(guild.getPoints());
                out.writeInt(guild.getLevel());
                out.writeInt(guild.getGgp());
                break;
//            case Res_SetSkill_Done:
//                out.writeInt(guild.getId());
//                out.writeInt(skill.getSkillID());
//                out.writeInt(intArg); // nBuyCharacterID
//                out.encode(skill);
//                break;
            case Res_BattleSkillOpen:
//                out.writeInt(guild.getBattleSp());
                break;
            case Res_Rank_Reflash:
                out.writeInt(guild.getRank());
                break;
            case Res_SetSkill_LevelSet_Unknown:
                out.writeBool(false);
                break;
            case Res_ChangeSettings:
                out.writeInt(guild.getLeaderId());  //两个ID todo 暂时不清楚
                out.writeInt(guild.getLeaderId());
                out.writeBool(guild.isAppliable());
                out.writeInt(guild.getTrendActive());
                out.writeInt(guild.getTrendTime());
                out.writeInt(guild.getTrendAges());
                break;
        }
    }


    public static GuildResult updateSetting(Guild guild) {
        GuildResult gri = new GuildResult(Res_LoadGuild_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult loadGuild(Guild guild) {
        GuildResult gri = new GuildResult(Res_LoadGuild_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult setGradeName(Guild guild, String[] gradeNames) {
        GuildResult gri = new GuildResult(Res_SetGradeName_Done);
        gri.guild = guild;
        gri.gradeNames = gradeNames;
        return gri;
    }

    public static GuildResult createNewGuild(Guild guild) {
        GuildResult gri = new GuildResult(Res_CreateNewGuild_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult inviteGuild(MapleCharacter chr) {
        GuildResult gri = new GuildResult(Req_InviteGuild);
        gri.chr = chr;
        return gri;
    }

    public static GuildResult joinGuild(Guild guild, GuildMember member) {
        GuildResult gri = new GuildResult(Res_JoinGuild_Done);
        gri.guild = guild;
        gri.member = member;
        return gri;
    }

    public static GuildResult leaveGuild(Guild guild, int leaverID, String leaverName, boolean expelled) {
        GuildResult gri = new GuildResult(expelled ? Res_KickGuild_Done : Res_WithdrawGuild_Done);
        gri.guild = guild;
        gri.intArg = leaverID;
        gri.stringArg = leaverName;
        return gri;
    }

    public static GuildResult setMark(Guild guild) {
        GuildResult gri = new GuildResult(Res_SetMark_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult setMemberGrade(Guild guild, GuildMember member) {
        GuildResult gri = new GuildResult(Res_SetMemberGrade_Done);
        gri.guild = guild;
        gri.member = member;
        return gri;
    }

    public static GuildResult changeLevelOrJob(Guild guild, GuildMember member) {
        GuildResult gri = new GuildResult(Res_ChangeLevelOrJob);
        gri.guild = guild;
        gri.member = member;
        return gri;
    }

    public static GuildResult notifyLoginOrLogout(Guild guild, GuildMember member, boolean online, boolean showBox) {
        GuildResult gri = new GuildResult(Res_NotifyLoginOrLogout);
        gri.guild = guild;
        gri.member = member;
        gri.online = online;
        gri.showBox = showBox;
        return gri;
    }

    public static GuildResult msg(GuildType type) {
        return new GuildResult(type);
    }

    public static GuildResult setMemberCommitment(Guild guild, GuildMember member) {
        GuildResult gri = new GuildResult(Res_SetMemberCommitment_Done);
        gri.guild = guild;
        gri.member = member;
        return gri;
    }

    public static GuildResult setMemberIgp(Guild guild, GuildMember member) {
        GuildResult gri = new GuildResult(Res_SetIGP_Done);
        gri.guild = guild;
        gri.member = member;
        return gri;
    }

    public static GuildResult setGgp(Guild guild) {
        GuildResult gri = new GuildResult(Res_SetGGP_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult setPointAndLevel(Guild guild) {
        GuildResult gr = new GuildResult(Res_IncPoint_Done);
        gr.guild = guild;
        return gr;
    }

//    public static GuildResult setSkill(Guild guild, GuildSkill skill, int buyCharID) {
//        GuildResult gr = new GuildResult(Res_SetSkill_Done);
//        gr.guild = guild;
//        gr.skill = skill;
//        gr.intArg = buyCharID;
//        return gr;
//    }

    public static GuildResult battleSkillOpen(Guild guild) {
        GuildResult gr = new GuildResult(Res_BattleSkillOpen);
        gr.guild = guild;
        return gr;
    }

    public static GuildResult setRank(Guild guild) {
        GuildResult gr = new GuildResult(Res_Rank_Reflash);
        gr.guild = guild;
        return gr;
    }

    public static GuildResult incMaxMemberNum(Guild guild) {
        GuildResult gr = new GuildResult(Res_IncMaxMemberNum_Done);
        gr.guild = guild;
        return gr;
    }

}
