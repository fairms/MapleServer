package im.cave.ms.client.quest;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.quest.progress.QuestProgressRequirement;
import im.cave.ms.enums.QuestStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
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
@Table(name = "quest")
@Getter
@Setter
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private QuestStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "questId")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<QuestProgressRequirement> progressRequirements;

    private long completedTime;

    @Transient
    private Map<String, String> properties = new HashMap<>();

    private int QrKey;
    private String qrValue;

    public Quest() {
        progressRequirements = new ArrayList<>();
    }

    public boolean isComplete(MapleCharacter chr) {
        return getProgressRequirements().stream().allMatch(pr -> pr.isComplete(chr));
    }

    public void addQuestProgressRequirement(QuestProgressRequirement qpr) {
        getProgressRequirements().add(qpr);
    }

    public void completeQuest() {
        setStatus(QuestStatus.Completed);
        setCompletedTime(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return String.format("%d, %s", getQrKey(), getQrValue());
    }
}

