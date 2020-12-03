package im.cave.ms.client.quest.progress;

import im.cave.ms.client.character.MapleCharacter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created on 3/2/2018.
 */
@Entity
@DiscriminatorValue("money")
public class QuestProgressMoneyRequirement extends QuestProgressRequirement {

    @Column(name = "requiredCount")
    private int money;

    public QuestProgressMoneyRequirement() {
    }

    public QuestProgressMoneyRequirement(int money) {
        this.money = money;
    }

    @Override
    public boolean isComplete(MapleCharacter chr) {
        return chr.getMeso() >= getMoney();
    }

    public QuestProgressRequirement deepCopy() {
        QuestProgressMoneyRequirement qpmr = new QuestProgressMoneyRequirement();
        qpmr.setMoney(getMoney());
        qpmr.setOrder(getOrder());
        return qpmr;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    public void addMoney(int money) {
        setMoney(getMoney() + money);
    }
}
