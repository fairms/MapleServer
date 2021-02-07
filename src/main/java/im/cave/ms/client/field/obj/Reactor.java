package im.cave.ms.client.field.obj;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 1/4 16:34
 */
@Getter
@Setter
public class Reactor extends MapleMapObj {

    private byte state;
    private String name = "";
    private int ownerId;
    private int properEventIdx;
    private int reactorTime;
    private boolean phantomForest;
    private int hitCount;

}
