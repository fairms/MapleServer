package im.cave.ms.provider.data;

import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.client.field.obj.mob.MobGen;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.FieldType;
import im.cave.ms.enums.PortalType;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static im.cave.ms.provider.wz.MapleDataType.CANVAS;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 11/22 0:06
 */
public class MapData {
    private static final List<MapleMap> maps = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(MapData.class);
    private static final MapleDataProvider mapData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Map002.wz"));

    public static MapleMap loadMapDataFromWz(int mapId, int world, int channel) {
        String wzPath = getMapWzPath(mapId);
        MapleData data = mapData.getData(wzPath);
        if (data == null) {
            return null;
        }
        MapleData info = data.getChildByPath("info");
        String link = MapleDataTool.getString(data.getChildByPath("link"), "");
        if (!link.equals("")) {
            wzPath = getMapWzPath(Integer.parseInt(link));
            data = mapData.getData(wzPath);
        }
        String[] imgName = data.getName().split("\\.");
        int id = Integer.parseInt(imgName[0]);
        MapleMap map = new MapleMap(id, world, channel);
        for (MapleData attr : info.getChildren()) {
            if (attr.getType() == CANVAS) {
                continue;
            }
            String name = attr.getName();
            String value = MapleDataTool.getString(attr);
            switch (name) {
                case "town":
                    map.setTown(Integer.parseInt(value) != 0);
                    break;
                case "swim":
                    map.setSwim(Integer.parseInt(value) != 0);
                    break;
                case "fieldLimit":
                    map.setFieldLimit(Long.parseLong(value));
                    break;
                case "returnMap":
                    map.setReturnMap(Integer.parseInt(value));
                    break;
                case "forcedReturn":
                    map.setForcedReturn(Integer.parseInt(value));
                    break;
                case "mobRate":
                    map.setMobRate(Double.parseDouble(value));
                    break;
                case "fly":
                    map.setFly(Integer.parseInt(value) != 0);
                    break;
                case "onFirstUserEnter":
                    map.setOnFirstUserEnter(value);
                    break;
                case "onUserEnter":
                    map.setOnUserEnter(value);
                    break;
                case "fieldScript":
                    map.setFieldScript(value);
                    break;
                case "reactorShuffle":
                    map.setReactorShuffle(Integer.parseInt(value) != 0);
                    break;
                case "expeditionOnly":
                    map.setExpeditionOnly(Integer.parseInt(value) != 0);
                    break;
                case "partyOnly":
                    map.setPartyOnly(Integer.parseInt(value) != 0);
                    break;
                case "isNeedSkillForFly":
                    map.setNeedSkillForFly(Integer.parseInt(value) != 0);
                    break;
                case "fixedMobCapacity":
                    map.setFixedMobCapacity(Integer.parseInt(value));
                    break;
                case "createMobInterval":
                    map.setCreateMobInterval(Integer.parseInt(value));
                    break;
                case "timeOut":
                    map.setTimeOut(Integer.parseInt(value));
                    break;
                case "timeLimit":
                    map.setTimeLimit(Integer.parseInt(value));
                    break;
                case "lvLimit":
                    map.setLvLimit(Integer.parseInt(value));
                    break;
                case "lvForceMove":
                    map.setLvForceMove(Integer.parseInt(value));
                    break;
                case "consumeItemCoolTime":
                    map.setConsumeItemCoolTime(Integer.parseInt(value));
                    break;
                case "link":
                    map.setLink(Integer.parseInt(value));
                    break;
                case "bossMobID":
                    map.setBossMobID(Integer.parseInt(value));
                    break;
                case "VRTop":
                    map.setVrTop(Integer.parseInt(value));
                    break;
                case "VRLeft":
                    map.setVrLeft(Integer.parseInt(value));
                    break;
                case "VRBottom":
                    map.setVrBottom(Integer.parseInt(value));
                    break;
                case "VRRight":
                    map.setVrRight(Integer.parseInt(value));
                    break;
                case "fieldType":
                    if (value.equals("")) {
                        map.setFieldType(FieldType.DEAFULT);
                    } else {
                        FieldType fieldType = FieldType.getByVal(Integer.parseInt(value));
                        if (fieldType == null) {
                            map.setFieldType(FieldType.DEAFULT);
                            break;
                        }

                        map.setFieldType(fieldType);
                    }
                    break;
            }
        }
        if (map.getFieldType() == null) {
            map.setFieldType(FieldType.DEAFULT);
        }
        MapleData footholds = data.getChildByPath("foothold");
        if (footholds != null) {
            for (MapleData footRoot : footholds.getChildren()) {
                int layerId = Integer.parseInt(footRoot.getName());
                for (MapleData footCat : footRoot) {
                    int groupId = Integer.parseInt(footCat.getName());
                    for (MapleData footHold : footCat) {
                        int fhId = Integer.parseInt(footHold.getName());
                        int x1 = MapleDataTool.getInt(footHold.getChildByPath("x1"));
                        int y1 = MapleDataTool.getInt(footHold.getChildByPath("y1"));
                        int x2 = MapleDataTool.getInt(footHold.getChildByPath("x2"));
                        int y2 = MapleDataTool.getInt(footHold.getChildByPath("y2"));
                        int prev = MapleDataTool.getInt(footHold.getChildByPath("prev"));
                        int next = MapleDataTool.getInt(footHold.getChildByPath("next"));
                        int force = MapleDataTool.getInt(footHold.getChildByPath("force"), 0);
                        Foothold fh = new Foothold(fhId, layerId, groupId, x1, y1, x2, y2, prev, next, force);
                        map.addFoothold(fh);
                    }
                }
            }
        }
        MapleData portals = data.getChildByPath("portal");
        if (portals != null) {
            for (MapleData portalData : portals.getChildren()) {
                int portalId = Integer.parseInt(portalData.getName());
                Portal portal = new Portal((byte) portalId);
                for (MapleData attr : portalData.getChildren()) {
                    String name = attr.getName();
                    switch (name) {
                        case "pn":
                            portal.setName(MapleDataTool.getString(attr));
                            break;
                        case "tn":
                            portal.setTargetPortalName(MapleDataTool.getString(attr));
                            break;
                        case "tm":
                            portal.setTargetMapId(MapleDataTool.getInt(attr));
                            break;
                        case "x":
                            portal.setX(MapleDataTool.getInt(attr));
                            break;
                        case "y":
                            portal.setY(MapleDataTool.getInt(attr));
                            break;
                        case "script":
                            portal.setScript(MapleDataTool.getString(attr));
                            break;
                        case "pt":
                            portal.setType(PortalType.getTypeByInt(MapleDataTool.getInt(attr)));
                            break;
                        case "horizontalImpact":
                            portal.setHorizontalImpact(MapleDataTool.getInt(attr));
                            break;
                        case "verticalImpact":
                            portal.setVerticalImpact(MapleDataTool.getInt(attr));
                            break;
                        case "onlyOnce":
                            portal.setOnlyOnce(MapleDataTool.getInt(attr) != 0);
                            break;
                        case "delay":
                            portal.setDelay(MapleDataTool.getInt(attr));
                    }
                }
                map.addPortal(portal);
            }
        }

        MapleData lifes = data.getChildByPath("life");
        if (lifes != null) {
            for (MapleData lifeData : lifes) {
                MapleMapObj life = new MapleMapObj();
                for (MapleData lifeAttr : lifeData.getChildren()) {
                    String name = lifeAttr.getName();
                    String value = MapleDataTool.getString(lifeAttr);
                    switch (name) {
                        case "type":
                            life.setLifeType(value);
                            break;
                        case "id":
                            life.setTemplateId(Integer.parseInt(value));
                            break;
                        case "x":
                            life.setX(Integer.parseInt(value));
                            break;
                        case "y":
                            life.setY(Integer.parseInt(value));
                            break;
                        case "mobTime":
                            life.setMobTime(Integer.parseInt(value));
                            break;
                        case "f":
                            life.setFlip(Integer.parseInt(value) != 0);
                            break;
                        case "hide":
                            life.setHide(Integer.parseInt(value) != 0);
                            break;
                        case "fh":
                            life.setFh(Integer.parseInt(value));
                            break;
                        case "cy":
                            life.setCy(Integer.parseInt(value));
                            break;
                        case "rx0":
                            life.setRx0(Integer.parseInt(value));
                            break;
                        case "rx1":
                            life.setRx1(Integer.parseInt(value));
                            break;
                        case "limitedname":
                            life.setLimitedName(value);
                            break;
                        case "useDay":
                            life.setUseDay(Integer.parseInt(value) != 0);
                            break;
                        case "useNight":
                            life.setUseNight(Integer.parseInt(value) != 0);
                            break;
                        case "hold":
                            life.setHold(Integer.parseInt(value) != 0);
                            break;
                        case "nofoothold":
                            life.setNoFoothold(Integer.parseInt(value) != 0);
                            break;
                        case "dummy":
                            life.setDummy(Integer.parseInt(value) != 0);
                            break;
                        case "spine":
                            life.setSpine(Integer.parseInt(value) != 0);
                            break;
                        case "mobTimeOnDie":
                            life.setMobTimeOnDie(Integer.parseInt(value) != 0);
                            break;
                        case "regenStart":
                            life.setRegenStart(Integer.parseInt(value));
                            break;
                        case "mobAliveReq":
                            life.setMobAliveReq(Integer.parseInt(value));
                            break;
                        default:
                            log.warn("UnKnown life property {}", name);
                            break;
                    }
                }
                if (life.getLifeType().equalsIgnoreCase("m")) {
                    MobGen mobGen = life.getMobGen();
                    if (mobGen != null) {
                        map.addMobGen(mobGen);
                    }
                } else if (life.getLifeType().equalsIgnoreCase("n")) {
                    Npc npc = life.getNpc();
                    if (npc != null) {
                        map.addObj(life.getNpc());
                    }
                }
            }
        }
        if (map.getFixedMobCapacity() == 0 && map.getMobGens().size() != 0) {
            map.setFixedMobCapacity(map.getMobGens().size());
        }
        map.generateMobs(true);
        maps.add(map);
        return map;
    }


    private static String getMapWzPath(int mapId) {
        String imgName = StringUtil.getLeftPaddedStr(Integer.toString(mapId), '0', 9);
        int area = mapId / 100000000;
        return String.format("Map/Map%s/%s.img", area, imgName);
    }

}
