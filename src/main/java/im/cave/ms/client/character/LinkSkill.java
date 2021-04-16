package im.cave.ms.client.character;

import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Table(name = "skill_link")
@Entity
public class LinkSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int fromId;
    private int toId;
    private int skillId;
    private int level;
    private long time;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkSkill that = (LinkSkill) o;
        return fromId == that.fromId && skillId == that.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, skillId);
    }


    public void encode(OutPacket out) {
        out.writeInt(getFromId());
        out.writeInt(getToId() == 0 ? getFromId() : getToId());
        out.writeInt(getSkillId());
        out.writeShort(getLevel());
        out.writeLong(getTime());
        out.writeInt(0);
    }

}
