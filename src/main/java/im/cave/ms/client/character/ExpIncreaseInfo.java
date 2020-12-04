package im.cave.ms.client.character;

import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import static im.cave.ms.enums.ExpIncreaseInfoFlags.*;

/**
 * Created on 1/25/2018.
 */
public class ExpIncreaseInfo {
    private boolean isLastHit;
    private int incEXP;
    private boolean onQuest;
    private int selectedMobBonusExp;
    private int partyBonusPercentage;
    private int questBonusRate;
    private int questBonusRemainCount;
    private int weddingBonusExp;
    private int partyBonusExp;
    private int itemBonusExp;
    private int premiumIPBonusExp;
    private int rainbowWeekEventBonusExp;
    private int boomupEventBonusExp;
    private int plusExpBuffBonusExp;
    private int psdBonusExpRate;
    private int indieBonusExp;
    private int relaxBonusExp;
    private int installItemBonusExp;
    private int aswanWinnerBonusExp;
    private int expByIncExpR;
    private int valuePackBonusExp;
    private int expByIncPQExpR;
    private int baseAddExp;
    private int bloodAllianceBonusExp;
    private int freezeHotEventBonusExp;
    private int restFieldBonusExp;
    private int restFieldExpRate;
    private int userHPRateBonusExp;
    private int fieldValueBonusExp;
    private int mobKillBonusExp;
    private int liveEventBonusExp;

    public boolean isLastHit() {
        return isLastHit;
    }

    public void setLastHit(boolean lastHit) {
        isLastHit = lastHit;
    }

    public int getIncEXP() {
        return incEXP;
    }

    public void setIncEXP(int incEXP) {
        this.incEXP = incEXP;
    }

    public boolean isOnQuest() {
        return onQuest;
    }

    public void setOnQuest(boolean onQuest) {
        this.onQuest = onQuest;
    }

    public int getSelectedMobBonusExp() {
        return selectedMobBonusExp;
    }

    public void setSelectedMobBonusExp(int selectedMobBonusExp) {
        this.selectedMobBonusExp = selectedMobBonusExp;
    }

    public int getPartyBonusPercentage() {
        return partyBonusPercentage;
    }

    public void setPartyBonusPercentage(int partyBonusPercentage) {
        this.partyBonusPercentage = partyBonusPercentage;
    }

    public int getQuestBonusRate() {
        return questBonusRate;
    }

    public void setQuestBonusRate(int questBonusRate) {
        this.questBonusRate = questBonusRate;
    }

    public int getQuestBonusRemainCount() {
        return questBonusRemainCount;
    }

    public void setQuestBonusRemainCount(int questBonusRemainCount) {
        this.questBonusRemainCount = questBonusRemainCount;
    }

    public int getWeddingBonusExp() {
        return weddingBonusExp;
    }

    public void setWeddingBonusExp(int weddingBonusExp) {
        this.weddingBonusExp = weddingBonusExp;
    }

    public int getPartyBonusExp() {
        return partyBonusExp;
    }

    public void setPartyBonusExp(int partyBonusExp) {
        this.partyBonusExp = partyBonusExp;
    }

    public int getItemBonusExp() {
        return itemBonusExp;
    }

    public void setItemBonusExp(int itemBonusExp) {
        this.itemBonusExp = itemBonusExp;
    }

    public int getPremiumIPBonusExp() {
        return premiumIPBonusExp;
    }

    public void setPremiumIPBonusExp(int premiumIPBonusExp) {
        this.premiumIPBonusExp = premiumIPBonusExp;
    }

    public int getRainbowWeekEventBonusExp() {
        return rainbowWeekEventBonusExp;
    }

    public void setRainbowWeekEventBonusExp(int rainbowWeekEventBonusExp) {
        this.rainbowWeekEventBonusExp = rainbowWeekEventBonusExp;
    }

    public int getBoomupEventBonusExp() {
        return boomupEventBonusExp;
    }

    public void setBoomupEventBonusExp(int boomupEventBonusExp) {
        this.boomupEventBonusExp = boomupEventBonusExp;
    }

    public int getPlusExpBuffBonusExp() {
        return plusExpBuffBonusExp;
    }

    public void setPlusExpBuffBonusExp(int plusExpBuffBonusExp) {
        this.plusExpBuffBonusExp = plusExpBuffBonusExp;
    }

    public int getPsdBonusExpRate() {
        return psdBonusExpRate;
    }

    public void setPsdBonusExpRate(int psdBonusExpRate) {
        this.psdBonusExpRate = psdBonusExpRate;
    }

    public int getIndieBonusExp() {
        return indieBonusExp;
    }

    public void setIndieBonusExp(int indieBonusExp) {
        this.indieBonusExp = indieBonusExp;
    }

    public int getRelaxBonusExp() {
        return relaxBonusExp;
    }

    public void setRelaxBonusExp(int relaxBonusExp) {
        this.relaxBonusExp = relaxBonusExp;
    }

    public int getInstallItemBonusExp() {
        return installItemBonusExp;
    }

    public void setInstallItemBonusExp(int installItemBonusExp) {
        this.installItemBonusExp = installItemBonusExp;
    }

