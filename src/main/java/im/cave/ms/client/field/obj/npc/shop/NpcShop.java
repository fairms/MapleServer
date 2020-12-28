package im.cave.ms.client.field.obj.npc.shop;

import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 12/27 0:22
 */
@Getter
@Setter
public class NpcShop {
    private int shopId;
    private int selectNpcItemId;
    private int npcTemplateId;
    private int starCoin;
    private int shopVerNo;
    private List<NpcShopItem> items = new ArrayList<>();

    public List<NpcShopItem> getItems() {
        return items;
    }

    public void setItems(List<NpcShopItem> items) {
        this.items = items;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeZeroBytes(20);
        outPacket.writeInt(DateUtil.getTime());
        outPacket.writeBool(false); //hasQuest
        outPacket.writeShort(items.size());
        items.forEach(npcShopItem -> npcShopItem.encode(outPacket));
    }
}
