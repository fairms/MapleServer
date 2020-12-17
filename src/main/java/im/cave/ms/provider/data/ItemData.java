package im.cave.ms.provider.data;

import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.items.ItemOption;
import im.cave.ms.client.items.ItemSkill;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.ItemState;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataDirectoryEntry;
import im.cave.ms.provider.wz.MapleDataFileEntry;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static im.cave.ms.client.items.Item.Type.ITEM;
import static im.cave.ms.constants.GameConstants.MAX_TIME;
import static im.cave.ms.enums.ScrollStat.createType;
import static im.cave.ms.enums.ScrollStat.cursed;
import static im.cave.ms.enums.ScrollStat.forceUpgrade;
import static im.cave.ms.enums.ScrollStat.incACC;
import static im.cave.ms.enums.ScrollStat.incDEX;
import static im.cave.ms.enums.ScrollStat.incEVA;
import static im.cave.ms.enums.ScrollStat.incINT;
import static im.cave.ms.enums.ScrollStat.incIUC;
import static im.cave.ms.enums.ScrollStat.incJump;
import static im.cave.ms.enums.ScrollStat.incLUK;
import static im.cave.ms.enums.ScrollStat.incMAD;
import static im.cave.ms.enums.ScrollStat.incMDD;
import static im.cave.ms.enums.ScrollStat.incMHP;
import static im.cave.ms.enums.ScrollStat.incMMP;
import static im.cave.ms.enums.ScrollStat.incPAD;
import static im.cave.ms.enums.ScrollStat.incPDD;
import static im.cave.ms.enums.ScrollStat.incPERIOD;
import static im.cave.ms.enums.ScrollStat.incRandVol;
import static im.cave.ms.enums.ScrollStat.incReqLevel;
import static im.cave.ms.enums.ScrollStat.incSTR;
import static im.cave.ms.enums.ScrollStat.incSpeed;
import static im.cave.ms.enums.ScrollStat.maxSuperiorEqp;
import static im.cave.ms.enums.ScrollStat.noNegative;
import static im.cave.ms.enums.ScrollStat.optionType;
import static im.cave.ms.enums.ScrollStat.randOption;
import static im.cave.ms.enums.ScrollStat.randStat;
import static im.cave.ms.enums.ScrollStat.reqEquipLevelMax;
import static im.cave.ms.enums.ScrollStat.reqRUC;
import static im.cave.ms.enums.ScrollStat.speed;
import static im.cave.ms.enums.ScrollStat.success;
import static im.cave.ms.enums.ScrollStat.tuc;
import static im.cave.ms.provider.wz.MapleDataType.CANVAS;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.wz
 * @date 11/21 20:48
 */
public class ItemData {
    private static final Logger log = LoggerFactory.getLogger(ItemData.class);
    private static final MapleDataProvider chrData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Character.wz"));
    private static final MapleDataProvider itemData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Item.wz"));

    public static Map<Integer, Equip> equips = new HashMap<>();
    public static Map<Integer, ItemInfo> items = new HashMap<>();
    public static Set<Integer> startItems = new HashSet<>();
    public static Map<Integer, ItemOption> familiarOptions = new HashMap<>();
    public static Map<Integer, ItemOption> itemOptions = new HashMap<>();

    static {
        loadStartItems();
        loadHotSpotItems();
        loadItemOptions();
        loadFamiliarOptions();
    }

    private static void loadHotSpotItems() {

    }


    public static Equip getEquipById(int equipId) {
        if (!equips.containsKey(equipId)) {
            return getEquipFromWz(equipId);
        }
        return equips.get(equipId);
    }

    public static ItemInfo getItemInfoById(int itemId) {
        if (!items.containsKey(itemId)) {
            return getItemFromWz(itemId);
        }
        return items.get(itemId);
    }


    public static Map<Integer, Equip> getEquips() {
        return equips;
    }

    public static Map<Integer, ItemInfo> getItems() {
        return items;
    }


