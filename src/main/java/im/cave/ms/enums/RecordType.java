package im.cave.ms.enums;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/7 17:04
 */
public enum RecordType {
    MAP_TRANSFER_COUPON_FREE(1, 60 * 60 * 24 * 7),
    MAP_TRANSFER_COUPON_CASH(1);


    private final int max;
    private final long interval; //秒

    RecordType(int max, long interval) {
        this.max = max;
        this.interval = interval;
    }

    RecordType(int max) {
        this.max = max;
        this.interval = 0;
    }


    public int getMax() {
        return max;
    }

    public long getInterval() {
        return interval * 1000; //毫秒
    }

}
