package im.cave.ms.enums;

import im.cave.ms.tools.Util;

import java.util.Arrays;

/**
 * @author Sjonnie
 * Created on 7/26/2018.
 */
public enum InstanceTableType {

    HyperPassiveSkill("hyper", 28, 0),
    HyperActiveSkill("hyper", 28, 1),
    HyperStatIncAmount("incHyperStat", 0, 0),
    NeedHyperStatLv("needHyperStatLv", 0, 0),
    Skill_9200("92000000", 0, 0),
    Skill_9201("92010000", 0, 0),
    Skill_9202("92020000", 0, 0),
    Skill_9203("92030000", 0, 0),
    Skill_9204("92040000", 0, 0),
    ;

    private final String tableName;
    private final int type;
    private final int subType;

    InstanceTableType(String tableName, int type, int subType) {
        this.tableName = tableName;
        this.type = type;
        this.subType = subType;
    }

    public static InstanceTableType getByStr(String requestStr) {
        return Util.findWithPred(Arrays.asList(values()), itt -> itt.getTableName().equals(requestStr));
    }

    public String getTableName() {
        return tableName;
    }

    public int getType() {
        return type;
    }

    public int getSubType() {
        return subType;
    }
}
