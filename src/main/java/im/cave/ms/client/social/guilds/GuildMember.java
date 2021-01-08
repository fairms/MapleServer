package im.cave.ms.client.social.guilds;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.network.netty.OutPacket;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "guild_member")
public class GuildMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int charId;
    @Transient
    private MapleCharacter chr;
    private int grade;
    private int allianceGrade;
    private int commitment;
    private int dayCommitment;
    private int igp;
    private long commitmentIncTime;
    private String name;
    private int job;
    private int level;
    @Column(name = "loggedIn")
    private boolean online;

    public GuildMember() {
    }

    public GuildMember(MapleCharacter chr) {
        this.chr = chr;
        updateInfoFromChar(chr);
        grade = 5;
        allianceGrade = 5;
    }

    public void updateInfoFromChar(MapleCharacter chr) {
        setName(chr.getName());
        setCharId(chr.getId());
        setJob(chr.getJob());
        setLevel(chr.getLevel());
        setOnline(chr.isOnline());
    }

    public MapleCharacter getChr() {
        return chr;
    }

    public void setChr(MapleCharacter chr) {
        this.chr = chr;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getAllianceGrade() {
        return allianceGrade;
    }

    public void setAllianceGrade(int allianceGrade) {
        this.allianceGrade = allianceGrade;
    }

    public int getCommitment() {
        return commitment;
    }

    public void setCommitment(int commitment) {
        this.commitment = commitment;
    }

    public int getDayCommitment() {
        return dayCommitment;
    }

    public void setDayCommitment(int dayCommitment) {
        this.dayCommitment = dayCommitment;
    }

    public int getIgp() {
        return igp;
    }

    public void setIgp(int igp) {
        this.igp = igp;
    }

    public long getCommitmentIncTime() {
        return commitmentIncTime;
    }

    public void setCommitmentIncTime(long commitmentIncTime) {
        this.commitmentIncTime = commitmentIncTime;
    }

    public void encode(OutPacket out) {
        out.writeAsciiString(getName(), 13);
        out.writeInt(getJob());
        out.writeInt(getLevel());
        out.writeInt(getGrade());
        out.writeInt(isOnline() ? 1 : 0);
        out.writeInt(getAllianceGrade());
        out.writeInt(getCommitment());
        out.writeInt(getDayCommitment());
        out.writeInt(getIgp());
        out.writeLong(getCommitmentIncTime());
    }

    public int getRemainingDayCommitment() {
        return GameConstants.MAX_DAY_COMMITMENT - getDayCommitment();
    }

//    public void addCommitment(int commitment) {
//        setCommitment(getCommitment() + commitment);
//        setDayCommitment(getDayCommitment() + commitment);
//        addIgp((int) (commitment * GameConstants.IGP_PER_CONTRIBUTION));
//        setCommitmentIncTime(System.currentTimeMillis());
//        if (getChr() != null) {
//            Guild g = getChr().getGuild();
//            g.broadcast(WvsContext.guildResult(GuildResult.setMemberCommitment(g, this)));
//            getChr().write(WvsContext.message(MessageType.INC_COMMITMENT_MESSAGE, commitment, "", (byte) 0));
//        }
//
//    }

    private void addIgp(int igp) {
        setIgp(getIgp() + igp);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GuildMember && ((GuildMember) obj).getChr().equals(getChr());
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
