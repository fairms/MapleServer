package im.cave.ms.enums;

import java.util.Arrays;

import static im.cave.ms.enums.EquipBaseStat.iACC;
import static im.cave.ms.enums.EquipBaseStat.iDex;
import static im.cave.ms.enums.EquipBaseStat.iEVA;
import static im.cave.ms.enums.EquipBaseStat.iInt;
import static im.cave.ms.enums.EquipBaseStat.iJump;
import static im.cave.ms.enums.EquipBaseStat.iLuk;
import static im.cave.ms.enums.EquipBaseStat.iMAD;
import static im.cave.ms.enums.EquipBaseStat.iMDD;
import static im.cave.ms.enums.EquipBaseStat.iMaxHP;
import static im.cave.ms.enums.EquipBaseStat.iMaxMP;
import static im.cave.ms.enums.EquipBaseStat.iPAD;
import static im.cave.ms.enums.EquipBaseStat.iPDD;
import static im.cave.ms.enums.EquipBaseStat.iReduceReq;
import static im.cave.ms.enums.EquipBaseStat.iSpeed;
import static im.cave.ms.enums.EquipBaseStat.iStr;


public enum ScrollStat {
    success,
    incSTR,
    incDEX,
    incINT,
    incLUK,
    incPAD,
    incMAD,
    incPDD,
    incMDD,
    incACC,
    incEVA,
    incMHP,
    incMMP,
    incSpeed,
    incJump,
    incIUC,
    incPERIOD,
    incReqLevel,
    reqRUC,
    randOption,
    randStat,
    tuc,
    speed,
    forceUpgrade,
    cursed,
    noCursed,
    maxSuperiorEqp,
    noNegative,
    incRandVol,
    incALB,
    reqEquipLevelMin,
    reqEquipLevelMax,
    createType,
    optionType;

    public static ScrollStat getScrollStatByString(String name) {
        return Arrays.stream(values()).filter(ss -> ss.toString().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static EquipBaseStat[] getRandStats() {
        return new EquipBaseStat[]{iStr, iDex, iInt, iLuk, iMaxHP, iMaxMP, iPAD, iMAD, iPDD, iMDD, iACC, iEVA};
    }

    public EquipBaseStat getEquipStat() {
        switch (this) {
            case incSTR:
                return iStr;
            case incDEX:
                return iDex;
            case incINT:
                return iInt;
            case incLUK:
                return iLuk;
            case incPAD:
                return iPAD;
            case incMAD:
                return iMAD;
            case incPDD:
                return iPDD;
            case incMDD:
                return iMDD;
            case incACC:
                return iACC;
            case incEVA:
                return iEVA;
            case incMHP:
                return iMaxHP;
            case incMMP:
                return iMaxMP;
            case incSpeed:
            case speed:
                return iSpeed;
            case incJump:
                return iJump;
            case incReqLevel:
                return iReduceReq;
            default:
                return null;
        }
    }
}
