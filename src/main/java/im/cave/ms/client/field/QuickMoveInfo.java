package im.cave.ms.client.field;

import im.cave.ms.enums.QuickMoveType;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;


public class QuickMoveInfo {
    private int qmiID;
    private int templateID;
    private QuickMoveType code;
    private int levelMin;
    private String msg;
    private long start;
    private long end;
    private boolean noInstances;

    public QuickMoveInfo(int qmiID, int templateID, QuickMoveType code) {
        this.qmiID = qmiID;
        this.templateID = templateID;
        this.code = code;
        this.start = ZERO_TIME;
        this.start = MAX_TIME;
    }

    public QuickMoveInfo(int qmiID, int templateID, QuickMoveType code, int levelMin, String msg, boolean noInstances, long start, long end) {
        this.qmiID = qmiID;
        this.templateID = templateID;
        this.code = code;
        this.levelMin = levelMin;
        this.msg = msg;
        this.noInstances = noInstances;
        this.start = start;
        this.end = end;
    }

    public QuickMoveInfo withLevelMin(int level) {
        setLevelMin(level);
        return this;
    }

    public QuickMoveInfo withMsg(String msg) {
        setMsg(msg);
        return this;
    }

    public QuickMoveInfo withStartAndEnd(long start, long end) {
        setStart(start);
        setEnd(end);
        return this;
    }

    public int getQmiID() {
        return qmiID;
    }

    public void setQmiID(int qmiID) {
        this.qmiID = qmiID;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public QuickMoveType getCode() {
        return code;
    }

    public void setCode(QuickMoveType code) {
        this.code = code;
    }

    public int getLevelMin() {
        return levelMin;
    }

    public void setLevelMin(int levelMin) {
        this.levelMin = levelMin;
    }

    public boolean isNoInstances() {
        return noInstances;
    }

    public void setNoInstances(boolean noInstances) {
        this.noInstances = noInstances;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

}
