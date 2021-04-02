package im.cave.ms.client.field.obstacleatom;


import im.cave.ms.connection.netty.OutPacket;

public class ObstacleRadianInfo {

    private int effect;
    private int minRad; // guess
    private int maxRad; // guess
    private int startX;
    private int height;

    public ObstacleRadianInfo() {
    }

    public ObstacleRadianInfo(int effect, int minRad, int maxRad, int startX, int height) {
        this.effect = effect;
        this.minRad = minRad;
        this.maxRad = maxRad;
        this.startX = startX;
        this.height = height;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getEffect());
        outPacket.writeInt(getMinRad());
        outPacket.writeInt(getMaxRad());
        outPacket.writeInt(getStartX());
        outPacket.writeInt(getHeight());
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getMinRad() {
        return minRad;
    }

    public void setMinRad(int minRad) {
        this.minRad = minRad;
    }

    public int getMaxRad() {
        return maxRad;
    }

    public void setMaxRad(int maxRad) {
        this.maxRad = maxRad;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
