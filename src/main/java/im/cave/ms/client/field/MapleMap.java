package im.cave.ms.client.field;

import im.cave.ms.client.character.Clock;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.connection.netty.Packet;
import im.cave.ms.provider.info.DropInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobGen;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.UserRemote;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.FieldOption;
import im.cave.ms.enums.FieldType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.scripting.map.MapScriptManager;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
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
    private List<MobGen> mobGens = new ArrayList<>();
    private String onFirstUserEnter = "";
    private String onUserEnter = "";
    private int fixedMobCapacity;
    private AtomicInteger objectIdCounter = new AtomicInteger(1000000);
    private boolean userFirstEnter = false;
    private String fieldScript = "";
    private int bossMobID;
    private int VrTop, VrBottom, VrLeft, VrRight;
    private Map<MapleMapObj, ScheduledFuture<MapleMapObj>> objScheduledFutures;
    private ConcurrentHashMap<MapleMapObj, MapleCharacter> objControllers = new ConcurrentHashMap<>();
    /*
           时钟 :  1.地图 √
                  2.角色
     */
    private Clock clock;

    private Set<FieldEffect> fieldEffects;

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
        if (!getCharacters().contains(chr)) {
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
            broadcastMessage(chr, WorldPacket.userEnterMap(chr), false);
        }
    }

    public void sendMapObject() {
        for (MapleCharacter character : characters) {
            sendMapObjectPackets(character);
        }
    }

    public void sendMapObjectPackets(MapleCharacter chr) {
        List<MapleMapObj> objects = new ArrayList<>(objs.values());
        for (MapleMapObj object : objects) {
            Rect rectAround = chr.getVisibleRect();
            if (rectAround.hasPositionInside(object.getPosition()) && !chr.getVisibleMapObjs().contains(object)) {
                chr.addVisibleMapObj(object);
                spawnObj(object, chr);
            } else if (!rectAround.hasPositionInside(object.getPosition()) && chr.getVisibleMapObjs().contains(object)) {
                objFarawayChr(object, chr);
                chr.removeVisibleMapObj(object);
            }
        }
        for (MapleCharacter character : characters) {
            if (character == chr) {
                continue;
            }
            if (!chr.getVisibleChars().contains(character)) {
                chr.announce(WorldPacket.userEnterMap(character));
                chr.announce(UserRemote.hiddenEffectEquips(character));
                chr.announce(UserRemote.setSoulEffect(character));
                chr.getVisibleChars().add(character);
            }
        }
    }


    public void broadcastMessage(OutPacket packet) {
        broadcastMessage(null, packet);
    }

    public void broadcastMessage(MapleCharacter source, Packet packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet);
    }


    public void broadcastMessage(MapleCharacter source, Packet packet) {
        for (MapleCharacter chr : characters) {
            if (chr != source) {
                chr.announce(packet);
            }
        }
    }


    public Portal getPortal(String portalName) {
        return portals.stream()
                .filter(portal -> portal.getName().equals(portalName))
                .findAny().orElse(null);
    }

    public Portal getPortal(byte portal) {
        return portals.stream()
                .filter(p -> p.getId() == portal)
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
        //todo
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

    public Set<Npc> getNpcs() {
        return getLifesByClass(Npc.class);
    }

    public Set<Mob> getMobs() {
        return getLifesByClass(Mob.class);
    }

    public Set<Drop> getDrops() {
        return getLifesByClass(Drop.class);
    }

    private <T extends MapleMapObj> Set<T> getLifesByClass(Class<? extends MapleMapObj> clazz) {
        return (Set<T>) getObjs().values().stream()
                .filter(l -> l.getClass().equals(clazz))
                .collect(Collectors.toSet());
    }


    public void removeChar(MapleCharacter chr) {
        characters.removeIf(character -> character.getId() == chr.getId());
        for (Map.Entry<MapleMapObj, MapleCharacter> entry : getObjControllers().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(chr)) {
                getObjControllers().remove(entry.getKey());
                setRandomController(entry.getKey());
            }
        }
        broadcastMessage(chr, WorldPacket.userLeaveMap(chr.getId()));
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
                x = x + diff;
                Position posTo;
                if (fh == null) {
                    posTo = position.deepCopy();
                } else {
                    int y;
                    if (x > maxX || x < minX) {
                        Foothold footholdBelow = getFootholdBelow(new Position(x, position.getY()));
                        y = footholdBelow != null ? footholdBelow.getYFromX(x) : position.getY();
                    } else {
                        y = fh.getYFromX(x);
                    }
                    posTo = new Position(x, y);
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
        drop.setOwnerId(ownerId);
        drop.setQuestId(dropInfo.getQuestId());
        Set<Integer> quests = new HashSet<>();
        if (itemID != 0) {
            item = ItemData.getItemCopy(itemID, true);
            if (item != null) {
                item.setQuantity(dropInfo.getQuantity());
                drop.setItem(item);
                ItemInfo itemInfo = ItemData.getItemInfoById(itemID);
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
        drop.setExpireTime(DateUtil.getFileTime(System.currentTimeMillis() + GameConstants.DROP_REMOVE_OWNERSHIP_TIME * 1000));
        addObjScheduledFuture(drop, EventManager.addEvent(() -> removeDrop(drop.getObjectId(), DropLeaveType.Fade, 0, true), DROP_REMAIN_ON_GROUND_TIME, TimeUnit.SECONDS));
        EventManager.addEvent(() -> drop.setOwnerId(0), GameConstants.DROP_REMOVE_OWNERSHIP_TIME, TimeUnit.SECONDS);
        for (MapleCharacter chr : getCharacters()) {
            if (chr.hasAnyQuestsInProgress(quests)) {
                chr.announce(WorldPacket.dropEnterField(drop, DropEnterType.Floating, posFrom, posTo, 190, drop.canBePickedUpBy(chr)));
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
            ItemInfo itemInfo = ItemData.getItemInfoById(item.getItemId());
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
            broadcastMessage(WorldPacket.dropEnterField(drop, DropEnterType.FadeAway, posFrom));
        } else {
            broadcastMessage(WorldPacket.dropEnterField(drop, DropEnterType.Floating, posFrom, posTo));
        }
    }


    public void removeDrop(int dropId, int pickupUserId, boolean fromSchedule, int petId) {
        MapleMapObj obj = getObj(dropId);
        if (obj instanceof Drop) {
            if (petId >= 0) {
                broadcastMessage(WorldPacket.dropLeaveField(DropLeaveType.PetPickup, pickupUserId, obj.getObjectId(),
                        (short) 0, petId, 0));
            } else if (pickupUserId != 0) {
                broadcastMessage(WorldPacket.dropLeaveField(dropId, pickupUserId));
            } else {
                broadcastMessage(WorldPacket.dropLeaveField(DropLeaveType.Fade, pickupUserId, obj.getObjectId(),
                        (short) 0, 0, 0));
            }
            removeObj(dropId, fromSchedule);
        }
    }

    public void removeDrop(int dropId, DropLeaveType type, int charId, boolean schedule) {
        MapleMapObj obj = getObj(dropId);
        if (obj instanceof Drop) {
            broadcastMessage(WorldPacket.dropLeaveField(type, charId, dropId));
            removeObj(dropId, schedule);
        }
    }

    public Foothold findFootHoldBelow(Position position) {
        return getFoothold(position);
    }

    public Foothold getFoothold(int fh) {
        return getFootholds().stream().filter(f -> f.getId() == fh).findFirst().orElse(null);
    }

    public Foothold getFootholdBelow(Position position) {
        return getFoothold(position);
    }

    private Foothold getFoothold(Position position) {
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

    public Portal getSpawnPortalNearby(Position position) {
        double des = Double.MAX_VALUE;
        Portal ret = null;
        for (Portal spawnPortal : getSpawnPortals()) {
            Position spPos = new Position(spawnPortal.getX(), spawnPortal.getY());
            double distance = position.getDistance(spPos);
            if (distance < des) {
                des = distance;
                ret = spawnPortal;
            }
        }
        return ret;
    }

    public List<Portal> getSpawnPortals() {
        return getPortals().stream().filter(portal -> portal.getName().equalsIgnoreCase("sp")).collect(Collectors.toList());
    }

    public Portal getSpawnPortal() {
        return Util.findWithPred(getPortals(), portal -> portal.getName().equalsIgnoreCase("sp"));
    }

    public void spawnSummon(Summon summon) {
        Summon oldSummon = (Summon) getObjs().values().stream()
                .filter(s -> s instanceof Summon &&
                        ((Summon) s).getChr() == summon.getChr() &&
                        ((Summon) s).getSkillID() == summon.getSkillID()).findFirst().orElse(null);
        if (oldSummon != null) {
            removeObj(oldSummon.getObjectId(), false);
        }
        spawnObj(summon, null);
    }


    private void objFarawayChr(MapleMapObj object, MapleCharacter chr) {
        if (getObjControllers().get(object) == chr) {
            getObjControllers().remove(object);
            object.sendLeavePacket(chr);
            setRandomController(object);
        }
    }


    public void spawnObj(MapleMapObj obj, MapleCharacter chr) {
        addObj(obj);
        if (getCharacters().size() > 0) {
            if (chr == null) {
                getCharInRect(obj.getVisibleRect()).forEach(obj::sendSpawnPacket);
            } else {
                obj.sendSpawnPacket(chr);
            }
            MapleCharacter controller = null;
            if (getObjControllers().containsKey(obj)) {
                controller = getObjControllers().get(obj);
            }
            if (controller == null) {
                setRandomController(obj);
            }
        }
    }

    private void setRandomController(MapleMapObj obj) {
        MapleCharacter controller;
        if (getCharacters().size() > 0) {
            List<MapleCharacter> characters = getCharInRect(obj.getVisibleRect());
            if (characters.size() > 0) {
                controller = Util.getRandomFromCollection(characters);
                obj.notifyControllerChange(controller);
                putObjController(obj, controller);
            }
        }
    }

    private void putObjController(MapleMapObj obj, MapleCharacter controller) {
        getObjControllers().put(obj, controller);
    }


    public List<MapleCharacter> getCharInRect(Rect rect) {
        return getCharacters().stream().filter(character -> rect.hasPositionInside(character.getPosition())).collect(Collectors.toList());
    }

    public MapleCharacter getCharById(int charId) {
        return Util.findWithPred(characters, character -> character.getId() == charId);
    }

    public MapleCharacter getCharByName(String name) {
        return Util.findWithPred(characters, character -> character.getName().equals(name));
    }

    public Npc getNpcById(int npcId) {
        return Util.findWithPred(getNpcs(), npc -> npc.getTemplateId() == npcId);
    }
}
