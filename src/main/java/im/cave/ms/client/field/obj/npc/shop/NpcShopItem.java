package im.cave.ms.client.field.obj.npc.shop;

import im.cave.ms.client.character.items.Item;
import im.cave.ms.connection.netty.OutPacket;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;


@Entity
@Table(name = "shop_item")
public class NpcShopItem {
    @Id
    @GeneratedValue
    private long id;
    private int shopId;
    @Transient
    private Item item;
    private int itemId;
    private int price;
    private int tokenItemId;
    private int tokenPrice;
    private int pointQuestId;
    private int pointPrice;
    private int starCoin;
    private int questExId;
    private String questExKey;
    private int questExValue;
    private int itemPeriod;
    private int levelLimited;
    private int showLevMin;
    private int showLevMax;
    private int questId;
    private long sellStart;
    private long sellEnd;
    private int tabIndex;
    private boolean worldBlock;
    private int potentialGrade;
    private int buyLimit;
    @Transient
    private BuyLimitInfo buyLimitInfo;
    private short quantity;
    private long unitPrice;
    private short maxPerSlot;
    private int discountPerc;

    public NpcShopItem() {
        sellStart = ZERO_TIME;
        sellEnd = MAX_TIME;
        maxPerSlot = 1000;
    }

    public void encode(OutPacket out) {
        out.writeInt(1000000);
        out.writeInt(getItemId());
        out.writeInt(0);
        out.writeInt(1000000);
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(getPrice());
        out.writeInt(getTokenItemId());
        out.writeInt(getTokenPrice());
        out.writeInt(getPointQuestID());  // 7907 组队积分
        out.writeInt(getPointPrice());
        out.writeInt(getStarCoin());
        out.write(0);
        out.writeInt(getItemPeriod());
        out.writeInt(getLevelLimited());
        out.writeInt(0);
        out.writeShort(getShowLevMin());
        out.writeShort(getShowLevMax());
        if (getBuyLimitInfo() != null) {
            getBuyLimitInfo().encode(out);
        } else {
            new BuyLimitInfo().encode(out);
        }
        out.write(0);
        out.writeLong(getSellStart() == 0 ? ZERO_TIME : getSellStart());
        out.writeLong(getSellEnd() == 0 ? MAX_TIME : getSellEnd());
        out.writeInt(getTabIndex());
        out.writeShort(1); // show?
        out.writeBool(isWorldBlock());
        out.writeInt(getquestExId());
        out.writeMapleAsciiString(getQuestExKey());
        out.writeInt(getQuestExValue());
        out.writeInt(getPotentialGrade());
        out.write(0);
        int prefix = getItemId() / 10000;
        if (prefix != 207 && prefix != 233) {
            out.writeShort(getQuantity());
        } else {
            out.writeLong(getUnitPrice());
        }
        out.writeShort(getMaxPerSlot());
        out.writeLong(MAX_TIME);

        out.writeZeroBytes(8);
        out.writeMapleAsciiString("1900010100");
        out.writeMapleAsciiString("2079010100");
        out.writeZeroBytes(17);
        int[] idarr = new int[]{9410165, 9410166, 9410167, 9410168, 9410198};
        for (int i : idarr) {
            out.writeInt(i);
            out.writeInt(0);
        }
        out.writeBool(item != null);
        if (item != null) {
            item.encode(out);
        }
    }

    public int getItemId() {
        return itemId;
    }


    /**
     * Sets the item id of this item.
     *
     * @param itemID The id of this item
     */
    public void setItemID(int itemID) {
        this.itemId = itemID;
    }

    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of this item, in mesos. If both this and token price are set, the item will not be displayed.
     *
     * @param price The price of this item
     */
    public void setPrice(int price) {
        this.price = price;
    }

    public int getTokenItemId() {
        return tokenItemId;
    }

    /**
     * Sets the token id. Token ids start with 431. Items that aren't tokens won't get their token displayed.
     *
     * @param tokenItemID The id of the token
     */
    public void setTokenItemID(int tokenItemID) {
        this.tokenItemId = tokenItemID;
    }

    public int getTokenPrice() {
        return tokenPrice;
    }

