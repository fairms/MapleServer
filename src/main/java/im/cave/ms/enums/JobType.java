package im.cave.ms.enums;

import im.cave.ms.tools.Util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 1/25 9:44
 */
public enum JobType {
    BEGINNER(0, -1, "新手"),
    WARRIOR(100, 0, "战士"),
    FIGHTER(110, 100, "剑客"),
    CRUSADER(111, 110, "勇士"),
    HERO(112, 111, "英雄"),
    PAGE(120, 100, "准骑士"),
    WHITE_KNIGHT(121, 120, "骑士"),
    PALADIN(122, 121, "圣骑士"),
    SPEARMAN(130, 100, "枪战士"),
    DRAGON_KNIGHT(131, 130, "龙骑士"),
    DARK_KNIGHT(132, 131, "黑骑士"),
    MAGICIAN(200, 0, "魔法师"),
    FP_WIZARD(210, 200, "法师(火,毒)"),
    FP_MAGE(211, 210, "魔导师(火,毒)"),
    FP_ARCHMAGE(212, 211, "巫师(火,毒)"),
    IL_WIZARD(220, 200, "法师(冰,雷)"),
    IL_MAGE(221, 220, "魔导师(冰,雷)"),
    IL_ARCHMAGE(222, 221, "巫师(冰,雷)"),
    CLERIC(230, 200, "牧师"),
    PRIEST(231, 230, "祭司"),
    BISHOP(232, 231, "主教"),
    BOWMAN(300, 0, "弓箭手"),
    HUNTER(310, 300, "猎人"),
    RANGER(311, 310, "射手"),
    BOW_MASTER(312, 311, "神射手"),
    CROSS_BOWMAN(320, 300, "弩弓手"),
    SNIPER(321, 320, "游侠"),
    MARKSMAN(322, 321, "箭神"),
    PATH_FINDER1(301, -1, "古迹猎人"),
    PATH_FINDER2(330, 301, "古迹猎人"),
    PATH_FINDER3(331, 330, "古迹猎人"),
    PATH_FINDER4(332, 331, "古迹猎人"),
    THIEF(400, 0, "飞侠"),
    ASSASSIN(410, 400, "刺客"),
    HERMIT(411, 411, "无影人"),
    NIGHT_LORD(412, 412, "隐士"),
    BANDIT(420, 400, "侠客"),
    CHIEF_BANDIT(421, 420, "独行侠"),
    SHADOWER(422, 421, "侠盗"),
    BLADE_RECRUIT(430, 400, "见习刀客"),
    BLADE_ACOLYTE(431, 430, "双刀客"),
    BLADE_SPECIALIST(432, 431, "双刀侠"),
    BLADE_LORD(433, 432, "血刀"),
    BLADE_MASTER(434, 433, "暗影双刀"),
    PIRATE(500, 0, "海盗"),
    PIRATE_CANNONNEER(501, -1, "火炮手"),
    JETT1(508, -1, "龙的传人(1转)"),
    BRAWLER(510, 500, "拳手"),
    MARAUDER(511, 510, "斗士"),
    BUCCANEER(512, 511, "冲锋队长"),
    GUNSLINGER(520, 500, "火枪手"),
    OUTLAW(521, 520, "大副"),
    CORSAIR(522, 521, "船长"),
    CANNONEER(530, 501, "火炮手"),
    CANNON_BLASTER(531, 530, "毁灭炮手"),
    CANNON_MASTER(532, 531, "神炮王"),
    JETT2(570, 508, "龙的传人"),
    JETT3(571, 570, "龙的传人"),
    JETT4(572, 571, "龙的传人"),
    BRAWLER_NEW(580, -1), //deprecated
    MARAUDER_NEW(581, 580), //deprecated
    BUCCANEER_NEW(582, 581), //deprecated
    GUNSLINGER_NEW(590, -1), //deprecated
    OUTLAW_NEW(591, 590), //deprecated
    CORSAIR_NEW(592, 591), //deprecated
    MANAGER(800, -1, "巡检员"),
    GM(900, -1, "管理员"),
    SUPER_GM(910, -1, "超级管理员"),
    NOBLESSE(1000, -1, "初心者"),
    DAWNWARRIOR1(1100, 1000, "魂骑士"),
    DAWNWARRIOR2(1110, 1100, "魂骑士"),
    DAWNWARRIOR3(1111, 1110, "魂骑士"),
    DAWNWARRIOR4(1112, 1111, "魂骑士"),
    BLAZEWIZARD1(1200, 1000, "炎术士"),
    BLAZEWIZARD2(1210, 1200, "炎术士"),
    BLAZEWIZARD3(1211, 1210, "炎术士"),
    BLAZEWIZARD4(1212, 1211, "炎术士"),
    WINDARCHER1(1300, 1000, "风灵使者"),
    WINDARCHER2(1310, 1300, "风灵使者"),
    WINDARCHER3(1311, 1311, "风灵使者"),
    WINDARCHER4(1312, 1312, "风灵使者"),
    NIGHTWALKER1(1400, 1000, "夜行者"),
    NIGHTWALKER2(1410, 1400, "夜行者"),
    NIGHTWALKER3(1411, 1410, "夜行者"),
    NIGHTWALKER4(1412, 1411, "夜行者"),
    THUNDERBREAKER1(1500, 1000, "奇袭者"),
    THUNDERBREAKER2(1510, 1500, "奇袭者"),
    THUNDERBREAKER3(1511, 1510, "奇袭者"),
    THUNDERBREAKER4(1512, 1511, "奇袭者"),
    LEGEND(2000, -1, "战童"),
    EVAN_NOOB(2001, -1, "龙神"),
    ARAN1(2100, 2000, "战神"),
    ARAN2(2110, 2100, "战神"),
    ARAN3(2111, 2111, "战神"),
    ARAN4(2112, 2112, "战神"),
    EVAN(2200, 2001, "龙神"),
    EVAN1(2210, 2200, "龙神"),
    EVAN2(2212, 2210, "龙神"),
    EVAN3(2214, 2212, "龙神"),
    EVAN4(2218, 2214, "龙神"),
    MERCEDES(2002, -1, "双弩精灵"),
    MERCEDES1(2300, 2002, "双弩精灵"),
    MERCEDES2(2310, 2300, "双弩精灵"),
    MERCEDES3(2311, 2310, "双弩精灵"),
    MERCEDES4(2312, 2311, "双弩精灵"),
    PHANTOM(2003, -1, "幻影"),
    PHANTOM1(2400, 2003, "幻影"),
    PHANTOM2(2410, 2400, "幻影"),
    PHANTOM3(2411, 2410, "幻影"),
    PHANTOM4(2412, 2411, "幻影"),
    SHADE(2005, -1, "隐月"),
    SHADE1(2500, 2005, "隐月"),
    SHADE2(2510, 2500, "隐月"),
    SHADE3(2511, 2510, "隐月"),
    SHADE4(2512, 2511, "隐月"),
    LUMINOUS(2004, -1, "夜光法师"),
    LUMINOUS1(2700, 2004, "夜光法师"),
    LUMINOUS2(2710, 2700, "夜光法师"),
    LUMINOUS3(2711, 2710, "夜光法师"),
    LUMINOUS4(2712, 2711, "夜光法师"),
    CITIZEN(3000, -1, "预备兵"),
    DEMON_SLAYER(3001, -1, "恶魔"),
    XENON(3002, -1, "尖兵"),
    DEMON_SLAYER1(3100, 3001, "恶魔猎手"),
    DEMON_SLAYER2(3110, 3100, "恶魔猎手"),
    DEMON_SLAYER3(3111, 3110, "恶魔猎手"),
    DEMON_SLAYER4(3112, 3111, "恶魔猎手"),
    DEMON_AVENGER1(3101, 3001, "恶魔复仇者"),
    DEMON_AVENGER2(3120, 3101, "恶魔复仇者"),
    DEMON_AVENGER3(3121, 3120, "恶魔复仇者"),
    DEMON_AVENGER4(3122, 3121, "恶魔复仇者"),
    BATTLE_MAGE_1(3200, 3000, "幻灵斗师"),
    BATTLE_MAGE_2(3210, 3200, "幻灵斗师"),
    BATTLE_MAGE_3(3211, 3210, "幻灵斗师"),
    BATTLE_MAGE_4(3212, 3211, "幻灵斗师"),
    WILD_HUNTER_1(3300, 3000, "豹弩游侠"),
    WILD_HUNTER_2(3310, 3300, "豹弩游侠"),
    WILD_HUNTER_3(3311, 3310, "豹弩游侠"),
    WILD_HUNTER_4(3312, 3311, "豹弩游侠"),
    MECHANIC_1(3500, 3000, "机械师"),
    MECHANIC_2(3510, 3500, "机械师"),
    MECHANIC_3(3511, 3510, "机械师"),
    MECHANIC_4(3512, 3511, "机械师"),
    BLASTER_1(3700, 3000, "爆破手"),
    BLASTER_2(3710, 3700, "爆破手"),
    BLASTER_3(3711, 3710, "爆破手"),
    BLASTER_4(3712, 3711, "爆破手"),
    XENON1(3600, 3002, "尖兵"),
    XENON2(3610, 3600, "尖兵"),
    XENON3(3611, 3610, "尖兵"),
    XENON4(3612, 3611, "尖兵"),
    HAYATO(4001, -1, "剑豪"),
    KANNA(4002, -1, "阴阳师"),
    HAYATO1(4100, 4001, "剑豪"),
    HAYATO2(4110, 4100, "剑豪"),
    HAYATO3(4111, 4110, "剑豪"),
    HAYATO4(4112, 4111, "剑豪"),
    KANNA1(4200, 4002, "阴阳师"),
    KANNA2(4210, 4200, "阴阳师"),
    KANNA3(4211, 4210, "阴阳师"),
    KANNA4(4212, 4211, "阴阳师"),
    NAMELESS_WARDEN(5000, -1, "无名少年"),
    MIHILE1(5100, 5000, "米哈尔"),
    MIHILE2(5110, 5100, "米哈尔"),
    MIHILE3(5111, 5110, "米哈尔"),
    MIHILE4(5112, 5111, "米哈尔"),
    KAISER(6000, -1, "狂龙战士"),
    ANGELIC_BUSTER(6001, -1, "爆莉萌天使"),
    CADENA(6002, -1, "魔链影士"),
    KAISER1(6100, 6000, "狂龙战士"),
    KAISER2(6110, 6100, "狂龙战士"),
    KAISER3(6111, 6110, "狂龙战士"),
    KAISER4(6112, 6111, "狂龙战士"),
    CADENA1(6400, 6002, "魔链影士"),
    CADENA2(6410, 6400, "魔链影士"),
    CADENA3(6411, 6410, "魔链影士"),
    CADENA4(6412, 6411, "魔链影士"),
    ANGELIC_BUSTER1(6500, 6001, "爆莉萌天使"),
    ANGELIC_BUSTER2(6510, 6500, "爆莉萌天使"),
    ANGELIC_BUSTER3(6511, 6510, "爆莉萌天使"),
    ANGELIC_BUSTER4(6512, 6511, "爆莉萌天使"),
    RIDE_SKILLS(8000, -1),
    ADDITIONAL_SKILLS(9000, -1),
    ZERO(10000, -1, "神之子"),
    ZERO1(10100, 10000, "神之子"),
    ZERO2(10110, 10100, "神之子"),
    ZERO3(10111, 10110, "神之子"),
    ZERO4(10112, 10111, "神之子"),
    BEAST_TAMER(11000, -1, "林之灵"),
    BEAST_TAMER_1(11200, 11000, "林之灵"),
    BEAST_TAMER_2(11210, 11200, "林之灵"),
    BEAST_TAMER_3(11211, 11210, "林之灵"),
    BEAST_TAMER_4(11212, 11211, "林之灵"),
    PINK_BEAN_0(13000, -1, "品克缤"),
    PINK_BEAN_1(13100, 13000, "品克缤"),
    KINESIS_0(14000, -1, "超能力者"),
    KINESIS_1(14200, 14000, "超能力者"),
    KINESIS_2(14210, 14200, "超能力者"),
    KINESIS_3(14211, 14210, "超能力者"),
    KINESIS_4(14212, 14211, "超能力者"),
    ILLIUM(15000, -1, "圣晶使徒"),
    ILLIUM1(15200, 15200, "圣晶使徒"),
    ILLIUM2(15210, 15210, "圣晶使徒"),
    ILLIUM3(15211, 15211, "圣晶使徒"),
    ILLIUM4(15212, 15212, "圣晶使徒"),
    ARK(15001, -1, "影魂异人"),
    ARK1(15500, 15001, "影魂异人"),
    ARK2(15510, 15500, "影魂异人"),
    ARK3(15511, 15510, "影魂异人"),
    ARK4(15512, 15511, "影魂异人"),
    HOYOUNG(16000, -1, "虎影"),
    HOYOUNG1(16400, 16000, "虎影"),
    HOYOUNG2(16410, 16400, "虎影"),
    HOYOUNG3(16411, 16410, "虎影"),
    HOYOUNG4(16412, 16411, "虎影"),
    ADELE(15002, -1, "阿黛尔"),
    ADELE1(15100, 15002, "阿黛尔"),
    ADELE2(15110, 15100, "阿黛尔"),
    ADELE3(15111, 15110, "阿黛尔"),
    ADELE4(15112, 15111, "阿黛尔"),
    EMPTY_0(30000, -1),
    V_SKILLS(40000, -1),
    EMPTY_2(40001, -1),
    EMPTY_3(40002, -1),
    EMPTY_4(40003, -1),
    EMPTY_5(40004, -1),
    EMPTY_6(40005, -1),
    PINK_BEAN_EMPTY_0(800000, 13000, "品克缤"),
    PINK_BEAN_EMPTY_1(800001, 800000, "品克缤"),
    PINK_BEAN_EMPTY_2(800002, 800001, "品克缤"),
    PINK_BEAN_EMPTY_3(800003, 800002, "品克缤"),
    PINK_BEAN_EMPTY_4(800004, 800003, "品克缤"),
    PINK_BEAN_EMPTY_5(800010, 800004, "品克缤"),
    PINK_BEAN_EMPTY_6(800011, 800010, "品克缤"),
    PINK_BEAN_EMPTY_7(800012, 800011, "品克缤"),
    PINK_BEAN_EMPTY_8(800013, 800012, "品克缤"),
    PINK_BEAN_EMPTY_9(800014, 800013, "品克缤"),
    PINK_BEAN_EMPTY_10(800015, 800014, "品克缤"),
    PINK_BEAN_EMPTY_11(800016, 800015, "品克缤"),
    PINK_BEAN_EMPTY_12(800017, 800016, "品克缤"),
    PINK_BEAN_EMPTY_13(800018, 800017, "品克缤"),
    PINK_BEAN_EMPTY_14(800019, 800018, "品克缤"),
    PINK_BEAN_EMPTY_15(800022, 800019, "品克缤");

