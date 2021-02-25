package im.cave.ms.enums;

import java.security.acl.Owner;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 2/24 19:34
 */
public enum DropTimeoutStrategy {
    TimeoutForNonOwner(0),
    TimeoutForNonOwnerParty(1),
    FFA(2), //无所有权
    Explosive(3);

    private byte val;

    DropTimeoutStrategy(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public void setVal(byte val) {
        this.val = val;
    }
}
