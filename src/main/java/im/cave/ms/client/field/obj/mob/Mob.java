package im.cave.ms.client.field.obj.mob;

import im.cave.ms.client.character.ExpIncreaseInfo;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.provider.info.DropInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.configs.Config;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.MobPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.enums.RemoveMobType;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Tuple;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static im.cave.ms.enums.RemoveMobType.ANIMATION_DEATH;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.life
 * @date 11/28 17:15
 */
@Getter
@Setter
public class Mob extends MapleMapObj {
    private boolean sealedInsteadDead, patrolMob;
    private int option, effectItemID, range, detectX, senseX, phase, curZoneDataType;
    private int refImgMobID, lifeReleaseOwnerAID, afterAttack, currentAction, scale, eliteGrade = -1, eliteType, targetUserIdFromServer;
    private long hp;
    private long mp;
    private byte calcDamageIndex, moveAction = 5, appearType = -2, teamForMCarnival = -1;
    private Position prevPos;
    private Foothold curFoothold;
    private Foothold homeFoothold;
    private String lifeReleaseOwnerName = "", lifeReleaseMobName = "";
    //    private ShootingMoveStat shootingMoveStat;
    private ForcedMobStat forcedMobStat;
    private MobTemporaryStat temporaryStat;
    private int firstAttack;
    private int summonType;
    private int category;
    private String mobType = "";
    private int link;
    private double fs;
    private String elemAttr = "";
    private int hpTagColor;
    private int hpTagBgColor;
    private boolean HPGaugeHide;
    private int rareItemDropLevel;
    private boolean boss;
    private int hpRecovery;
    private int mpRecovery;
    private boolean undead;
    private int mBookID;
    private boolean noRegen;
    private int chaseSpeed;
    private int explosiveReward;
    private int flySpeed;
    private boolean invincible;
    private boolean hideName;
    private boolean hideHP;
    private String changeableMobType = "";
    private boolean changeable;
    private boolean noFlip;
    private boolean tower;
    private boolean partyBonusMob;
    private int wp;
    private boolean useReaction;
    private boolean publicReward;
    private boolean minion;
    private boolean forward;
    private boolean isRemoteRange;
    private boolean ignoreFieldOut;
    private boolean ignoreMoveImpact;
    private int summonEffect;
    private boolean skeleton;
    private boolean hideUserDamage;
    private int fixedDamage;
    private boolean individualReward;
    private int removeAfter;
    private boolean notConsideredFieldSet;
    private String fixedMoveDir = "";
    private boolean noDoom;
    private boolean useCreateScript;
    private boolean knockBack;
    private boolean blockUserMove;
    private int bodyDisease;
    private int bodyDiseaseLevel;
    private int point;
    private int partyBonusR;
    private boolean removeQuest;
    private int passiveDisease;
    private int coolDamageProb;
    private int coolDamage;
    private int damageRecordQuest;
    private int sealedCooltime;
    private int willEXP;
    private boolean onFieldSetSummon;
    private boolean userControll;
    private boolean noDebuff;
    private boolean targetFromSvr;
    private int charismaEXP;
    private boolean isSplit;
    private int splitLink;
    private Map<MapleCharacter, Long> injuryStatistics = new HashMap<>();
    private Set<DropInfo> drops = new HashSet<>();
    private List<MobSkill> skills = new ArrayList<>();
    private List<MobSkill> attacks = new ArrayList<>();
    private Set<Integer> quests = new HashSet<>();
    private Set<Integer> revives = new HashSet<>();
    private Map<Integer, Long> skillCooldowns = new HashMap<>();
    private long nextPossibleSkillTime = 0;
    private List<Tuple<Integer, Integer>> eliteSkills = new ArrayList<>();
    private boolean selfDestruction;
    private List<MobSkill> skillDelays = new CopyOnWriteArrayList<>();
    private boolean inAttack;
    private boolean isBanMap;
    private int banType = 1;// default
    private int banMsgType = 1;// default
    private String banMsg = "";
    private List<Tuple<Integer, String>> banMaps = new ArrayList<>();// field, portal name
    private boolean isEscortMob = false;
    private List<EscortDest> escortDest = new ArrayList<>();
    private int currentDestIndex = 0;
    private int escortStopDuration = 0;

    public void addBanMap(int banFieldID, String banPortal) {
        banMaps.add(new Tuple<>(banFieldID, banPortal));
    }

    public void addRevive(int revive) {
        revives.add(revive);
    }

    public Mob(int id) {
        super(id);
        forcedMobStat = new ForcedMobStat();
        temporaryStat = new MobTemporaryStat(this);
        scale = 100;
        calcDamageIndex = 1;
    }

