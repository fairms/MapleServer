package im.cave.ms.client.quest.reward;

import im.cave.ms.client.character.ExpIncreaseInfo;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.tools.Util;

/**
 * Created on 3/2/2018.
 */
public class QuestExpReward implements QuestReward {

    private long exp;

    public QuestExpReward(long exp) {
        this.exp = exp;
    }

    public QuestExpReward() {

    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getExp() {
        return exp;
    }

    @Override
    public void giveReward(MapleCharacter chr) {
        ExpIncreaseInfo eii = new ExpIncreaseInfo();
        eii.setLastHit(true);
        eii.setIncEXP(Util.maxInt(getExp()));
        eii.setOnQuest(true);
        chr.addExp(getExp(), eii);
    }

}
