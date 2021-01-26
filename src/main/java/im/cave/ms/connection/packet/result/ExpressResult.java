package im.cave.ms.connection.packet.result;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.multiplayer.Express;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.ExpressAction;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet.result
 * @date 1/17 15:56
 */
@Getter
@Setter
public class ExpressResult {
    private ExpressAction action;
    private String fromName;
    private Item item;
    private MapleCharacter chr;
    private int arg1;
    private int arg2;


    public static ExpressResult open() {
        ExpressResult expressResult = new ExpressResult();
        expressResult.setAction(ExpressAction.Res_Open_Dialog);
        expressResult.setArg1(1);
        return expressResult;
    }

    public static ExpressResult initLocker(MapleCharacter chr) {
        ExpressResult expressResult = new ExpressResult();
        expressResult.setAction(ExpressAction.Res_Init_Locker);
        expressResult.setChr(chr);
        return expressResult;
    }

    public static ExpressResult remove(int expressId, int reason) {
        ExpressResult expressResult = new ExpressResult();
        expressResult.setAction(ExpressAction.Res_Remove_Done);
        expressResult.setArg1(expressId);
        expressResult.setArg2(reason);
        return expressResult;
    }

    public static ExpressResult message(ExpressAction action) {
        ExpressResult expressResult = new ExpressResult();
        expressResult.setAction(action);
        return expressResult;
    }

    public static ExpressResult haveNewExpress(Express express) {
        ExpressResult expressResult = new ExpressResult();
        expressResult.setFromName(express.getFromChar());
        expressResult.setArg1(express.getStatus());
        return expressResult;
    }
}
