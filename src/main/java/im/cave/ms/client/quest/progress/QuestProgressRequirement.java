package im.cave.ms.client.quest.progress;

import im.cave.ms.client.character.MapleCharacter;
import lombok.Data;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.quest
 * @date 11/27 20:47
 */
@Entity
@Data
@Table(name = "questprogressrequirements")
@DiscriminatorColumn(name = "progressType")
public abstract class QuestProgressRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int order = 999;

    public abstract boolean isComplete(MapleCharacter chr);
}
