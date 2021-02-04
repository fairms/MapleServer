package im.cave.ms.client.multiplayer.guilds;

import im.cave.ms.enums.GuildRight;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.multiplayer.guilds
 * @date 2/3 9:10
 */
@Entity
@Table(name = "guild_grade")
@Getter
@Setter
public class GuildGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int grade;
    private String name;
    private int right;

    public GuildGrade(int grade, String name, GuildRight... rights) {
        this.grade = grade;
        this.name = name;
        for (GuildRight right : rights) {
            this.right = this.right | right.getVal();
        }
    }

    public GuildGrade() {

    }

    public static List<GuildGrade> getDefault() {
        List<GuildGrade> grades = new ArrayList<>();
        grades.add(new GuildGrade(1, "族长", GuildRight.ALL));
        grades.add(new GuildGrade(2, "副族长", GuildRight.INVITE, GuildRight.VERIFY_APPLY));
        grades.add(new GuildGrade(3, "成员", GuildRight.NULL));
        grades.add(new GuildGrade(4, "成员", GuildRight.NULL));
        grades.add(new GuildGrade(5, "成员", GuildRight.NULL));
        return grades;
    }
}
