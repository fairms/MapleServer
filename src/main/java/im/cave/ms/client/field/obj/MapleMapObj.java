package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobGen;
import im.cave.ms.provider.data.MobData;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.tools.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.life
 * @date 11/28 17:14
 */
@Getter
@Setter
public class MapleMapObj {
    private Position position;
    private int objectId = -1;
    protected int cy, fh, templateId, mobTime, rx0, rx1, type, x, y;
    protected boolean flip;
    private String lifeType = "";
    private boolean hide;
    private String limitedName = "";
    private boolean useDay;
    private boolean useNight;
    private boolean hold;
    private boolean noFoothold;
    private int regenStart;
    private int mobAliveReq;
    private boolean dummy;
    private boolean spine;
    private boolean mobTimeOnDie;
    private boolean notRespawnable;
    private byte moveAction;
    private MapleMap map;
    private Position homePosition;
    private Position vPosition;

    public MapleMapObj(int templateId) {
        this.templateId = templateId;
        this.position = new Position(0, 0);
    }

    public MapleMapObj() {

    }

    public Npc getNpc() {
        Npc npc = null;
        if (getLifeType().equals("n")) {
            npc = NpcData.getNpc(templateId).deepCopy();
            if (npc == null) {
                return null;
            }
            npc.setObjectId(getObjectId());
            npc.setLifeType(getLifeType());
            npc.setX(getX());
            npc.setY(getY());
            npc.setPosition(new Position(getX(), getY()));
            npc.setMobTime(getMobTime());
            npc.setFlip(isFlip());
            npc.setHide(isHide());
            npc.setFh(getFh());
            npc.setCy(getCy());
            npc.setRx0(getRx0());
            npc.setRx1(getRx1());
            npc.setLimitedName(getLimitedName());
            npc.setUseDay(isUseDay());
            npc.setUseNight(isUseNight());
            npc.setHold(isHold());
            npc.setNoFoothold(isNoFoothold());
            npc.setDummy(isDummy());
            npc.setSpine(isSpine());
            npc.setMobTimeOnDie(isMobTimeOnDie());
            npc.setRegenStart(getRegenStart());
            npc.setMobAliveReq(getMobAliveReq());
        }
        return npc;
    }


    public MobGen getMobGen() {
        MobGen mobGen = null;
        if (getLifeType().equals("m")) {
            mobGen = new MobGen(templateId);
            mobGen.setPosition(getHomePosition());
            Mob mob = MobData.getMobDeepCopyById(templateId);
            if (mob == null) {
                return null;
            }
            mobGen.setMob(mob);
            mob.setObjectId(getObjectId());
            mob.setLifeType(getLifeType());
            mob.setTemplateId(getTemplateId());
            mob.setX(getX());
            mob.setY(getY());
            mob.setHomePosition(new Position(getX(), getY()));
            mob.setPosition(new Position(getX(), getY()));
            mob.setMobTime(getMobTime());
            mob.setFlip(isFlip());
            mob.setHide(isHide());
            mob.setFh(getFh());
            mob.setCy(getCy());
            mob.setRx0(getRx0());
            mob.setRx1(getRx1());
            mob.setLimitedName(getLimitedName());
            mob.setUseDay(isUseDay());
            mob.setUseNight(isUseNight());
            mob.setHold(isHold());
            mob.setNoFoothold(isNoFoothold());
            mob.setDummy(isDummy());
            mob.setSpine(isSpine());
            mob.setMobTimeOnDie(isMobTimeOnDie());
            mob.setRegenStart(getRegenStart());
            mob.setMobAliveReq(getMobAliveReq());
        }
        return mobGen;
    }

    public Position getHomePosition() {
        if (homePosition == null) {
            homePosition = new Position(getX(), getY());
        }
        return homePosition;
    }

    public Position getPosition() {
        if (position == null) {
            position = new Position(getX(), getY());
        }
        return position;
    }


    public void sendSpawnData(MapleCharacter chr) {
    }

    public void faraway(MapleCharacter chr) {
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, objectId, map);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Mob) {
            Mob mob = (Mob) obj;
            return mob.getTemplateId() == getTemplateId() && mob.getObjectId() == getObjectId() && mob.getMap().equals(getMap());
        }
        return false;
    }
}
