package im.cave.ms.client.character.skill;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.netty.OutPacket;

/**
 * 怪物燃烧
 * <p>
 * Created on 1/4/2018.
 */
public class BurnedInfo {
    private int characterId, skillId, interval, end, dotAnimation, dotCount, superPos, attackDelay, dotTickIdx, dotTickDamR;
    private int startTime;
    private int lastUpdate;
    private MapleCharacter chr;
    private long damage;

    public BurnedInfo deepCopy() {
        BurnedInfo copy = new BurnedInfo();
        copy.setCharacterId(getCharacterId());
        copy.setChr(getChr());
        copy.setSkillId(getSkillId());
        copy.setDamage(getDamage());
        copy.setInterval(getInterval());
        copy.setEnd(getEnd());
        copy.setDotAnimation(getDotAnimation());
        copy.setDotCount(getDotCount());
        copy.setSuperPos(getSuperPos());
        copy.setAttackDelay(getAttackDelay());
        copy.setDotTickIdx(getDotTickIdx());
        copy.setDotTickDamR(getDotTickDamR());
        copy.setLastUpdate(getLastUpdate());
        copy.setStartTime(getStartTime());
        return copy;
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public long getDamage() {
        return damage;
    }

    public void setDamage(long damage) {
        this.damage = damage;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getDotAnimation() {
        return dotAnimation;
    }

    public void setDotAnimation(int dotAnimation) {
        this.dotAnimation = dotAnimation;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public int getSuperPos() {
        return superPos;
    }

    public void setSuperPos(int superPos) {
        this.superPos = superPos;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }

    public int getDotTickIdx() {
        return dotTickIdx;
    }

    public void setDotTickIdx(int dotTickIdx) {
        this.dotTickIdx = dotTickIdx;
    }

    public int getDotTickDamR() {
        return dotTickDamR;
    }

    public void setDotTickDamR(int dotTickDamR) {
        this.dotTickDamR = dotTickDamR;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void encode(OutPacket out) {
        out.writeInt(getCharacterId());
        out.writeInt(getSkillId());
        out.writeLong(getDamage());
        out.writeInt(getInterval());
        out.writeInt(getEnd());
        out.writeInt(getDotAnimation());
        out.writeInt(getDotCount());
        out.writeInt(getSuperPos());
        out.writeInt(getAttackDelay());
        out.writeInt(getDotTickIdx());
        out.writeInt(getDotTickDamR());
        out.writeInt(getLastUpdate());
        out.writeInt(getStartTime());
    }

    public MapleCharacter getChr() {
        return chr;
    }

    public void setChr(MapleCharacter chr) {
        this.chr = chr;
    }
}

