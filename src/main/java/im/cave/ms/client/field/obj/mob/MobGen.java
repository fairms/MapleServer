package im.cave.ms.client.field.obj.mob;

import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;

/**
 * @author Sjonnie
 * Created on 7/26/2018.
 */
public class MobGen extends MapleMapObj {

    private Mob mob;
    private long nextPossibleSpawnTime = Long.MIN_VALUE;
    private boolean hasSpawned;

    public MobGen(int templateId) {
        super(templateId);
    }

    public Mob getMob() {
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
        this.setPosition(mob.getHomePosition().deepCopy());
    }

    public long getNextPossibleSpawnTime() {
        return nextPossibleSpawnTime;
    }

    public void setNextPossibleSpawnTime(long nextPossibleSpawnTime) {
        this.nextPossibleSpawnTime = nextPossibleSpawnTime;
    }

    public boolean hasSpawned() {
        return hasSpawned;
    }

    public void setHasSpawned(boolean hasSpawned) {
        this.hasSpawned = hasSpawned;
    }

    public boolean canSpawnOnField(MapleMap map) {

        int currentMobs = map.getMobs().size();
        return currentMobs < map.getMobCapacity() &&
                getNextPossibleSpawnTime() < System.currentTimeMillis() &&
                (getMob().getMobTime() != -1 || !hasSpawned());
    }

    public void spawnMob(MapleMap map) {
        Mob mob = getMob().deepCopy();
        Position pos = mob.getHomePosition();
        mob.setPosition(pos.deepCopy());
        mob.setHomePosition(pos.deepCopy());
        map.spawnObj(mob, null);
        setNextPossibleSpawnTime(System.currentTimeMillis() + (getMob().getMobTime() * 1000));
        setHasSpawned(true);
    }
}
