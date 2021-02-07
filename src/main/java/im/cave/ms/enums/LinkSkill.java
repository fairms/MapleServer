package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 2/1 11:24
 */
public enum LinkSkill {
    WARRIOR(0, JobType.WARRIOR),
    MAGICIAN(1, JobType.MAGICIAN),
    BOWMAN(2, JobType.BOWMAN),
    THIEF(3, JobType.THIEF),
    PIRATE(4, JobType.PIRATE),
    JETT(80011964, JobType.JETT1),
    DEMON(6, JobType.NOBLESSE),
    ARAN1(80000370, JobType.ARAN1),
    EVAN(80000369, JobType.EVAN),
    MERCEDES(9, JobType.MERCEDES),
    PHANTOM_INSTINCT(80000002, JobType.PHANTOM),
    SHADE(11, JobType.SHADE),
    LIGHT_WASH(80000005, JobType.LUMINOUS),
    RHINNE_BLESSING(80000110, JobType.ZERO),
    CITIZEN(13, JobType.CITIZEN),
    FURY_UNLEASHED(80000001, JobType.DEMON_SLAYER1),
    HYBRID_LOGIC(80000047, JobType.XENON),
    WILD_RAGE(80000050, JobType.DEMON_AVENGER1),
    KEEN_EDGE(18, JobType.HAYATO),
    ELEMENTALISM(80000004, JobType.KANNA),
    IRON_WILL(80000006, JobType.KAISER),
    JUDGMENT(80000188, JobType.KINESIS_0),
    CADENA(23, JobType.CADENA),
    ILLIUM(24, JobType.ILLIUM),
    ARK(25, JobType.ARK),
    HOYOUNG(80000609, JobType.HOYOUNG),
    ADELE(28, JobType.ADELE);

    private int skillId;
    private JobType job;

    LinkSkill(int skillId, JobType job) {
        this.skillId = skillId;
        this.job = job;
    }

    public static LinkSkill getLinkSkillByJob(short job) {
        return Arrays.stream(values()).filter(linkSkill -> JobType.getJobById(job).isAdvancedJobOf(linkSkill.job)).findAny().orElse(null);
    }

    public JobType getJob() {
        return job;
    }

    public void setJob(JobType job) {
        this.job = job;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
