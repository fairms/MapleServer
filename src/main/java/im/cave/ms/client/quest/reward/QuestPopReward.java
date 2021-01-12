package im.cave.ms.client.quest.reward;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Stat;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.enums.BroadcastMsgType;


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
        chr.addStat(Stat.FAME, getPop());
        chr.announce(UserPacket.message(BroadcastMsgType.INC_POP_MESSAGE, getPop(), null, (byte) 0));
    }

}
