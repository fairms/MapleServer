package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Item;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.network.packet.WorldPacket;
import lombok.Getter;
import lombok.Setter;

import static im.cave.ms.constants.GameConstants.ZERO_TIME;

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
    private int ownerID;
    private boolean canBePickedUpByPet;
    private long expireTime;

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
        chr.announce(WorldPacket.dropEnterField(this, DropEnterType.Instant, getPosition()));
    }

    public boolean canBePickedUpBy(MapleCharacter chr) {
        int owner = getOwnerID();
        return owner == chr.getId();
    }


    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isMoney() {
        return money > 0;
    }

}
