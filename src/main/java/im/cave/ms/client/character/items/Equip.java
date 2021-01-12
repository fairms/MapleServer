package im.cave.ms.client.character.items;

import im.cave.ms.connection.db.InlinedIntArrayConverter;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.EnchantStat;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.ItemGrade;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character.items
 * @date 11/21 17:15
 */
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "itemId")
@Entity
@Table(name = "equip")
public class Equip extends Item {
    private int equipSkin;
    private String title = "";
    private short tuc, cuc;
    private short iStr, iDex, iInt, iLuk, iMaxHp, iMaxMp, iPad, iMad, iPDD, iMDD;
    private short iAcc, iEva, iCraft, iSpeed, iJump, attribute;
    private short levelUpType, level, exp, durability, iuc;
    private byte iReduceReq;
    private short specialAttribute, durabilityMax, iIncReq, growthEnchant;
    private short psEnchant, bdr, imdr;
    private boolean bossReward, superiorEqp;
    private short cuttable = -1;
    private short itemState = 256;
    private short damR, statR, hyperUpgrade, chuc;
    private short soulOptionId, soulSocketId, soulOption;
    private short rStr, rDex, rInt, rLuk, rLevel, rJob, rPop;
    private int specialGrade;
    private boolean fixedPotential, noPotential, tradeBlock, isOnly, notSale;
    private int attackSpeed, price, charmEXP, setItemId;
    private boolean expireOnLogout, exItem, equipTradeBlock;
    private String iSlot = "";
    private String vSlot = "";
    private int fixedGrade;
    private byte tradeAvailable = 0;
    private boolean showEffect = true;
    private long limitBreak;
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "flame")
    private FlameStats flameStats;
    @Transient
    private int dropStreak = 0;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Short> sockets = new ArrayList<>();
    @Transient
    private short iucMax = ItemConstants.MAX_HAMMER_SLOTS;
    @Transient
    private boolean hasIUCMax = false;
    @Transient
    private int effectItemId = 0;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> options = new ArrayList<>();
    @Transient
    private Map<EnchantStat, Integer> enchantStats = new HashMap<>();
    @Transient
    private List<ItemSkill> itemSkills = new ArrayList<>();
    @Transient
    private final short flameLevel = getFlameLevel();
    @Transient
    private final short flameLevelExtended = getFlameLevelExtended();


    public void addItemSkill(ItemSkill itemSkill) {
        itemSkills.add(itemSkill);
    }

    public int getEquipStatMask(int pos) {
        int mask = 0;
        for (EquipBaseStat ebs : EquipBaseStat.values()) {
            if (hasStat(ebs) && ebs.getPos() == pos) {
                int value = ebs.getVal();
                if (value != EquipBaseStat.iMDD.getVal() && value != EquipBaseStat.iACC.getVal() && value != EquipBaseStat.iEVA.getVal())
                    mask |= ebs.getVal();
            }
        }
        return mask;
    }

    public boolean hasStat(EquipBaseStat ebs) {
        return getBaseStat(ebs) != 0 || getBaseStatFlame(ebs) != 0 || getEnchantmentStat(ebs) != 0;
    }

    private int getEnchantmentStat(EquipBaseStat ebs) {
        switch (ebs) {
            case iStr:
                return getEnchantStat(EnchantStat.STR);
            case iDex:
                return getEnchantStat(EnchantStat.DEX);
            case iInt:
                return getEnchantStat(EnchantStat.INT);
            case iLuk:
                return getEnchantStat(EnchantStat.LUK);
            case iMaxHP:
                return getEnchantStat(EnchantStat.MHP);
            case iMaxMP:
                return getEnchantStat(EnchantStat.MMP);
            case iPAD:
                return getEnchantStat(EnchantStat.PAD);
            case iMAD:
                return getEnchantStat(EnchantStat.MAD);
            case iPDD:
                return getEnchantStat(EnchantStat.PDD);
            case iMDD:
                return getEnchantStat(EnchantStat.MDD);
            case iSpeed:
                return getEnchantStat(EnchantStat.SPEED);
            case iJump:
                return getEnchantStat(EnchantStat.JUMP);
            default:
                return 0;
        }
    }

    public int getEnchantStat(EnchantStat es) {
        return enchantStats.getOrDefault(es, 0);
    }

    public int getBaseStatFlame(EquipBaseStat ebs) {
        if (flameStats == null) {
            return 0;
        }
        switch (ebs) {
            case iStr:
                return flameStats.getFStr() * flameLevel +
                        (flameStats.getFStrDex() + flameStats.getFStrInt() + flameStats.getFStrLuk()) * flameLevelExtended;
            case iDex:
                return flameStats.getFDex() * flameLevel +
                        (flameStats.getFStrDex() + flameStats.getFDexInt() + flameStats.getFDexLuk()) * flameLevelExtended;
            case iInt:
                return flameStats.getFInt() * flameLevel +
                        (flameStats.getFStrInt() + flameStats.getFDexInt() + flameStats.getFIntLuk()) * flameLevelExtended;
            case iLuk:
                return flameStats.getFLuk() * flameLevel +
                        (flameStats.getFStrLuk() + flameStats.getFDexLuk() + flameStats.getFIntLuk()) * flameLevelExtended;
            case iMaxHP:
                return flameStats.getFHp() * ((getRLevel() + getIIncReq()) / 10) * 30;
            case iMaxMP:
                return flameStats.getFMp() * ((getRLevel() + getIIncReq()) / 10) * 30;
            case iPAD:
                return getATTBonus(flameStats.getFAtt());
            case iMAD:
                return getATTBonus(flameStats.getFMatt());
            case iPDD:
            case iMDD:
                return flameStats.getFDff() * flameLevelExtended;
            case iSpeed:
                return flameStats.getFSpeed();
            case iJump:
                return flameStats.getFJump();
            case statR:
                return flameStats.getFAllStat();
            case bdr:
                return flameStats.getFBoss() * 2;
            case damR:
                return flameStats.getFDamage();
            case iReduceReq:
                return flameStats.getFLevel() * 5;
            default:
                return 0;
        }
    }

    public double getBaseStat(BaseStat baseStat) {
        // TODO: Sockets
        double res = 0;
//        for (int i = 0; i < getOptions().size() - 1; i++) { // last one is anvil => skipped
//            int id = getOptions().get(i);
//            int level = (getRLevel() + getIIncReq()) / 10;
//            ItemOption io = ItemData.getItemOptionById(id);
//            if (io != null) {
//                Map<BaseStat, Double> valMap = io.getStatValuesByLevel(level);
//                res += valMap.getOrDefault(baseStat, 0D);
//            }
//        }
        switch (baseStat) {
            case str:
                res += getTotalStat(EquipBaseStat.iStr);
                break;
            case dex:
                res += getTotalStat(EquipBaseStat.iDex);
                break;
            case inte:
                res += getTotalStat(EquipBaseStat.iInt);
                break;
            case luk:
                res += getTotalStat(EquipBaseStat.iLuk);
                break;
            case pad:
                res += getTotalStat(EquipBaseStat.iPAD);
                break;
            case mad:
                res += getTotalStat(EquipBaseStat.iMAD);
                break;
            case pdd:
                res += getTotalStat(EquipBaseStat.iPDD);
                break;
            case mdd:
                res += getTotalStat(EquipBaseStat.iMDD);
                break;
            case mhp:
                res += getTotalStat(EquipBaseStat.iMaxHP);
                break;
            case mmp:
                res += getTotalStat(EquipBaseStat.iMaxMP);
                break;
            case fd:
                res += getTotalStat(EquipBaseStat.damR);
                break;
            case bd:
                res += getTotalStat(EquipBaseStat.bdr);
                break;
            case ied:
                res += getTotalStat(EquipBaseStat.imdr);
                break;
            case eva:
                res += getTotalStat(EquipBaseStat.iEVA);
                break;
            case acc:
                res += getTotalStat(EquipBaseStat.iACC);
                break;
            case speed:
                res += getTotalStat(EquipBaseStat.iSpeed);
                break;
            case jump:
                res += getTotalStat(EquipBaseStat.iJump);
                break;
            case booster:
                res += getAttackSpeed();
                break;
            case strR:
            case dexR:
            case intR:
            case lukR:
                res += getTotalStat(EquipBaseStat.statR);
                break;
        }
        return res;
    }


    public long getTotalStat(EquipBaseStat stat) {
        switch (stat) {
            case tuc:
                return getTuc();
            case cuc:
                return getCuc();
            case iStr:
                return getIStr() + getBaseStatFlame(EquipBaseStat.iStr) + getEnchantStat(EnchantStat.STR);
            case iDex:
                return getIDex() + getBaseStatFlame(EquipBaseStat.iDex) + getEnchantStat(EnchantStat.DEX);
            case iInt:
                return getIInt() + getBaseStatFlame(EquipBaseStat.iInt) + getEnchantStat(EnchantStat.INT);
            case iLuk:
                return getILuk() + getBaseStatFlame(EquipBaseStat.iLuk) + getEnchantStat(EnchantStat.LUK);
            case iMaxHP:
                return getIMaxHp() + getBaseStatFlame(EquipBaseStat.iMaxHP) + getEnchantStat(EnchantStat.MHP);
            case iMaxMP:
                return getIMaxMp() + getBaseStatFlame(EquipBaseStat.iMaxMP) + getEnchantStat(EnchantStat.MMP);
            case iPAD:
                return getIPad() + getBaseStatFlame(EquipBaseStat.iPAD) + getEnchantStat(EnchantStat.PAD);
            case iMAD:
                return getIMad() + getBaseStatFlame(EquipBaseStat.iMAD) + getEnchantStat(EnchantStat.MAD);
            case iPDD:
                return getIPDD() + getBaseStatFlame(EquipBaseStat.iPDD) + getEnchantStat(EnchantStat.PDD);
            case iMDD:
                return getIMDD() + getBaseStatFlame(EquipBaseStat.iMDD) + getEnchantStat(EnchantStat.MDD);
            case iACC:
                return getIAcc() + getEnchantStat(EnchantStat.ACC);
            case iEVA:
                return getIEva() + getEnchantStat(EnchantStat.EVA);
            case iCraft:
                return getICraft();
            case iSpeed:
                return getISpeed() + getBaseStatFlame(EquipBaseStat.iSpeed) + getEnchantStat(EnchantStat.SPEED);
            case iJump:
                return getIJump() + getBaseStatFlame(EquipBaseStat.iJump) + getEnchantStat(EnchantStat.JUMP);
            case attribute:
                return getAttribute();
            case levelUpType:
                return getLevelUpType();
            case level:
                return getLevel();
            case exp:
                return getExp();
            case durability:
                return getDurability();
            case iuc:
                return getIuc(); // hammer
            case iPvpDamage:
                break;
//                return getIPvpDamage();
            case iReduceReq:
                return (byte) (getIReduceReq() + getBaseStatFlame(EquipBaseStat.iReduceReq));
            case specialAttribute:
                return getSpecialAttribute();
            case durabilityMax:
                return getDurabilityMax();
            case iIncReq:
                return getIIncReq();
            case growthEnchant:
                return getGrowthEnchant();// ygg
            case psEnchant:
                return getPsEnchant(); // final strike
            case bdr:
                return getBdr() + getBaseStatFlame(EquipBaseStat.bdr); // bd
            case imdr:
                return getImdr(); // ied
            case damR:
                return getDamR() + getBaseStatFlame(EquipBaseStat.damR); // td
            case statR:
                return getStatR() + getBaseStatFlame(EquipBaseStat.statR); // as
            case cuttable:
                return getCuttable(); // sok
            case flame:
                return getFlame();
            case itemState:
                return getHyperUpgrade();
        }
        return 0;
    }


    public long getBaseStat(EquipBaseStat ebs) {
        switch (ebs) {
            case tuc:
                return getTuc();
            case cuc:
                return getCuc();
            case iStr:
                return getIStr();
            case iDex:
                return getIDex();
            case iInt:
                return getIInt();
            case iLuk:
                return getILuk();
            case iMaxHP:
                return getIMaxHp();
            case iMaxMP:
                return getIMaxMp();
            case iPAD:
                return getIPad();
            case iMAD:
                return getIMad();
            case iPDD:
                return getIPDD();
            case iMDD:
                return getIMDD();
            case iACC:
                return getIAcc();
            case iEVA:
                return getIEva();
            case iCraft:
                return getICraft();
            case iSpeed:
                return getISpeed();
            case iJump:
                return getIJump();
            case attribute:
                return getAttribute();
            case levelUpType:
                return getLevelUpType();
            case level:
                return getLevel();
            case exp:
                return getExp();
            case durability:
                return getDurability();
            case iuc:
                return getIuc();
            case iPvpDamage:
                return 0;
            case iReduceReq:
                return getIReduceReq();
            case specialAttribute:
                return getSpecialAttribute();
            case durabilityMax:
                return getDurabilityMax();
            case iIncReq:
                return getIIncReq();
            case growthEnchant:
                return getGrowthEnchant();
            case psEnchant:
                return getPsEnchant();
            case bdr:
                return getBdr();
            case imdr:
                return getImdr();
            case damR:
                return getDamR();
            case statR:
                return getStatR();
            case cuttable:
                return getCuttable();
            case flame:
                return getFlame();
            case itemState:
                return getItemState();
            default:
                return 0;
        }
    }

    public short getGrade() {
        ItemGrade bonusGrade = ItemGrade.getGradeByVal(getBonusGrade());
        if (bonusGrade.isHidden()) {
            return ItemGrade.getHiddenBonusGradeByBaseGrade(ItemGrade.getGradeByVal(getBaseGrade())).getVal();
        }
        return getBaseGrade();
    }

    public short getBaseGrade() {
        return ItemGrade.getGradeByOption(getOptionBase(0)).getVal();
    }

    public int getOptionBase(int num) {
        return getOptions().get(num);
    }

    public int getBonusGrade() {
        return ItemGrade.getGradeByOption(getOptionBonus(0)).getVal();
    }

    public int getOptionBonus(int num) {
        return getOptions().get(num + 3);
    }


    public long getSerialNumber() {
        return getId();
    }

    public boolean hasAttribute(EquipAttribute equipAttribute) {
        return (getAttribute() & equipAttribute.getVal()) != 0;
    }

    public boolean hasSpecialAttribute(EquipSpecialAttribute equipSpecialAttribute) {
        return (getSpecialAttribute() & equipSpecialAttribute.getVal()) != 0;
    }

    public void removeAttribute(EquipAttribute equipAttribute) {
        if (!hasAttribute(equipAttribute)) {
            return;
        }
        short attr = getAttribute();
        attr ^= equipAttribute.getVal();
        setAttribute(attr);
    }

    public void addAttribute(EquipAttribute ea) {
        short attr = getAttribute();
        attr |= ea.getVal();
        setAttribute(attr);
    }


    public Equip deepCopy() {
        Equip ret = new Equip();
        ret.quantity = quantity;
        ret.pos = pos;
        ret.title = title;
        ret.tuc = tuc;
        ret.iucMax = iucMax;
        ret.hasIUCMax = hasIUCMax;
        ret.cuc = cuc;
        ret.iStr = iStr;
        ret.iDex = iDex;
        ret.iInt = iInt;
        ret.iLuk = iLuk;
        ret.iMaxHp = iMaxHp;
        ret.iMaxMp = iMaxMp;
        ret.iPad = iPad;
        ret.iMad = iMad;
        ret.iPDD = iPDD;
        ret.iMDD = iMDD;
        ret.iAcc = iAcc;
        ret.iEva = iEva;
        ret.iCraft = iCraft;
        ret.iSpeed = iSpeed;
        ret.iJump = iJump;
        ret.attribute = attribute;
        ret.levelUpType = levelUpType;
        ret.level = level;
        ret.exp = exp;
        ret.durability = durability;
        ret.iuc = iuc;
        ret.iReduceReq = iReduceReq;
        ret.specialAttribute = specialAttribute;
        ret.durabilityMax = durabilityMax;
        ret.iIncReq = iIncReq;
        ret.growthEnchant = growthEnchant;
        ret.psEnchant = psEnchant;
        ret.bdr = bdr;
        ret.imdr = imdr;
        ret.bossReward = bossReward;
        ret.superiorEqp = superiorEqp;
        ret.damR = damR;
        ret.statR = statR;
        ret.cuttable = cuttable;
        ret.hyperUpgrade = hyperUpgrade;
        ret.itemState = itemState;
        ret.chuc = chuc;
        ret.soulOptionId = soulOptionId;
        ret.soulSocketId = soulSocketId;
        ret.soulOption = soulOption;
        ret.rStr = rStr;
        ret.rDex = rDex;
        ret.rInt = rInt;
        ret.rLuk = rLuk;
        ret.rLevel = rLevel;
        ret.rJob = rJob;
        ret.rPop = rPop;
        ret.iSlot = iSlot;
        ret.vSlot = vSlot;
        ret.fixedGrade = fixedGrade;
        ret.options = new ArrayList<>();
        ret.options.addAll(options);
        ret.specialGrade = specialGrade;
        ret.fixedPotential = fixedPotential;
        ret.noPotential = noPotential;
        ret.tradeBlock = tradeBlock;
        ret.isOnly = isOnly;
        ret.notSale = notSale;
        ret.attackSpeed = attackSpeed;
        ret.price = price;
        ret.charmEXP = charmEXP;
        ret.expireOnLogout = expireOnLogout;
        ret.setItemId = setItemId;
        ret.exItem = exItem;
        ret.equipTradeBlock = equipTradeBlock;
        ret.setOwner(getOwner());
        ret.itemId = itemId;
        ret.cashItemSerialNumber = cashItemSerialNumber;
        ret.expireTime = expireTime;
        ret.invType = invType;
        ret.type = type;
        ret.isCash = isCash;
        ret.sockets = new ArrayList<>(sockets);
        if (flameStats != null) {
            ret.flameStats = flameStats.deepCopy();
        }
        ret.dropStreak = dropStreak;
        ret.itemSkills = itemSkills;
        ret.effectItemId = effectItemId;
        return ret;
    }

    public void addStat(EquipBaseStat stat, int amount) {
        int cur = (int) getBaseStat(stat);
        int newStat = Math.max(cur + amount, 0); // stat cannot be negative
        setBaseStat(stat, newStat);
    }

    private void setBaseStat(EquipBaseStat stat, int amount) {
        switch (stat) {
            case tuc:
                setTuc((short) amount);
                break;
            case cuc:
                setCuc((short) amount);
                break;
            case iStr:
                setIStr((short) amount);
                break;
            case iDex:
                setIDex((short) amount);
                break;
            case iInt:
                setIInt((short) amount);
                break;
            case iLuk:
                setILuk((short) amount);
                break;
            case iMaxHP:
                setIMaxHp((short) amount);
                break;
            case iMaxMP:
                setIMaxMp((short) amount);
                break;
            case iPAD:
                setIPad((short) amount);
                break;
            case iMAD:
                setIMad((short) amount);
                break;
            case iPDD:
                setIPDD((short) amount);
                break;
            case iMDD:
                setIMDD((short) amount);
                break;
            case iACC:
                setIAcc((short) amount);
                break;
            case iEVA:
                setIEva((short) amount);
                break;
            case iCraft:
                setICraft((short) amount);
                break;
            case iSpeed:
                setISpeed((short) amount);
                break;
            case iJump:
                setIJump((short) amount);
                break;
            case attribute:
                setAttribute((short) amount);
                break;
            case levelUpType:
                setLevelUpType((short) amount);
                break;
            case level:
                setLevel((short) amount);
                break;
            case exp:
                setExp((short) amount);
                break;
            case durability:
                setDurability((short) amount);
                break;
            case iuc:
                setIuc((short) amount);
                break;
            case iPvpDamage:
                break;
            case iReduceReq:
                setIReduceReq((byte) amount);
                break;
            case specialAttribute:
                setSpecialAttribute((short) amount);
                break;
            case durabilityMax:
                setDurabilityMax((short) amount);
                break;
            case iIncReq:
                setIIncReq((short) amount);
                break;
            case growthEnchant:
                setGrowthEnchant((short) amount);
                break;
            case psEnchant:
                setPsEnchant((short) amount);
                break;
            case bdr:
                setBdr((short) amount);
                break;
            case imdr:
                setImdr((short) amount);
                break;
            case damR:
                setDamR((short) amount);
                break;
            case statR:
                setStatR((short) amount);
                break;
            case cuttable:
                setCuttable((short) amount);
                break;
            case itemState:
                setHyperUpgrade((short) amount);
                break;
        }

    }

    //还原卷
    public void applyInnocenceScroll() {
        Equip defaultEquip = ItemData.getEquipDeepCopyFromID(getItemId(), false);
        for (EquipBaseStat ebs : EquipBaseStat.values()) {
            if (ebs != EquipBaseStat.attribute && ebs != EquipBaseStat.growthEnchant && ebs != EquipBaseStat.psEnchant) {
                setBaseStat(ebs, (int) defaultEquip.getBaseStat(ebs));
            }
        }
        setChuc((short) 0);
        reCalcEnchantmentStats();
    }

    public boolean hasUsedSlots() {
        Equip defaultEquip = ItemData.getEquipDeepCopyFromID(getItemId(), false);
        return defaultEquip.getTuc() != getTuc();
    }

    public void reCalcEnchantmentStats() {
        getEnchantStats().clear();
        for (int i = 0; i < getChuc(); i++) {
            for (EnchantStat es : getHyperUpgradeStats().keySet()) {
                putEnchantStat(es, getEnchantStats().getOrDefault(es, 0) +
                        GameConstants.getEnchantmentValByChuc(this, es, (short) i, (int) getBaseStat(es.getEquipBaseStat())));
            }
        }
    }

    public void putEnchantStat(EnchantStat es, int val) {
        getEnchantStats().put(es, val);
    }

    public TreeMap<EnchantStat, Integer> getHyperUpgradeStats() {
        Comparator<EnchantStat> comparator = Comparator.comparingInt(EnchantStat::getVal);
        TreeMap<EnchantStat, Integer> res = new TreeMap<>(comparator);
        for (EnchantStat es : EnchantStat.values()) {
            int curAmount = (int) getBaseStat(es.getEquipBaseStat());
            if (curAmount > 0 || es == EnchantStat.PAD || es == EnchantStat.MAD || es == EnchantStat.PDD || es == EnchantStat.MDD) {
                res.put(es, GameConstants.getEnchantmentValByChuc(this, es, getChuc(), curAmount));
            }
        }
        return res;
    }

    public void randomizeFlameStats(boolean obtained) {
        if (flameStats == null) {
            flameStats = new FlameStats();
        }
        flameStats.randomize(this, obtained);
    }

    private short getFlameLevel() {
        return (short) Math.ceil((getRLevel() + getIIncReq() + 1.0) / ItemConstants.EQUIP_FLAME_LEVEL_DIVIDER);
    }

    public short getFlameLevelExtended() {
        return (short) Math.ceil((getRLevel() + getIIncReq() + 1.0) / ItemConstants.EQUIP_FLAME_LEVEL_DIVIDER_EXTENDED);
    }

    public short getATTBonus(short tier) {
        if (ItemConstants.isWeapon(getItemId())) {
            final double[] multipliers = isBossReward() ? ItemConstants.WEAPON_FLAME_MULTIPLIER_BOSS_WEAPON : ItemConstants.WEAPON_FLAME_MULTIPLIER;
            Equip baseEquip = ItemData.getEquipById(getItemId());
            int att = Math.max(baseEquip.getIPad(), baseEquip.getIMad());
            return (short) Math.ceil(att * (multipliers[tier - 1] * getFlameLevel()) / 100.0);
        } else {
            return tier;
        }
    }

    private void resetFlameStats() {
        if (flameStats == null) {
            return;
        }
        flameStats.reset();
    }

    public void setHiddenOptionBase(short val, int thirdLineChance) {
        if (!ItemConstants.canEquipHavePotential(this)) {
            return;
        }
        int max = 3;
        if (getOptionBase(3) == 0) {
            // If this equip did not have a 3rd line already, thirdLineChance to get it
            if (Util.succeedProp(100 - thirdLineChance)) {
                max = 2;
            }
        }
        for (int i = 0; i < max; i++) {
            setOptionBase(i, -val);
        }
    }

    public int getOption(int num, boolean bonus) {
        return bonus ? getOptionBonus(num) : getOptionBase(num);
    }

    public void setOption(int num, int val, boolean bonus) {
        if (bonus) {
            setOptionBonus(num, val);
        } else {
            setOptionBase(num, val);
        }
    }

    public void setOptionBonus(int num, int val) {
        getOptions().set(num + 3, val);
    }

    private void setOptionBase(int num, int val) {
        getOptions().set(num, val);
    }

    public void releaseOptions(boolean bonus) {
        if (!ItemConstants.canEquipHavePotential(this)) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            if (getOption(i, bonus) < 0) {
                setOption(i, getRandomOption(bonus, i), bonus);
            }
        }
    }

    public int getRandomOption(boolean bonus, int line) {
        List<Integer> data = ItemConstants.getWeightedOptionsByEquip(this, bonus, line);
        return data.get(Util.getRandom(data.size() - 1));
    }

    @Override
    public Type getType() {
        return Type.EQUIP;
    }

    public void addLimitBreak(int incALB) {
        setLimitBreak(getLimitBreak() + incALB);
    }

    public long getFlame() {
        return getFlameStats() == null ? 0 : getFlameStats().getFlag();
    }


    @Override
    public void encode(OutPacket out) {
        super.encode(out);
        out.writeInt(getEquipStatMask(0));
        if (hasStat(EquipBaseStat.tuc)) {
            out.write(getTuc());
        }
        if (hasStat(EquipBaseStat.cuc)) {
            out.write(getCuc());
        }
        if (hasStat(EquipBaseStat.iStr)) {
            out.writeShort(getIStr() + getBaseStatFlame(EquipBaseStat.iStr) + getEnchantStat(EnchantStat.STR));
        }
        if (hasStat(EquipBaseStat.iDex)) {
            out.writeShort(getIDex() + getBaseStatFlame(EquipBaseStat.iDex) + getEnchantStat(EnchantStat.DEX));
        }
        if (hasStat(EquipBaseStat.iInt)) {
            out.writeShort(getIInt() + getBaseStatFlame(EquipBaseStat.iInt) + getEnchantStat(EnchantStat.INT));
        }
        if (hasStat(EquipBaseStat.iLuk)) {
            out.writeShort(getILuk() + getBaseStatFlame(EquipBaseStat.iLuk) + getEnchantStat(EnchantStat.LUK));
        }
        if (hasStat(EquipBaseStat.iMaxHP)) {
            out.writeShort(getIMaxHp() + getBaseStatFlame(EquipBaseStat.iMaxHP) + getEnchantStat(EnchantStat.MHP));
        }
        if (hasStat(EquipBaseStat.iMaxMP)) {
            out.writeShort(getIMaxMp() + getBaseStatFlame(EquipBaseStat.iMaxMP) + getEnchantStat(EnchantStat.MMP));
        }
        if (hasStat(EquipBaseStat.iPAD)) {
            out.writeShort(getIPad() + getBaseStatFlame(EquipBaseStat.iPAD) + getEnchantStat(EnchantStat.PAD));
        }
        if (hasStat(EquipBaseStat.iMAD)) {
            out.writeShort(getIMad() + getBaseStatFlame(EquipBaseStat.iMAD) + getEnchantStat(EnchantStat.MAD));
        }
        if (hasStat(EquipBaseStat.iPDD)) {
            out.writeShort(getIPDD() + getBaseStatFlame(EquipBaseStat.iPDD) + getEnchantStat(EnchantStat.PDD));
        }
        if (hasStat(EquipBaseStat.iCraft)) {
            out.writeShort(getICraft());
        }
        if (hasStat(EquipBaseStat.iSpeed)) {
            out.writeShort(getISpeed() + getBaseStatFlame(EquipBaseStat.iSpeed) + getEnchantStat(EnchantStat.SPEED));
        }
        if (hasStat(EquipBaseStat.iJump)) {
            out.writeShort(getIJump() + getBaseStatFlame(EquipBaseStat.iJump) + getEnchantStat(EnchantStat.JUMP));
        }
        if (hasStat(EquipBaseStat.attribute)) {
            out.writeInt(getAttribute());
        }
        if (hasStat(EquipBaseStat.levelUpType)) {
            out.write(getLevelUpType());
        }
        if (hasStat(EquipBaseStat.level)) {
            out.write(getLevel());
        }
        if (hasStat(EquipBaseStat.exp)) {
            out.writeLong(getExp());
        }
        if (hasStat(EquipBaseStat.durability)) {
            out.writeInt(getDurability());
        }
        if (hasStat(EquipBaseStat.iuc)) {
            out.writeInt(getIuc());
        }
        if (hasStat(EquipBaseStat.iReduceReq)) {
            byte bLevel = (byte) (getIReduceReq() + getBaseStatFlame(EquipBaseStat.iReduceReq));
            if (getRLevel() + getIIncReq() - bLevel < 0) {
                bLevel = (byte) (getRLevel() + getIIncReq());
            }
            out.write(bLevel);
        }
        if (hasStat(EquipBaseStat.specialAttribute)) {
            out.writeShort(getSpecialAttribute());
        }
        if (hasStat(EquipBaseStat.durabilityMax)) {
            out.writeInt(getDurabilityMax());
        }
        if (hasStat(EquipBaseStat.iIncReq)) {
            out.write(getIIncReq());
        }
        if (hasStat(EquipBaseStat.growthEnchant)) {
            out.write(getGrowthEnchant());
        }
        if (hasStat(EquipBaseStat.psEnchant)) {
            out.write(getPsEnchant());
        }
        if (hasStat(EquipBaseStat.bdr)) {
            out.write(getBdr() + getBaseStatFlame(EquipBaseStat.bdr));
        }
        if (hasStat(EquipBaseStat.imdr)) {
            out.write(getImdr());
        }
        out.writeInt(getEquipStatMask(1));
        if (hasStat(EquipBaseStat.damR)) {
            out.write(getDamR() + getBaseStatFlame(EquipBaseStat.damR));
        }
        if (hasStat(EquipBaseStat.statR)) {
            out.write(getStatR() + getBaseStatFlame(EquipBaseStat.statR));
        }
        if (hasStat(EquipBaseStat.cuttable)) {
            out.write(getCuttable());
        }
        if (hasStat(EquipBaseStat.flame)) {
            out.writeLong(getFlame());
        }
        if (hasStat(EquipBaseStat.itemState)) {
            out.writeInt(getItemState());
        }

        out.writeMapleAsciiString(getOwner());
        out.write(getGrade());
        out.write(getChuc());
        for (int i = 0; i < 7; i++) {
            out.writeShort(getOptions().get(i));
        }
        // sockets
        out.writeShort(0);
        out.writeShort(-1);
        out.writeShort(-1);
        out.writeShort(-1);

        out.writeInt(0);
        out.writeLong(getId());
        out.writeLong(ZERO_TIME);
        out.writeInt(-1);
        out.writeLong(0); //0
        out.writeLong(ZERO_TIME);
        out.writeZeroBytes(16); //grade

        out.writeShort(getSoulOptionId());
        out.writeShort(getSoulSocketId());
        out.writeShort(getSoulOption());

        if (getItemId() / 10000 == 171) {
            out.writeShort(0); //ARC
            out.writeInt(0); //ARC EXP
            out.writeShort(0); //ARC LEVEL
        }
        out.writeShort(-1);
        out.writeLong(MAX_TIME);
        out.writeLong(ZERO_TIME);
        out.writeLong(MAX_TIME);
        out.writeLong(getLimitBreak());

    }
}
