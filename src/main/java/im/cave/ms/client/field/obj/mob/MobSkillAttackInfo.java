package im.cave.ms.client.field.obj.mob;


import im.cave.ms.tools.Position;

import java.util.ArrayList;
import java.util.List;

public class MobSkillAttackInfo {
    public byte actionAndDirMask;
    public byte action;
    public long targetInfo;
    public short skillID;
    public List<Position> multiTargetForBalls = new ArrayList<>();
    public List<Short> randTimeForAreaAttacks = new ArrayList<>();
    public List<Integer> unks = new ArrayList<>();
}
