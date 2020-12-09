package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;

/**
 * Created on 3/2/2018.
 */
public class QuestStartMinStatRequirement implements QuestStartRequirement {

    private MapleStat stat;
    private short reqAmount;

    public QuestStartMinStatRequirement(MapleStat stat, short reqAmount) {
        this.reqAmount = reqAmount;
        this.stat = stat;
    }

    public QuestStartMinStatRequirement() {

    }

    private void setReqAmount(short reqAmount) {
        this.reqAmount = reqAmount;
    }

    private short getReqAmount() {
        return reqAmount;
    }

    private MapleStat getStat() {
        return stat;
    }

    @Override
    public boolean hasRequirements(MapleCharacter chr) {
        return chr.getStat(getStat()) >= getReqAmount();
    }

}
