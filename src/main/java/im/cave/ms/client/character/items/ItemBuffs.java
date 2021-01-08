package im.cave.ms.client.character.items;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.provider.data.ItemData;

import java.util.Map;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ACC;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ACCR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.AsrRByItem;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.BdR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Booster;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.CombatOrders;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.DEX;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.EVA;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.EVAR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ExpBuffRate;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.INT;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieACC;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieAllStat;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieBDR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieBooster;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieDEX;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieEVA;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieForceJump;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieForceSpeed;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieINT;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieIgnoreMobpdpR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieJump;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieLUK;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMADR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMDD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMDDR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMHP;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMHPR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMMP;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMMPR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePADR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePDD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePDDR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieQrPointTerm;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieSTR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieSpeed;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieStance;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieStatR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieUnk1;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Inflation;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ItemUpByItem;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Jump;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.LUK;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MDD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MaxHP;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MaxMP;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MesoUpByItem;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Morph;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.PAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.PDD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.RepeatEffect;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.STR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Speed;

public class ItemBuffs {
    public static void giveItemBuffsFromItemID(MapleCharacter chr, int itemID) {
        Map<SpecStat, Integer> specStats = ItemData.getItemInfoById(itemID).getSpecStats();
        long time = specStats.getOrDefault(SpecStat.time, 0) / 1000;
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        for (Map.Entry<SpecStat, Integer> entry : specStats.entrySet()) {
            SpecStat ss = entry.getKey();
            int value = entry.getValue();
            Option o = new Option(-itemID, time);
            o.nOption = value;
            o.nValue = value;
            switch (ss) {
                case hp:
                    chr.heal(value);
                    break;
                case hpR:
                    chr.heal((int) ((value / 100D) * chr.getStat(MapleStat.MAXHP)));
                    break;
                case mp:
                    chr.healMP(value);
                    break;
                case mpR:
                    chr.healMP((int) ((value / 100D) * chr.getStat(MapleStat.MAXMP)));
                    break;
                case eva:
                    tsm.putCharacterStatValue(EVA, o);
                    break;
                case speed:
                    tsm.putCharacterStatValue(Speed, o);
                    break;
                case pad:
                    tsm.putCharacterStatValue(PAD, o);
                    break;
                case mad:
                    tsm.putCharacterStatValue(MAD, o);
                    break;
                case pdd:
                    tsm.putCharacterStatValue(PDD, o);
                    break;
                case mdd:
                    tsm.putCharacterStatValue(MDD, o);
                    break;
                case acc:
                    tsm.putCharacterStatValue(ACC, o);
                    break;
                case jump:
                    tsm.putCharacterStatValue(Jump, o);
                    break;
                case imhp:
                    tsm.putCharacterStatValue(MaxHP, o);
                    break;
                case immp:
                    tsm.putCharacterStatValue(MaxMP, o);
                    break;
                case indieAllStat:
                    tsm.putCharacterStatValue(IndieAllStat, o);
                    break;
                case indieSpeed:
                    tsm.putCharacterStatValue(IndieSpeed, o);
                    break;
                case indieJump:
                    tsm.putCharacterStatValue(IndieJump, o);
                    break;
                case indieSTR:
                    tsm.putCharacterStatValue(IndieSTR, o);
                    break;
                case indieDEX:
                    tsm.putCharacterStatValue(IndieDEX, o);
                    break;
                case indieINT:
                    tsm.putCharacterStatValue(IndieINT, o);
                    break;
                case indieLUK:
                    tsm.putCharacterStatValue(IndieLUK, o);
                    break;
                case indiePad:
                    tsm.putCharacterStatValue(IndiePAD, o);
                    break;
                case indiePdd:
                    tsm.putCharacterStatValue(IndiePDD, o);
                    break;
                case indieMad:
                    tsm.putCharacterStatValue(IndieMAD, o);
                    break;
                case indieMdd:
                    tsm.putCharacterStatValue(IndieMDD, o);
                    break;
                case indieBDR:
                    tsm.putCharacterStatValue(IndieBDR, o);
                    break;
                case indieIgnoreMobpdpR:
                    tsm.putCharacterStatValue(IndieIgnoreMobpdpR, o);
                    break;
                case indieStatR:
                    tsm.putCharacterStatValue(IndieStatR, o);
                    break;
                case indieMhp:
                    tsm.putCharacterStatValue(IndieMHP, o);
                    break;
                case indieMmp:
                    tsm.putCharacterStatValue(IndieMMP, o);
                    break;
                case indieBooster:
                    tsm.putCharacterStatValue(IndieBooster, o);
                    break;
                case indieAcc:
                    tsm.putCharacterStatValue(IndieACC, o);
                    break;
                case indieEva:
                    tsm.putCharacterStatValue(IndieEVA, o);
                    break;
                case indieAllSkill:
                    tsm.putCharacterStatValue(CombatOrders, o);
                    break;
                case indieMhpR:
                    tsm.putCharacterStatValue(IndieMHPR, o);
                    break;
                case indieMmpR:
                    tsm.putCharacterStatValue(IndieMMPR, o);
                    break;
                case indieStance:
                    tsm.putCharacterStatValue(IndieStance, o);
                    break;
                case indieForceSpeed:
                    tsm.putCharacterStatValue(IndieForceSpeed, o);
                    break;
                case indieForceJump:
                    tsm.putCharacterStatValue(IndieForceJump, o);
                    break;
                case indieQrPointTerm:
                    tsm.putCharacterStatValue(IndieQrPointTerm, o);
                    break;
                case indieWaterSmashBuff:
                    tsm.putCharacterStatValue(IndieUnk1, o);
                    break;
                case padRate:
                    tsm.putCharacterStatValue(IndiePADR, o);
                    break;
                case madRate:
                    tsm.putCharacterStatValue(IndieMADR, o);
                    break;
                case pddRate:
                    tsm.putCharacterStatValue(IndiePDDR, o);
                    break;
                case mddRate:
                    tsm.putCharacterStatValue(IndieMDDR, o);
                    break;
                case accRate:
                    tsm.putCharacterStatValue(ACCR, o);
                    break;
                case evaRate:
                    tsm.putCharacterStatValue(EVAR, o);
                    break;
                case mhpR:
                case mhpRRate:
                    tsm.putCharacterStatValue(IndieMHPR, o);
                    break;
                case mmpR:
                case mmpRRate:
                    tsm.putCharacterStatValue(IndieMHPR, o);
                    break;
                case booster:
                    tsm.putCharacterStatValue(Booster, o);
                    break;
                case expinc:
                    tsm.putCharacterStatValue(ExpBuffRate, o);
                    break;
                case str:
                    tsm.putCharacterStatValue(STR, o);
                    break;
                case dex:
                    tsm.putCharacterStatValue(DEX, o);
                    break;
                case inte:
                    tsm.putCharacterStatValue(INT, o);
                    break;
                case luk:
                    tsm.putCharacterStatValue(LUK, o);
                    break;
                case asrR:
                    tsm.putCharacterStatValue(AsrRByItem, o);
                    break;
                case bdR:
                    tsm.putCharacterStatValue(BdR, o);
                    break;
                case prob:
                    tsm.putCharacterStatValue(ItemUpByItem, o);
                    tsm.putCharacterStatValue(MesoUpByItem, o);
                    break;
                case inflation:
                    tsm.putCharacterStatValue(Inflation, o);
                    break;
                case morph:
                    tsm.putCharacterStatValue(Morph, o);
                    break;
                case repeatEffect:
                    tsm.putCharacterStatValue(RepeatEffect, o);
                    break;
            }
        }
        tsm.sendSetStatPacket();
    }
}
