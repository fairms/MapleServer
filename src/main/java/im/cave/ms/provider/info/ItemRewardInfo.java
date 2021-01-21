package im.cave.ms.provider.info;


import im.cave.ms.client.character.items.Item;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;

public class ItemRewardInfo {
    private int count;
    private int itemId;
    private double prob;
    private int period;
    private String effect = "";

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double getProb() {
        return prob;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getEffect() {
        return effect;
    }

    public Item getItem() {
        Item item = ItemData.getItemCopy(getItemId(), false);
        item.setQuantity(getCount());
        item.setExpireTime(getPeriod() == 0 ? MAX_TIME : DateUtil.getFileTime(System.currentTimeMillis(), getPeriod()));
        return item;
    }
}
