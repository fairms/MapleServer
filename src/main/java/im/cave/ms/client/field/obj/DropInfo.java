package im.cave.ms.client.field.obj;

import im.cave.ms.constants.GameConstants;
import im.cave.ms.tools.Util;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 12/10 14:24
 */
@Getter
@Setter
@Entity
@Table(name = "mob_drops")
public class DropInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int mobId;
    private int itemID;
    private int chance;
    @Transient
    private int money;
    @Transient
    private int minMoney;
    @Transient
    private int maxMoney;
    private int minQuantity = 1;
    private int maxQuantity = 1;
    @Transient
    private int quantity = 1;


    public DropInfo() {

    }

    public DropInfo(int chance, int minMoney, int maxMoney) {
        this.chance = chance;
        this.minMoney = minMoney;
        this.maxMoney = maxMoney;
        generateNextDrop();

    }

    public void generateNextDrop() {
        if (getMaxMoney() > 0) {
            setMoney(getMinMoney() + Util.getRandom(getMaxMoney() - getMinMoney()));
        } else {
            setQuantity(getMinQuantity() + Util.getRandom(getMaxQuantity() - getMinQuantity()));
        }
    }

    public boolean willDrop(int dropRate) {
        // Added 50x multiplier for the dropping chance if the item is a Quest item.
        int chance = getChance();
        chance *= (100 + dropRate) / 100D;
        return Util.succeedProp(chance, GameConstants.MAX_DROP_CHANCE);
    }

    public boolean isMoney() {
        return getMoney() > 0 || getMinMoney() > 0;
    }

    public DropInfo deepCopy() {
        DropInfo di = new DropInfo();
        di.setItemID(getItemID());
        di.setChance(getChance());
        di.setMoney(getMoney());
        di.setMinMoney(getMinMoney());
        di.setMaxMoney(getMaxMoney());
        di.setMinQuantity(getMinQuantity());
        di.setMaxQuantity(getMaxQuantity());
        di.setQuantity(getQuantity());

        return di;
    }

    @Override
    public String toString() {
        if (getItemID() != 0) {
            return String.format("Item %d, chance %d", getItemID(), getChance());
        } else {
            return String.format("%d mesos.", getMoney());
        }
    }
    
}