    /**
     * Sets the token price of this item. If both this and mesos price are set, the item will not be displayed.
     *
     * @param tokenPrice The token price of this item.
     */
    public void setTokenPrice(int tokenPrice) {
        this.tokenPrice = tokenPrice;
    }

    public int getPointQuestID() {
        return pointQuestId;
    }

    public void setPointQuestID(int pointQuestID) {
        this.pointQuestId = pointQuestID;
    }

    public int getPointPrice() {
        return pointPrice;
    }

    public void setPointPrice(int pointPrice) {
        this.pointPrice = pointPrice;
    }

    public int getStarCoin() {
        return starCoin;
    }

    public void setStarCoin(int starCoin) {
        this.starCoin = starCoin;
    }

    public int getquestExId() {
        return questExId;
    }

    public void setquestExId(int questExId) {
        this.questExId = questExId;
    }

    public String getQuestExKey() {
        return questExKey != null ? questExKey : "";
    }

    public void setQuestExKey(String questExKey) {
        this.questExKey = questExKey;
    }

    public int getQuestExValue() {
        return questExValue;
    }

    public void setQuestExValue(int questExValue) {
        this.questExValue = questExValue;
    }

    public int getItemPeriod() {
        return itemPeriod;
    }

    public void setItemPeriod(int itemPeriod) {
        this.itemPeriod = itemPeriod;
    }

    public int getLevelLimited() {
        return levelLimited;
    }

    public void setLevelLimited(int levelLimited) {
        this.levelLimited = levelLimited;
    }

    public int getShowLevMin() {
        return showLevMin;
    }

    public void setShowLevMin(int showLevMin) {
        this.showLevMin = showLevMin;
    }

    public int getShowLevMax() {
        return showLevMax;
    }

    public void setShowLevMax(int showLevMax) {
        this.showLevMax = showLevMax;
    }

    public int getQuestID() {
        return questId;
    }

    public void setQuestID(int questId) {
        this.questId = questId;
    }

    public Long getSellStart() {
        return sellStart;
    }

    public void setSellStart(Long sellStart) {
        this.sellStart = sellStart;
    }

    public Long getSellEnd() {
        return sellEnd;
    }

    public void setSellEnd(Long sellEnd) {
        this.sellEnd = sellEnd;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Sets the tab index of this item.
     *
     * @param tabIndex the tab index of this item.
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public boolean isWorldBlock() {
        return worldBlock;
    }

    /**
     * Sets whether or not this item should be displayed on this worldId.
     *
     * @param worldBlock whether or not this item should be displayed on this worldId.
     */
    public void setWorldBlock(boolean worldBlock) {
        this.worldBlock = worldBlock;
    }

    public int getPotentialGrade() {
        return potentialGrade;
    }


    public void setPotentialGrade(int potentialGrade) {
        this.potentialGrade = potentialGrade;
    }

    public int getBuyLimit() {
        return buyLimit;
    }

    /**
     * Sets the buy limit of this item.
     *
     * @param buyLimit The buy limit of this item.
     */
    public void setBuyLimit(int buyLimit) {
        this.buyLimit = buyLimit;
    }

    public BuyLimitInfo getBuyLimitInfo() {
        return buyLimitInfo;
    }

    public void setBuyLimitInfo(BuyLimitInfo buyLimitInfo) {
        this.buyLimitInfo = buyLimitInfo;
    }

    public short getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity this item should be given with.
     *
     * @param quantity The quantity of this item
     */
    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public short getMaxPerSlot() {
        return maxPerSlot;
    }

    /**
     * Sets the maximum amount of items the user can buy of these at once.
     *
     * @param maxPerSlot
     */
    public void setMaxPerSlot(short maxPerSlot) {
        this.maxPerSlot = maxPerSlot;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getDiscountPerc() {
        return discountPerc;
    }

    /**
     * Sets the discount percentage of this item, from 0 to 100.
     *
     * @param discountPerc The discount percentage of this item
     */
    public void setDiscountPerc(int discountPerc) {
        this.discountPerc = discountPerc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getShopID() {
        return shopId;
    }

    public void setShopID(int shopID) {
        this.shopId = shopID;
    }
}
