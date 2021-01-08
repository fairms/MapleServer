package im.cave.ms.client.quest.progress;

import im.cave.ms.client.character.MapleCharacter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.quest
 * @date 11/27 20:47
 */
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "quest_progressrequirement")
@DiscriminatorColumn(name = "progressType")
public abstract class QuestProgressRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "orderNum")
    private int order = 999;

    public abstract boolean isComplete(MapleCharacter chr);

    public abstract QuestProgressRequirement deepCopy();

}