    public Mob() {
        forcedMobStat = new ForcedMobStat();
    }


    public long getHp() {
        return hp;
    }

    @Override
    public void notifyControllerChange(MapleCharacter controller) {
        for (MapleCharacter chr : getMap().getCharInRect(getVisibleRect())) {
            if (chr == getMap().getObjControllers().get(this)) {
                continue;
            }
            chr.announce(MobPacket.changeMobController(this, false, controller == chr));
        }
    }


    @Override
    public void sendLeavePacket(MapleCharacter chr) {
        chr.announce(MobPacket.removeMob(getObjectId(), RemoveMobType.STAY));
    }

    @Override
    public void sendSpawnPacket(MapleCharacter chr) {
        chr.announce(MobPacket.spawnMob(this, false));
    }

    public Mob deepCopy() {
        Mob copy = new Mob(getTemplateId());
        // start life
        copy.setObjectId(getObjectId());
        copy.setLifeType(getLifeType());
        copy.setTemplateId(getTemplateId());
        copy.setX(getX());
        copy.setY(getY());
        copy.setMobTime(getMobTime());
        copy.setFlip(isFlip());
        copy.setHide(isHide());
        copy.setFh(getFh());
        copy.setCy(getCy());
        copy.setRx0(getRx0());
        copy.setRx1(getRx1());
        copy.setLimitedName(getLimitedName());
        copy.setUseDay(isUseDay());
        copy.setUseNight(isUseNight());
        copy.setHold(isHold());
        copy.setNoFoothold(isNoFoothold());
        copy.setDummy(isDummy());
        copy.setSpine(isSpine());
        copy.setMobTimeOnDie(isMobTimeOnDie());
        copy.setRegenStart(getRegenStart());
        copy.setMobAliveReq(getMobAliveReq());
        // end life
        copy.setSealedInsteadDead(isSealedInsteadDead());
        copy.setOption(getOption());
        copy.setEffectItemID(getEffectItemID());
        copy.setPatrolMob(isPatrolMob());
        copy.setRange(getRange());
        copy.setDetectX(getDetectX());
        copy.setSenseX(getSenseX());
        copy.setPhase(getPhase());
        copy.setCurZoneDataType(getCurZoneDataType());
        copy.setRefImgMobID(getRefImgMobID());
        copy.setLifeReleaseOwnerAID(getLifeReleaseOwnerAID());
        copy.setAfterAttack(getAfterAttack());
        copy.setCurrentAction(getCurrentAction());
        copy.setScale(getScale());
        copy.setEliteGrade(getEliteGrade());
        copy.setEliteType(getEliteType());
        copy.setTargetUserIdFromServer(getTargetUserIdFromServer());
        copy.setHp(getMaxHp());
        copy.setMaxHp(getMaxHp());
        copy.setLevel(forcedMobStat.getLevel());
        copy.setCalcDamageIndex(getCalcDamageIndex());
        copy.setMoveAction(getMoveAction());
        copy.setAppearType(getAppearType());
        copy.setTeamForMCarnival(getTeamForMCarnival());
        if (getPrevPos() != null) {
            copy.setPrevPos(getPrevPos().deepCopy());
        }
        if (getCurFoothold() != null) {
            copy.setCurFoothold(getCurFoothold().deepCopy());
        }
        if (getHomeFoothold() != null) {
            copy.setHomeFoothold(getHomeFoothold().deepCopy());
        }
        copy.setLifeReleaseOwnerName(getLifeReleaseOwnerName());
        copy.setLifeReleaseMobName(getLifeReleaseMobName());
//        copy.setShootingMoveStat(null);
        if (getForcedMobStat() != null) {
            copy.setForcedMobStat(getForcedMobStat().deepCopy());
        }
//        if (getTemporaryStat() != null) {
//            copy.setTemporaryStat(getTemporaryStat().deepCopy());
//        }
        copy.setFirstAttack(getFirstAttack());
        copy.setSummonType(getSummonType());
        copy.setCategory(getCategory());
        copy.setMobType(getMobType());
        copy.setLink(getLink());
        copy.setFs(getFs());
        copy.setElemAttr(getElemAttr());
        copy.setHpTagColor(getHpTagColor());
        copy.setHpTagBgColor(getHpTagBgColor());
        copy.setHPGaugeHide(isHPGaugeHide());
        copy.setRareItemDropLevel(getRareItemDropLevel());
        copy.setBoss(isBoss());
        copy.setHpRecovery(getHpRecovery());
        copy.setMpRecovery(getMpRecovery());
        copy.setUndead(isUndead());
        copy.setMBookID(getMBookID());
        copy.setNoRegen(isNoRegen());
        copy.setChaseSpeed(getChaseSpeed());
        copy.setExplosiveReward(getExplosiveReward());
        copy.setFlySpeed(getFlySpeed());
        copy.setInvincible(isInvincible());
        copy.setHideName(isHideName());
        copy.setHideHP(isHideHP());
        copy.setChangeableMobType(getChangeableMobType());
        copy.setChangeable(isChangeable());
        copy.setNoFlip(isNoFlip());
        copy.setTower(isTower());
        copy.setPartyBonusMob(isPartyBonusMob());
        copy.setWp(getWp());
        copy.setUseReaction(isUseReaction());
        copy.setPublicReward(isPublicReward());
        copy.setMinion(isMinion());
        copy.setForward(isForward());
        copy.setRemoteRange(isRemoteRange());
        copy.setIgnoreFieldOut(isIgnoreFieldOut());
        copy.setIgnoreMoveImpact(isIgnoreMoveImpact());
        copy.setSummonEffect(getSummonEffect());
        copy.setSkeleton(isSkeleton());
        copy.setHideUserDamage(isHideUserDamage());
        copy.setFixedDamage(getFixedDamage());
        copy.setIndividualReward(isIndividualReward());
        copy.setRemoveAfter(getRemoveAfter());
        copy.setNotConsideredFieldSet(isNotConsideredFieldSet());
        copy.setFixedMoveDir(getFixedMoveDir());
        copy.setNoDoom(isNoDoom());
        copy.setUseCreateScript(isUseCreateScript());
        copy.setKnockBack(isKnockBack());
        copy.setBlockUserMove(isBlockUserMove());
        copy.setBodyDisease(getBodyDisease());
        copy.setBodyDiseaseLevel(getBodyDiseaseLevel());
        copy.setPoint(getPoint());
        copy.setPartyBonusR(getPartyBonusR());
        copy.setRemoveQuest(isRemoveQuest());
        copy.setPassiveDisease(getPassiveDisease());
        copy.setCoolDamageProb(getCoolDamageProb());
        copy.setCoolDamage(getCoolDamage());
        copy.setDamageRecordQuest(getDamageRecordQuest());
        copy.setSealedCooltime(getSealedCooltime());
        copy.setWillEXP(getWillEXP());
        copy.setOnFieldSetSummon(isOnFieldSetSummon());
        copy.setUserControll(isUserControll());
        copy.setNoDebuff(isNoDebuff());
        copy.setTargetFromSvr(isTargetFromSvr());
        copy.setCharismaEXP(getCharismaEXP());
        copy.setMp(getMaxMp());
        copy.setMaxMp(getMaxMp());
        copy.setDrops(getDrops());
        copy.setBanMap(isBanMap());
        copy.setBanType(getBanType());
        copy.setBanMsgType(getBanMsgType());
        copy.setBanMsg(getBanMsg());
        copy.setBanMaps(getBanMaps());
        for (MobSkill ms : getSkills()) {
            copy.addSkill(ms);
        }
        for (MobSkill ms : getAttacks()) {
            copy.addAttack(ms);
        }
        for (int rev : getRevives()) {
            copy.addRevive(rev);
        }
        for (int i : getQuests()) {
            copy.addQuest(i);
        }
        copy.setEscortMob(isEscortMob());
        return copy;

    }

