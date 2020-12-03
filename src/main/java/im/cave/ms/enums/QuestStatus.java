package im.cave.ms.enums;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 11/27 20:46
 */
public enum QuestStatus {
    NotStarted(0),
    Started(1),
    Completed(2);

    private final byte val;

    QuestStatus(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
