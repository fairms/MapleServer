package im.cave.ms.client;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.RecordType;
import im.cave.ms.tools.Util;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

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

    public RecordManager(MapleCharacter chr) {
        this.chr = chr;
    }

    public RecordManager(Account account) {
        this.account = account;
    }

    public Set<Record> getRecords() {
        if (chr != null) {
            return chr.getRecords();
        } else if (account != null) {
            return account.getRecords();
        }
        return new HashSet<>();
    }

    public void addRecord(Record record) {
        if (chr != null) {
            chr.getRecords().add(record);
        } else if (account != null) {
            account.getRecords().add(record);
        }
    }

    public Record getRecord(RecordType type, int key) {
        return Util.findWithPred(getRecords(), record -> record.getType() == type && record.getKey() == key);
    }

    public Record getRecord(RecordType type) {
        return Util.findWithPred(getRecords(), record -> record.getType() == type);
    }

}
