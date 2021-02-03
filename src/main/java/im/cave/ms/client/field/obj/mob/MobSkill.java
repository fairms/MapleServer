package im.cave.ms.client.field.obj.mob;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj.mob
 * @date 2/2 16:54
 */
@Getter
@Setter
public class MobSkill {
    private int skillSN;
    private byte action;
    private int level;
    private int effectAfter;
    private int skillAfter;
    private byte priority;
    private boolean firstAttack;
    private boolean onlyFsm;
    private boolean onlyOtherSkill;
    private int skillForbid;
    private int afterDelay;
    private int fixDamR;
    private boolean doFirst;
    private int preSkillIndex;
    private int preSkillCount;
    private String info;
    private String text;
    private boolean afterDead;
    private int afterAttack = -1;
    private int afterAttackCount;
    private int castTime;
    private int coolTime;
    private int delay;
    private int useLimit;
    private String speak;
    private int skillID;
}
