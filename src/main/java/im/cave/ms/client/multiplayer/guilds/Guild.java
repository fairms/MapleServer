package im.cave.ms.client.multiplayer.guilds;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.packet.result.GuildResult;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.social.guilds
 * @date 1/4 15:59
 */
@Getter
@Setter
@Entity
@Table(name = "guild")
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int leaderId;
    private int worldId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @CollectionTable(name = "guild_requestor", joinColumns = @JoinColumn(name = "guildId"))
    private List<GuildRequestor> requestors = new ArrayList<>();
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "guildId")
    private List<GuildGrade> grades = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "guildId")
    private List<GuildMember> members = new ArrayList<>();
    private int markBg;
    private int markBgColor;
    private int mark;
    private int markColor;
    private int maxMembers;
    private String notice;
    private int points;
    @Transient
    private int seasonPoints;
    @Transient
    private int allianceID; //联盟ID
    private int level;
    private int rank; // 似乎没用
    private int ggp; //贡献
    private boolean appliable; //允许申请
    private int battleSp; //高级家族技能技能点
    private int trendActive;
    private int trendTime;
    private int trendAges;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "guildId")
    private List<GuildSkill> skills;


    public GuildMember getGuildLeader() {
        return getMemberByCharId(getLeaderId());
    }

    private GuildMember getMemberByCharId(int charId) {
        return getMembers().stream().filter(gm -> gm.getCharId() == charId).findAny().orElse(null);

    }

    public Guild() {
        setGrades(GuildGrade.getDefault());
        setAppliable(true);
        setMaxMembers(GameConstants.GUILD_MAX_MEMBERS_DEFAULT);
        setLevel(1);
        setName("Default");
    }

    public void setGrades(List<GuildGrade> grades) {
        getGrades().clear();
        getGrades().addAll(grades);
    }

    public void setGradeName(String name, int grade) {
        getGrades().get(grade - 1).setName(name);
    }

    public void setGradeRight(int right, int grade) {
        getGrades().get(grade - 1).setRight(right);
    }

    public int getAverageMemberLevel() {
        int size = getMembers().size();
        int averageLevel = 0;
        for (GuildMember gm : getMembers()) {
            averageLevel += gm.getLevel();
        }
        return averageLevel / size;
    }

    public List<GuildMember> getOnlineMembers() {
        return getMembers().stream().filter(GuildMember::isOnline).collect(Collectors.toList());
    }

    public void broadcast(OutPacket out) {
        getOnlineMembers().stream().filter(gm -> gm.getChr() != null).forEach(gm -> gm.getChr().write(out));
    }

    public void encode(OutPacket out) {
        out.writeInt(50000);
        out.writeInt(1);
        out.write(0);
        out.writeInt(getId());
        out.writeMapleAsciiString(getName());
        for (GuildGrade grade : getGrades()) {
            out.writeMapleAsciiString(grade.getName()); // 5 times total
            out.writeInt(grade.getRight());
        }
        out.writeShort(getMembers().size());
        getMembers().forEach(gm -> out.writeInt(gm.getCharId()));
        getMembers().forEach(gm -> gm.encode(out));
        out.writeShort(getRequestors().size());
        //todo encode::requestors
        out.writeInt(getMaxMembers());
        out.writeShort(getMarkBg());
        out.write(getMarkBgColor());
        out.writeShort(getMark());
        out.write(getMarkColor());
        out.writeMapleAsciiString(getNotice());
        out.writeInt(getPoints());
        out.writeInt(getSeasonPoints());
        out.writeInt(getAllianceID());
        out.write(getLevel());
        out.writeShort(getRank());
        out.writeInt(getGgp());
        out.writeShort(0);
        out.writeAsciiString("19691230", 4); //啥意思呢
        out.writeBool(isAppliable());
        out.writeLong(116444736000000000L);
        out.writeZeroBytes(12); //可能是家族加入设置 ...
        out.writeShort(0); //getSkills().size()
        //skillId int
        //skillLevel short
        //maxTime long
        //out.writeMapleAsciiString(); 升级者的名字? buy char name
        //out.writeShort(0)  extend char name
        out.writeZeroBytes(9);
        out.writeInt(GameConstants.guildExp.length);
        for (int exp : GameConstants.guildExp) {
            out.writeInt(exp);
        }

    }

    public GuildMember getMemberByChar(MapleCharacter chr) {
        return getMembers().stream().filter(gm -> gm.getChr().equals(chr)).findAny().orElse(null);
    }


    //解散
    public void disband() {
        broadcast(WorldPacket.guildResult(GuildResult.guildRemoved(this)));
    }

    public void addMember(MapleCharacter chr) {
        addMember(new GuildMember(chr));
    }

    public void addMember(GuildMember guildMember) {
        getMembers().add(guildMember);
        if (guildMember.getChr() != null && guildMember.getChr().getGuild() == null) {
            guildMember.getChr().setGuild(this);
        }
        if (getLeaderId() == 0) {
            setLeader(guildMember);
        } else {
            guildMember.setGrade(getGrades().size());
        }
    }

    private void setLeader(GuildMember guildMember) {
        int oldGrade = guildMember.getGrade();
        if (getLeaderId() != 0) {
            getMemberByCharId(getLeaderId()).setGrade(oldGrade);
        }
        this.leaderId = guildMember.getCharId();
        guildMember.setGrade(1);
    }


    public void addCommitmentToChar(MapleCharacter chr, int commitment) {
        GuildMember gm = getMemberByCharID(chr.getId());
        if (gm != null && gm.getRemainingDayCommitment() > 0) {
            int commitmentInc = gm.getDayCommitment() + commitment > GameConstants.MAX_DAY_COMMITMENT
                    ? GameConstants.MAX_DAY_COMMITMENT - gm.getDayCommitment()
                    : commitment;
            addPoints(commitment);
            gm.addCommitment(commitmentInc);
            addGgp((int) (commitmentInc * GameConstants.GGP_PER_CONTRIBUTION));
        }
    }

    private void addGgp(int ggp) {
        setGgp(getGgp() + ggp);
    }

    public GuildMember getMemberByCharID(int id) {
        return getMembers().stream().filter(gm -> gm.getCharId() == id).findAny().orElse(null);
    }

    private void addPoints(int commitment) {
        setPoints(getPoints() + commitment);
        if (getLevel() < GameConstants.MAX_GUILD_LV && getPoints() > GameConstants.getExpRequiredForNextGuildLevel(getLevel())) {
            setLevel(getLevel() + 1);
            broadcast(WorldPacket.chatMessage(String.format("%s has reached level %d!",
                    getName(), getLevel()), ChatType.Notice2));
        }
        broadcast(WorldPacket.guildResult(GuildResult.setPointAndLevel(this)));
    }


    public int getSpentSp() {
        return getSkills().stream().mapToInt(GuildSkill::getLevel).sum();
    }

    public GuildSkill getSkillById(int skillId) {
        return Util.findWithPred(getSkills(), guildSkill -> guildSkill.getId() == skillId);
    }

    public int getSpentBattleSp() {
        int spentSp = 0;
        for (int i = 91001022; i < 91001024; i++) {
            GuildSkill gs = getSkillById(i);
            spentSp += gs == null ? 0 : gs.getLevel();
        }
        return spentSp;
    }

    public void addGuildSkill(GuildSkill skill) {
        getSkills().add(skill);
    }

    public void encodeForRemote(OutPacket out) {
        out.writeInt(getId());
        out.writeMapleAsciiString(getName());
        out.writeShort(getMarkBg());
        out.write(getMarkBgColor());
        out.writeShort(getMark());
        out.write(getMarkColor());
    }

    public static void defaultEncodeForRemote(OutPacket out) {
        out.writeInt(0);
        out.writeMapleAsciiString("");
        out.writeShort(0);
        out.write(0);
        out.writeShort(0);
        out.write(0);
    }

    public void incMaxMembers(int amount) {
        setMaxMembers(getMaxMembers() + amount);
    }


    public int getLeaderId() {
        return leaderId;
    }
}