    public static Equip getEquipFromWz(int equipId) {
        MapleData data = getItemData(equipId);
        if (data == null) {
            return null;
        }
        MapleData info = data.getChildByPath("info");
        if (info == null) {
            return null;
        }
        Equip equip = new Equip();
        equip.setInvType(InventoryType.EQUIP);
        equip.setType(Item.Type.EQUIP);
        equip.setItemId(equipId);
        equip.setExpireTime(MAX_TIME);
        List<Integer> options = new ArrayList<>(7);
        for (MapleData attr : info.getChildren()) {
            if (attr.getType() == CANVAS) {
                continue;
            }
            String name = attr.getName();
            switch (name) {
                case "islot":
                    equip.setISlot(MapleDataTool.getString(attr));
                    break;
                case "vslot":
                    equip.setVSlot(MapleDataTool.getString(attr));
                    break;
                case "reqJob":
                    equip.setRJob(MapleDataTool.getShort(attr));
                    break;
                case "reqLevel":
                    equip.setRLevel(MapleDataTool.getShort(attr));
                    break;
                case "reqSTR":
                    equip.setRStr(MapleDataTool.getShort(attr));
                    break;
                case "reqDEX":
                    equip.setRDex(MapleDataTool.getShort(attr));
                    break;
                case "reqINT":
                    equip.setRInt(MapleDataTool.getShort(attr));
                    break;
                case "reqPOP":
                    equip.setRPop(MapleDataTool.getShort(attr));
                    break;
                case "incSTR":
                    equip.setIStr(MapleDataTool.getShort(attr));
                    break;
                case "incDEX":
                    equip.setIDex(MapleDataTool.getShort(attr));
                    break;
                case "incINT":
                    equip.setIInt(MapleDataTool.getShort(attr));
                    break;
                case "incLUK":
                    equip.setILuk(MapleDataTool.getShort(attr));
                    break;
                case "incPDD":
                    equip.setIPDD(MapleDataTool.getShort(attr));
                    break;
                case "incMDD":
                    equip.setIMDD(MapleDataTool.getShort(attr));
                    break;
                case "incMHP":
                    equip.setIMaxHp(MapleDataTool.getShort(attr));
                    break;
                case "incMMP":
                    equip.setIMaxMp(MapleDataTool.getShort(attr));
                    break;
                case "incPAD":
                    equip.setIPad(MapleDataTool.getShort(attr));
                    break;
                case "incMAD":
                    equip.setIMad(MapleDataTool.getShort(attr));
                    break;
                case "incEVA":
                    equip.setIEva(MapleDataTool.getShort(attr));
                    break;
                case "incACC":
                    equip.setIAcc(MapleDataTool.getShort(attr));
                    break;
                case "incSpeed":
                    equip.setISpeed(MapleDataTool.getShort(attr));
                    break;
                case "incJump":
                    equip.setIJump(MapleDataTool.getShort(attr));
                    break;
                case "damR":
                    equip.setDamR(MapleDataTool.getShort(attr));
                    break;
                case "statR":
                    equip.setStatR(MapleDataTool.getShort(attr));
                    break;
                case "imdR":
                    equip.setImdr(MapleDataTool.getShort(attr));
                    break;
                case "bdR":
                    equip.setBdr(MapleDataTool.getShort(attr));
                    break;
                case "tuc":
                    equip.setTuc(MapleDataTool.getShort(attr));
                    break;
                case "IUCMax":
                    equip.setHasIUCMax(true);
                    equip.setIucMax(MapleDataTool.getShort(attr));
                    break;
                case "setItemID":
                    equip.setSetItemId(MapleDataTool.getInt(attr));
                    break;
                case "price":
                    equip.setPrice(MapleDataTool.getInt(attr));
                    break;
                case "attackSpeed":
                    equip.setAttackSpeed(MapleDataTool.getInt(attr));
                    break;
                case "cash":
                    equip.setCash(MapleDataTool.getInt(attr) != 0);
                    break;
                case "expireOnLogout":
                    equip.setExpireOnLogout(MapleDataTool.getInt(attr) != 0);
                    break;
                case "exItem":
                    equip.setExItem(MapleDataTool.getInt(attr) != 0);
                    break;
                case "notSale":
                    equip.setNotSale(MapleDataTool.getInt(attr) != 0);
                    break;
                case "only":
                    equip.setOnly(MapleDataTool.getInt(attr) != 0);
                    break;
                case "tradeBlock":
                    equip.setTradeBlock(MapleDataTool.getInt(attr) != 0);
                    break;
                case "fixedPotential":
                    equip.setFixedPotential(MapleDataTool.getInt(attr) != 0);
                    break;
                case "noPotential":
                    equip.setNoPotential(MapleDataTool.getInt(attr) != 0);
                    break;
                case "bossReward":
                    equip.setBossReward(MapleDataTool.getInt(attr) != 0);
                    break;
                case "superiorEqp":
                    equip.setSuperiorEqp(MapleDataTool.getInt(attr) != 0);
                    break;
                case "reduceReq":
                    equip.setIReduceReq((byte) MapleDataTool.getShort(attr));
                    break;
                case "fixedGrade":
                    equip.setFixedGrade(MapleDataTool.getInt(attr));
                    break;
                case "specialGrade":
                    equip.setSpecialGrade(MapleDataTool.getInt(attr));
                    break;
                case "charmEXP":
                    equip.setCharmEXP(MapleDataTool.getInt(attr));
                    break;
                case "limitBreak":
                    equip.setLimitBreak(MapleDataTool.getInt(attr));
                case "level":
                    // TODO: proper parsing, actual stats and skills for each level the equip gets
                    MapleData levelCase = attr.getChildByPath("case");
                    if (levelCase != null) {
                        MapleData case0 = levelCase.getChildByPath("0");
                        if (case0 != null) {
                            MapleData case1 = case0.getChildByPath("1");
                            if (case1 != null) {
                                MapleData equipmentSkill = case1.getChildByPath("EquipmentSkill");
                                if (equipmentSkill != null) {
                                    for (MapleData equipSkill : equipmentSkill.getChildren()) {
                                        MapleData id = equipSkill.getChildByPath("id");
                                        MapleData level = equipSkill.getChildByPath("level");
                                        equip.addItemSkill(new ItemSkill(Integer.parseInt(MapleDataTool.getString(id)), Byte.parseByte(MapleDataTool.getString(level))));
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "option":
                    for (MapleData whichOption : attr.getChildren()) {
                        int index = Integer.parseInt(whichOption.getName());
                        MapleData option = whichOption.getChildByPath("option");
                        MapleData id = option.getChildByPath("attr");
                        options.set(index, MapleDataTool.getInt(id));
                    }
                    break;
                case "effectItemID":
                    equip.setEffectItemID(MapleDataTool.getInt(attr));
                    break;
            }
            for (int i = 0; i < 7 - options.size(); i++) {
                options.add(0);
            }
            equip.setOptions(options);
        }
        equips.put(equip.getItemId(), equip);
        return equip;
    }

    public static ItemInfo getItemFromWz(int itemId) {
        MapleData data = getItemData(itemId);
        if (data == null) {
            return null;
        }
        MapleData info = data.getChildByPath("info");
        if (info == null) {
            return null;
        }
        ItemInfo item = new ItemInfo();
        item.setItemId(itemId);
        if (ItemConstants.isEquip(itemId)) {
            item.setInvType(InventoryType.EQUIP);
        } else {
            MapleData parent = (MapleData) data.getParent();
            item.setInvType(InventoryType.getInvTypeByString(parent.getSubPath()));
        }
        for (MapleData attr : info.getChildren()) {
            if (attr.getType() == CANVAS) {
                continue;
            }
            String name = attr.getName();
            int intValue = 0;
            if (Util.isNumber(MapleDataTool.getString(attr, ""))) {
                intValue = MapleDataTool.getInt(attr);
            }
            switch (name) {
                case "cash":
                    item.setCash(intValue != 0);
                    break;
                case "price":
                    item.setPrice(intValue);
                    break;
                case "slotMax":
                    item.setSlotMax(intValue);
                    break;
                // info not currently interesting. May be interesting in the future.
                case "icon":
                case "iconRaw":
                case "iconD":
                case "iconReward":
                case "iconShop":
                case "recoveryHP":
                case "recoveryMP":
                case "sitAction":
                case "bodyRelMove":
                case "only":
                case "noDrop":
                case "timeLimited":
                case "accountSharable":
                case "nickTag":
                case "nickSkill":
                case "endLotteryDate":
                case "noFlip":
                case "noMoveToLocker":
                case "soldInform":
                case "purchaseShop":
                case "flatRate":
                case "limitMin":
                case "protectTime":
                case "maxDays":
                case "reset":
                case "replace":
                case "expireOnLogout":
                case "max":
                case "lvOptimum":
                case "lvRange":
                case "limitedLv":
                case "tradeReward":
                case "type":
                case "floatType":
                case "message":
                case "pquest":
                case "bonusEXPRate":
                case "notExtend":
                    break;
                case "skill":
                case "spec":
                case "stateChangeItem":
                case "direction":
                case "exGrade":
                case "exGradeWeight":
                case "effect":
                case "bigSize":
                case "nickSkillTimeLimited":
                case "StarPlanet":
                case "useTradeBlock":
                case "commerce":
                case "invisibleWeapon":
                case "sitEmotion":
                case "sitLeft":
                case "tamingMob":
                case "textInfo":
                case "lv":
                case "tradeAvailable":
                case "pickUpBlock":
                case "rewardItemID":
                case "autoPrice":
                case "selectedSlot":
                case "minusLevel":
                case "addTime":
                case "reqLevel":
                case "waittime":
                case "buffchair":
                case "cooltime":
                case "consumeitem":
                case "distanceX":
                case "distanceY":
                case "maxDiff":
                case "maxDX":
                case "levelDX":
                case "maxLevel":
                case "exp":
                case "dropBlock":
                case "dropExpireTime":
                case "animation_create":
                case "animation_dropped":
                case "noCancelMouse":
                case "soulItemType":
                case "Rate":
                case "unitPrice":
                case "delayMsg":
                case "bridlePropZeroMsg":
                case "create":
                case "nomobMsg":
                case "bridleProp":
                case "bridlePropChg":
                case "bridleMsgType":
                case "mobHP":
                case "left":
                case "right":
                case "top":
                case "bottom":
                case "useDelay":
                case "name":
                case "uiData":
                case "UI":
                case "recoveryRate":
                case "itemMsg":
                case "noRotateIcon":
                case "endUseDate":
                case "noSound":
                case "slotMat":
                case "isBgmOrEffect":
                case "bgmPath":
                case "repeat":
                case "NoCancel":
                case "rotateSpeed":
                case "gender":
                case "life":
                case "pickupItem":
                case "add":
                case "consumeHP":
                case "longRange":
                case "dropSweep":
                case "pickupAll":
                case "ignorePickup":
                case "consumeMP":
                case "autoBuff":
                case "smartPet":
                case "giantPet":
                case "shop":
                case "recall":
                case "autoSpeaking":
                case "consumeCure":
                case "meso":
                case "maplepoint":
                case "rate":
                case "overlap":
                case "lt":
                case "rb":
                case "path4Top":
                case "jumplevel":
                case "slotIndex":
                case "addDay":
                case "incLEV":
                case "cashTradeBlock":
                case "dressUpgrade":
                case "skillEffectID":
                case "emotion":
                case "tradBlock":
                case "tragetBlock":
                case "scanTradeBlock":
                case "mobPotion":
                case "ignoreTendencyStatLimit":
                case "effectByItemID":
                case "pachinko":
                case "iconEnter":
                case "iconLeave":
                case "noMoveIcon":
                case "noShadow":
                case "preventslip":
                case "recover":
                case "warmsupport":
                case "reqCUC":
                case "incCraft":
                case "reqEquipLevelMin":
                case "incPVPDamage":
                case "successRates":
                case "enchantCategory":
                case "additionalSuccess":
                case "level":
                case "specialItem":
                case "exNew":
                case "cuttable":
                case "setItemCategory":
                case "perfectReset":
                case "resetRUC":
                case "incMax":
                case "noSuperior":
                case "noRecovery":
                case "reqMap":
                case "random":
                case "limit":
                case "cantAccountSharable":
                case "LvUpWarning":
                case "canAccountSharable":
                case "canUseJob":
                case "createPeriod":
                case "iconLarge":
                case "morphItem":
                case "consumableFrom":
                case "noExpend":
                case "sample":
                case "notPickUpByPet":
                case "sharableOnce":
                case "bonusStageItem":
                case "sampleOffsetY":
                case "runOnPickup":
                case "noSale":
                case "skillCast":
                case "activateCardSetID":
                case "summonSoulMobID":
                case "cursor":
                case "karma":
                case "pointCost":
                case "itemPoint":
                case "sharedStatCostGrade":
                case "levelVariation":
                case "accountShareable":
                case "extendLimit":
                case "showMessage":
                case "mcType":
                case "consumeItem":
                case "hybrid":
                case "mobId":
                case "lvMin":
                case "lvMax":
                case "picture":
                case "ratef":
                case "time":
                case "reqGuildLevel":
                case "guild":
                case "randEffect":
                case "accountShareTag":
                case "removeEffect":
                case "forcingItem":
                case "fixFrameIdx":
                case "buffItemID":
                case "removeCharacterInfo":
                case "nameInfo":
                case "bgmInfo":
                case "flip":
                case "pos":
                case "randomChair":
                case "maxLength":
                case "continuity":
                case "specificDX":
                case "groupTWInfo":
                case "face":
                case "removeBody":
                case "mesoChair":
                case "towerBottom":
                case "towerTop":
                case "topOffset":
                case "craftEXP":
                case "willEXP":
                    break;
                case "familiarID":
                    item.setFamiliarID(intValue);
                    break;
                case "reqSkillLevel":
                    item.setReqSkillLv(intValue);
                    break;
                case "masterLevel":
                    item.setMasterLv(intValue);
                    break;
                case "tradeBlock":
                    item.setTradeBlock(intValue != 0);
                    break;
                case "notSale":
                    item.setNotSale(intValue != 0);
                    break;
                case "path":
                    item.setPath(MapleDataTool.getString(attr));
                    break;
                case "noCursed":
                    item.setNoCursed(intValue != 0);
                    break;
                case "noNegative":
                    item.putScrollStat(noNegative, intValue);
                    break;
                case "incRandVol":
                    item.putScrollStat(incRandVol, intValue);
                    break;
                case "success":
                    item.putScrollStat(success, intValue);
                    break;
                case "incSTR":
                    item.putScrollStat(incSTR, intValue);
                    break;
                case "incDEX":
                    item.putScrollStat(incDEX, intValue);
                    break;
                case "incINT":
                    item.putScrollStat(incINT, intValue);
                    break;
                case "incLUK":
                    item.putScrollStat(incLUK, intValue);
                    break;
                case "incPAD":
                    item.putScrollStat(incPAD, intValue);
                    break;
                case "incMAD":
                    item.putScrollStat(incMAD, intValue);
                    break;
                case "incPDD":
                    item.putScrollStat(incPDD, intValue);
                    break;
                case "incMDD":
                    item.putScrollStat(incMDD, intValue);
                    break;
                case "incEVA":
                    item.putScrollStat(incEVA, intValue);
                    break;
                case "incACC":
                    item.putScrollStat(incACC, intValue);
                    break;
                case "incPERIOD":
                    item.putScrollStat(incPERIOD, intValue);
                    break;
                case "incMHP":
                case "incMaxHP":
                    item.putScrollStat(incMHP, intValue);
                    break;
                case "incMMP":
                case "incMaxMP":
                    item.putScrollStat(incMMP, intValue);
                    break;
                case "incSpeed":
                    item.putScrollStat(incSpeed, intValue);
                    break;
                case "incJump":
                    item.putScrollStat(incJump, intValue);
                    break;
                case "incReqLevel":
                    item.putScrollStat(incReqLevel, intValue);
                    break;
                case "randOption":
                    item.putScrollStat(randOption, intValue);
                    break;
                case "randstat":
                case "randStat":
                    item.putScrollStat(randStat, intValue);
                    break;
                case "tuc":
                    item.putScrollStat(tuc, intValue);
                    break;
                case "incIUC":
                    item.putScrollStat(incIUC, intValue);
                    break;
                case "speed":
                    item.putScrollStat(speed, intValue);
                    break;
                case "forceUpgrade":
                    item.putScrollStat(forceUpgrade, intValue);
                    break;
                case "cursed":
                    item.putScrollStat(cursed, intValue);
                    break;
                case "maxSuperiorEqp":
                    item.putScrollStat(maxSuperiorEqp, intValue);
                    break;
                case "reqRUC":
                    item.putScrollStat(reqRUC, intValue);
                    break;
                case "bagType":
                    item.setBagType(intValue);
                    break;
                case "charmEXP":
                case "charismaEXP":
                    item.setCharmEXP(intValue);
                    break;
                case "senseEXP":
                    item.setSenseEXP(intValue);
                    break;
                case "quest":
                    item.setQuest(intValue != 0);
                    break;
                case "reqQuestOnProgress":
                    item.setReqQuestOnProgress(intValue);
                    break;
                case "qid":
                case "questId": {
                    String value = MapleDataTool.getString(attr);
                    if (value.contains(".") && value.split("[.]").length > 0) {
                        item.addQuest(Integer.parseInt(value.split("[.]")[0]));
                    } else {
                        item.addQuest(intValue);
                    }
                    break;
                }
                case "notConsume":
                    item.setNotConsume(intValue != 0);
                    break;
                case "monsterBook":
                    item.setMonsterBook(intValue != 0);
                    break;
                case "mob":
                    item.setMobID(intValue);
                    break;
                case "npc":
                    item.setNpcID(intValue);
                    break;
                case "linkedID":
                    item.setLinkedID(intValue);
                    break;
                case "reqEquipLevelMax":
                    item.putScrollStat(reqEquipLevelMax, intValue);
                    break;
                case "createType":
                    item.putScrollStat(createType, intValue);
                    break;
                case "optionType":
                    item.putScrollStat(optionType, intValue);
                    break;
                case "grade":
                    item.setGrade(intValue);
                    break;
                case "android":
                    item.setAndroid(intValue);
                    break;
                default:
                    log.warn(String.format("Unknown node: %s, itemID = %s", name, item.getItemId()));

            }
        }
        MapleData socketData = data.getChildByPath("socket");
        if (socketData != null) {
            for (MapleData socketAttr : socketData.getChildren()) {
                String name = socketAttr.getName();
                String value = MapleDataTool.getString(socketAttr);
                if (name.equals("optionType")) {
                    item.putScrollStat(optionType, Integer.parseInt(value));
                }
            }
        }
        MapleData specData = data.getChildByPath("spec");
        if (specData != null) {
            for (MapleData specAttr : specData.getChildren()) {
                String name = specAttr.getName();
                String value = MapleDataTool.getString(specAttr);
                switch (name) {
                    case "script":
                        item.setScript(value);
                        break;
                    case "npc":
                        item.setScriptNPC(Integer.parseInt(value));
                        break;
                    case "moveTo":
                        item.setMoveTo(Integer.parseInt(value));
                        break;
                    default:
                        SpecStat ss = SpecStat.getSpecStatByName(name);
                        if (ss != null && value != null) {
                            item.putSpecStat(ss, Integer.parseInt(value));
                        } else {
                            log.warn(String.format("Unhandled spec for id %d, name %s, value %s", itemId, name, value));
                        }
                }
            }
        }
        items.put(item.getItemId(), item);
        return item;
    }

    private static MapleData getItemData(int itemId) {
        boolean equip = ItemConstants.isEquip(itemId);
        MapleData ret;
        String idStr = StringUtil.getLeftPaddedStr(String.valueOf(itemId), '0', 8);
        MapleDataDirectoryEntry root;
        if (equip) {
            root = chrData.getRoot();
            for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
                for (MapleDataFileEntry iFile : topDir.getFiles()) {
                    if (iFile.getName().equals(idStr + ".img")) {
                        return chrData.getData(topDir.getName() + "/" + iFile.getName());
                    }
                }
            }
        } else {
            root = itemData.getRoot();
            for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
                for (MapleDataFileEntry iFile : topDir.getFiles()) {
                    if (iFile.getName().equals(idStr.substring(0, 4) + ".img")
                            || iFile.getName().equals(idStr.substring(0, 5) + ".img")
                            || iFile.getName().equals(idStr.substring(0, 7) + ".img")
                    ) {
                        ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                        if (ret == null) {
                            return null;
                        }
                        ret = ret.getChildByPath(idStr);
                        return ret;
                    } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                        return itemData.getData(topDir.getName() + "/" + iFile.getName());
                    }
                }
            }
        }
        return null;
    }

    public static void loadStartItems() {
        MapleDataProvider provider = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Etc.wz"));
        MapleData data = provider.getData("MakeCharInfo.img");
        startItems.addAll(searchForStartingItems(data));
    }

    private static Set<Integer> searchForStartingItems(MapleData node) {
        for (MapleData n : node.getChildren()) {
            String name = n.getName();
            String value = MapleDataTool.getString(n);
            if (value != null && StringUtil.isNumber(value) && StringUtil.isNumber(name)) {
                startItems.add(Integer.parseInt(value));
            }
            startItems.addAll(searchForStartingItems(n));
        }
        return startItems;
    }


    public static Item getItemCopy(int itemId, boolean randomize) {
        if (ItemConstants.isEquip(itemId)) {
            return getEquipDeepCopyFromID(itemId, randomize);
        } else if (ItemConstants.isPet(itemId)) {
//            return getPetDeepCopyFromID(id);
        }

        return getDeepCopyByItemInfo(getItemInfoById(itemId));
    }

    public static Equip getEquipDeepCopyFromID(int itemId, boolean randomize) {
        Equip e = getEquipById(itemId);
        Equip ret = e == null ? null : e.deepCopy();
        if (ret != null) {
            ret.setQuantity(1);
            ret.setCuttable((short) -1);
            ret.setHyperUpgrade((short) ItemState.AmazingHyperUpgradeChecked.getVal());
            ret.setType(Item.Type.EQUIP);
            ret.setInvType(InventoryType.EQUIP);
            if (randomize) {
//                if (ItemConstants.canEquipHaveFlame(ret)) {
//                    ret.randomizeFlameStats(true);
//                }
//                if (ItemConstants.canEquipHavePotential(ret)) {
//                    ItemGrade grade = ItemGrade.None;
//                    if (Util.succeedProp(GameConstants.RANDOM_EQUIP_UNIQUE_CHANCE)) {
//                        grade = ItemGrade.HiddenUnique;
//                    } else if (Util.succeedProp(GameConstants.RANDOM_EQUIP_EPIC_CHANCE)) {
//                        grade = ItemGrade.HiddenEpic;
//                    } else if (Util.succeedProp(GameConstants.RANDOM_EQUIP_RARE_CHANCE)) {
//                        grade = ItemGrade.HiddenRare;
//                    }
//                    if (grade != ItemGrade.None) {
//                        ret.setHiddenOptionBase(grade.getVal(), ItemConstants.THIRD_LINE_CHANCE);
//                    }
//                }
            }
        }
        return ret;
    }


    public static Item getDeepCopyByItemInfo(ItemInfo itemInfo) {
        if (itemInfo == null) {
            return null;
        }
        Item res = new Item();
        res.setItemId(itemInfo.getItemId());
        res.setQuantity(1);
        res.setType(ITEM);
        res.setInvType(itemInfo.getInvType());
        res.setCash(itemInfo.isCash());
        return res;
    }

    public static void loadFamiliarOptions() {
        loadItemOptions("FamiliarOption.img", getFamiliarOptions());
    }

    public static void loadItemOptions() {
        loadItemOptions("itemOption.img", getItemOptions());
    }

    private static Map<Integer, ItemOption> getItemOptions() {
        return itemOptions;
    }

    public static void loadItemOptions(String path, Map<Integer, ItemOption> options) {
        for (final MapleData d : itemData.getData(path)) {
            ItemOption itemOption = new ItemOption();
            itemOption.setOptionType(MapleDataTool.getInt("info/optionType", d, 0));
            itemOption.setReqLevel(MapleDataTool.getInt("info/reqLevel", d, 0));
            itemOption.setString(MapleDataTool.getString("info/string", d, ""));
            itemOption.setId(Integer.parseInt(d.getName()));
            for (MapleData levelInfo : d.getChildByPath("level")) {
                int level = Integer.parseInt(levelInfo.getName());
                for (MapleData levelAttr : levelInfo.getChildren()) {
                    String name = levelAttr.getName();
                    String stringValue = MapleDataTool.getString(levelAttr, "");
                    int value = 0;
                    if (Util.isNumber(stringValue)) {
                        value = Integer.parseInt(stringValue);
                    }
                    switch (name) {
                        case "incSTR":
                            itemOption.addStatValue(level, BaseStat.str, value);
                            break;
                        case "incDEX":
                            itemOption.addStatValue(level, BaseStat.dex, value);
                            break;
                        case "incINT":
                            itemOption.addStatValue(level, BaseStat.inte, value);
                            break;
                        case "incLUK":
                            itemOption.addStatValue(level, BaseStat.luk, value);
                            break;
                        case "incMHP":
                            itemOption.addStatValue(level, BaseStat.mhp, value);
                            break;
                        case "incMMP":
                            itemOption.addStatValue(level, BaseStat.mmp, value);
                            break;
                        case "incACC":
                            itemOption.addStatValue(level, BaseStat.acc, value);
                            break;
                        case "incEVA":
                            itemOption.addStatValue(level, BaseStat.eva, value);
                            break;
                        case "incSpeed":
                            itemOption.addStatValue(level, BaseStat.speed, value);
                            break;
                        case "incJump":
                            itemOption.addStatValue(level, BaseStat.jump, value);
                            break;
                        case "incPAD":
                            itemOption.addStatValue(level, BaseStat.pad, value);
                            break;
                        case "incMAD":
                            itemOption.addStatValue(level, BaseStat.mad, value);
                            break;
                        case "incPDD":
                            itemOption.addStatValue(level, BaseStat.pdd, value);
                            break;
                        case "incMDD":
                            itemOption.addStatValue(level, BaseStat.mdd, value);
                            break;
                        case "incCr":
                            itemOption.addStatValue(level, BaseStat.cr, value);
                            break;
                        case "incPADr":
                            itemOption.addStatValue(level, BaseStat.padR, value);
                            break;
                        case "incMADr":
                            itemOption.addStatValue(level, BaseStat.madR, value);
                            break;
                        case "incSTRr":
                            itemOption.addStatValue(level, BaseStat.strR, value);
                            break;
                        case "incDEXr":
                            itemOption.addStatValue(level, BaseStat.dexR, value);
                            break;
                        case "incINTr":
                            itemOption.addStatValue(level, BaseStat.intR, value);
                            break;
                        case "incLUKr":
                            itemOption.addStatValue(level, BaseStat.lukR, value);
                            break;
                        case "ignoreTargetDEF":
                            itemOption.addStatValue(level, BaseStat.ied, value);
                            break;
                        case "boss":
                            itemOption.addStatValue(level, BaseStat.bd, value);
                            break;
                        case "incDAMr":
                            itemOption.addStatValue(level, BaseStat.fd, value);
                            break;
                        case "incAllskill":
                            itemOption.addStatValue(level, BaseStat.incAllSkill, value);
                            break;
                        case "incMHPr":
                            itemOption.addStatValue(level, BaseStat.mhpR, value);
                            break;
                        case "incMMPr":
                            itemOption.addStatValue(level, BaseStat.mmpR, value);
                            break;
                        case "incACCr":
                            itemOption.addStatValue(level, BaseStat.accR, value);
                            break;
                        case "incEVAr":
                            itemOption.addStatValue(level, BaseStat.evaR, value);
                            break;
                        case "incPDDr":
                            itemOption.addStatValue(level, BaseStat.pddR, value);
                            break;
                        case "incMDDr":
                            itemOption.addStatValue(level, BaseStat.mddR, value);
                            break;
                        case "RecoveryHP":
                            itemOption.addStatValue(level, BaseStat.hpRecovery, value);
                            break;
                        case "RecoveryMP":
                            itemOption.addStatValue(level, BaseStat.mpRecovery, value);
                            break;
                        case "incMaxDamage":
                            itemOption.addStatValue(level, BaseStat.damageOver, value);
                            break;
                        case "incSTRlv":
                            itemOption.addStatValue(level, BaseStat.strLv, value / 10D);
                            break;
                        case "incDEXlv":
                            itemOption.addStatValue(level, BaseStat.dexLv, value / 10D);
                            break;
                        case "incINTlv":
                            itemOption.addStatValue(level, BaseStat.intLv, value / 10D);
                            break;
                        case "incLUKlv":
                            itemOption.addStatValue(level, BaseStat.lukLv, value / 10D);
                            break;
                        case "RecoveryUP":
                            itemOption.addStatValue(level, BaseStat.recoveryUp, value);
                            break;
                        case "incTerR":
                            itemOption.addStatValue(level, BaseStat.ter, value);
                            break;
                        case "incAsrR":
                            itemOption.addStatValue(level, BaseStat.asr, value);
                            break;
                        case "incEXPr":
                            itemOption.addStatValue(level, BaseStat.expR, value);
                            break;
                        case "mpconReduce":
                            itemOption.addStatValue(level, BaseStat.mpconReduce, value);
                            break;
                        case "reduceCooltime":
                            itemOption.addStatValue(level, BaseStat.reduceCooltime, value);
                            break;
                        case "incMesoProp":
                            itemOption.addStatValue(level, BaseStat.mesoR, value);
                            break;
                        case "incRewardProp":
                            itemOption.addStatValue(level, BaseStat.dropR, value);
                            break;
                        case "incCriticaldamageMin":
                            itemOption.addStatValue(level, BaseStat.minCd, value);
                            break;
                        case "incCriticaldamageMax":
                            itemOption.addStatValue(level, BaseStat.maxCd, value);
                            break;
                        case "incPADlv":
                            itemOption.addStatValue(level, BaseStat.padLv, value / 10D);
                            break;
                        case "incMADlv":
                            itemOption.addStatValue(level, BaseStat.madLv, value / 10D);
                            break;
                        case "incMHPlv":
                            itemOption.addStatValue(level, BaseStat.mhpLv, value / 10D);
                            break;
                        case "incMMPlv":
                            itemOption.addStatValue(level, BaseStat.mmpLv, value / 10D);
                            break;
                        case "prop":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.prop, value);
                            break;
                        case "face":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.face, value);
                            break;
                        case "time":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.time, value);
                            break;
                        case "HP":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.hpRecoveryOnHit, value);
                            break;
                        case "MP":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.mpRecoveryOnHit, value);
                            break;
                        case "attackType":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.attackType, value);
                            break;
                        case "level":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.level, value);
                            break;
                        case "ignoreDAM":
                            itemOption.addMiscValue(level, ItemOption.ItemOptionType.ignoreDam, value);
                            break;
                        default:
                            log.warn("未知的潜能属性:" + name + " ID:" + itemOption.getId());
                            break;
                    }
                }
            }
            options.put(itemOption.getId(), itemOption);
        }

    }

    private static Map<Integer, ItemOption> getFamiliarOptions() {
        return familiarOptions;
    }
}
