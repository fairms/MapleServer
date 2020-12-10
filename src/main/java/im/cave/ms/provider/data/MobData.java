package im.cave.ms.provider.data;

import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.DropInfo;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobStat;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 11/22 0:06
 */
public class MobData {
    private static final MapleDataProvider mobData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Mob.wz"));
    private static final MapleDataProvider mob001Data = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Mob001.wz"));
    private static final MapleDataProvider mob2Data = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Mob2.wz"));

    private static final Map<Integer, Mob> mobs = new HashMap<>();

    public static Mob getMob(int mobId) {
        if (!mobs.containsKey(mobId)) {
            return getMobFromWz(mobId);
        }
        return mobs.get(mobId);
    }

    public static Mob getMobFromWz(int mobId) {
        Mob mob = new Mob(mobId);
        String imgName = StringUtil.getLeftPaddedStr(String.valueOf(mobId), '0', 7) + ".img";
        MapleData data;
        data = mobData.getData(imgName);
        if (data == null) {
            data = mob2Data.getData(imgName);
            if (data == null) {
                data = mob001Data.getData(imgName);
                if (data == null) {
                    return null;
                }
            }
        }
        MapleData info = data.getChildByPath("info");
        if (info != null) {
            MobStat stat = mob.getForcedMobStat();
            for (MapleData attr : info.getChildren()) {
                String name = attr.getName();
                String value = MapleDataTool.getString(attr);
                switch (name) {
                    case "level":
                    case "Level":
                        stat.setLevel(Integer.parseInt(value));
                        break;
                    case "firstAttack":
                    case "firstattack":
                        mob.setFirstAttack((int) Double.parseDouble(value));
                        break;
                    case "bodyAttack":
                    case "bodyattack": // ...
//                        Option bodyOpt = new Option();
//                        bodyOpt.nOption = Integer.parseInt(value);
//                        mts.addStatOptions(BodyAttack, bodyOpt);
                        break;
                    case "maxHP":
                    case "finalmaxHP":
                        if (StringUtil.isNumber(value)) {
                            stat.setMaxHP(Long.parseLong(value));
                        } else {
                            stat.setMaxHP(1337);
                        }
                        break;
                    case "maxMP":
                        stat.setMaxMP(Integer.parseInt(value));
                        break;
                    case "PADamage":
                        stat.setPad(Integer.parseInt(value));
                        break;
                    case "PDDamage":
//                            mts.addStatOptions(PDR, "nPDR", Integer.parseInt(value));
                        break;
                    case "PDRate":
                        stat.setPdr(Integer.parseInt(value));
                        break;
                    case "MADamage":
                        stat.setMad(Integer.parseInt(value));
                        break;
                    case "MDDamage":
//                            mts.addStatOptions(PDR, "nMDR", Integer.parseInt(value));
                        break;
                    case "MDRate":
                        stat.setMdr(Integer.parseInt(value));
                        break;
                    case "acc":
                        stat.setAcc(Integer.parseInt(value));
                        break;
                    case "eva":
                        stat.setEva(Integer.parseInt(value));
                        break;
                    case "pushed":
                        stat.setPushed(Long.parseLong(value));
                        break;
                    case "exp":
                        stat.setExp(Integer.parseInt(value));
                        break;
                    case "summonType":
                        mob.setSummonType(Integer.parseInt(value));
                        break;
                    case "category":
                        mob.setCategory(Integer.parseInt(value));
                        break;
                    case "mobType":
                        mob.setMobType(value);
                        break;
                    case "link":
                        mob.setLink(Integer.parseInt(value));
                        break;
                    case "speed":
                    case "Speed":
                        stat.setSpeed(Integer.parseInt(value));
                        break;
                    case "fs":
                        mob.setFs(Double.parseDouble(value));
                        break;
                    case "elemAttr":
                        mob.setElemAttr(value);
                        break;
                    case "hpTagColor":
                        mob.setHpTagColor(Integer.parseInt(value));
                        break;
                    case "hpTagBgcolor":
                        mob.setHpTagBgcolor(Integer.parseInt(value));
                        break;
                    case "HPgaugeHide":
                        mob.setHPgaugeHide(Integer.parseInt(value) == 1);
                        break;
                    case "boss":
                        mob.setBoss(Integer.parseInt(value) == 1);
                        break;
                    case "undead":
                    case "Undead":
                        mob.setUndead(Integer.parseInt(value) == 1);
                        break;
                    case "noregen":
                        mob.setNoRegen(Integer.parseInt(value) == 1);
                        break;
                    case "invincible":
                    case "invincibe": // neckson pls
                        mob.setInvincible(Integer.parseInt(value) == 1);
                        break;
                    case "hideName":
                    case "hidename":
                        mob.setHideName(Integer.parseInt(value) == 1);
                        break;
                    case "hideHP":
                        mob.setHideHP(Integer.parseInt(value) == 1);
                        break;
                    case "changeableMob":
                        mob.setChangeable(Integer.parseInt(value) == 1);
                        break;
                    case "noFlip":
                        mob.setNoFlip(Integer.parseInt(value) == 1);
                        break;
                    case "tower":
                        mob.setTower(Integer.parseInt(value) == 1);
                        break;
                    case "partyBonusMob":
                        mob.setPartyBonusMob(Integer.parseInt(value) == 1);
                        break;
                    case "useReaction":
                        mob.setUseReaction(Integer.parseInt(value) == 1);
                        break;
                    case "publicReward":
                        mob.setPublicReward(Integer.parseInt(value) == 1);
                        break;
                    case "minion":
                        mob.setMinion(Integer.parseInt(value) == 1);
                        break;
                    case "forward":
                        mob.setForward(Integer.parseInt(value) == 1);
                        break;
                    case "isRemoteRange":
                        mob.setRemoteRange(Integer.parseInt(value) == 1);
                        break;
                    case "ignoreFieldOut":
                        mob.setIgnoreFieldOut(Integer.parseInt(value) == 1);
                        break;
                    case "ignoreMoveImpact":
                        mob.setIgnoreMoveImpact(Integer.parseInt(value) == 1);
                        break;
                    case "skeleton":
                        mob.setSkeleton(Integer.parseInt(value) == 1);
                        break;
                    case "hideUserDamage":
                        mob.setHideUserDamage(Integer.parseInt(value) == 1);
                        break;
                    case "individualReward":
                        mob.setIndividualReward(Integer.parseInt(value) == 1);
                        break;
                    case "notConsideredFieldSet":
                        mob.setNotConsideredFieldSet(Integer.parseInt(value) == 1);
                        break;
                    case "noDoom":
                        mob.setNoDoom(Integer.parseInt(value) == 1);
                        break;
                    case "useCreateScript":
                        mob.setUseCreateScript(Integer.parseInt(value) == 1);
                        break;
                    case "blockUserMove":
                        mob.setBlockUserMove(Integer.parseInt(value) == 1);
                        break;
                    case "knockback":
                        mob.setKnockback(Integer.parseInt(value) == 1);
                        break;
                    case "removeQuest":
                        mob.setRemoveQuest(Integer.parseInt(value) == 1);
                        break;
                    case "onFieldSetSummon":
                        mob.setOnFieldSetSummon(Integer.parseInt(value) == 1);
                        break;
                    case "userControll":
                        mob.setUserControll(Integer.parseInt(value) == 1);
                        break;
                    case "noDebuff":
                        mob.setNoDebuff(Integer.parseInt(value) == 1);
                        break;
                    case "targetFromSvr":
                        mob.setTargetFromSvr(Integer.parseInt(value) == 1);
                        break;
                    case "changeableMob_Type":
                        mob.setChangeableMobType(value);
                        break;
                    case "rareItemDropLevel":
                        mob.setRareItemDropLevel(Integer.parseInt(value));
                        break;
                    case "hpRecovery":
                        mob.setHpRecovery(Integer.parseInt(value));
                        break;
                    case "mpRecovery":
                        mob.setMpRecovery(Integer.parseInt(value));
                        break;
                    case "mbookID":
                        mob.setMbookID(Integer.parseInt(value));
                        break;
                    case "chaseSpeed":
                        mob.setChaseSpeed(Integer.parseInt(value));
                        break;
                    case "explosiveReward":
                        mob.setExplosiveReward(Integer.parseInt(value));
                        break;
                    case "charismaEXP":
                        mob.setCharismaEXP(Integer.parseInt(value));
                        break;
                    case "flyspeed":
                    case "flySpeed":
                    case "FlySpeed":
                        mob.setFlySpeed(Integer.parseInt(value));
                        break;
                    case "wp":
                        mob.setWp(Integer.parseInt(value));
                        break;
                    case "summonEffect":
                        mob.setSummonEffect(Integer.parseInt(value));
                        break;
                    case "fixedDamage":
                        mob.setFixedDamage(Integer.parseInt(value));
                        break;
                    case "removeAfter":
                        mob.setRemoveAfter(Integer.parseInt(value));
                        break;
                    case "bodyDisease":
                        mob.setBodyDisease(Integer.parseInt(value));
                        break;
                    case "bodyDiseaseLevel":
                        mob.setBodyDiseaseLevel(Integer.parseInt(value));
                        break;
                    case "point":
                        mob.setPoint(Integer.parseInt(value));
                        break;
                    case "partyBonusR":
                        mob.setPartyBonusR(Integer.parseInt(value));
                        break;
                    case "PassiveDisease":
                        mob.setPassiveDisease(Integer.parseInt(value));
                        break;
                    case "coolDamage":
                        mob.setCoolDamage(Integer.parseInt(value));
                        break;
                    case "coolDamageProb":
                        mob.setCoolDamageProb(Integer.parseInt(value));
                        break;
                    case "damageRecordQuest":
                        mob.setDamageRecordQuest(Integer.parseInt(value));
                        break;
                    case "sealedCooltime":
                        mob.setSealedCooltime(Integer.parseInt(value));
                        break;
                    case "willEXP":
                        mob.setWillEXP(Integer.parseInt(value));
                        break;
                    case "fixedMoveDir":
                        mob.setFixedMoveDir(value);
                        break;
                    case "PImmune":
//                        Option immOpt = new Option();
//                        immOpt.nOption = Integer.parseInt(value);
//                        mts.addStatOptions(PImmune, immOpt);
                        break;
                    case "patrol":
                        mob.setPatrolMob(true);
                        for (MapleData patrol : attr.getChildren()) {
                            String patrolName = patrol.getName();
                            String patrolValue = MapleDataTool.getString(patrol);
                            switch (patrolName) {
                                case "range":
                                    mob.setRange(Integer.parseInt(patrolValue));
                                    break;
                                case "detectX":
                                    mob.setDetectX(Integer.parseInt(patrolValue));
                                    break;
                                case "senseX":
                                    mob.setSenseX(Integer.parseInt(patrolValue));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case "ban":
                        mob.setBanMap(true);
                        for (MapleData banData : attr.getChildren()) {
                            String banDataName = banData.getName();
                            String banDataValue = MapleDataTool.getString(banData);
                            switch (banDataName) {
                                case "banType":
                                    mob.setBanType(Integer.parseInt(banDataValue));
                                    break;
                                case "banMsgType":
                                    mob.setBanMsgType(Integer.parseInt(banDataValue));
                                    break;
                                case "banMsg":
                                    mob.setBanMsg(banDataValue);
                                    break;
                                case "banMap":
                                    for (MapleData banMaps : banData.getChildren()) {
                                        int banFieldID = 0;
                                        String banPortal = "";
                                        for (MapleData banMap : banMaps.getChildren()) {
                                            String banMapName = banMap.getName();
                                            String banMapValue = MapleDataTool.getString(banMap);
                                            switch (banMapName) {
                                                case "field":
                                                    banFieldID = Integer.parseInt(banMapValue);
                                                    break;
                                                case "portal":
                                                    banPortal = banMapValue;
                                                    break;
                                                default:
                                                    break;
                                            }
                                            if (banFieldID != 0) {
                                                mob.addBanMap(banFieldID, banPortal);
                                            }

                                        }
                                    }
                                    break;
                            }
                        }

                        break;
                    case "revive":
                        for (MapleData revive : attr.getChildren()) {
                            mob.addRevive(MapleDataTool.getInt(revive));
                        }
                        break;
                    case "skill":
                    case "attack":
//                        boolean attack = "attack".equalsIgnoreCase(name);
//
//                        for (Node skillIDNode : XMLApi.getAllChildren(n)) {
//                            if (!Util.isNumber(XMLApi.getNamedAttribute(skillIDNode, "name"))) {
//                                continue;
//                            }
//                            MobSkill mobSkill = new MobSkill();
//                            mobSkill.setSkillSN(Integer.parseInt(XMLApi.getNamedAttribute(skillIDNode, "name")));
//                            for (Node skillInfoNode : XMLApi.getAllChildren(skillIDNode)) {
//                                String skillNodeName = XMLApi.getNamedAttribute(skillInfoNode, "name");
//                                String skillNodeValue = XMLApi.getNamedAttribute(skillInfoNode, "value");
//                                switch (skillNodeName) {
//                                    case "skill":
//                                        mobSkill.setSkillID(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "action":
//                                        mobSkill.setAction(Byte.parseByte(skillNodeValue));
//                                        break;
//                                    case "level":
//                                        mobSkill.setLevel(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "effectAfter":
//                                        if (!skillNodeValue.equals("")) {
//                                            mobSkill.setEffectAfter(Integer.parseInt(skillNodeValue));
//                                        }
//                                        break;
//                                    case "skillAfter":
//                                        mobSkill.setSkillAfter(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "priority":
//                                        mobSkill.setPriority(Byte.parseByte(skillNodeValue));
//                                        break;
//                                    case "onlyFsm":
//                                        mobSkill.setOnlyFsm(Integer.parseInt(skillNodeValue) != 0);
//                                        break;
//                                    case "onlyOtherSkill":
//                                        mobSkill.setOnlyOtherSkill(Integer.parseInt(skillNodeValue) != 0);
//                                        break;
//                                    case "doFirst":
//                                        mobSkill.setDoFirst(Integer.parseInt(skillNodeValue) != 0);
//                                        break;
//                                    case "afterDead":
//                                        mobSkill.setAfterDead(Integer.parseInt(skillNodeValue) != 0);
//                                        break;
//                                    case "skillForbid":
//                                        mobSkill.setSkillForbid(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "afterAttack":
//                                        mobSkill.setAfterAttack(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "afterAttackCount":
//                                        mobSkill.setAfterAttackCount(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "afterDelay":
//                                        mobSkill.setAfterDelay(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "fixDamR":
//                                        mobSkill.setFixDamR(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "preSkillIndex":
//                                        mobSkill.setPreSkillIndex(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "preSkillCount":
//                                        mobSkill.setPreSkillCount(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "castTime":
//                                        mobSkill.setCastTime(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "cooltime":
//                                        mobSkill.setCoolTime(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "delay":
//                                        mobSkill.setDelay(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "useLimit":
//                                        mobSkill.setUseLimit(Integer.parseInt(skillNodeValue));
//                                        break;
//                                    case "info":
//                                        mobSkill.setInfo(skillNodeValue);
//                                        break;
//                                    case "text":
//                                        mobSkill.setText(skillNodeValue);
//                                        break;
//                                    case "speak":
//                                        mobSkill.setSpeak(skillNodeValue);
//                                        break;
//                                    default:
//                                        if (LOG_UNKS) {
//                                            log.warn(String.format("Unknown skill node %s with value %s", skillNodeName, skillNodeValue));
//                                        }
//                                }
//                            }
//                            if (attack) {
//                                mob.addAttack(mobSkill);
//                            } else {
//                                mob.addSkill(mobSkill);
//                            }
//                        }
                        break;
                    case "selfDestruction":
                        // TODO Maybe more info?
                        mob.setSelfDestruction(true);
                        break;
                    case "escort":
                        mob.setEscortMob(Integer.parseInt(value) != 0);
                        break;
                    case "speak":
                    case "thumbnail":
                    case "default":
                    case "defaultHP":
                    case "defaultMP":
                    case "passive":
                    case "firstAttackRange":
                    case "nonLevelCheckEVA":
                    case "nonLevelCheckACC":
                    case "changeImg":
                    case "showNotRemoteDam":
                    case "buff":
                    case "damagedBySelectedMob":
                    case "damagedByMob":
                    case "getCP":
                    case "loseItem":
                    case "0":
                    case "onlyNormalAttack":
                    case "notConsiderFieldSet":
                    case "overSpeed":
                    case "ignoreMovable":
                    case "jsonLoad":
                    case "fixedBodyAttackDamageR":
                    case "adjustLayerZ":
                    case "damagedBySelectedSkill":
                    case "option_damagedByMob":
                    case "bodyKnockBack":
                    case "straightMoveDir":
                    case "onlyHittedByCommonAttack":
                    case "invincibleAttack":
                    case "noChase":
                    case "notAttack":
                    case "alwaysInAffectedRect":
                    case "firstShowHPTag":
                    case "pointFPSMode":
                    case "11":
                    case "prevLinkMob":
                    case "option_skeleton":
                    case "lifePoint":
                    case "defenseMob":
                    case "forceChaseEscort":
                    case "damageModification":
                    case "randomFlyingBoss":
                    case "randomFlyingMob":
                    case "stalking":
                    case "minimap":
                    case "removeOnMiss":
                    case "fieldEffect":
                    case "onceReward":
                    case "onMobDeadQR":
                    case "peddlerMob":
                    case "peddlerDamR":
                    case "rewardSprinkle":
                    case "rewardSprinkleCount":
                    case "rewardSprinkleSpeed":
                    case "ignorMoveImpact":
                    case "dropItemPeriod":
                    case "hideMove":
                    case "atom":
                    case "smartPhase":
                    case "trans":
                    case "chaseEffect":
                    case "dualGauge":
                    case "noReturnByDead":
                    case "AngerGauge":
                    case "ChargeCount":
                    case "upperMostLayer":
                    case "cannotEvade":
                    case "phase":
                    case "doNotRemove":
                    case "healOnDestroy":
                    case "debuffobj":
                    case "obtacle":
                    case "mobZone":
                    case "weapon":
                    case "forcedFlip":
                    case "buffStack":
                    case "001":
                    case "002":
                    case "003":
                    case "004":
                    case "005":
                    case "006":
                    case "007":
                    case "008":
                    case "009":
                    case "010":
                    case "011":
                    case "onlySelectedSkill":
                    case "finalAdjustedDamageRate":
                    case "battlePvP":
                    case "mobJobCategory":
                    case "considerUserCount":
                    case "randomMob":
                    case "dieHeight":
                    case "dieHeightTime":
                    case "notChase":
                    case "fixedStat":
                    case "allyMob":
                    case "linkMob":
                    case "skelAniMixRate":
                    case "mobZoneObjID":
                    case "mobZoneObjType":
                    case "holdRange":
                    case "targetChaseTime":
                    case "copyCharacter":
                    case "disable":
                    case "underObject":
                    case "1582":
                    case "peddlerCount":
                    case "bodyAttackInfo":
                    case "mobZoneType":
                    case "largeDamageRecord":
                    case "considerUserCounter":
                    case "damageByObtacleAtom":
                    case "info":
                    case "cantPassByTeleport":
                    case "250000":
                    case "forward_direction":
                        break;
                    default:
                        break;
                }

            }
        }

        mob.setDrops(DropData.getDrops(mobId));
        mob.getDrops().add(new DropInfo(GameConstants.MAX_DROP_CHANCE,
                GameConstants.MIN_MONEY_MULT * mob.getForcedMobStat().getLevel(),
                GameConstants.MAX_MONEY_MULT * mob.getForcedMobStat().getLevel()
        ));
        for (DropInfo di : mob.getDrops()) {
            di.generateNextDrop();
        }
        mobs.put(mobId, mob);
        return mob;
    }

    public static Mob getMobDeepCopyById(int templateId) {
        Mob from = getMob(templateId);
        Mob copy = null;
        if (from != null) {
            copy = from.deepCopy();
        }
        return copy;

    }
}
