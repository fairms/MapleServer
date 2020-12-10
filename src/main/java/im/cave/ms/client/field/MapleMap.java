package im.cave.ms.client.field;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.DropInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobGen;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.FieldOption;
import im.cave.ms.enums.FieldType;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.PlayerPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.service.EventManager;
import im.cave.ms.scripting.map.MapScriptManager;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Util;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static im.cave.ms.constants.GameConstants.DROP_REMAIN_ON_GROUND_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 11/25 8:56
 */
@Getter
@Setter
public class MapleMap {
    private static final Logger log = LoggerFactory.getLogger(MapleMap.class);
    private double mobRate;
    private int id, world, channel;
    private FieldType fieldType;
    private long fieldLimit = 0;
    private int returnMap, forcedReturn, createMobInterval, timeOut, timeLimit, lvLimit, lvForceMove;
    private int consumeItemCoolTime, link;
    private boolean town, swim, fly, reactorShuffle, expeditionOnly, partyOnly, isNeedSkillForFly;
    private Set<Portal> portals = new HashSet<>();
    private Set<Foothold> footholds = new HashSet<>();
    private List<MapleCharacter> characters = new CopyOnWriteArrayList<>();
    private Map<Integer, MapleMapObj> objs;
    // 怪物出生点
    private List<MobGen> mobGens = new ArrayList<>();
    private String onFirstUserEnter = "";
    private String onUserEnter = "";
    private int fixedMobCapacity;
    private AtomicInteger objectIdCounter = new AtomicInteger(1000000);
    private boolean userFirstEnter = false;
    private String fieldScript = "";
    private int bossMobID;
    private int VrTop, VrBottom, VrLeft, VrRight;
    private Map<MapleMapObj, ScheduledFuture> objScheduledFutures;

    public MapleMap(int id, int world, int channel) {
        this.id = id;
        this.world = world;
        objs = new ConcurrentHashMap<>();
        objScheduledFutures = new HashMap<>();
        this.channel = channel;
    }

    public void addFoothold(Foothold fh) {
        footholds.add(fh);
    }

    public void addPortal(Portal portal) {
        portals.add(portal);
    }

    public int getHeight() {
        return getVrTop() - getVrBottom();
    }

    public int getWidth() {
        return getVrRight() - getVrLeft();
    }

    public void addPlayer(MapleCharacter chr) {
        characters.add(chr);
        chr.setMapId(id);
        MapScriptManager msm = MapScriptManager.getInstance();
        int charSize = characters.size();
        if (charSize == 1) {
            if (onFirstUserEnter.length() != 0) {
                msm.runMapScript(chr.getClient(), "onFirstUserEnter/" + onFirstUserEnter, true);
            }
        }
        if (onUserEnter.length() != 0) {
            msm.runMapScript(chr.getClient(), "onUserEnter/" + onUserEnter, false);
        }
        broadcastSpawnPlayerMapObjectMessage(chr, chr, true);
        chr.announce(PlayerPacket.hiddenEffectEquips(chr)); //maybe broadcast
        chr.setJob(chr.getJob());
        sendMapObject(chr);
    }

    private void sendMapObject(MapleCharacter chr) {
        List<MapleMapObj> objects = new ArrayList<>(objs.values());
        for (MapleMapObj object : objects) {
            object.sendSpawnData(chr);
        }
    }

    private void broadcastSpawnPlayerMapObjectMessage(MapleCharacter source, MapleCharacter player, boolean enteringField) {
        for (MapleCharacter character : characters) {
            if (character != source) {
//                character.announce(MaplePacketCreator.spawnPlayer);
            }
        }
    }

    public void broadcastMessage(MaplePacketLittleEndianWriter packet) {
        broadcastMessage(null, packet);
    }

