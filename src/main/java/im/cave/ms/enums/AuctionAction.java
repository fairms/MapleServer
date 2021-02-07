package im.cave.ms.enums;

import im.cave.ms.tools.Util;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 2/5 10:31
 */
public enum AuctionAction {
    Put_On_Sell(10),
    Pull_Off_Shelves(12),
    Search(40),
    QuickSearch(41),
    Sell_List(70);


    private final int val;


    AuctionAction(int val) {
        this.val = val;
    }

    public static AuctionAction getActionByVal(int val) {
        return Util.findWithPred(values(), auctionAction -> auctionAction.getVal() == val);
    }

    public int getVal() {
        return val;
    }
}
