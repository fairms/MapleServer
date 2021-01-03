package im.cave.ms.client.quest.requirement;

import java.util.Arrays;

public enum QuestStartRequirementType {

    QUEST(0),
    ITEM(1),
    JOB(2),
    MARRIAGE(3),
    MAX_LEVEL(4),
    MIN_STAT(5);

    private final byte val;

    QuestStartRequirementType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public static QuestStartRequirementType getQPRTByObj(Object o) {
        return o instanceof QuestStartCompletionRequirement ? QUEST :
                o instanceof QuestStartItemRequirement ? ITEM :
                        o instanceof QuestStartJobRequirement ? JOB :
                                o instanceof QuestStartMarriageRequirement ? MARRIAGE :
                                        o instanceof QuestStartMaxLevelRequirement ? MAX_LEVEL :
                                                o instanceof QuestStartMinStatRequirement ? MIN_STAT
                                                        : null;
    }

    public static QuestStartRequirementType getQPRTByVal(byte val) {
        return Arrays.stream(QuestStartRequirementType.values())
                .filter(qprt -> qprt.getVal() == val).findFirst().orElse(null);
    }


}
