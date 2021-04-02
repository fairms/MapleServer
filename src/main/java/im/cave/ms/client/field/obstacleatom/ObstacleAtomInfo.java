package im.cave.ms.client.field.obstacleatom;


import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;


public class ObstacleAtomInfo {
    private int atomType;
    private int key;
    private Position startPos;
    private Position endPos;
    private int hitBoxRange;
    private int trueDamR;
    private int mobDamR;
    private int createDelay;
    private int height;
    private int vPerSec;
    private int maxP;
    private int length;
    private int angle;
    private ObstacleDiagonalInfo obstacleDiagonalInfo;

    public ObstacleAtomInfo() {
    }

    public ObstacleAtomInfo(int atomType, int key, Position startPos, Position endPos, int hitBoxRange, int trueDamR,
                            int mobDamR, int createDelay, int height, int vPerSec, int maxP, int length, int angle) {
        this.atomType = atomType;
        this.key = key;
        this.startPos = startPos;
        this.endPos = endPos;
        this.hitBoxRange = hitBoxRange;
        this.trueDamR = trueDamR;
        this.mobDamR = mobDamR;
        this.createDelay = createDelay;
        this.height = height;
        this.vPerSec = vPerSec;
        this.maxP = maxP;
        this.length = length;
        this.angle = angle;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getAtomType());
        outPacket.writeInt(getKey());
        outPacket.writeInt(getStartPos().getX());
        outPacket.writeInt(getStartPos().getY());
        outPacket.writeInt(getEndPos().getX());
        outPacket.writeInt(getEndPos().getY());
        outPacket.writeInt(getHitBoxRange());
        outPacket.writeInt(getTrueDamR()); //百分比
        outPacket.writeInt(getMobDamR());
        outPacket.writeInt(getCreateDelay());
        outPacket.writeInt(getHeight());
        outPacket.writeInt(getvPerSec());
        outPacket.writeInt(getMaxP());
        outPacket.writeInt(getLength());
        outPacket.writeInt(getAngle());
    }

    public int getAtomType() {
        return atomType;
    }

    public void setAtomType(int atomType) {
        this.atomType = atomType;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Position getStartPos() {
        return startPos;
    }

    public void setStartPos(Position startPos) {
        this.startPos = startPos;
    }

    public Position getEndPos() {
        return endPos;
    }

    public void setEndPos(Position endPos) {
        this.endPos = endPos;
    }

    public int getHitBoxRange() {
        return hitBoxRange;
    }

    public void setHitBoxRange(int hitBoxRange) {
        this.hitBoxRange = hitBoxRange;
    }

    public int getTrueDamR() {
        return trueDamR;
    }

    public void setTrueDamR(int trueDamR) {
        this.trueDamR = trueDamR;
    }

    public int getMobDamR() {
        return mobDamR;
    }

    public void setMobDamR(int mobDamR) {
        this.mobDamR = mobDamR;
    }

    public int getCreateDelay() {
        return createDelay;
    }

    public void setCreateDelay(int createDelay) {
        this.createDelay = createDelay;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getvPerSec() {
        return vPerSec;
    }

    public void setvPerSec(int vPerSec) {
        this.vPerSec = vPerSec;
    }

    public int getMaxP() {
        return maxP;
    }

    public void setMaxP(int maxP) {
        this.maxP = maxP;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public ObstacleDiagonalInfo getObtacleDiagonalInfo() {
        return obstacleDiagonalInfo;
    }

    public void setObtacleDiagonalInfo(ObstacleDiagonalInfo obstacleDiagonalInfo) {
        this.obstacleDiagonalInfo = obstacleDiagonalInfo;
    }
}
