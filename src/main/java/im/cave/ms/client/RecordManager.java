package im.cave.ms.client;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.RecordType;
import lombok.Getter;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 1/7 17:23
 */
@Getter
public class RecordManager {
    private MapleCharacter chr;
    private Account account;
    private final List<Record> records;

    public RecordManager(MapleCharacter chr) {
        this.chr = chr;
        this.records = chr.getRecords();
    }

    public RecordManager(Account account) {
        this.account = account;
        this.records = account.getRecords();
    }

    public void addRecord(RecordType type) {
        records.add(new Record(type));
    }

    public void refreshRecord(RecordType type) {
        Record record = records.stream().filter(r -> r.getType().equals(type)).findAny().orElse(null);
        if (record != null) {
            record.setUsed(0);
            record.setLastRefresh(System.currentTimeMillis());
        } else {
            records.add(new Record(type));
        }
    }

    public int getRecordUsed(RecordType type) {
        Record record = records.stream().filter(r -> r.getType().equals(type)).findAny().orElse(null);
        if (record != null) {
            return record.getUsed();
        } else {
            return 0;
        }
    }

    public int getRecordRemaining(RecordType type) {
        Record record = records.stream().filter(r -> r.getType().equals(type)).findAny().orElse(null);
        if (record != null) {
            return record.getRemaining();
        } else {
            return 0;
        }
    }


    public boolean cost(RecordType type, int amount) {
        Record record = records.stream().filter(r -> r.getType().equals(type)).findAny().orElse(null);
        if (record != null) {
            return record.cost(amount);
        } else {
            return false;
        }
    }

    public boolean cost(RecordType type) {
        Record record = records.stream().filter(r -> r.getType().equals(type)).findAny().orElse(null);
        if (record != null) {
            return record.cost();
        } else {
            return false;
        }
    }

}
