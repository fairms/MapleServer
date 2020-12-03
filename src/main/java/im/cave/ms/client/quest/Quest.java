package im.cave.ms.client.quest;

import im.cave.ms.client.quest.progress.QuestProgressRequirement;
import im.cave.ms.enums.QuestStatus;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.quest
 * @date 11/27 20:39
 */
@Entity
@Table
@Data
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private QuestStatus status;

    @OneToMany
    @JoinColumn(name = "questId")
    @Cascade(CascadeType.DELETE)
    private List<QuestProgressRequirement> progressReqirements;

    private long completedTime;
    @Transient
    private Map<String, String> customData = new HashMap<>();
}