    public void addAttack(MobSkill mobSkill) {
        getAttacks().add(mobSkill);
    }

    public void addSkill(MobSkill skill) {
        getSkills().add(skill);
    }

    public void addQuest(int questID) {
        getQuests().add(questID);
    }


    private void setLevel(int level) {
        forcedMobStat.setLevel(level);
    }

    private void setMaxHp(long maxHp) {
        forcedMobStat.setMaxHP(maxHp);
    }

    public void setMaxMp(long maxMp) {
        forcedMobStat.setMaxMP(maxMp);
    }


    public long getMaxHp() {
        return forcedMobStat.getMaxHP();
    }

    public long getMaxMp() {
        return forcedMobStat.getMaxMP();
    }

    public void damage(MapleCharacter chr, long damage) {
        addDamage(chr, damage);
        long maxHp = getMaxHp();
        long oldHp = getHp();
        long newHp = oldHp - damage;
        setHp(newHp);
        if (oldHp > 0 && newHp <= 0) {
            die();
            if (isBoss() && getHpTagColor() != 0) {
                getMap().broadcastMessage(WorldPacket.fieldEffect(FieldEffect.mobHPTagFieldEffect(this)));
            }
        } else if (isBoss() && getHpTagColor() != 0) {
            getMap().broadcastMessage(WorldPacket.fieldEffect(FieldEffect.mobHPTagFieldEffect(this)));
        } else {
            double percentage = (double) newHp / maxHp;
            getMap().broadcastMessage(MobPacket.hpIndicator(getObjectId(), (byte) (percentage * 100)));
        }
    }

