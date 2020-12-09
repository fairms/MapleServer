package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;

/**
 * Created on 3/2/2018.
 */
public class QuestStartItemRequirement implements QuestStartRequirement {
    private int id;
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addReqItem(int reqItem, int count) {
        setId(reqItem);
        setQuantity(count);
    }

    @Override
    public boolean hasRequirements(MapleCharacter chr) {
        return chr.hasItemCount(getId(), getQuantity());
    }

}
