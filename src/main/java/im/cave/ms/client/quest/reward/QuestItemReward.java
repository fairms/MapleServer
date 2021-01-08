package im.cave.ms.client.quest.reward;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.Effect;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.provider.data.ItemData;


public class QuestItemReward implements QuestReward {
    private int id;
    private short quantity;
    private String potentialGrade;
    private int status;
    private int prop;
    private int gender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    @Override
    public void giveReward(MapleCharacter chr) {
        Item item = ItemData.getItemCopy(getId(), false);
        item.setQuantity(getQuantity());
        if (getQuantity() < 0) {
            chr.consumeItem(item.getItemId(), -getQuantity());
        } else {
            chr.addItemToInv(item);
        }
        chr.announce(UserPacket.effect(Effect.gainQuestItem(item.getItemId(), getQuantity())));
    }

    public void setPotentialGrade(String potentialGrade) {
        this.potentialGrade = potentialGrade;
    }

    public String getPotentialGrade() {
        return potentialGrade;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setProp(int prop) {
        this.prop = prop;
    }

    public int getProp() {
        return prop;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

}
