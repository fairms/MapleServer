package im.cave.ms.client.social.guilds;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.network.netty.OutPacket;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created on 3/21/2018.
 */
@Entity
@Table(name = "guild_requestor")
public class GuildRequestor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int charId;
    @Transient
    private MapleCharacter chr;
    private String name;
    private int job;
    private int level;
    @Column(name = "loggedIn")
    private boolean online;

    public GuildRequestor() {
    }

    public GuildRequestor(MapleCharacter chr) {
        this.chr = chr;
        updateInfoFromChar(chr);
    }

    public void updateInfoFromChar(MapleCharacter chr) {
        setName(chr.getName());
        setCharID(chr.getId());
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

    public void encode(OutPacket out) {
        out.writeAsciiString(getName(), 13);
        out.writeInt(getJob());
        out.writeInt(getLevel());
        out.writeInt(0);
        out.writeInt(isOnline() ? 1 : 0);
        // Following is guild specific info, requestors don't have these
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GuildMember && ((GuildMember) obj).getChr().equals(getChr());
    }

    public int getCharID() {
        return charId;
    }

    public void setCharID(int charId) {
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

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }
}
