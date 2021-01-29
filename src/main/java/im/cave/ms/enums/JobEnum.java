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
public enum JobEnum {
    BEGINNER(0, -1),
    WARRIOR(100, 0),
    FIGHTER(110, 100),
    CRUSADER(111, 110),
    HERO(112, 111),
    PAGE(120, 100),
    WHITE_KNIGHT(121, 120),
    PALADIN(122, 121),
    SPEARMAN(130, 100),
    DRAGON_KNIGHT(131, 130),
    DARK_KNIGHT(132, 131),
    MAGICIAN(200, 0),
    FP_WIZARD(210, 200),
    FP_MAGE(211, 210),
    FP_ARCHMAGE(212, 211),
    IL_WIZARD(220, 200),
    IL_MAGE(221, 220),
    IL_ARCHMAGE(222, 221),
    CLERIC(230, 200),
    PRIEST(231, 230),
    BISHOP(232, 231),
    BOWMAN(300, 0),
    HUNTER(310, 300),
    RANGER(311, 310),
    BOW_MASTER(312, 311),
    CROSS_BOWMAN(320, 300),
    SNIPER(321, 320),
    MARKSMAN(322, 321),
    PATH_FINDER1(301, -1),
    PATH_FINDER2(330, 301),
    PATH_FINDER3(331, 330),
    PATH_FINDER4(332, 331),
    THIEF(400, 0),
    ASSASSIN(410, 400),
    HERMIT(411, 411),
    NIGHT_LORD(412, 412),
    BANDIT(420, 400),
    CHIEF_BANDIT(421, 420),
    SHADOWER(422, 421),
    BLADE_RECRUIT(430, 400),
    BLADE_ACOLYTE(431, 430),
    BLADE_SPECIALIST(432, 431),
    BLADE_LORD(433, 432),
    BLADE_MASTER(434, 433),
    PIRATE(500, 0),
    PIRATE_CANNONNEER(501, -1),
    JETT1(508, -1),
    BRAWLER(510, 500),
    MARAUDER(511, 510),
    BUCCANEER(512, 511),
    GUNSLINGER(520, 500),
    OUTLAW(521, 520),
    CORSAIR(522, 521),
    CANNONEER(530, 501),
    CANNON_BLASTER(531, 530),
    CANNON_MASTER(532, 531),
    JETT2(570, 508),
    JETT3(571, 570),
    JETT4(572, 571),
    BRAWLER_NEW(580, -1), //deprecated
    MARAUDER_NEW(581, 580), //deprecated
    BUCCANEER_NEW(582, 581), //deprecated
    GUNSLINGER_NEW(590, -1), //deprecated
    OUTLAW_NEW(591, 590), //deprecated
    CORSAIR_NEW(592, 591), //deprecated
    MANAGER(800, -1),
    GM(900, -1),
    SUPER_GM(910, -1),
    NOBLESSE(1000, -1), //骑士团
    DAWNWARRIOR1(1100, 1000),
    DAWNWARRIOR2(1110, 1100),
    DAWNWARRIOR3(1111, 1110),
    DAWNWARRIOR4(1112, 1111),
    BLAZEWIZARD1(1200, 1000),
    BLAZEWIZARD2(1210, 1200),
    BLAZEWIZARD3(1211, 1210),
    BLAZEWIZARD4(1212, 1211),
    WINDARCHER1(1300, 1000),
    WINDARCHER2(1310, 1300),
    WINDARCHER3(1311, 1311),
    WINDARCHER4(1312, 1312),
    NIGHTWALKER1(1400, 1000),
    NIGHTWALKER2(1410, 1400),
    NIGHTWALKER3(1411, 1410),
    NIGHTWALKER4(1412, 1411),
    THUNDERBREAKER1(1500, 1000),
    THUNDERBREAKER2(1510, 1500),
    THUNDERBREAKER3(1511, 1510),
    THUNDERBREAKER4(1512, 1511),
    LEGEND(2000, -1), //传说中的技术
    EVAN_NOOB(2001, -1), //精通新手
    ARAN1(2100, 2000),
    ARAN2(2110, 2100),
    ARAN3(2111, 2111),
    ARAN4(2112, 2112),
    EVAN(2200, 2001),
    EVAN1(2210, 2200),
    EVAN2(2212, 2210),
    EVAN3(2214, 2212),
    EVAN4(2218, 2214),
    MERCEDES(2002, -1),
    MERCEDES1(2300, 2002),
    MERCEDES2(2310, 2300),
    MERCEDES3(2311, 2310),
    MERCEDES4(2312, 2311),
    PHANTOM(2003, -1),
    PHANTOM1(2400, 2003),
    PHANTOM2(2410, 2400),
    PHANTOM3(2411, 2410),
    PHANTOM4(2412, 2411),
    SHADE(2005, -1),
    SHADE1(2500, 2005),
    SHADE2(2510, 2500),
    SHADE3(2511, 2510),
    SHADE4(2512, 2511),
    LUMINOUS(2004, -1),
    LUMINOUS1(2700, 2004),
    LUMINOUS2(2710, 2700),
    LUMINOUS3(2711, 2710),
    LUMINOUS4(2712, 2711),
    CITIZEN(3000, -1),
    DEMON_SLAYER(3001, -1),
    XENON(3002, -1),
    DEMON_SLAYER1(3100, 3001),
    DEMON_SLAYER2(3110, 3100),
    DEMON_SLAYER3(3111, 3110),
    DEMON_SLAYER4(3112, 3111),
    DEMON_AVENGER1(3101, 3001),
    DEMON_AVENGER2(3120, 3101),
    DEMON_AVENGER3(3121, 3120),
    DEMON_AVENGER4(3122, 3121),
    BATTLE_MAGE_1(3200, 3000),
    BATTLE_MAGE_2(3210, 3200),
    BATTLE_MAGE_3(3211, 3210),
    BATTLE_MAGE_4(3212, 3211),
    WILD_HUNTER_1(3300, 3000),
    WILD_HUNTER_2(3310, 3300),
    WILD_HUNTER_3(3311, 3310),
    WILD_HUNTER_4(3312, 3311),
    MECHANIC_1(3500, 3000),
    MECHANIC_2(3510, 3500),
    MECHANIC_3(3511, 3510),
    MECHANIC_4(3512, 3511),
    BLASTER_1(3700, 3000),
    BLASTER_2(3710, 3700),
    BLASTER_3(3711, 3710),
    BLASTER_4(3712, 3711),
    XENON1(3600, 3002),
    XENON2(3610, 3600),
    XENON3(3611, 3610),
    XENON4(3612, 3611),
    HAYATO(4001, -1),
    KANNA(4002, -1),
    HAYATO1(4100, 4001),
    HAYATO2(4110, 4100),
    HAYATO3(4111, 4110),
    HAYATO4(4112, 4111),
    KANNA1(4200, 4002),
    KANNA2(4210, 4200),
    KANNA3(4211, 4210),
    KANNA4(4212, 4211),
    NAMELESS_WARDEN(5000, -1), //米哈哈
    MIHILE1(5100, 5000),
    MIHILE2(5110, 5100),
    MIHILE3(5111, 5110),
    MIHILE4(5112, 5111),
    KAISER(6000, -1),
    ANGELIC_BUSTER(6001, -1),
    CADENA(6002, -1),
    KAISER1(6100, 6000),
    KAISER2(6110, 6100),
    KAISER3(6111, 6110),
    KAISER4(6112, 6111),
    CADENA1(6400, 6002),
    CADENA2(6410, 6400),
    CADENA3(6411, 6410),
    CADENA4(6412, 6411),
    ANGELIC_BUSTER1(6500, 6001),
    ANGELIC_BUSTER2(6510, 6500),
    ANGELIC_BUSTER3(6511, 6510),
    ANGELIC_BUSTER4(6512, 6511),
    RIDE_SKILLS(8000, -1),
    ADDITIONAL_SKILLS(9000, -1),
    ZERO(10000, -1),
    ZERO1(10100, 10000),
    ZERO2(10110, 10100),
    ZERO3(10111, 10110),
    ZERO4(10112, 10111),
    BEAST_TAMER(11000, -1),
    BEAST_TAMER_1(11200, 11000),
    BEAST_TAMER_2(11210, 11200),
    BEAST_TAMER_3(11211, 11210),
    BEAST_TAMER_4(11212, 11211),
    PINK_BEAN_0(13000, -1),
    PINK_BEAN_1(13100, 13000),
    KINESIS_0(14000, -1),
    KINESIS_1(14200, 14000),
    KINESIS_2(14210, 14200),
    KINESIS_3(14211, 14210),
    KINESIS_4(14212, 14211),
    ILLIUM(15000, -1),
    ILLIUM1(15200, 15200),
    ILLIUM2(15210, 15210),
    ILLIUM3(15211, 15211),
    ILLIUM4(15212, 15212),
    ARK(15001, -1),
    ARK1(15500, 15001),
    ARK2(15510, 15500),
    ARK3(15511, 15510),
    ARK4(15512, 15511),
    HOYOUNG(16000, -1),
    HOYOUNG1(16400, 16000),
    HOYOUNG2(16410, 16400),
    HOYOUNG3(16411, 16410),
    HOYOUNG4(16412, 16411),
    ADELE(15002, -1),
    ADELE1(15100, 15002),
    ADELE2(15110, 15100),
    ADELE3(15111, 15110),
    ADELE4(15112, 15111),
    EMPTY_0(30000, -1),
    V_SKILLS(40000, -1),
    EMPTY_2(40001, -1),
    EMPTY_3(40002, -1),
    EMPTY_4(40003, -1),
    EMPTY_5(40004, -1),
    EMPTY_6(40005, -1),
    PINK_BEAN_EMPTY_0(800000, 13000),
    PINK_BEAN_EMPTY_1(800001, 800000),
    PINK_BEAN_EMPTY_2(800002, 800001),
    PINK_BEAN_EMPTY_3(800003, 800002),
    PINK_BEAN_EMPTY_4(800004, 800003),
    PINK_BEAN_EMPTY_5(800010, 800004),
    PINK_BEAN_EMPTY_6(800011, 800010),
    PINK_BEAN_EMPTY_7(800012, 800011),
    PINK_BEAN_EMPTY_8(800013, 800012),
    PINK_BEAN_EMPTY_9(800014, 800013),
    PINK_BEAN_EMPTY_10(800015, 800014),
    PINK_BEAN_EMPTY_11(800016, 800015),
    PINK_BEAN_EMPTY_12(800017, 800016),
    PINK_BEAN_EMPTY_13(800018, 800017),
    PINK_BEAN_EMPTY_14(800019, 800018),
    PINK_BEAN_EMPTY_15(800022, 800019);

    private final short jobId;
    private final short prevJobId;
    private final String name;

    JobEnum(short jobId, short prevJobId, String name) {
        this.jobId = jobId;
        this.prevJobId = prevJobId;
        this.name = name;
    }

    JobEnum(int jobId, int prevJobId) {
        this((short) jobId, (short) prevJobId, "");
    }


    public static JobEnum getBeginnerJob(int jobId) {
        JobEnum job = getJobById((short) jobId);
        do {
            JobEnum finalJob = job;
            job = Util.findWithPred(values(), jobEnum -> finalJob.getPrevJob() == jobEnum.getJob());
        } while (job.getPrevJob() != -1);
        return job;
    }

    public static List<JobEnum> getAdvancedJobs(int jobId) {
        return Arrays.stream(values()).filter(jobEnum -> jobEnum.getPrevJob() == jobId).collect(Collectors.toList());
    }

    public static JobEnum getJobById(short id) {
        return Arrays.stream(JobEnum.values()).filter(j -> j.getJob() == id).findAny().orElse(null);
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
    public boolean isAdvancedJobOf(JobEnum job) {
        return getJob() >= job.getJob();
    }
}
