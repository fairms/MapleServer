package im.cave.ms.client;

import im.cave.ms.enums.RecordType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 1/7 16:36
 */
@Entity
@Getter
@Setter
@Builder
@Table(name = "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "`type`")
    private RecordType type;
    @Column(name = "`key`")
    private int key;
    @Column(name = "`value`")
    private int value;
    private long lastUpdated;
    private long lastReset;

    public Record() {

    }

    public Record(RecordType type) {
        this.type = type;
        this.lastReset = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
    }

    public Record(RecordType type, int value) {
        this.type = type;
        this.value = value;
        this.lastReset = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
    }

    public Record(long id, RecordType type, int key, int value, long lastUpdated, long lastReset) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.lastUpdated = lastUpdated;
        this.lastReset = lastReset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (key != record.key) return false;
        return type == record.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + key;
        return result;
    }
}
