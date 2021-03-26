package im.cave.ms.connection.packet.result;

import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.FameAction;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet.result
 * @date 3/25 8:57
 */
@Getter
@Setter
public class FameResult {
    private FameAction action;
    private String str;
    private int arg1;
    private int arg2;
    private int arg3;

    public static FameResult alreadyAddInThisMonth() {
        FameResult fameResult = new FameResult();
        fameResult.action = FameAction.AlreadyAddInThisMonth;
        return fameResult;
    }

    public static FameResult receiveFame(String from, int mode) {
        FameResult fameResult = new FameResult();
        fameResult.action = FameAction.Receive;
        fameResult.str = from;
        fameResult.arg1 = mode;
        return fameResult;
    }

    public static FameResult addFame(String to, int mode, int newFame) {
        FameResult fameResult = new FameResult();
        fameResult.action = FameAction.Receive;
        fameResult.str = to;
        fameResult.arg1 = mode;
        fameResult.arg2 = newFame;
        return fameResult;
    }


    public static void encode(OutPacket out) {

    }
}
