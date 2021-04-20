package im.cave.ms.client.character.skill;


import im.cave.ms.connection.netty.OutPacket;

import java.util.ArrayList;
import java.util.List;

public class StopForceAtom {
    private int idx;
    private int count;
    private int weaponId;
    private List<Integer> angleInfo = new ArrayList<>();

    public void encode(OutPacket out) {
        out.writeInt(getIdx());
        out.writeInt(getCount());
        out.writeInt(getWeaponId());
        out.writeInt(getAngleInfo().size());
        for (int i : getAngleInfo()) {
            out.writeInt(i);
        }
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }

    public List<Integer> getAngleInfo() {
        return angleInfo;
    }

    public void setAngleInfo(List<Integer> angleInfo) {
        this.angleInfo = angleInfo;
    }
}
