package im.cave.ms.client.character.items;

import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character.items
 * @date 2/5 10:44
 */
@Setter
@Getter
public class AuctionRecord {
    private int id;
    private int accId;
    private int worldId;
    private int sellerId;
    private String sellerName;
    private boolean meso;
    private long price; //单价
    private int count;
    private int buyerId;
    private String buyerName;
    private long startSaleTime;
    private long endSaleTime;
    private long saleTime;
    private Item item;


    public void encode(OutPacket out) {
        out.writeInt(getId());
        out.writeInt(getWorldId());
        out.writeInt(getAccId());
        out.writeInt(getSellerId());
        out.writeInt(5);
        out.writeInt(2);
        out.writeAsciiString(getSellerName(), 13);
        out.writeLong(0);  //price
        out.writeLong(-1); //price
        out.writeLong(getPrice() * getCount());
        out.writeLong(getPrice());
        out.writeLong(getEndSaleTime());
        out.writeInt(getBuyerId());
        out.writeAsciiString(getBuyerName(), 13);
        out.writeInt(-1);
        out.writeLong(0);
        out.writeLong(getStartSaleTime());
        out.writeLong(2000); //0
        out.writeInt(2); //1
        out.writeInt(1);  // 0
        out.writeLong(getSaleTime());
        out.writeInt(1);
        getItem().encode(out);
    }
}
