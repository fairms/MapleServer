package im.cave.ms.client.field.obj.npc.shop;

import im.cave.ms.client.items.Item;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.PacketHelper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import static im.cave.ms.constants.GameConstants.MAX_TIME;
import static im.cave.ms.constants.GameConstants.ZERO_TIME;

/**
 * Created on 3/27/2018.
 */
@Entity
@Table(name = "shop_items")
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

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(1000000);
        outPacket.writeInt(getItemId());
        outPacket.writeInt(0);
        outPacket.writeInt(1000000);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(getPrice());
        outPacket.writeInt(getTokenItemId());
        outPacket.writeInt(getTokenPrice());
        outPacket.writeInt(getPointQuestID());
        outPacket.writeInt(getPointPrice());
        outPacket.writeInt(getStarCoin());
        outPacket.write(0);
        outPacket.writeInt(getItemPeriod());
        outPacket.writeInt(getLevelLimited());
        outPacket.writeInt(0);
        outPacket.writeShort(getShowLevMin());
        outPacket.writeShort(getShowLevMax());
        if (getBuyLimitInfo() != null) {
            getBuyLimitInfo().encode(outPacket);
        } else {
            new BuyLimitInfo().encode(outPacket);
        }
        outPacket.write(0);
        outPacket.writeLong(getSellStart());
        outPacket.writeLong(getSellEnd());
        outPacket.writeInt(getTabIndex());
        outPacket.writeShort(1); // show?
        outPacket.writeBool(isWorldBlock());
        outPacket.writeInt(getquestExId());
        outPacket.writeMapleAsciiString(getQuestExKey());
        outPacket.writeInt(getQuestExValue());
        outPacket.writeInt(getPotentialGrade());
        outPacket.write(0);
        int prefix = getItemId() / 10000;
        if (prefix != 207 && prefix != 233) {
            outPacket.writeShort(getQuantity());
        } else {
            outPacket.writeLong(getUnitPrice());
        }
        outPacket.writeShort(getMaxPerSlot());
        outPacket.writeLong(MAX_TIME);

        outPacket.writeZeroBytes(8);
        outPacket.writeMapleAsciiString("1900010100");
        outPacket.writeMapleAsciiString("2079010100");
        outPacket.writeZeroBytes(17);
        int[] idarr = new int[]{9410165, 9410166, 9410167, 9410168, 9410198};
        for (int i : idarr) {
            outPacket.writeInt(i);
            outPacket.writeInt(0);
        }
        outPacket.writeBool(item != null);
        if (item != null) {
            PacketHelper.addItemInfo(outPacket, item);
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
     * Sets whether or not this item should be displayed on this world.
     *
     * @param worldBlock whether or not this item should be displayed on this world.
     */
    public void setWorldBlock(boolean worldBlock) {
        this.worldBlock = worldBlock;
    }

    public int getPotentialGrade() {
        return potentialGrade;
    }

    /**
     * Sets the potential grade of this item (see {@link ItemGrade}). Will do nothing if this item is not an equip.
     *
     * @param potentialGrade The potential grade of this item
     */
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