    public int getAswanWinnerBonusExp() {
        return aswanWinnerBonusExp;
    }

    public void setAswanWinnerBonusExp(int aswanWinnerBonusExp) {
        this.aswanWinnerBonusExp = aswanWinnerBonusExp;
    }

    public int getExpByIncExpR() {
        return expByIncExpR;
    }

    public void setExpByIncExpR(int expByIncExpR) {
        this.expByIncExpR = expByIncExpR;
    }

    public int getValuePackBonusExp() {
        return valuePackBonusExp;
    }

    public void setValuePackBonusExp(int valuePackBonusExp) {
        this.valuePackBonusExp = valuePackBonusExp;
    }

    public int getExpByIncPQExpR() {
        return expByIncPQExpR;
    }

    public void setExpByIncPQExpR(int expByIncPQExpR) {
        this.expByIncPQExpR = expByIncPQExpR;
    }

    public int getBaseAddExp() {
        return baseAddExp;
    }

    public void setBaseAddExp(int baseAddExp) {
        this.baseAddExp = baseAddExp;
    }

    public int getBloodAllianceBonusExp() {
        return bloodAllianceBonusExp;
    }

    public void setBloodAllianceBonusExp(int bloodAllianceBonusExp) {
        this.bloodAllianceBonusExp = bloodAllianceBonusExp;
    }

    public int getFreezeHotEventBonusExp() {
        return freezeHotEventBonusExp;
    }

    public void setFreezeHotEventBonusExp(int freezeHotEventBonusExp) {
        this.freezeHotEventBonusExp = freezeHotEventBonusExp;
    }

    public int getRestFieldBonusExp() {
        return restFieldBonusExp;
    }

    public void setRestFieldBonusExp(int restFieldBonusExp) {
        this.restFieldBonusExp = restFieldBonusExp;
    }

    public int getRestFieldExpRate() {
        return restFieldExpRate;
    }

    public void setRestFieldExpRate(int restFieldExpRate) {
        this.restFieldExpRate = restFieldExpRate;
    }

    public int getUserHPRateBonusExp() {
        return userHPRateBonusExp;
    }

    public void setUserHPRateBonusExp(int userHPRateBonusExp) {
        this.userHPRateBonusExp = userHPRateBonusExp;
    }

    public int getFieldValueBonusExp() {
        return fieldValueBonusExp;
    }

    public void setFieldValueBonusExp(int fieldValueBonusExp) {
        this.fieldValueBonusExp = fieldValueBonusExp;
    }

    public int getMobKillBonusExp() {
        return mobKillBonusExp;
    }

    public void setMobKillBonusExp(int mobKillBonusExp) {
        this.mobKillBonusExp = mobKillBonusExp;
    }

    public int getLiveEventBonusExp() {
        return liveEventBonusExp;
    }

    public void setLiveEventBonusExp(int liveEventBonusExp) {
        this.liveEventBonusExp = liveEventBonusExp;
    }

