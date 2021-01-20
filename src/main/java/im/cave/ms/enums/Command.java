package im.cave.ms.enums;

import im.cave.ms.tools.Util;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/20 9:48
 */
public enum Command {
    SPAWN(0, 1),
    WARP(0, 1),
    JOB(0, 1),
    GAIN(0, 1),
    DROP(0, 1),
    CHANGE(0, 1),
    RELOAD(0, 1),
    KILL(0, 1),
    EA(0, 0),
    NPC(0, 0),
    HELP(0, 0),
    FAMILIAR(30, 0),
    SAVE(0, 0),
    EMS(10, 0);
    private final int reqLev;
    private final int reqGm;

    Command(int reqLev, int reqGm) {
        this.reqLev = reqLev;
        this.reqGm = reqGm;
    }

    public int getReqLev() {
        return reqLev;
    }

    public int getReqGm() {
        return reqGm;
    }

    public static Command getByName(String cmd) {
        return Util.findWithPred(values(), command -> command.name().equalsIgnoreCase(cmd));
    }
}
