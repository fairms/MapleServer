package im.cave.ms.client.field.obj.mob;

import im.cave.ms.tools.Position;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj.mob
 * @date 3/17 9:22
 */
public class EscortDest {
    private final Position destPos;
    private final int attr;
    private final int mass;
    private final int stopDuration;

    public EscortDest(int destPosX, int destPosY, int attr, int mass, int stopDuration) {
        this.destPos = new Position(destPosX, destPosY);
        this.attr = attr;
        this.mass = mass;
        this.stopDuration = stopDuration;
    }

    public Position getDestPos() {
        return destPos;
    }

    public int getAttr() {
        return attr;
    }

    public int getMass() {
        return mass;
    }

    public int getStopDuration() {
        return stopDuration;
    }
}