    public long getMask() {
        long mask = 0;
        if (getSelectedMobBonusExp() > 0) {
            mask |= SelectedMobBonusExp.getVal();
        }
        if (getPartyBonusPercentage() > 0) {
            mask |= PartyBonusPercentage.getVal();
        }
        if (getWeddingBonusExp() > 0) {
            mask |= WeddingBonusExp.getVal();
        }
        if (getPartyBonusExp() > 0) {
            mask |= PartyBonusExp.getVal();
        }
        if (getItemBonusExp() > 0) {
            mask |= ItemBonusExp.getVal();
        }
        if (getPremiumIPBonusExp() > 0) {
            mask |= PremiumIPBonusExp.getVal();
        }
        if (getRainbowWeekEventBonusExp() > 0) {
            mask |= RainbowWeekEventBonusExp.getVal();
        }
        if (getBoomupEventBonusExp() > 0) {
            mask |= BoomUpEventBonusExp.getVal();
        }
        if (getPlusExpBuffBonusExp() > 0) {
            mask |= PlusExpBuffBonusExp.getVal();
        }
        if (getPsdBonusExpRate() > 0) {
            mask |= PsdBonusExpRate.getVal();
        }
        if (getIndieBonusExp() > 0) {
            mask |= IndieBonusExp.getVal();
        }
        if (getRelaxBonusExp() > 0) {
            mask |= RelaxBonusExp.getVal();
        }
        if (getInstallItemBonusExp() > 0) {
            mask |= InstallItemBonusExp.getVal();
        }
        if (getAswanWinnerBonusExp() > 0) {
            mask |= AswanWinnerBonusExp.getVal();
        }
        if (getExpByIncExpR() > 0) {
            mask |= ExpByIncExpR.getVal();
        }
        if (getValuePackBonusExp() > 0) {
            mask |= ValuePackBonusExp.getVal();
        }
        if (getExpByIncPQExpR() > 0) {
            mask |= ExpByIncPQExpR.getVal();
        }
        if (getBaseAddExp() > 0) {
            mask |= BaseAddExp.getVal();
        }
        if (getBloodAllianceBonusExp() > 0) {
            mask |= BloodAllianceBonusExp.getVal();
        }
        if (getFreezeHotEventBonusExp() > 0) {
            mask |= FreezeHotEventBonusExp.getVal();
        }
        if (getRestFieldBonusExp() > 0 || getRestFieldExpRate() > 0) {
            mask |= RestField.getVal();
        }
        if (getUserHPRateBonusExp() > 0) {
            mask |= UserHPRateBonusExp.getVal();
        }
        if (getFieldValueBonusExp() > 0) {
            mask |= FieldValueBonusExp.getVal();
        }
        if (getMobKillBonusExp() > 0) {
            mask |= MobKillBonusExp.getVal();
        }
        if (getLiveEventBonusExp() > 0) {
            mask |= LiveEventBonusExp.getVal();
        }

        return mask;
    }

    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.writeBool(isLastHit());
        mplew.writeLong(getIncEXP());
        mplew.writeBool(isOnQuest());
        long mask = getMask();
        mplew.writeLong(mask);
        if ((mask & SelectedMobBonusExp.getVal()) != 0) {
            mplew.writeInt(getSelectedMobBonusExp());
        }
        if ((mask & PartyBonusPercentage.getVal()) != 0) {
            mplew.write(getPartyBonusPercentage());
        }
        if (isOnQuest()) {
            mplew.write(getQuestBonusRate());
        }
        if (getQuestBonusRemainCount() > 0) {
            mplew.write(getQuestBonusRemainCount());
        }
        if ((mask & WeddingBonusExp.getVal()) != 0) {
            mplew.writeInt(getWeddingBonusExp());
        }
        if ((mask & PartyBonusExp.getVal()) != 0) {
            mplew.writeInt(getPartyBonusExp());
        }
        if ((mask & ItemBonusExp.getVal()) != 0) {
            mplew.writeInt(getItemBonusExp());
        }
        if ((mask & PremiumIPBonusExp.getVal()) != 0) {
            mplew.writeInt(getPremiumIPBonusExp());
        }
        if ((mask & RainbowWeekEventBonusExp.getVal()) != 0) {
            mplew.writeInt(getRainbowWeekEventBonusExp());
        }
        if ((mask & BoomUpEventBonusExp.getVal()) != 0) {
            mplew.writeInt(getBoomupEventBonusExp());
        }
        if ((mask & PlusExpBuffBonusExp.getVal()) != 0) {
            mplew.writeInt(getPlusExpBuffBonusExp());
        }
        if ((mask & PsdBonusExpRate.getVal()) != 0) {
            mplew.writeInt(getPsdBonusExpRate());
        }
        if ((mask & IndieBonusExp.getVal()) != 0) {
            mplew.writeInt(getIndieBonusExp());
        }
        if ((mask & RelaxBonusExp.getVal()) != 0) {
            mplew.writeInt(getRelaxBonusExp());
        }
        if ((mask & InstallItemBonusExp.getVal()) != 0) {
            mplew.writeInt(getInstallItemBonusExp());
        }
        //if ((mask & AswanWinnerBonusExp.getVal()) != 0) {
        //    mplew.writeInt(getAswanWinnerBonusExp());
        //}
        if ((mask & ExpByIncExpR.getVal()) != 0) {
            mplew.writeInt(getExpByIncExpR());
        }
        if ((mask & ValuePackBonusExp.getVal()) != 0) {
            mplew.writeInt(getValuePackBonusExp());
        }
        if ((mask & ExpByIncPQExpR.getVal()) != 0) {
            mplew.writeInt(getExpByIncPQExpR());
        }
        if ((mask & BaseAddExp.getVal()) != 0) {
            mplew.writeInt(getBaseAddExp());
        }
        if ((mask & BloodAllianceBonusExp.getVal()) != 0) {
            mplew.writeInt(getBloodAllianceBonusExp());
        }
        if ((mask & FreezeHotEventBonusExp.getVal()) != 0) {
            mplew.writeInt(getFreezeHotEventBonusExp());
        }
        if ((mask & RestField.getVal()) != 0) {
            mplew.writeInt(getRestFieldBonusExp());
            mplew.writeInt(getRestFieldExpRate());
        }
        if ((mask & UserHPRateBonusExp.getVal()) != 0) {
            mplew.writeInt(getUserHPRateBonusExp());
        }
        if ((mask & FieldValueBonusExp.getVal()) != 0) {
            mplew.writeInt(getFieldValueBonusExp());
        }
        if ((mask & MobKillBonusExp.getVal()) != 0) {
            mplew.writeInt(getMobKillBonusExp());
        }
        if ((mask & LiveEventBonusExp.getVal()) != 0) {
            mplew.writeInt(getLiveEventBonusExp());
        }
        if ((mask & Unk8000000.getVal()) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & Unk10000000.getVal()) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & Unk20000000.getVal()) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & Unk40000000.getVal()) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & Unk80000000.getVal()) != 0) {
            mplew.write(0);
        }
        if ((mask & Unk1000000000.getVal()) != 0) {
            mplew.writeInt(0);
        }
    }
}