    private final short jobId;
    private final short prevJobId;
    private final String name;

    JobType(int jobId, int prevJobId, String name) {
        this.jobId = (short) jobId;
        this.prevJobId = (short) prevJobId;
        this.name = name;
    }

    JobType(int jobId, int prevJobId) {
        this((short) jobId, (short) prevJobId, "");
    }


    public static JobType getBeginnerJob(int jobId) {
        JobType job = getJobById((short) jobId);
        do {
            JobType finalJob = job;
            job = Util.findWithPred(values(), jobEnum -> finalJob.getPrevJob() == jobEnum.getJob());
        } while (job.getPrevJob() != -1);
        return job;
    }

    public static List<JobType> getAdvancedJobs(int jobId) {
        return Arrays.stream(values()).filter(jobEnum -> jobEnum.getPrevJob() == jobId).collect(Collectors.toList());
    }

    public static JobType getJobById(short id) {
        return Arrays.stream(JobType.values()).filter(j -> j.getJob() == id).findAny().orElse(null);
    }

    public short getJob() {
        return jobId;
    }

    public short getPrevJob() {
        return prevJobId;
    }

    public String getName() {
        return name;
    }

    //todo check job class
    public boolean isAdvancedJobOf(JobType job) {
        return getJob() >= job.getJob();
    }
}