    public void broadcastMessage(MapleCharacter source, MaplePacketLittleEndianWriter packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet);
    }


    public void broadcastMessage(MapleCharacter source, MaplePacketLittleEndianWriter packet) {
        for (MapleCharacter chr : characters) {
            if (chr != source) {
                chr.getClient().announce(packet);
            }
        }
    }


    public Portal getPortal(String portalName) {
        return portals.stream()
                .filter(portal -> portal.getName().equals(portalName))
                .findAny().orElse(null);
    }

    public void addObj(MapleMapObj obj) {
        if (obj.getObjectId() < 0) {
            obj.setObjectId(objectIdCounter.getAndIncrement());
        }
        if (!objs.containsValue(obj)) {
            objs.put(obj.getObjectId(), obj);
            obj.setMap(this);
        }
    }

    public MapleMapObj getObj(int objectId) {
        return objs.get(objectId);
    }

    public void generateMobs(boolean init) {
        if (init || getCharacters().size() > 0) {
            int currentMobs = getMobs().size();
            for (MobGen mg : getMobGens()) {
                if (mg.canSpawnOnField(this)) {
                    mg.spawnMob(this);
                    currentMobs++;
                    if ((getFieldLimit() & FieldOption.NoMobCapacityLimit.getVal()) == 0
                            && currentMobs > getFixedMobCapacity()) {
                        break;
                    }
                }
            }
        }
        EventManager.addEvent(() -> generateMobs(false),
                (long) (GameConstants.BASE_MOB_RESPAWN_RATE / (getMobRate())));
    }


    public int getMobCapacity() {
        return getFixedMobCapacity();//todo
    }


    public void addMobGen(MobGen mobGen) {
        mobGens.add(mobGen);
    }


    public Set<Mob> getMobs() {
        return getLifesByClass(Mob.class);
    }

    public Set<Drop> getDrops() {
        return getLifesByClass(Drop.class);
    }

    private <T> Set<T> getLifesByClass(Class clazz) {
        return (Set<T>) getObjs().values().stream()
                .filter(l -> l.getClass().equals(clazz))
                .collect(Collectors.toSet());
    }


    public void removePlayer(MapleCharacter player) {
        characters.removeIf(character -> character.getId().equals(player.getId()));
    }


    public Portal getDefaultPortal() {
        Optional<Portal> any = portals.stream().findFirst();
        return any.orElse(null);
    }

    public void removeObj(int objId, boolean schedule) {
        MapleMapObj obj = getObj(objId);
        if (obj == null) {
            return;
        }
        objs.remove(obj.getObjectId());
        removeSchedule(obj, schedule);
    }

    private void removeSchedule(MapleMapObj obj, boolean schedule) {
        if (!getObjScheduledFutures().containsKey(obj)) {
            return;
        }
        if (!schedule) {
            getObjScheduledFutures().get(obj).cancel(false);
        }
        getObjScheduledFutures().remove(obj);
    }

    public MapleCharacter getPlayer(int charId) {
        return Util.findWithPred(characters, character -> character.getId() == charId);
    }

    public void drop(Drop drop, Position posFrom, Position posTo) {
        drop(drop, posFrom, posTo, false);
    }


    public void drop(Set<DropInfo> drops, Foothold fh, Position position, int ownerId, int mesoRate, int dropRate, boolean isElite) {
        int x = position.getX();
        int minX = fh == null ? position.getX() : fh.getX1();
        int maxX = fh == null ? position.getX() : fh.getX2();
        int diff = 0;
        for (DropInfo drop : drops) {
            if (drop.willDrop(dropRate)) {
                x = (x + diff) > maxX ? maxX - 10 : (x + diff) < minX ? minX + 10 : x + diff;
                Position posTo;
                if (fh == null) {
                    posTo = position.deepCopy();
                } else {
                    posTo = new Position(x, fh.getYFromX(x));
                }
                DropInfo copy = null;
                if (drop.isMoney()) {
                    copy = drop.deepCopy();
                    copy.setMoney((int) (drop.getMoney() * ((100 + mesoRate) / 100D)));
                }

                drop(copy != null ? copy : drop, position, posTo, ownerId);
                diff = diff < 0 ? Math.abs(diff - GameConstants.DROP_DIFF) : -(diff + GameConstants.DROP_DIFF);
                drop.generateNextDrop();
            }
        }

    }

    public void drop(DropInfo dropInfo, Position posFrom, Position posTo, int ownerId) {
        int itemID = dropInfo.getItemID();
        Item item;
        Drop drop = new Drop(-1);
        drop.setPosition(posTo);
        drop.setOwnerID(ownerId);
        Set<Integer> quests = new HashSet<>();
        if (itemID != 0) {
            item = ItemData.getItemCopy(itemID, true);
            if (item != null) {
                item.setQuantity(dropInfo.getQuantity());
                drop.setItem(item);
                ItemInfo itemInfo = ItemData.getItemById(itemID);
                if (itemInfo != null && itemInfo.isQuest()) {
                    quests = itemInfo.getQuestIDs();
                }
            } else {
                log.error("Was not able to find the item to drop! id = " + itemID);
                return;
            }
        } else {
            drop.setMoney(dropInfo.getMoney());
        }
        addObj(drop);
        drop.setExpireTime(System.currentTimeMillis() + GameConstants.DROP_REMOVE_OWNERSHIP_TIME * 1000);
        addObjScheduledFuture(drop, EventManager.addEvent(() -> removeDrop(drop.getObjectId(), DropLeaveType.Fade, 0, true), DROP_REMAIN_ON_GROUND_TIME, TimeUnit.SECONDS));
        EventManager.addEvent(() -> drop.setOwnerID(0), GameConstants.DROP_REMOVE_OWNERSHIP_TIME, TimeUnit.SECONDS);
        for (MapleCharacter chr : getCharacters()) {
            if (chr.hasAnyQuestsInProgress(quests)) {
                broadcastMessage(ChannelPacket.dropEnterField(drop, DropEnterType.Floating, posFrom, posTo, ownerId, drop.canBePickedUpBy(chr)));
            }
        }
    }

    public void addObjScheduledFuture(MapleMapObj obj, ScheduledFuture scheduledFuture) {
        getObjScheduledFutures().put(obj, scheduledFuture);
    }

    public void drop(Drop drop, Position posFrom, Position posTo, boolean ignoreTradability) {
        Item item = drop.getItem();
        boolean isTradable = true;
        if (item != null) {
            ItemInfo itemInfo = ItemData.getItemById(item.getItemId());
            isTradable = ignoreTradability ||
                    (item.isTradable() && (ItemConstants.isEquip(item.getItemId()) || itemInfo != null
                            && !itemInfo.isQuest()));
        }
        drop.setPosition(posTo);
        if (isTradable) {
            addObj(drop);
            addObjScheduledFuture(drop, EventManager.addEvent(() -> removeDrop(drop.getObjectId(), DropLeaveType.Fade, 0, true), DROP_REMAIN_ON_GROUND_TIME, TimeUnit.SECONDS));
        } else {
            drop.setObjectId(getObjectIdCounter().getAndIncrement());
        }
        if (!isTradable) {
            broadcastMessage(ChannelPacket.dropEnterField(drop, DropEnterType.FadeAway, posFrom));
        } else {
            broadcastMessage(ChannelPacket.dropEnterField(drop, DropEnterType.Floating, posFrom, posTo));
        }
    }


    public void removeDrop(int dropId, DropLeaveType type, int charId, boolean schedule) {
        MapleMapObj obj = getObj(dropId);
        if (obj instanceof Drop) {
            broadcastMessage(ChannelPacket.dropLeaveField(type, charId, dropId));
            removeObj(dropId, schedule);
        }
    }

    public Foothold findFootHoldBelow(Position position) {
        Set<Foothold> footholds = getFootholds().stream().filter(fh -> fh.getX1() <= position.getX() && fh.getX2() >= position.getX()).collect(Collectors.toSet());
        Foothold res = null;
        int lastY = Integer.MAX_VALUE;
        for (Foothold fh : footholds) {
            int y = fh.getYFromX(position.getX());
            if (res == null && y >= position.getY()) {
                res = fh;
                lastY = y;
            } else {
                if (y < lastY && y >= position.getY()) {
                    res = fh;
                    lastY = y;
                }
            }
        }
        return res;
    }

    public Foothold getFoothold(int fh) {
        return getFootholds().stream().filter(f -> f.getId() == fh).findFirst().orElse(null);
    }

    public Foothold getFootholdBelow(Position position) {
        Set<Foothold> footholds = getFootholds().stream().filter(fh -> fh.getX1() <= position.getX() && fh.getX2() >= position.getX()).collect(Collectors.toSet());
        Foothold res = null;
        int lastY = Integer.MAX_VALUE;
        for (Foothold fh : footholds) {
            int y = fh.getYFromX(position.getX());
            if (res == null && y >= position.getY()) {
                res = fh;
                lastY = y;
            } else {
                if (y < lastY && y >= position.getY()) {
                    res = fh;
                    lastY = y;
                }
            }
        }
        return res;
    }
}
