package im.cave.ms.client.character.skill;

import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;

/**
 * Created on 1/7/2018.
 */
public class ForceAtomInfo {
    private int key;
    private int inc;
    private int firstImpact;
    private int secondImpact;
    private int angle;
    private int startDelay;
    private int createTime;
    private int maxHitCount;
    private int effectIdx;
    private Position startPosition;

    public ForceAtomInfo() {
    }

    public ForceAtomInfo(int key, int inc, int firstImpact, int secondImpact, int angle, int startDelay, int createTime, int maxHitCount, int effectIdx, Position startPosition) {
        this.key = key;
        this.inc = inc;
        this.firstImpact = firstImpact;
        this.secondImpact = secondImpact;
        this.angle = angle;
        this.startDelay = startDelay;
        this.createTime = createTime;
        this.maxHitCount = maxHitCount;
        this.effectIdx = effectIdx;
        this.startPosition = startPosition;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getKey());
        outPacket.writeInt(getInc());
        outPacket.writeInt(getFirstImpact());
        outPacket.writeInt(getSecondImpact());
        outPacket.writeInt(getAngle());
        outPacket.writeInt(getStartDelay());
        outPacket.writeInt(getStartPosition().getX());
        outPacket.writeInt(getStartPosition().getY());
        outPacket.writeInt(getCreateTime());
        outPacket.writeInt(getMaxHitCount());
        outPacket.writeInt(getEffectIdx());
        outPacket.writeInt(0);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getInc() {
        return inc;
    }

    public void setInc(int inc) {
        this.inc = inc;
    }

    public int getFirstImpact() {
        return firstImpact;
    }

    public void setFirstImpact(int firstImpact) {
        this.firstImpact = firstImpact;
    }

    public int getSecondImpact() {
        return secondImpact;
    }

    public void setSecondImpact(int secondImpact) {
        this.secondImpact = secondImpact;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getMaxHitCount() {
        return maxHitCount;
    }

    public void setMaxHitCount(int maxHitCount) {
        this.maxHitCount = maxHitCount;
    }

    public int getEffectIdx() {
        return effectIdx;
    }

    public void setEffectIdx(int effectIdx) {
        this.effectIdx = effectIdx;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }
}
