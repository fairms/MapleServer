package im.cave.ms.client;

import im.cave.ms.enums.RecordType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 1/7 16:36
 */
@Entity
@Getter
@Setter
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "name")
    private RecordType type;
    private int used;
    private int total;
    private long lastUsed;        //上次使用时间
    private long lastRefresh;     //上次刷新时间
    private long refreshInterval; //刷新间隔时间

    public Record(RecordType type) {
        this.type = type;
        this.total = type.getMax();
        this.refreshInterval = type.getInterval();
    }

    public Record() {

    }

    public String getName() {
        return type.name();
    }

    public int getRemaining() {
        return total - used;
    }

    public boolean hasSurplus(int amount) {
        long now = System.currentTimeMillis();
        if (now > lastUsed + refreshInterval) {
            used = 0;
            lastRefresh = now;
        }
        return used + amount <= total;
    }

    public boolean hasSurplus() {
        return hasSurplus(1);
    }

    public boolean cost() {
        return cost(1);
    }

    public boolean cost(int amount) {
        if (hasSurplus(amount)) {
            used += amount;
            lastUsed = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

}
