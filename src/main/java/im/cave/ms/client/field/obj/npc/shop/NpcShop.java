package im.cave.ms.client.field.obj.npc.shop;

import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

    public void encode(OutPacket out, List<NpcShopItem> repurchaseItems) {
        out.writeZeroBytes(20);
        out.writeInt(DateUtil.getTime());
        out.writeBool(false);
        out.writeShort(items.size() + repurchaseItems.size());
        items.forEach(npcShopItem -> npcShopItem.encode(out));
        ListIterator<NpcShopItem> itemListIterator;
        for (itemListIterator = repurchaseItems.listIterator(); itemListIterator.hasNext(); ) {
            itemListIterator.next();
        }
        while (itemListIterator.hasPrevious()) {
            NpcShopItem shopItem = itemListIterator.previous();
            shopItem.encode(out);
        }
    }

    public NpcShopItem getItemByIndex(int idx) {
        NpcShopItem item = null;
        if (idx >= 0 && idx < getItems().size()) {
            item = getItems().get(idx);
        }
        return item;
    }

}
