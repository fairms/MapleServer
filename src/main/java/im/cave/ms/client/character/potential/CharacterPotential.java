package im.cave.ms.client.character.potential;

import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.provider.data.SkillData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


@Entity
@Table(name = "character_potential")
public class CharacterPotential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "potKey")
    private byte key;
    private int skillId;
    private byte slv;
    private byte grade;

    public CharacterPotential() {
    }

    public CharacterPotential(byte key, int skillId, byte slv, byte grade) {
        this.key = key;
        this.skillId = skillId;
        this.slv = slv;
        this.grade = grade;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getKey() {
        return key;
    }

    public void setKey(byte key) {
        this.key = key;
    }

    public int getSkillID() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public byte getSlv() {
        return slv;
    }

    public void setSlv(byte slv) {
        this.slv = slv;
    }

    public byte getGrade() {
        return grade;
    }

    public void setGrade(byte grade) {
        this.grade = grade;
    }

    public void encode(OutPacket out) {
        out.write(getKey());
        out.writeInt(getSkillID());
        out.write(getSlv());
        out.write(getGrade());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterPotential that = (CharacterPotential) o;
        return key == that.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public Skill getSkill() {
        Skill skill = SkillData.getSkill(getSkillID());
        if (skill != null) {
            skill.setCurrentLevel(getSlv());
        }
        return skill;
    }


    @Override
    public String toString() {
        return "CharacterPotential{" +
                "id=" + id +
                ", key=" + key +
                ", skillId=" + skillId +
                ", slv=" + slv +
                ", grade=" + grade +
                '}';
    }
}