    private void addDamage(MapleCharacter chr, long damage) {
        long cur = 0;
        if (getInjuryStatistics().containsKey(chr)) {
            cur = getInjuryStatistics().get(chr);
        }
        cur += Math.min(damage, getHp());
        getInjuryStatistics().put(chr, cur);
    }

    private void die() {
        MapleMap map = getMap();
        map.broadcastMessage(MobPacket.removeMob(getObjectId(), ANIMATION_DEATH));
        map.removeObj(getObjectId(), false);
        distributeExp();
        dropDrops();
        for (MapleCharacter chr : getInjuryStatistics().keySet()) {
            chr.getQuestManager().handleMobKill(this);
        }
    }

    private void distributeExp() {
        long exp = getForcedMobStat().getExp();
        long totalDamage = getInjuryStatistics().values().stream().mapToLong(l -> l).sum();
        for (MapleCharacter chr : getInjuryStatistics().keySet()) {
            double damagePercent = getInjuryStatistics().get(chr) / (double) totalDamage;
            int mobExpRate = chr.getLevel() < 10 ? 1 : Config.worldConfig.getWorldInfo(chr.getWorldId()).exp_rate;
            long appliedExpPre = (long) (exp * damagePercent * mobExpRate);
            long appliedExpPost = appliedExpPre;
            ExpIncreaseInfo expIncreaseInfo = new ExpIncreaseInfo();

            //Burning map todo

            //+exp mob stat todo
            expIncreaseInfo.setLastHit(true);
            expIncreaseInfo.setIncEXP((int) appliedExpPre);
            chr.addExp(appliedExpPost, expIncreaseInfo);

        }
    }

    public MapleCharacter getMostDamageChar() {
        Tuple<MapleCharacter, Long> max = new Tuple<>(null, (long) -1);
        for (Map.Entry<MapleCharacter, Long> entry : getInjuryStatistics().entrySet()) {
            MapleCharacter chr = entry.getKey();
            long damage = entry.getValue();
            if (damage > max.getRight()) {
                max.setLeft(chr);
                max.setRight(damage);
            }
        }
        return max.getLeft();
    }

    private void dropDrops() {
        MapleCharacter chr = getMostDamageChar();
        int ownerId = 0;
        if (chr != null) {
            ownerId = chr.getId();
        }
        int fh = getFh();
        if (fh == 0) {
            Foothold foothold = getMap().getFootholdBelow(getPosition());
            if (foothold != null) {
                fh = foothold.getId();
            }
        }
        int totalMesoRate = 0;
        int totalDropRate = 0;

        getMap().drop(getDrops(), getMap().getFoothold(fh), getPosition(), ownerId, totalMesoRate, totalDropRate, false);
        if (chr != null) {
            TemporaryStatManager tsm = chr.getTemporaryStatManager();
            Option option = tsm.getOption(CharacterTemporaryStat.SoulMP);

        }
    }

    public void encodeInit(OutPacket out) {
        out.writeShort(getPosition().getX());
        out.writeShort(getPosition().getY());
        out.write(getMoveAction());
        out.writeShort(getFh());
        out.writeShort(getFh());
        out.writeShort(getAppearType());
        out.write(getTeamForMCarnival());
        out.writeLong(getMaxHp());
        out.writeInt(getEffectItemID());
        out.writeInt(getPhase());
        out.writeInt(getCurZoneDataType());
        out.writeInt(getRefImgMobID());
        out.writeInt(0);

        out.writeInt(-1);
        out.writeInt(0);
        out.writeInt(-1);
        out.writeInt(0);
        out.write(0);
        out.writeInt(getScale());
        out.writeInt(getEliteGrade()); //getEliteGrade
        if (getEliteGrade() >= 0) {
            // 03 00 00 00  技能数量
            // 86 00 00 00 00 00 00 00 技能1
            // 7D 00 00 00 00 00 00 00 技能2
            // 7A 00 00 00 00 00 00 00 技能3
            // 01 00 00 00 // 类型normal, 3 elite boss probably
        }
        out.writeZeroBytes(42);
    }

    public void addEscortDest(int destPosX, int destPosY, int attr) {
        addEscortDest(destPosX, destPosY, attr, 0, 0);
    }

    private void addEscortDest(int destPosX, int destPosY, int attr, int mass, int stopDuration) {
        escortDest.add(new EscortDest(destPosX, destPosY, attr, mass, stopDuration));
    }

    public void escortFullPath(int oldAttr) {
        getMap().broadcastMessage(MobPacket.escortFullPath(this, oldAttr, false));
    }
}
