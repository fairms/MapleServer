package im.cave.ms.enums;

import java.util.Arrays;


public enum MapTransferType {
    DeleteListRecv(0),
    RegisterListRecv(1),
    DeleteListSend(2),
    RegisterListSend(3),
    Use(4),
    Unknown(5),
    TargetNotExist(6),
    TargetDied(7),
    NotAllowed(8),
    AlreadyInMap(9),
    RegisterFail(10),
    NotAllowedForVariousReason(11);

    private byte val;

    MapTransferType(int val) {
        this.val = (byte) val;
    }

    public static MapTransferType getByVal(byte val) {
        return Arrays.stream(values()).filter(mtt -> mtt.getVal() == val).findAny().orElse(null);
    }

    public byte getVal() {
        return val;
    }
}
