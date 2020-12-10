package im.cave.ms.client.quest.reward;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.enums.MessageType;
import im.cave.ms.net.packet.PlayerPacket;

/**
 * Created on 3/6/2018.
 */
public class QuestPopReward implements QuestReward {

    private int pop;

    public QuestPopReward(int pop) {
        this.pop = pop;
    }

    public QuestPopReward() {

    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    @Override
    public void giveReward(MapleCharacter chr) {
        chr.addStat(MapleStat.FAME, getPop());
        chr.announce(PlayerPacket.message(MessageType.INC_POP_MESSAGE, getPop(), null, (byte) 0));
    }

}
