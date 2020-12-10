package im.cave.ms.client.quest.reward;

import java.util.Arrays;

/**
 * Created on 3/8/2018.
 */
public enum QuestRewardType {
    EXP(0),
    ITEM(1),
    MONEY(2),
    POP(3),
    BUFF_ITEM(4);

    private final byte val;

    QuestRewardType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public static QuestRewardType getQuestRewardType(Object o) {
        return o instanceof QuestExpReward ? EXP :
                o instanceof QuestItemReward ? ITEM :
                        o instanceof QuestMoneyReward ? MONEY :
                                o instanceof QuestPopReward ? POP :
                                        o instanceof QuestBuffItemReward ? BUFF_ITEM : null;
    }

    public static QuestRewardType getQuestRewardType(byte val) {
        return Arrays.stream(QuestRewardType.values())
                .filter(qprt -> qprt.getVal() == val).findFirst().orElse(null);
    }

}
