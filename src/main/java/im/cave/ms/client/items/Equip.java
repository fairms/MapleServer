package im.cave.ms.client.items;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.EnchantStat;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.ItemGrade;
import im.cave.ms.net.db.InlinedIntArrayConverter;
import im.cave.ms.provider.data.ItemData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    private short damR, statR, exGradeOption, hyperUpgrade, chuc;
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
    @Transient
    private int dropStreak = 0;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Short> sockets = new ArrayList<>();
    @Transient
    private short iucMax = ItemConstants.MAX_HAMMER_SLOTS;
    @Transient
    private boolean hasIUCMax = false;
    @Transient
    private int effectItemID = 0;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> options = new ArrayList<>();
    @Transient
    private Map<EnchantStat, Integer> enchantStats = new HashMap<>();
    @Transient
    private List<ItemSkill> itemSkills = new ArrayList<>();
    private boolean showEffect = true;


    private long limitBreak;

    private short fSTR;
    private short fDEX;
    private short fINT;
    private short fLUK;
    private short fATT;
    private short fMATT;
    private short fDEF;
    private short fHP;
    private short fMP;
    private short fSpeed;
    private short fJump;
    private short fAllStat;
    private short fBoss;
    private short fDamage;
    private byte fLevel;

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

    private int getBaseStatFlame(EquipBaseStat ebs) {
        switch (ebs) {
            case iStr:
                return getFSTR();
            case iDex:
                return getFDEX();
            case iInt:
                return getFINT();
            case iLuk:
                return getFLUK();
            case iMaxHP:
                return getFHP();
            case iMaxMP:
                return getFMP();
            case iPAD:
                return getFATT();
            case iMAD:
                return getFMATT();
            case iPDD:
            case iMDD:
                return getFDEF();
            case iSpeed:
                return getFSpeed();
            case iJump:
                return getFJump();
            case statR:
                return getFAllStat();
            case bdr:
                return getFBoss();
            case damR:
                return getFDamage();
            case iReduceReq:
                return getFLevel();
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


    public int getTotalStat(EquipBaseStat stat) {
        switch (stat) {
            case tuc:
                return getTuc();
            case cuc:
                return getCuc();
            case iStr:
                return getIStr() + getFSTR() + getEnchantStat(EnchantStat.STR);
            case iDex:
                return getIDex() + getFDEX() + getEnchantStat(EnchantStat.DEX);
            case iInt:
                return getIInt() + getFINT() + getEnchantStat(EnchantStat.INT);
            case iLuk:
                return getILuk() + getFLUK() + getEnchantStat(EnchantStat.LUK);
            case iMaxHP:
                return getIMaxHp() + getFHP() + getEnchantStat(EnchantStat.MHP);
            case iMaxMP:
                return getIMaxMp() + getFMP() + getEnchantStat(EnchantStat.MMP);
            case iPAD:
                return getIPad() + getFATT() + getEnchantStat(EnchantStat.PAD);
            case iMAD:
                return getIMad() + getFMATT() + getEnchantStat(EnchantStat.MAD);
            case iPDD:
                return getIPDD() + getFDEF() + getEnchantStat(EnchantStat.PDD);
            case iMDD:
                return getIMDD() + getFDEF() + getEnchantStat(EnchantStat.MDD);
            case iACC:
                return getIAcc() + getEnchantStat(EnchantStat.ACC);
            case iEVA:
                return getIEva() + getEnchantStat(EnchantStat.EVA);
            case iCraft:
                return getICraft();
            case iSpeed:
                return getISpeed() + getFSpeed() + getEnchantStat(EnchantStat.SPEED);
            case iJump:
                return getIJump() + getFJump() + getEnchantStat(EnchantStat.JUMP);
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
                return (byte) (getIReduceReq() + getFLevel());
            case specialAttribute:
                return getSpecialAttribute();
            case durabilityMax:
                return getDurabilityMax();
            case iIncReq:
                return getIIncReq();
            case growthEnchant:
                return getGrowthEnchant(); // ygg
            case psEnchant:
                return getPsEnchant(); // final strike
            case bdr:
                return getBdr() + getFBoss(); // bd
            case imdr:
                return getImdr(); // ied
            case damR:
                return getDamR() + getFDamage(); // td
            case statR:
                return getStatR() + getFAllStat(); // as
            case cuttable:
                return getCuttable(); // sok
            case exGradeOption:
                return getExGradeOption();
            case itemState:
                return getHyperUpgrade();
        }
        return 0;
    }


    public int getBaseStat(EquipBaseStat ebs) {
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
            case exGradeOption:
                return getExGradeOption();
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

    private short getBaseGrade() {
        return ItemGrade.getGradeByOption(getOptionBase(0)).getVal();
    }

    private int getOptionBase(int num) {
        return getOptions().get(num);
    }

    private int getBonusGrade() {
        return ItemGrade.getGradeByOption(getOptionBonus(0)).getVal();
    }

    private int getOptionBonus(int num) {
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
        ret.exGradeOption = exGradeOption;
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
        ret.fSTR = fSTR;
        ret.fDEX = fDEX;
        ret.fINT = fINT;
        ret.fLUK = fLUK;
        ret.fATT = fATT;
        ret.fMATT = fMATT;
        ret.fDEF = fDEF;
        ret.fHP = fHP;
        ret.fMP = fMP;
        ret.fSpeed = fSpeed;
        ret.fJump = fJump;
        ret.fAllStat = fAllStat;
        ret.fBoss = fBoss;
        ret.fDamage = fDamage;
        ret.fLevel = fLevel;
        ret.dropStreak = dropStreak;
        ret.itemSkills = itemSkills;
        ret.effectItemID = effectItemID;
        return ret;
    }

    public void addStat(EquipBaseStat stat, int amount) {
        int cur = getBaseStat(stat);
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
            case exGradeOption:
                setExGradeOption((short) amount);
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
                setBaseStat(ebs, defaultEquip.getBaseStat(ebs));
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

}
