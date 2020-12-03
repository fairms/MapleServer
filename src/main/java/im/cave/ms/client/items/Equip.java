package im.cave.ms.client.items;

import im.cave.ms.enums.EnchantStat;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.ItemGrade;
import im.cave.ms.net.db.InlinedIntArrayConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private int getBaseStat(EquipBaseStat ebs) {
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

}
