package im.cave.ms.client.field.obstacleatom;


import im.cave.ms.connection.netty.OutPacket;

public class ObstacleDiagonalInfo {
    private int effect;
    private int minAngle; // guess
    private int maxAngle; // guess
    private int createDuration;
    private int height;

    public ObstacleDiagonalInfo() {
    }

    public ObstacleDiagonalInfo(int effect, int minAngle, int maxAngle, int createDuration, int height) {
        this.effect = effect;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.createDuration = createDuration;
        this.height = height;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getEffect());
        outPacket.writeInt(getMinAngle());
        outPacket.writeInt(getMaxAngle());
        outPacket.writeInt(getCreateDuration());
        outPacket.writeInt(getHeight());
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(int minAngle) {
        this.minAngle = minAngle;
    }

    public int getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(int maxAngle) {
        this.maxAngle = maxAngle;
    }

    public int getCreateDuration() {
        return createDuration;
    }

    public void setCreateDuration(int createDuration) {
        this.createDuration = createDuration;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
