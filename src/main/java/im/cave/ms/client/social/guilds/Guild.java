package im.cave.ms.client.social.guilds;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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
    @CollectionTable(name = "guild_requestors", joinColumns = @JoinColumn(name = "guildId"))
    private List<GuildRequestor> requestors = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "grade_names", joinColumns = @JoinColumn(name = "guildID"))
    @Column(name = "gradeName")
    private List<String> gradeNames = new ArrayList<>();
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
    private int seasonPoints;
    private int allianceID;
    private int level;
    private int rank;
    private int ggp;
    private boolean appliable;
    private int joinSetting;
    private int reqLevel;
    private int battleSp;
}
