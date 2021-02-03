package im.cave.ms.connection.packet.result;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.guilds.GuildMember;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.GuildType;

import static im.cave.ms.enums.GuildType.Res_ChangeSettings;
import static im.cave.ms.enums.GuildType.Res_IncPoint_Done;
import static im.cave.ms.enums.GuildType.Res_InputGuildName;
import static im.cave.ms.enums.GuildType.Res_LoadGuild_Done;
import static im.cave.ms.enums.GuildType.Res_Rank;
import static im.cave.ms.enums.GuildType.Res_SetMemberCommitment_Done;

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

    public static GuildResult setPointAndLevel(Guild guild) {
        GuildResult gr = new GuildResult(Res_IncPoint_Done);
        gr.guild = guild;
        return gr;
    }

    public void encode(OutPacket out) {
        out.write(type.getVal());
        switch (type) {
            case Res_InputGuildName:
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
            case Res_ChangeSettings:
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
        }
    }


    public static GuildResult inputGuildName() {
        return new GuildResult(Res_InputGuildName);
    }


    public static GuildResult updateRank(Guild guild) {
        GuildResult gri = new GuildResult(Res_Rank);
        gri.guild = guild;
        return gri;
    }

    public static GuildResult updateSetting(Guild guild) {
        GuildResult gri = new GuildResult(Res_ChangeSettings);
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
