package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.ItemInfo;
import lombok.Getter;
import lombok.Setter;

import static im.cave.ms.constants.ServerConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 12/3 17:06
 */
@Getter
@Setter
public class Drop extends MapleMapObj {
    private Item item;
    private int money;
    private int ownerId;
    private boolean canBePickedUpByPet;
    private long expireTime;
    private int questId;

    public Drop(int templateId, Item item) {
        super(templateId);
        this.item = item;
        expireTime = ZERO_TIME;
    }

    public Drop(int templateId) {
        super(templateId);
        canBePickedUpByPet = true;
    }

    public Drop(int templateId, int money) {
        super(templateId);
        this.money = money;
        expireTime = ZERO_TIME;
    }

    @Override
    public void sendSpawnPacket(MapleCharacter chr) {
        Item item = getItem();
        ItemInfo ii = null;
        if (item != null) {
            ii = ItemData.getItemInfoById(item.getItemId());
        }
        boolean canBeSeen = isMoney()
                || (item != null && ItemConstants.isEquip(item.getItemId()))
                || (ii != null && chr.hasQuestInProgress(getQuestId()));
        if (ii != null && ii.isQuest() && getQuestId() == 0) {
            return;
        }
        if (canBeSeen) {
            chr.announce(WorldPacket.dropEnterField(this, DropEnterType.Instant, getPosition()));
        }
    }

    public boolean canBePickedUpBy(MapleCharacter chr) {
        int owner = getOwnerId();
        return owner == chr.getId() || (chr.getParty() != null && chr.getParty().hasPartyMember(owner)) || owner == 0;
    }


    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isMoney() {
        return money > 0;
    }

}
