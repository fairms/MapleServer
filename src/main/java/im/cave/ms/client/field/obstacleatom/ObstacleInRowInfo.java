package im.cave.ms.client.field.obstacleatom;


import im.cave.ms.connection.netty.OutPacket;

public class ObstacleInRowInfo {
    private int effect;
    private boolean leftToRight;
    private int duration;
    private int i;
    private int startX;
    private int endX;

    public ObstacleInRowInfo() {
    }

    public ObstacleInRowInfo(int effect, boolean leftToRight, int duration, int i, int startX, int endX) {
        this.effect = effect;
        this.leftToRight = leftToRight;
        this.duration = duration;
        this.i = i;
        this.startX = startX;
        this.endX = endX;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getEffect());
        outPacket.writeBool(isLeftToRight());
        outPacket.writeInt(getDuration());
        outPacket.writeInt(getI());
        outPacket.writeInt(getStartX());
        outPacket.writeInt(getEndX());
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public void setLeftToRight(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }
}
