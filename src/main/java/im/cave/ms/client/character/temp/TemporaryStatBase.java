package im.cave.ms.client.character.temp;

import im.cave.ms.client.character.Option;

import java.nio.file.attribute.FileTime;

/**
 * Created on 2/3/2018.
 */
public class TemporaryStatBase {
    private Option option;
    private long lastUpdated;
    protected int expireTerm;
    private boolean dynamicTermSet;

    public TemporaryStatBase(boolean dynamicTermSet) {
        option = new Option();
        option.nOption = 0;
        option.rOption = 0;
        lastUpdated = System.currentTimeMillis();
        this.dynamicTermSet = dynamicTermSet;
    }

    public Option getOption() {
        return option;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getExpireTerm() {
        if (isDynamicTermSet()) {
            return 1000 * expireTerm;
        }
        return Integer.MAX_VALUE;
    }

    public void setExpireTerm(int expireTerm) {
        this.expireTerm = expireTerm;
    }

    public boolean isDynamicTermSet() {
        return dynamicTermSet;
    }

    public void setDynamicTermSet(boolean dynamicTermSet) {
        this.dynamicTermSet = dynamicTermSet;
    }

    public int getMaxValue() {
        return 10000;
    }

    public boolean isActive() {
        return getOption().nOption >= 10000;
    }

    public boolean hasExpired(long time) {
        boolean result = false;
        if (isDynamicTermSet()) {
            result = getExpireTerm() > time - getLastUpdated();
        }
        return result;
    }

    public int getNOption() {
        return getOption().nOption;
    }

    public int getROption() {
        return getOption().rOption;
    }

    public void reset() {
        getOption().nOption = 0;
        getOption().rOption = 0;
        setLastUpdated(System.currentTimeMillis());
    }

    public void setNOption(int i) {
        getOption().nOption = i;
    }

    public void setROption(int reason) {
        getOption().rOption = reason;
    }
}
