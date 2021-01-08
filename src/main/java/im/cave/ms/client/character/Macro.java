package im.cave.ms.client.character;


import im.cave.ms.network.db.InlinedIntArrayConverter;
import im.cave.ms.network.netty.OutPacket;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Entity
@Table(name = "macro")
public class Macro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private boolean muted;
    @Convert(converter = InlinedIntArrayConverter.class)
    private final List<Integer> skills = new ArrayList<>(Arrays.asList(0, 0, 0));

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getSkills() {
        return skills;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setSkillAtPos(int pos, int skillID) {
        if (pos >= 0 && pos < 3) {
            getSkills().set(pos, skillID);
        }
    }

    public void encode(OutPacket out) {
        out.writeMapleAsciiString(name);
        out.writeBool(isMuted());
        for (int i : getSkills()) {
            out.writeInt(i);
        }
    }
}
