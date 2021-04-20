package im.cave.ms.client.character.skill;


import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.ForceAtomEnum;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;

import java.util.List;

public class ForceAtom {
    private boolean byMob;
    private boolean toMob;
    private int userOwner;
    private int charID;
    private int skillID;
    private int arriveDir;
    private int arriveRange;
    private int bulletID;
    private ForceAtomEnum forceAtomType;
    private List<Integer> targets;
    private List<ForceAtomInfo> faiList;
    private Rect rect;
    private Position forcedTargetPos;
    private Position pos;

    public ForceAtom(boolean byMob, int userOwner, int charID, ForceAtomEnum forceAtomType, boolean toMob, List<Integer> targets, int skillID, List<ForceAtomInfo> faiList, Rect rect, int arriveDir, int arriveRange, Position forcedTargetPos, int bulletID, Position pos) {
        this.setByMob(byMob);
        this.setUserOwner(userOwner);
        this.setCharID(charID);
        this.setForceAtomType(forceAtomType);
        this.setToMob(toMob);
        this.setTargets(targets);
        this.setSkillID(skillID);
        this.setFaiList(faiList);
        this.setRect(rect);
        this.setArriveDir(arriveDir);
        this.setArriveRange(arriveRange);
        this.setForcedTargetPos(forcedTargetPos);
        this.setBulletID(bulletID);
        this.setPos(pos);
    }

    public void encode(OutPacket out) {
        out.writeBool(isByMob());
        if (isByMob()) {
            out.writeInt(getUserOwner());
        }
        out.writeInt(getCharID());
        out.writeInt(getForceAtomType().getForceAtomType());
        if (getForceAtomType().getForceAtomType() != 0 &&
                getForceAtomType().getForceAtomType() != 9 &&
                getForceAtomType().getForceAtomType() != 14 &&
                getForceAtomType().getForceAtomType() != 29 &&
                getForceAtomType().getForceAtomType() != 35 &&
                getForceAtomType().getForceAtomType() != 42) {
            out.writeBool(isToMob());
            switch (getForceAtomType().getForceAtomType()) {
                case 2:
                case 3:
                case 6:
                case 7:
                case 11:
                case 12:
                case 13:
                case 17:
                case 19:
                case 20:
                case 23:
                case 24:
                case 25:
                case 27:
                case 28:
                case 30:
                case 32:
                case 34:
                case 38:
                case 39:
                case 40:
                case 41:
                case 47:
                case 48:
                case 49:
                case 52:
                case 53:
                case 54:
                case 55:
                    out.writeInt(getTargets().size());
                    for (int i : getTargets()) {
                        out.writeInt(i);
                    }
                    break;
                default:
                    out.writeInt(getTargets().get(0));
                    break;
            }
            out.writeInt(getSkillID());
        }
        for (ForceAtomInfo fai : faiList) {
            out.write(1);
            fai.encode(out);
        }
        out.write(0);
        switch (getForceAtomType().getForceAtomType()) {
            case 11:
                out.writeRect(getRect());
                out.writeInt(getBulletID());
                break;
            case 9:
                out.writeRect(getRect());
                break;
            case 15:
                out.writeRect(getRect());
                out.writeBool(false);
                break;
            case 29:
                out.writeRect(getRect());
                out.writePositionInt(getForcedTargetPos());
                break;
            case 16:
            case 4:
            case 26:
            case 33:
                out.writePositionInt(getPos());
                break;
            case 17:
                out.writeInt(getArriveDir());
                out.writeInt(getArriveRange());
                break;
            case 18:
                out.writePositionInt(getForcedTargetPos());
                break;
            case 27:
            case 28:
            case 34:
                out.writeRect(getRect());// 16 bytes buffer
                out.writeInt(0);// duration ? (x * 1000)
                break;
            case 36:
            case 39:
                out.writeInt(0);
                out.writeInt(0);
                out.writeInt(0);
                out.writeRect(getRect());// 16 bytes buffer
                if (getForceAtomType().getForceAtomType() == 36) {
                    out.writeRect(getRect());// 16 bytes buffer
                    out.writeInt(0);
                }
                break;
            case 37:
                out.writeInt(0);
                out.writeRect(getRect());// 16 bytes buffer
                out.writeInt(0);
                out.writeInt(0);
                break;
            case 42:
                out.writeRect(getRect());// 16 bytes buffer
                break;
            case 49:// not sure
                out.writeInt(0);
                out.writeInt(0);
                break;
        }
    }

    public boolean isByMob() {
        return byMob;
    }

    public void setByMob(boolean byMob) {
        this.byMob = byMob;
    }

    public boolean isToMob() {
        return toMob;
    }

    public void setToMob(boolean toMob) {
        this.toMob = toMob;
    }

    public int getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(int userOwner) {
        this.userOwner = userOwner;
    }

    public int getCharID() {
        return charID;
    }

    public void setCharID(int charID) {
        this.charID = charID;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getArriveDir() {
        return arriveDir;
    }

    public void setArriveDir(int arriveDir) {
        this.arriveDir = arriveDir;
    }

    public int getArriveRange() {
        return arriveRange;
    }

    public void setArriveRange(int arriveRange) {
        this.arriveRange = arriveRange;
    }

    public int getBulletID() {
        return bulletID;
    }

    public void setBulletID(int bulletID) {
        this.bulletID = bulletID;
    }

    public ForceAtomEnum getForceAtomType() {
        return forceAtomType;
    }

    public void setForceAtomType(ForceAtomEnum forceAtomType) {
        this.forceAtomType = forceAtomType;
    }

    public List<Integer> getTargets() {
        return targets;
    }

    public void addTarget(int target) {
        this.targets.add(target);
    }

    public void setTargets(List<Integer> targets) {
        this.targets = targets;
    }

    public List<ForceAtomInfo> getFaiList() {
        return faiList;
    }

    public void addFaiList(ForceAtomInfo fai) {
        this.faiList.add(fai);
    }

    public void setFaiList(List<ForceAtomInfo> faiList) {
        this.faiList = faiList;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Position getForcedTargetPos() {
        return forcedTargetPos;
    }

    public void setForcedTargetPos(Position forcedTargetPos) {
        this.forcedTargetPos = forcedTargetPos;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
}
