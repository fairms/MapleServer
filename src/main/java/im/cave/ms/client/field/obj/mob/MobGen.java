package im.cave.ms.client.field.obj.mob;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.net.packet.MaplePacketCreator;
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

    /**
     * Spawns a Mob at the position of this MobGen.
     */
    @Override
    public void sendSpawnData(MapleCharacter chr) {
        Position pos = mob.getHomePosition();
        mob.setPosition(pos);
        mob.setHomePosition(pos);
        mob.setObjectId(getObjectId());
        mob.sendSpawnData(chr);
        setNextPossibleSpawnTime(System.currentTimeMillis() + (getMob().getMobTime() * 1000));
        setHasSpawned(true);
    }


    //    public MobGen deepCopy() {
//        MobGen mobGen = new MobGen(getTemplateId());
//        if (getMob() != null) {
//            mobGen.setMob(getMob().deepCopy());
//        }
//        return mobGen;
//    }

    //    public boolean canSpawnOnField(Field field) {
//        int currentMobs = field.getMobs().size();
//        // not over max mobs, delay of spawn ended, if mobtime == -1 (not respawnable) must not have yet spawned
//        // no mob in area around this, unless kishin is active
//        return currentMobs < field.getMobCapacity() &&
//                getNextPossibleSpawnTime() < System.currentTimeMillis() &&
//                (getMob().getMobTime() != -1 || !hasSpawned()) &&
//                (field.hasKishin() ||
//                        field.getMobsInRect(getPosition().getRectAround(GameConstants.MOB_CHECK_RECT)).size() == 0);
//    }
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
        map.addObj(mob);
        map.broadcastMessage(null, MaplePacketCreator.spawnMob(mob));
        map.broadcastMessage(null, MaplePacketCreator.mobChangeController(mob));
        setNextPossibleSpawnTime(System.currentTimeMillis() + (getMob().getMobTime() * 1000));
        setHasSpawned(true);
    }
}
