package im.cave.ms.provider.info;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.cashshop
 * @date 12/30 20:57
 */
@Getter
@Setter
public class CashItemInfo {
    private int SN;
    private int itemId;
    private int count;
    private int price;
    private int bonus;
    private int period;
    private int priority;
    private int reqPop;
    private int reqLev;
    private int gender;
    private boolean onSale;
    private int clazz;
    private int pbCash;
    private int pbPoint;
    private int pbGift;
    private boolean refundable;
    private boolean webShop;
}
