package im.cave.ms.client.quest.progress;

import java.util.Arrays;

public enum QuestProgressRequirementType {
    ITEM(0),
    LEVEL(1),
    MOB(2),
    MONEY(3);

    private final byte val;

    QuestProgressRequirementType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public static QuestProgressRequirementType getQPRTByObj(Object o) {
        return o instanceof QuestProgressItemRequirement ? ITEM :
                o instanceof QuestProgressLevelRequirement ? LEVEL :
                        o instanceof QuestProgressMobRequirement ? MOB :
                                o instanceof QuestProgressMoneyRequirement ? MONEY : null;
    }

    public static QuestProgressRequirementType getQPRTByVal(byte val) {
        return Arrays.stream(QuestProgressRequirementType.values())
                .filter(qprt -> qprt.getVal() == val).findFirst().orElse(null);
    }

}
