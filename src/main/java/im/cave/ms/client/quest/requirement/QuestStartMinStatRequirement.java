package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Stat;


public class QuestStartMinStatRequirement implements QuestStartRequirement {

    private Stat stat;
    private short reqAmount;

    public QuestStartMinStatRequirement(Stat stat, short reqAmount) {
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

    private Stat getStat() {
        return stat;
    }

    @Override
    public boolean hasRequirements(MapleCharacter chr) {
        return chr.getStat(getStat()) >= getReqAmount();
    }

}
