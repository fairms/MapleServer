package im.cave.ms.client.character;

import lombok.Data;

import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Table;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 11/23 12:57
 */
@Data
@Entity
@Table(name = "charstats")
public class CharStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
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
    private int prestige = 0;
    private int fatigue = 0;
    private int charismaExp;
    private int insightExp;
    private int willExp;
    private int craftExp;
    private int senseExp;
    private int charmExp;
    private int weaponPoint = 0;
}
