package im.cave.ms.client.character.items;

import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.FlameStat;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;

import static im.cave.ms.enums.FlameStat.AllStats;
import static im.cave.ms.enums.FlameStat.Attack;
import static im.cave.ms.enums.FlameStat.BossDamage;
import static im.cave.ms.enums.FlameStat.DEX;
import static im.cave.ms.enums.FlameStat.DEX_INT;
import static im.cave.ms.enums.FlameStat.DEX_LUK;
import static im.cave.ms.enums.FlameStat.Damage;
import static im.cave.ms.enums.FlameStat.Defense;
import static im.cave.ms.enums.FlameStat.INT;
import static im.cave.ms.enums.FlameStat.INT_LUK;
import static im.cave.ms.enums.FlameStat.Jump;
import static im.cave.ms.enums.FlameStat.LUK;
import static im.cave.ms.enums.FlameStat.LevelReduction;
import static im.cave.ms.enums.FlameStat.MagicAttack;
import static im.cave.ms.enums.FlameStat.MaxHP;
import static im.cave.ms.enums.FlameStat.MaxMP;
import static im.cave.ms.enums.FlameStat.STR;
import static im.cave.ms.enums.FlameStat.STR_DEX;
import static im.cave.ms.enums.FlameStat.STR_INT;
import static im.cave.ms.enums.FlameStat.STR_LUK;
import static im.cave.ms.enums.FlameStat.Speed;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character.items
 * @date 1/5 11:05
 */
@Getter
@Setter
@Entity
@Table(name = "equip_flame")
public class FlameStats {
    @Transient
    private static final Logger log = LoggerFactory.getLogger("FlameStats");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private byte fStr;
    private byte fDex;
    private byte fInt;
    private byte fLuk;
    private byte fStrDex;
    private byte fStrInt;
    private byte fStrLuk;
    private byte fDexInt;
    private byte fDexLuk;
    private byte fIntLuk;
    private byte fAtt;
    private byte fMatt;
    private byte fDff;
    private byte fHp;
    private byte fMp;
    private byte fSpeed;
    private byte fJump;
    private byte fAllStat;
    private byte fBoss;
    private byte fDamage;
    private byte fLevel;

    public void reset() {
        fStr = 0;
        fDex = 0;
        fInt = 0;
        fLuk = 0;
        fStrDex = 0;
        fStrInt = 0;
        fStrLuk = 0;
        fDexInt = 0;
        fDexLuk = 0;
        fIntLuk = 0;
        fAtt = 0;
        fMatt = 0;
        fDff = 0;
        fHp = 0;
        fMp = 0;
        fSpeed = 0;
        fJump = 0;
        fAllStat = 0;
        fBoss = 0;
        fDamage = 0;
        fLevel = 0;
    }

    public void randomize(Equip equip, boolean obtained) {
        reset();
        if (!ItemConstants.canEquipHaveFlame(equip)) {
            return;
        }
        int minTier = equip.isBossReward() || obtained ? 3 : 1;
        int maxTier = equip.isBossReward() || obtained ? 7 : 6;

        int bonusStats = equip.isBossReward() ? 4 : Util.getRandom(1, 4);
        int statsApplied = 0;
        boolean[] flameApplied = new boolean[FlameStat.values().length];
        while (statsApplied < bonusStats) {
            int stat = Util.getRandom(flameApplied.length - 1);
            if (flameApplied[stat] ||
                    (FlameStat.getByVal(stat) == FlameStat.LevelReduction && equip.getRLevel() + equip.getIIncReq() < 5) ||
                    ((FlameStat.getByVal(stat) == FlameStat.BossDamage || FlameStat.getByVal(stat) == FlameStat.Damage) && !ItemConstants.isWeapon(equip.getItemId()))) {
                continue;
            }

            byte flameTier = (byte) Util.getRandom(minTier, maxTier);
            switch (FlameStat.getByVal(stat)) {
                case STR:
                    setFStr(flameTier);
                    break;
                case DEX:
                    setFDex(flameTier);
                    break;
                case INT:
                    setFInt(flameTier);
                    break;
                case LUK:
                    setFLuk(flameTier);
                    break;
                case STR_DEX:
                    setFStrDex(flameTier);
                    break;
                case STR_INT:
                    setFStrInt(flameTier);
                    break;
                case STR_LUK:
                    setFStrLuk(flameTier);
                    break;
                case DEX_INT:
                    setFDexInt(flameTier);
                    break;
                case DEX_LUK:
                    setFDexLuk(flameTier);
                    break;
                case INT_LUK:
                    setFIntLuk(flameTier);
                    break;
                case Attack:
                    setFAtt(flameTier);
                    break;
                case MagicAttack:
                    setFMatt(flameTier);
                    break;
                case Defense:
                    setFDff(flameTier);
                    break;
                case MaxHP:
                    setFHp(flameTier);
                    break;
                case MaxMP:
                    setFMp(flameTier);
                    break;
                case Speed:
                    setFSpeed(flameTier);
                    break;
                case Jump:
                    setFJump(flameTier);
                    break;
                case AllStats:
                    setFAllStat(flameTier);
                    break;
                case BossDamage:
                    setFBoss(flameTier);
                    break;
                case Damage:
                    setFDamage(flameTier);
                    break;
                case LevelReduction:
                    setFLevel(flameTier);
                    break;
            }
            flameApplied[stat] = true;
            statsApplied++;
        }
    }

