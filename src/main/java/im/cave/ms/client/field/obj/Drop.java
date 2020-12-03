package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Item;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.tools.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.attribute.FileTime;

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
    private boolean isMeso;
    private int ownerID;
    private boolean explosiveDrop;
    private boolean canBePickedUpByPet;
    private long expireTime;
    private long mobExp;

    public Drop(int templateId, Item item) {
        super(templateId);
        this.item = item;
        isMeso = false;
        expireTime = DateUtil.getFileTime(-2);
    }

    public Drop(int templateId) {
        super(templateId);
        canBePickedUpByPet = true;
    }

    public Drop(int templateId, int money) {
        super(templateId);
        this.money = money;
        isMeso = true;
        expireTime = DateUtil.getFileTime(-2);
    }


    @Override
    public void sendSpawnData(MapleCharacter chr) {
        chr.announce(ChannelPacket.dropEnterField(this, DropEnterType.Instant, getPosition()));
    }
}
