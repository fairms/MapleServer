package im.cave.ms.client.character;

import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.network.db.InlinedIntArrayConverter;
import im.cave.ms.network.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 11/23 12:57
 */
@Getter
@Setter
@Entity
@Table(name = "charstat")
public class CharStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private short job;
    private short subJob;
    private int remainingAp = 0;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> remainingSp;
    private int level = 1;
    private long exp = 0;
    private long meso = 0;
    private int str = 12;
    private int dex = 4;
    private int int_ = 4;
    private int luk = 4;
    private int def;
    private int speed;
    private int jump;
    private int hp = 50;
    private int maxHP = 50;
    private int mp = 5;
    private int maxMP = 5;
    private int fame = 0;
    private int honerPoint = 0;
    private int fatigue = 0;
    private int fatigueUpdated;
    private int charismaExp;
    private int insightExp;
    private int willExp;
    private int craftExp;
    private int senseExp;
    private int charmExp;
    private int weaponPoint = 0;
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
            orphanRemoval = true, optional = false)
    private NonCombatStatDayLimit nonCombatStatDayLimit;

    @PostPersist
    public void initializeCandidateId() {
        nonCombatStatDayLimit.setId(id);
        DataBaseManager.saveToDB(nonCombatStatDayLimit);
    }

    public CharStats() {
        remainingSp = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));
        nonCombatStatDayLimit = new NonCombatStatDayLimit();
    }

    public static CharStats getDefaultStats(int jobId) {
        return new CharStats();
    }

    public void encode(OutPacket out) {
        out.writeInt(getLevel());
        out.writeShort(getJob());
        out.writeShort(getStr());
        out.writeShort(getDex());
        out.writeShort(getInt_());
        out.writeShort(getLuk());
        out.writeInt(getHp());
        out.writeInt(getMaxHP());
        out.writeInt(getMp());
        out.writeInt(getMaxMP());
        out.writeShort(getRemainingAp());
        encodeRemainingSp(out);
        out.writeLong(getExp());
        out.writeInt(getFame());
        out.writeInt(getWeaponPoint());
    }

    public void encodeRemainingSp(OutPacket out) {
        List<Integer> remainingSp = getRemainingSp();
        if (JobConstants.isExtendSpJob(getJob())) {
            out.write(getRemainingSpsSize());
            for (int i = 0; i < remainingSp.size(); i++) {
                if (remainingSp.get(i) > 0) {
                    out.write(i + 1);
                    out.writeInt(remainingSp.get(i));
                }
            }
        } else {
            out.writeShort(remainingSp.get(0));
        }
    }

    public int getRemainingSpsSize() {
        List<Integer> remainingSp = getRemainingSp();
        int i = 0;
        for (int sp : remainingSp) {
            if (sp > 0) {
                i++;
            }
        }
        return i;
    }


    public byte getNonCombatStatLevel(int totalExp) {
        byte level = 0;
        for (byte i = 0; i < 100; i++) {
            if (GameConstants.getTraitExpNeededForLevel(i) > totalExp) {
                level = (byte) (i - 1);
            }
        }
        return level;
    }


    public byte getCharismaLevel() {
        return getNonCombatStatLevel(getCharismaExp());
    }

    public byte getInsightLevel() {
        return getNonCombatStatLevel(getInsightExp());
    }

    public byte getWillLevel() {
        return getNonCombatStatLevel(getWillExp());
    }

    public byte getCraftLevel() {
        return getNonCombatStatLevel(getCraftExp());
    }

    public byte getSenseLevel() {
        return getNonCombatStatLevel(getSenseExp());
    }

    public byte getCharmLevel() {
        return getNonCombatStatLevel(getCharmExp());
    }
}
