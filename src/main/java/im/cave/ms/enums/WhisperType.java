package im.cave.ms.enums;

import im.cave.ms.tools.Util;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/9 23:20
 */
public enum WhisperType {
    Req_Whisper(6),
    Res_Whisper(10),
    Req_Find_Friend(68),
    Res_Find_Friend(72),
    ;
    private final byte val;

    WhisperType(byte val) {
        this.val = val;
    }

    WhisperType(int val) {
        this.val = (byte) val;
    }

    public static WhisperType getByVal(byte val) {
        return Util.findWithPred(values(), whisperType -> whisperType.val == val);
    }

    public byte getVal() {
        return val;
    }
}
