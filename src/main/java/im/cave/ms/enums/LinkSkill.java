package im.cave.ms.enums;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 2/1 11:24
 */
public enum LinkSkill {
    WARRIOR(0, JobEnum.WARRIOR),
    MAGICIAN(1, JobEnum.MAGICIAN),
    BOWMAN(2, JobEnum.BOWMAN),
    THIEF(3, JobEnum.THIEF),
    PIRATE(4, JobEnum.PIRATE),
    JETT(80011964, JobEnum.JETT1),
    DEMON(6, JobEnum.NOBLESSE),
    ARAN1(80000370, JobEnum.ARAN1),
    EVAN(80000369, JobEnum.EVAN),
    MERCEDES(9, JobEnum.MERCEDES),
    PHANTOM_INSTINCT(80000002, JobEnum.PHANTOM),
    SHADE(11, JobEnum.SHADE),
    LIGHT_WASH(80000005, JobEnum.LUMINOUS),
    RHINNE_BLESSING(80000110, JobEnum.ZERO),
    CITIZEN(13, JobEnum.CITIZEN),
    FURY_UNLEASHED(80000001, JobEnum.DEMON_SLAYER1),
    HYBRID_LOGIC(80000047, JobEnum.XENON),
    WILD_RAGE(80000050, JobEnum.DEMON_AVENGER1),
    KEEN_EDGE(18, JobEnum.HAYATO),
    ELEMENTALISM(80000004, JobEnum.KANNA),
    IRON_WILL(80000006, JobEnum.KAISER),
    JUDGMENT(80000188, JobEnum.KINESIS_0),
    CADENA(23, JobEnum.CADENA),
    ILLIUM(24, JobEnum.ILLIUM),
    ARK(25, JobEnum.ARK),
    HOYOUNG(80000609, JobEnum.HOYOUNG),
    ADELE(28, JobEnum.ADELE);

    private int skillId;
    private JobEnum job;

    LinkSkill(int skillId, JobEnum job) {
        this.skillId = skillId;
        this.job = job;
    }

    public static LinkSkill getLinkSkillByJob(short job) {
        return Arrays.stream(values()).filter(linkSkill -> JobEnum.getJobById(job).isAdvancedJobOf(linkSkill.job)).findAny().orElse(null);
    }

    public JobEnum getJob() {
        return job;
    }

    public void setJob(JobEnum job) {
        this.job = job;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
