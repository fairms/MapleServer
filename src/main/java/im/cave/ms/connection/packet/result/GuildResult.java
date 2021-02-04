package im.cave.ms.connection.packet.result;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.guilds.GuildGrade;
import im.cave.ms.client.multiplayer.guilds.GuildMember;
import im.cave.ms.client.multiplayer.guilds.GuildSkill;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.GuildType;

import static im.cave.ms.enums.GuildType.Res_ChangeSetting_Done;
import static im.cave.ms.enums.GuildType.Res_IncPoint_Done;
import static im.cave.ms.enums.GuildType.Res_InputGuildNameRequest;
import static im.cave.ms.enums.GuildType.Res_LoadGuild_Done;
import static im.cave.ms.enums.GuildType.Res_Rank;
import static im.cave.ms.enums.GuildType.Res_SetGradeNameAndRight_Done;
import static im.cave.ms.enums.GuildType.Res_SetGradeName_Done;
import static im.cave.ms.enums.GuildType.Res_SetGradeRight_Done;
import static im.cave.ms.enums.GuildType.Res_SetMark_Done;
import static im.cave.ms.enums.GuildType.Res_SetMemberCommitment_Done;
import static im.cave.ms.enums.GuildType.Res_SetMemberGrade_Done;
import static im.cave.ms.enums.GuildType.Res_SetNotice_Done;
import static im.cave.ms.enums.GuildType.Res_SetSkill_Done;

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
    private GuildSkill skill;

    private GuildResult(GuildType type) {
        this.type = type;
    }


    public void encode(OutPacket out) {
        out.write(type.getVal());
        switch (type) {
            case Res_InputGuildNameRequest:
                break;
            case Res_LoadGuild_Done:
                guild.encode(out);
                break;
            case Res_SetMemberCommitment_Done:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.writeInt(member.getCommitment());
                out.writeInt(member.getDayCommitment());
                out.writeInt(member.getIgp());
                out.writeLong(member.getCommitmentIncTime());
                break;
            case Res_IncPoint_Done:
                out.writeInt(guild.getId());
                out.writeInt(guild.getPoints());
                out.writeInt(guild.getLevel());
                out.writeInt(guild.getGgp());
                out.writeInt(0); //可能是排名之类的?
                break;
            case Res_ChangeSetting_Done:
                out.writeInt(guild.getLeaderId());  //两个ID todo 暂时不清楚
                out.writeInt(guild.getLeaderId());
                out.writeBool(guild.isAppliable());
                out.writeInt(guild.getTrendActive());
                out.writeInt(guild.getTrendTime());
                out.writeInt(guild.getTrendAges());
                break;
            case Res_Rank: //三个排名?
                out.writeShort(0);
                out.writeShort(guild.getRank()); //声望排名
                out.writeShort(0);
                break;
            case Res_SetNotice_Done:
                out.writeInt(guild.getId());
                out.writeInt(intArg);
                out.writeMapleAsciiString(guild.getNotice());
                break;
            case Res_SetGradeName_Done:
                out.writeInt(guild.getId());
                out.writeInt(intArg);
                for (GuildGrade grade : guild.getGrades()) {
                    out.writeMapleAsciiString(grade.getName());
                }
                break;
            case Res_SetGradeRight_Done:
                out.writeInt(guild.getId());
                out.writeInt(intArg);
                for (GuildGrade grade : guild.getGrades()) {
                    out.writeInt(grade.getRight());
                }
                break;
            case Res_SetGradeNameAndRight_Done:
                out.writeInt(guild.getId());
                out.writeInt(intArg);
                for (GuildGrade grade : guild.getGrades()) {
                    out.writeInt(grade.getRight());
                    out.writeMapleAsciiString(grade.getName());
                }
                break;
            case Res_SetMemberGrade_Done:
                out.writeInt(guild.getId());
                out.writeInt(member.getCharId());
                out.write(member.getGrade());
                break;
            case Res_SetSkill_Done:
                out.writeInt(guild.getId());
                out.writeInt(skill.getSkillId());
                out.writeInt(intArg);
                skill.encode(out);
                break;
            case Res_SetMark_Done:
                out.writeInt(guild.getId());
                out.writeInt(-1);
                out.write(0);
                out.writeShort(guild.getMarkBg());
                out.write(guild.getMarkBgColor());
                out.writeShort(guild.getMark());
                out.write(guild.getMarkBg());
                out.writeLong(0);
                break;
        }
    }

    public static GuildResult setMark(Guild guild) {
        GuildResult gr = new GuildResult(Res_SetMark_Done);
        gr.guild = guild;
        return gr;
    }

    public static GuildResult setSkill(Guild guild, GuildSkill skill, int operatorId) {
        GuildResult gr = new GuildResult(Res_SetSkill_Done);
        gr.guild = guild;
        gr.skill = skill;
        gr.intArg = operatorId;
        return gr;
    }

    public static GuildResult setMemberGrade(Guild guild, GuildMember gm) {
        GuildResult gr = new GuildResult(Res_SetMemberGrade_Done);
        gr.guild = guild;
        gr.member = gm;
        return gr;
    }


    public static GuildResult setPointAndLevel(Guild guild) {
        GuildResult gr = new GuildResult(Res_IncPoint_Done);
        gr.guild = guild;
        return gr;
    }

    public static GuildResult updateGradeName(Guild guild, int operatorId) {
        GuildResult gr = new GuildResult(Res_SetGradeName_Done);
        gr.guild = guild;
        gr.intArg = operatorId;
        return gr;
    }

    public static GuildResult setGradeNameAndRightDone(Guild guild, int operatorId) {
        GuildResult gr = new GuildResult(Res_SetGradeNameAndRight_Done);
        gr.guild = guild;
        gr.intArg = operatorId;
        return gr;
    }

    public static GuildResult setGradeRightDone(Guild guild, int operatorId) {
        GuildResult gri = new GuildResult(Res_SetGradeRight_Done);
        gri.guild = guild;
        gri.intArg = operatorId;
        return gri;
    }

    public static GuildResult inputGuildName() {
        return new GuildResult(Res_InputGuildNameRequest);
    }

    public static GuildResult setNoticeDone(Guild guild, MapleCharacter chr) {
        GuildResult gri = new GuildResult(Res_SetNotice_Done);
        gri.guild = guild;
        gri.intArg = chr.getId();
        return gri;
    }

    public static GuildResult updateRank(Guild guild) {
        GuildResult gri = new GuildResult(Res_Rank);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult updateSetting(Guild guild) {
        GuildResult gri = new GuildResult(Res_ChangeSetting_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult loadResult(Guild guild) {
        GuildResult gri = new GuildResult(Res_LoadGuild_Done);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult setMemberCommitment(Guild guild, GuildMember member) {
        GuildResult gri = new GuildResult(Res_SetMemberCommitment_Done);
        gri.guild = guild;
        gri.member = member;
        return gri;
    }
}
