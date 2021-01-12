package im.cave.ms.client.character;

import im.cave.ms.connection.netty.OutPacket;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static im.cave.ms.constants.ServerConstants.ZERO_TIME;


/**
 * Created on 2/18/2017.
 */
@Entity
@Table(name = "non_combat_stat_day_limit")
public class NonCombatStatDayLimit {
    @Id
    private int id;
    private short charisma;
    private short charm;
    private short insight;
    private short will;
    private short craft;
    private short sense;
    private long lastUpdateCharmByCashPR;
    private byte charmByCashPR;

    public NonCombatStatDayLimit(short charisma, short charm, byte charmByCashPR, short insight, short will, short craft, short sense, long lastUpdateCharmByCashPR) {
        this.charisma = charisma;
        this.charm = charm;
        this.charmByCashPR = charmByCashPR;
        this.insight = insight;
        this.will = will;
        this.craft = craft;
        this.sense = sense;
        this.lastUpdateCharmByCashPR = lastUpdateCharmByCashPR;
    }

    public NonCombatStatDayLimit() {
        this((short) 0, (short) 0, (byte) 0, (short) 0, (short) 0, (short) 0, (short) 0, ZERO_TIME);
    }

    public short getCharm() {
        return charm;
    }

    public void setCharm(short charm) {
        this.charm = charm;
    }

    public byte getCharmByCashPR() {
        return charmByCashPR;
    }

    public void setCharmByCashPR(byte charmByCashPR) {
        this.charmByCashPR = charmByCashPR;
    }

    public short getInsight() {
        return insight;
    }

    public void setInsight(short insight) {
        this.insight = insight;
    }

    public short getWill() {
        return will;
    }

    public void setWill(short will) {
        this.will = will;
    }

    public short getCraft() {
        return craft;
    }

    public void setCraft(short craft) {
        this.craft = craft;
    }

    public short getSense() {
        return sense;
    }

    public void setSense(short sense) {
        this.sense = sense;
    }

    public long getLastUpdateCharmByCashPR() {
        return lastUpdateCharmByCashPR;
    }

    public void setLastUpdateCharmByCashPR(long lastUpdateCharmByCashPR) {
        this.lastUpdateCharmByCashPR = lastUpdateCharmByCashPR;
    }

    public void encode(OutPacket out) {
        out.writeShort(getCharisma());
        out.writeShort(getInsight());
        out.writeShort(getWill());
        out.writeShort(getCraft());
        out.writeShort(getSense());
        out.writeShort(getCharm());
        out.write(getCharmByCashPR());
        out.writeLong(getLastUpdateCharmByCashPR());
    }

    public short getCharisma() {
        return charisma;
    }

    public void setCharisma(short charisma) {
        this.charisma = charisma;
    }

    public void setId(int id) {
        this.id = id;
    }
}
