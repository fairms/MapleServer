package im.cave.ms.enums;

import java.util.Arrays;

public enum MatrixStateType {
    DISASSEMBLED(0), INACTIVE(1), ACTIVE(2);
    private final int val;

    MatrixStateType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static MatrixStateType getStateByVal(int val) {
        return Arrays.stream(values()).filter(vst -> vst.getVal() == val).findAny().orElse(null);
    }
}