    public FlameStats deepCopy() {
        FlameStats flameStats = new FlameStats();
        flameStats.setFStr(fStr);
        flameStats.setFDex(fDex);
        flameStats.setFInt(fInt);
        flameStats.setFLuk(fLuk);
        flameStats.setFStrDex(fStrDex);
        flameStats.setFStrInt(fStrInt);
        flameStats.setFStrLuk(fStrLuk);
        flameStats.setFDexInt(fDexInt);
        flameStats.setFDexLuk(fDexLuk);
        flameStats.setFIntLuk(fIntLuk);
        flameStats.setFAtt(fAtt);
        flameStats.setFMatt(fMatt);
        flameStats.setFDff(fDff);
        flameStats.setFHp(fHp);
        flameStats.setFMp(fMp);
        flameStats.setFSpeed(fSpeed);
        flameStats.setFJump(fJump);
        flameStats.setFAllStat(fAllStat);
        flameStats.setFBoss(fBoss);
        flameStats.setFDamage(fDamage);
        flameStats.setFLevel(fLevel);
        return flameStats;
    }

    public long getFlag() {
        long flag = 0;
        HashMap<FlameStat, Byte> flameStats = new HashMap<>();
        extract(flameStats, fStr, STR, fDex, DEX, fInt, INT, fLuk, LUK, fStrDex, STR_DEX, fStrInt, STR_INT, fStrLuk, STR_LUK, fDexInt, DEX_INT, fDexLuk, DEX_LUK, fIntLuk, INT_LUK);
        extract(flameStats, fAtt, Attack, fMatt, MagicAttack, fDff, Defense, fHp, MaxHP, fMp, MaxMP, fSpeed, Speed, fJump, Jump, fBoss, BossDamage, fDamage, Damage, fAllStat, AllStats);
        if (fLevel > 0) {
            flameStats.put(LevelReduction, fLevel);
        }
        int size = flameStats.size();
        if (size > GameConstants.MAX_FLAME_BONUS_SAGAS) {
            log.error("超出游戏限制最大火花属性数量");
            return flag;
        }
        for (FlameStat flameStat : flameStats.keySet()) {
            flag *= GameConstants.FLAME_STAT_MULTIPLE;
            flag += flameStat.getVal() + flameStats.get(flameStat);
        }
        return flag;
    }

    private void extract(HashMap<FlameStat, Byte> flameStats, byte fStr, FlameStat str, byte fDex, FlameStat dex, byte fInt, FlameStat anInt, byte fLuk, FlameStat luk, byte fStrDex, FlameStat strDex, byte fStrInt, FlameStat strInt, byte fStrLuk, FlameStat strLuk, byte fDexInt, FlameStat dexInt, byte fDexLuk, FlameStat dexLuk, byte fIntLuk, FlameStat intLuk) {
        if (fStr > 0) {
            flameStats.put(str, fStr);
        }
        if (fDex > 0) {
            flameStats.put(dex, fDex);
        }
        if (fInt > 0) {
            flameStats.put(anInt, fInt);
        }
        if (fLuk > 0) {
            flameStats.put(luk, fLuk);
        }
        if (fStrDex > 0) {
            flameStats.put(strDex, fStrDex);
        }
        if (fStrInt > 0) {
            flameStats.put(strInt, fStrInt);
        }
        if (fStrLuk > 0) {
            flameStats.put(strLuk, fStrLuk);
        }
        if (fDexInt > 0) {
            flameStats.put(dexInt, fDexInt);
        }
        if (fDexLuk > 0) {
            flameStats.put(dexLuk, fDexLuk);
        }
        if (fIntLuk > 0) {
            flameStats.put(intLuk, fIntLuk);
        }
    }

    @Override
    public String toString() {
        return "FlameStats{" +
                "id=" + id +
                ", fStr=" + fStr +
                ", fDex=" + fDex +
                ", fInt=" + fInt +
                ", fLuk=" + fLuk +
                ", fStrDex=" + fStrDex +
                ", fStrInt=" + fStrInt +
                ", fStrLuk=" + fStrLuk +
                ", fDexInt=" + fDexInt +
                ", fDexLuk=" + fDexLuk +
                ", fIntLuk=" + fIntLuk +
                ", fAtt=" + fAtt +
                ", fMatt=" + fMatt +
                ", fDff=" + fDff +
                ", fHp=" + fHp +
                ", fMp=" + fMp +
                ", fSpeed=" + fSpeed +
                ", fJump=" + fJump +
                ", fAllStat=" + fAllStat +
                ", fBoss=" + fBoss +
                ", fDamage=" + fDamage +
                ", fLevel=" + fLevel +
                '}';
    }
}
