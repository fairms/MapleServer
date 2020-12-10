package im.cave.ms.client.quest;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.quest.progress.QuestProgressItemRequirement;
import im.cave.ms.client.quest.progress.QuestProgressMobRequirement;
import im.cave.ms.client.quest.progress.QuestProgressMoneyRequirement;
import im.cave.ms.client.quest.progress.QuestProgressRequirement;
import im.cave.ms.enums.QuestStatus;
import im.cave.ms.tools.Util;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return String.format("%d, %s", getQrKey(), getQRValue());
    }

    public void handleMobKill(int mobId) {
        QuestProgressMobRequirement qpmr = (QuestProgressMobRequirement) getProgressRequirements()
                .stream()
                .filter(q -> q instanceof QuestProgressMobRequirement &&
                        ((QuestProgressMobRequirement) q).getMobID() == mobId)
                .findFirst().get();
        if (qpmr.getCurrentCount() < qpmr.getRequiredCount()) {
            qpmr.incCurrentCount(1);
        }

    }

    public boolean reqMob(int mobId) {
        return getProgressRequirements().stream().filter(q -> q instanceof QuestProgressMobRequirement)
                .map(q -> ((QuestProgressMobRequirement) q))
                .anyMatch(q -> q.getMobID() == mobId);
    }

    public boolean hasMoneyReq() {
        return getProgressRequirements().stream().anyMatch(q -> q instanceof QuestProgressMoneyRequirement);
    }

    public void addMoney(int money) {
        getProgressRequirements().stream()
                .filter(q -> q instanceof QuestProgressMoneyRequirement)
                .map(q -> (QuestProgressMoneyRequirement) q)
                .findAny().ifPresent(qpmr -> qpmr.addMoney(money));
    }


    public String getQRValue() {
        if (qrValue != null && !qrValue.equalsIgnoreCase("")) {
            return qrValue;
        } else {
            StringBuilder sb = new StringBuilder();
            if (getProgressRequirements() == null) {
                return "";
            }
            List<QuestProgressMobRequirement> requirements = new ArrayList<>(getMobReqs());
            requirements.sort(Comparator.comparingInt(QuestProgressMobRequirement::getOrder));
            for (QuestProgressMobRequirement qpmr : requirements) {
                sb.append(Util.leftPaddedString(3, '0', qpmr.getValue()));
            }
            return sb.toString();
        }
    }

    public void convertQRValueToProperties() {
        String val = getQRValue();
        String[] props = val.split(";");
        for (String prop : props) {
            String[] keyVal = prop.split("=");
            if (keyVal.length == 2) {
                setProperty(keyVal[0], keyVal[1]);
            }
        }
    }

    public void setProperty(String key, String value) {
        getProperties().put(key, value);
        setQRValueToProperties();
    }

    private void setQRValueToProperties() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : getProperties().entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        setQrValue(stringBuilder.toString());
    }


    public List<QuestProgressMobRequirement> getMobReqs() {
        return getProgressRequirements().stream().filter(qpr -> qpr instanceof QuestProgressMobRequirement)
                .map(qpr -> (QuestProgressMobRequirement) qpr).collect(Collectors.toList());
    }

    public List<QuestProgressItemRequirement> getItemReqs() {
        return getProgressRequirements().stream().filter(qpr -> qpr instanceof QuestProgressItemRequirement)
                .map(qpr -> (QuestProgressItemRequirement) qpr).collect(Collectors.toList());
    }

    public QuestProgressMobRequirement getMobReqByMobID(int mobID) {
        return getMobReqs().stream().filter(qpmr -> qpmr.getMobID() == mobID).findFirst().orElse(null);
    }

    public boolean hasMobReq(int mobID) {
        return getMobReqByMobID(mobID) != null;
    }
}

