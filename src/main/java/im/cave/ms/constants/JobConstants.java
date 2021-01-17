package im.cave.ms.constants;


import java.util.Arrays;

/**
 * @author Itzik
 */
public class JobConstants {

    public static final boolean enableJobs = true;
    public static final int jobOrder = 2;

    public static boolean isXenon(short jobId) {
        return jobId / 100 == 36 || jobId == 3002;
    }

    public static boolean isBeastTamer(short job) {
        return job / 1000 == 11;
    }

    public static boolean isPinkBean(short job) {
        return job == JobEnum.PINK_BEAN_0.getJob() || job == JobEnum.PINK_BEAN_1.getJob();
    }

    public static JobEnum getJobEnumById(short jobId) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJob() == jobId)
                .findFirst().orElse(null);
    }

    public static boolean isWildHunter(short job) {
        return job / 100 == 33;
    }

    public static boolean isAngelicBuster(int id) {
        return id == JobConstants.JobEnum.ANGELIC_BUSTER.getJob() || id / 100 == 65;
    }

    public static boolean isBlazeWizard(short job) {
        return job / 100 == 12;
    }

    public static boolean isDawnWarrior(short job) {
        return job / 100 == 11;
    }

    public static boolean isShadower(short job) {
        return job / 10 == 42;
    }

    public static boolean isNightLord(short job) {
        return job / 10 == 41;
    }

    public static boolean isDualBlade(short job) {
        return job / 10 == 43;
    }

    public static boolean isHero(short job) {
        return job / 10 == 11;
    }

    public static boolean isPage(short job) {
        return job / 10 == 12;
    }

    public static boolean isDarkKnight(short job) {
        return job / 10 == 13;
    }

    public static boolean isFirePoison(short job) {
        return job / 10 == 21;
    }

    public static boolean isIceLightning(short job) {
        return job / 10 == 22;
    }

    public static boolean isCleric(short job) {
        return job / 10 == 23;
    }

    public static boolean isBuccaneer(short job) {
        return job / 10 == 51;
    }

    public static boolean isCorsair(short job) {
        return job / 10 == 52;
    }

    public static boolean isJett(short job) {
        return job / 10 == 57 || job == JobEnum.JETT1.getJob();
    }

    public static boolean isDemonSlayer(short job) {
        return job / 10 == 311;
    }

    public static boolean isDemonAvenger(short job) {
        return job / 10 == 312 || job == 3101;
    }

    public static boolean isKanna(short id) {
        return id == JobConstants.JobEnum.KANNA.getJob() || id / 100 == 42;
    }

    public static boolean isHayato(short id) {
        return id == JobConstants.JobEnum.HAYATO.getJob() || id / 100 == 41;
    }

    public static boolean isNightWalker(short id) {
        return id / 100 == 14;
    }

    public static boolean isThunderBreaker(short id) {
        return id / 100 == 15;
    }

    public static boolean isBlaster(short id) {
        return id / 100 == 37;
    }

    public static boolean isShade(short id) {
        return id == JobEnum.SHADE.getJob() || id / 100 == 25;
    }

    public static boolean isWindArcher(short id) {
        return id / 100 == 13;
    }

    public static boolean isMihile(short id) {
        return id / 100 == 51 || id == 5000;
    }

    public static double getDamageConstant(short job) {
        // get_job_damage_const
        if (job > 222) {
            if (job > 1200) {
                if (job >= 1210 && job <= 1212)
                    return 0.2;
            } else if (job == 1200 || job >= 230 && job <= 232) {
                return 0.2;
            }
            return 0.0;
        }
        if (job < 220) {
            switch (job) {
                case 110:
                case 111:
                case 112:
                    return 0.1;
                case 200:
                case 210:
                case 211:
                case 212:
                    return 0.2;
                default:
                    return 0.0;
            }
        }
        return 0.2;
    }

    public static int getJobCategory(short job) {
        int res = 0;
        switch (job / 100) {
            case 27:
            case 140:
            case 142:
                res = 2;
                break;
            case 36:
                res = 4;
                break;
            case 37:
                res = 1;
                break;
            default:
                res = job % 1000 / 100;
        }
        return res;
    }

    public static byte getJobLevelByZeroSkillID(int skillID) {
        int prefix = (skillID % 1000) / 100;
        return (byte) (prefix == 1 ? 2
                : prefix == 2 ? 1
                : 3);
    }

    public static boolean isMechanic(short id) {
        return id >= JobConstants.JobEnum.MECHANIC_1.getJob() && id <= JobConstants.JobEnum.MECHANIC_4.getJob();
    }

    public static boolean isBattleMage(short id) {
        return id >= JobConstants.JobEnum.BATTLE_MAGE_1.getJob() && id <= JobConstants.JobEnum.BATTLE_MAGE_4.getJob();
    }

    public static boolean isGmJob(short id) {
        return isGm(id) || isSuperGm(id);
    }

    public static boolean isGm(short id) {
        return id == JobEnum.GM.getJob();
    }

    public static boolean isSuperGm(short id) {
        return id == JobEnum.SUPER_GM.getJob();
    }

    public enum JobEnum {
        BEGINNER(0, 0),
        WARRIOR(100, 0),
        FIGHTER(110, 0),
        CRUSADER(111, 0),
        HERO(112, 0),
        PAGE(120, 0),
        WHITE_KNIGHT(121, 0),
        PALADIN(122, 0),
        SPEARMAN(130, 0),
        DRAGON_KNIGHT(131, 0),
        DARK_KNIGHT(132, 0),
        MAGICIAN(200, 0),
        FP_WIZARD(210, 0),
        FP_MAGE(211, 0),
        FP_ARCHMAGE(212, 0),
        IL_WIZARD(220, 0),
        IL_MAGE(221, 0),
        IL_ARCHMAGE(222, 0),
        CLERIC(230, 0),
        PRIEST(231, 0),
        BISHOP(232, 0),
        BOWMAN(300, 0),
        HUNTER(310, 0),
        RANGER(311, 0),
        BOW_MASTER(312, 0),
        CROSS_BOWMAN(320, 0),
        SNIPER(321, 0),
        MARKSMAN(322, 0),
        PATH_FINDER1(301, 0),
        PATH_FINDER2(330, 0),
        PATH_FINDER3(331, 0),
        PATH_FINDER4(332, 0),
        THIEF(400, 0),
        ASSASSIN(410, 0),
        HERMIT(411, 0),
        NIGHT_LORD(412, 0),
        BANDIT(420, 0),
        CHIEF_BANDIT(421, 0),
        SHADOWER(422, 0),
        BLADE_RECRUIT(430, 0),
        BLADE_ACOLYTE(431, 0),
        BLADE_SPECIALIST(432, 0),
        BLADE_LORD(433, 0),
        BLADE_MASTER(434, 0),
        PIRATE(500, 0),
        PIRATE_CANNONNEER(501, 0),
        JETT1(508, 0),
        BRAWLER(510, 0),
        MARAUDER(511, 0),
        BUCCANEER(512, 0),
        GUNSLINGER(520, 0),
        OUTLAW(521, 0),
        CORSAIR(522, 0),
        CANNONEER(530, 0),
        CANNON_BLASTER(531, 0),
        CANNON_MASTER(532, 0),
        JETT2(570, 0),
        JETT3(571, 0),
        JETT4(572, 0),
        BRAWLER_NEW(580, 0),
        MARAUDER_NEW(581, 0),
        BUCCANEER_NEW(582, 0),
        GUNSLINGER_NEW(590, 0),
        OUTLAW_NEW(591, 0),
        CORSAIR_NEW(592, 0),
        MANAGER(800, 0),
        GM(900, 0),
        SUPER_GM(910, 0),
        NOBLESSE(1000, 1000),
        DAWNWARRIOR1(1100, 1000),
        DAWNWARRIOR2(1110, 1000),
        DAWNWARRIOR3(1111, 1000),
        DAWNWARRIOR4(1112, 1000),
        BLAZEWIZARD1(1200, 1000),
        BLAZEWIZARD2(1210, 1000),
        BLAZEWIZARD3(1211, 1000),
        BLAZEWIZARD4(1212, 1000),
        WINDARCHER1(1300, 1000),
        WINDARCHER2(1310, 1000),
        WINDARCHER3(1311, 1000),
        WINDARCHER4(1312, 1000),
        NIGHTWALKER1(1400, 1000),
        NIGHTWALKER2(1410, 1000),
        NIGHTWALKER3(1411, 1000),
        NIGHTWALKER4(1412, 1000),
        THUNDERBREAKER1(1500, 1000),
        THUNDERBREAKER2(1510, 1000),
        THUNDERBREAKER3(1511, 1000),
        THUNDERBREAKER4(1512, 1000),
        LEGEND(2000, 2000),
        EVAN_NOOB(2001, 2001),
        ARAN1(2100, 2000),
        ARAN2(2110, 2000),
        ARAN3(2111, 2000),
        ARAN4(2112, 2000),
        EVAN(2200, 2001),
        EVAN1(2210, 2001),
        EVAN2(2212, 2001),
        EVAN3(2214, 2001),
        EVAN4(2218, 2001),
        MERCEDES(2002, 2002),
        MERCEDES1(2300, 2002),
        MERCEDES2(2310, 2002),
        MERCEDES3(2311, 2002),
        MERCEDES4(2312, 2002),
        PHANTOM(2003, 2003),
        PHANTOM1(2400, 2003),
        PHANTOM2(2410, 2003),
        PHANTOM3(2411, 2003),
        PHANTOM4(2412, 2003),
        SHADE(2005, 2005),
        SHADE1(2500, 2005),
        SHADE2(2510, 2005),
        SHADE3(2511, 2005),
        SHADE4(2512, 2005),
        LUMINOUS(2004, 2004),
        LUMINOUS1(2700, 2004),
        LUMINOUS2(2710, 2004),
        LUMINOUS3(2711, 2004),
        LUMINOUS4(2712, 2004),
        CITIZEN(3000, 3000),
        DEMON_SLAYER(3001, 3001),
        XENON(3002, 3002),
        DEMON_SLAYER1(3100, 3001),
        DEMON_SLAYER2(3110, 3001),
        DEMON_SLAYER3(3111, 3001),
        DEMON_SLAYER4(3112, 3001),
        DEMON_AVENGER1(3101, 3001),
        DEMON_AVENGER2(3120, 3001),
        DEMON_AVENGER3(3121, 3001),
        DEMON_AVENGER4(3122, 3001),
        BATTLE_MAGE_1(3200, 3000),
        BATTLE_MAGE_2(3210, 3000),
        BATTLE_MAGE_3(3211, 3000),
        BATTLE_MAGE_4(3212, 3000),
        WILD_HUNTER_1(3300, 3000),
        WILD_HUNTER_2(3310, 3000),
        WILD_HUNTER_3(3311, 3000),
        WILD_HUNTER_4(3312, 3000),
        MECHANIC_1(3500, 3000),
        MECHANIC_2(3510, 3000),
        MECHANIC_3(3511, 3000),
        MECHANIC_4(3512, 3000),
        BLASTER_1(3700, 3000),
        BLASTER_2(3710, 3000),
        BLASTER_3(3711, 3000),
        BLASTER_4(3712, 3000),
        XENON1(3600, 3002),
        XENON2(3610, 3002),
        XENON3(3611, 3002),
        XENON4(3612, 3002),
        HAYATO(4001, 4001),
        KANNA(4002, 4002),
        HAYATO1(4100, 4001),
        HAYATO2(4110, 4001),
        HAYATO3(4111, 4001),
        HAYATO4(4112, 4001),
        KANNA1(4200, 4002),
        KANNA2(4210, 4002),
        KANNA3(4211, 4002),
        KANNA4(4212, 4002),
        NAMELESS_WARDEN(5000, 5000),
        MIHILE1(5100, 5000),
        MIHILE2(5110, 5000),
        MIHILE3(5111, 5000),
        MIHILE4(5112, 5000),
        KAISER(6000, 6000),
        ANGELIC_BUSTER(6001, 6001),
        CADENA(6002, 6002),
        KAISER1(6100, 6000),
        KAISER2(6110, 6000),
        KAISER3(6111, 6000),
        KAISER4(6112, 6000),
        CADENA1(6400, 6002),
        CADENA2(6410, 6002),
        CADENA3(6411, 6002),
        CADENA4(6412, 6002),
        ANGELIC_BUSTER1(6500, 6001),
        ANGELIC_BUSTER2(6510, 6001),
        ANGELIC_BUSTER3(6511, 6001),
        ANGELIC_BUSTER4(6512, 6001),
        RIDE_SKILLS(8000, 0),
        ADDITIONAL_SKILLS(9000, 0),
        ZERO(10000, 10000),
        ZERO1(10100, 10000),
        ZERO2(10110, 10000),
        ZERO3(10111, 10000),
        ZERO4(10112, 10000),
        BEAST_TAMER(11000, 11000),
        BEAST_TAMER_1(11200, 11000),
        BEAST_TAMER_2(11210, 11000),
        BEAST_TAMER_3(11211, 11000),
        BEAST_TAMER_4(11212, 11000),
        PINK_BEAN_0(13000, 13000),
        PINK_BEAN_1(13100, 13000),
        KINESIS_0(14000, 14000),
        KINESIS_1(14200, 14000),
        KINESIS_2(14210, 14000),
        KINESIS_3(14211, 14000),
        KINESIS_4(14212, 14000),
        ILLIUM(15000, 15000),
        ILLIUM1(15200, 15000),
        ILLIUM2(15210, 15000),
        ILLIUM3(15211, 15000),
        ILLIUM4(15212, 15000),
        ARK(15001, 15001),
        ARK1(15500, 15001),
        ARK2(15510, 15001),
        ARK3(15511, 15001),
        ARK4(15512, 15001),
        HOYOUNG(16000, 16000),
        HOYOUNG1(16400, 16000),
        HOYOUNG2(16410, 16000),
        HOYOUNG3(16411, 16000),
        HOYOUNG4(16412, 16000),
        ADELE(15002, 15002),
        ADELE1(15100, 15002),
        ADELE2(15110, 15002),
        ADELE3(15111, 15002),
        ADELE4(15112, 15002),
        EMPTY_0(30000, 0),
        V_SKILLS(40000, 0),
        EMPTY_2(40001, 0),
        EMPTY_3(40002, 0),
        EMPTY_4(40003, 0),
        EMPTY_5(40004, 0),
        EMPTY_6(40005, 0),
        PINK_BEAN_EMPTY_0(800000, 13000),
        PINK_BEAN_EMPTY_1(800001, 13000),
        PINK_BEAN_EMPTY_2(800002, 13000),
        PINK_BEAN_EMPTY_3(800003, 13000),
        PINK_BEAN_EMPTY_4(800004, 13000),
        PINK_BEAN_EMPTY_5(800010, 13000),
        PINK_BEAN_EMPTY_6(800011, 13000),
        PINK_BEAN_EMPTY_7(800012, 13000),
        PINK_BEAN_EMPTY_8(800013, 13000),
        PINK_BEAN_EMPTY_9(800014, 13000),
        PINK_BEAN_EMPTY_10(800015, 13000),
        PINK_BEAN_EMPTY_11(800016, 13000),
        PINK_BEAN_EMPTY_12(800017, 13000),
        PINK_BEAN_EMPTY_13(800018, 13000),
        PINK_BEAN_EMPTY_14(800019, 13000),
        PINK_BEAN_EMPTY_15(800022, 13000);

        private final short jobId;
        private final int mapId;
        private final short beginnerJobId;

        JobEnum(short jobId, short beginnerJobId) {
            this.jobId = jobId;
            this.beginnerJobId = beginnerJobId;
            this.mapId = 100000000;
        }

        JobEnum(int jobId, int beginnerJobId, int mapId) {
            this.jobId = (short) jobId;
            this.beginnerJobId = (short) beginnerJobId;
            this.mapId = mapId;
        }

        JobEnum(int jobId, int beginnerJobId) {
            this((short) jobId, (short) beginnerJobId);
        }


        public short getJob() {
            return jobId;
        }

        public short getBeginnerJobId() {
            return beginnerJobId;
        }

        public static JobEnum getJobById(short id) {
            return Arrays.stream(JobEnum.values()).filter(j -> j.getJob() == id).findAny().orElse(null);
        }

        public int getMapId() {
            return mapId;
        }
    }

    public enum LoginJob {
        RESISTANCE(0, JobFlag.DISABLED, JobEnum.CITIZEN),
        EXPLORER(1, JobFlag.ENABLED, JobEnum.BEGINNER, 4000010),
        CYGNUS(2, JobFlag.ENABLED, JobEnum.NOBLESSE),
        ARAN(3, JobFlag.DISABLED, JobEnum.LEGEND),
        EVAN(4, JobFlag.DISABLED, JobEnum.EVAN_NOOB),
        MERCEDES(5, JobFlag.DISABLED, JobEnum.MERCEDES),
        DEMON(6, JobFlag.ENABLED, JobEnum.DEMON_SLAYER),
        PHANTOM(7, JobFlag.DISABLED, JobEnum.PHANTOM),
        DUAL_BLADE(8, JobFlag.DISABLED, JobEnum.BEGINNER),
        MIHILE(9, JobFlag.DISABLED, JobEnum.NAMELESS_WARDEN),
        LUMINOUS(10, JobFlag.ENABLED, JobEnum.LUMINOUS),
        KAISER(11, JobFlag.ENABLED, JobEnum.KAISER),
        ANGELIC(12, JobFlag.ENABLED, JobEnum.ANGELIC_BUSTER),
        CANNONER(13, JobFlag.DISABLED, JobEnum.BEGINNER),
        XENON(14, JobFlag.ENABLED, JobEnum.XENON),
        ZERO(15, JobFlag.DISABLED, JobEnum.ZERO),
        SHADE(16, JobFlag.DISABLED, JobEnum.SHADE),
        JETT(17, JobFlag.ENABLED, JobEnum.JETT1),
        HAYATO(18, JobFlag.ENABLED, JobEnum.HAYATO),
        KANNA(19, JobFlag.ENABLED, JobEnum.KANNA),
        CHASE(20, JobFlag.DISABLED, JobEnum.BEAST_TAMER),
        PINK_BEAN(21, JobFlag.DISABLED, JobEnum.PINK_BEAN_0),
        KINESIS(22, JobFlag.DISABLED, JobEnum.KINESIS_0),
        CADENA(23, JobFlag.DISABLED, JobEnum.CADENA),
        ILLIUM(24, JobFlag.DISABLED, JobEnum.ILLIUM),
        ARK(25, JobFlag.DISABLED, JobEnum.ARK),
        FINDER(26, JobFlag.DISABLED, JobEnum.BEGINNER),
        HOYOUNG(27, JobFlag.DISABLED, JobEnum.HOYOUNG),
        ADELE(28, JobFlag.DISABLED, JobEnum.ADELE);

        private final int jobType, flag, beginMap;
        private final JobEnum beginJob;

        LoginJob(int jobType, JobFlag flag, JobEnum beginJob) {
            this.jobType = jobType;
            this.flag = flag.getFlag();
            this.beginJob = beginJob;
            this.beginMap = 100000000;
        }

        LoginJob(int jobType, JobFlag flag, JobEnum beginJob, int beginMap) {
            this.jobType = jobType;
            this.flag = flag.getFlag();
            this.beginJob = beginJob;
            this.beginMap = beginMap;
        }

        public int getJobType() {
            return jobType;
        }

        public int getFlag() {
            return flag;
        }

        public JobEnum getBeginJob() {
            return beginJob;
        }

        public int getBeginMap() {
            return beginMap;
        }

        public enum JobFlag {

            DISABLED(0),
            ENABLED(1);
            private final int flag;

            JobFlag(int flag) {
                this.flag = flag;
            }

            public int getFlag() {
                return flag;
            }
        }

        public static LoginJob getLoginJobById(int id) {
            return Arrays.stream(LoginJob.values()).filter(j -> j.getJobType() == id).findFirst().orElse(null);
        }
    }


    public static boolean isAdventurerWarrior(short jobId) {
        return jobId == 100
                || jobId == 110
                || jobId == 111
                || jobId == 112
                || jobId == 120
                || jobId == 121
                || jobId == 122
                || jobId == 130
                || jobId == 131
                || jobId == 132;
    }

    public static boolean isAdventurerMage(short jobId) {
        return jobId == 200
                || jobId == 210
                || jobId == 211
                || jobId == 212
                || jobId == 220
                || jobId == 221
                || jobId == 222
                || jobId == 230
                || jobId == 231
                || jobId == 232;
    }

    public static boolean isAdventurerArcher(short jobId) {
        return jobId == 300 || jobId == 310 || jobId == 311 || jobId == 312 || jobId == 320 || jobId == 321 || jobId == 322;
    }

    public static boolean isAdventurerThief(short jobId) {
        return jobId == 400
                || jobId == 420
                || jobId == 421
                || jobId == 422
                || jobId == 410
                || jobId == 411
                || jobId == 412
                || jobId / 10 == 43;
    }

    public static boolean isAdventurerPirate(short jobId) {
        return jobId == 500
                || jobId == 510
                || jobId == 511
                || jobId == 512
                || jobId == 520
                || jobId == 521
                || jobId == 522
                || isCannonShooter(jobId);
    }

    public static boolean isCannonShooter(short jobId) {
        return jobId / 10 == 53 || jobId == 501;
    }

    public static boolean isCygnusKnight(short jobId) {
        return jobId / 1000 == 1;
    }

    public static boolean isResistance(short jobId) {
        return jobId / 1000 == 3;
    }

    public static boolean isEvan(short jobId) {
        return jobId / 100 == 22 || jobId == 2001;
    }

    public static boolean isMercedes(short jobId) {
        return jobId / 100 == 23 || jobId == 2002;
    }

    public static boolean isPhantom(short jobId) {
        return jobId / 100 == 24 || jobId == 2003;
    }

    public static boolean iinder(short jobId) {
        return jobId / 1000 == 5;
    }

    public static boolean isLuminous(short jobId) {
        return jobId / 100 == 27 || jobId == 2004;
    }

    public static boolean isKaiser(short jobId) {
        return jobId == JobConstants.JobEnum.KAISER.getJob() || jobId / 100 == 61;
    }

    public static boolean isZero(short jobId) {
        return jobId == 10000 || jobId == 10100 || jobId == 10110 || jobId == 10111 || jobId == 10112;
    }

    public static boolean isHidden(short jobId) {
        return jobId / 100 == 25 || jobId == 2005;
    }

    public static boolean isAran(short jobId) {
        return jobId / 100 == 21 || jobId == 2000;
    }

    public static boolean isKinesis(short jobId) {
        return jobId == 14000 || jobId == 14200 || jobId == 14210 || jobId == 14211 || jobId == 14212;
    }

    public static boolean isExtendSpJob(short jobId) {
        return !isBeastTamer(jobId) && !isPinkBean(jobId) && !isGmJob(jobId);
    }

    public static boolean isDemon(short jobId) {
        return jobId / 100 == 31 || jobId == 3001;
    }

    public static boolean isCadena(short jobId) {
        return jobId == JobEnum.CADENA.getJob() || jobId == JobEnum.CADENA1.getJob() || jobId == JobEnum.CADENA2.getJob() || jobId == JobEnum.CADENA3.getJob() || jobId == JobEnum.CADENA4.getJob();
    }

    public static boolean isIllium(short jobId) {
        return jobId == JobEnum.ILLIUM.getJob() || jobId == JobEnum.ILLIUM1.getJob() || jobId == JobEnum.ILLIUM2.getJob() || jobId == JobEnum.ILLIUM3.getJob() || jobId == JobEnum.ILLIUM4.getJob();
    }

    public static boolean isArk(short jobId) {
        return jobId == JobEnum.ARK.getJob() || jobId == JobEnum.ARK1.getJob() || jobId == JobEnum.ARK2.getJob() || jobId == JobEnum.ARK3.getJob() || jobId == JobEnum.ARK4.getJob();
    }

    public static boolean isBeginnerJob(short jobId) {
        switch (jobId) {
            case 2001:
            case 2002:
            case 2003:
            case 2004:
            case 2005:
            case 3001:
            case 3002:
            case 4001:
            case 4002:
            case 5000:
            case 6000:
            case 6001:
            case 6002:
            case 8001:
            case 13000:
            case 14000:
            case 15000:
            case 15001:
                return true;
            default:
                return jobId % 1000 == 0;
        }
    }

    public static int getJobLevel(short jobId) {
        int prefix;
        if (isBeginnerJob(jobId) || jobId % 100 <= 0 || jobId == 501 || jobId == 3101 || jobId == 508) {
            return 1;
        }
        if (isEvan(jobId)) {
            return getEvanJobLevel(jobId);
        }
        prefix = jobId % 10;
        if (isDualBlade(jobId)) {
            prefix = (jobId - 430) / 2;
        }
        return prefix <= 2 ? prefix + 2 : 0;
    }

    public static int getJobLevelByCharLevel(short job, int charLevel) {
        if (JobConstants.isDualBlade(job)) {
            if (charLevel <= 10) {
                return 0;
            } else if (charLevel <= 20) {
                return 1;
            } else if (charLevel <= 30) {
                return 2;
            } else if (charLevel <= 45) {
                return 3;
            } else if (charLevel <= 60) {
                return 4;
            } else if (charLevel <= 100) {
                return 5;
            } else {
                return 6;
            }
        }
        if (JobConstants.isGmJob(job)) {
            return 1;
        }
        if (charLevel <= 10) {
            return 0;
        } else if (charLevel <= 30) {
            return 1;
        } else if (charLevel <= 60) {
            return 2;
        } else if (charLevel <= 100) {
            return 3;
        } else {
            return 4;
        }
    }

    private static int getEvanJobLevel(short jobId) {
        int result;
        switch (jobId) {
            case 2200:
            case 2210:
                result = 1;
                break;
            case 2211:
            case 2212:
            case 2213:
                result = 2;
                break;
            case 2214:
            case 2215:
            case 2216:
                result = 3;
                break;
            case 2217:
            case 2218:
                result = 4;
                break;
            default:
                result = 0;
                break;
        }
        return result;
    }

    public static boolean isNoManaJob(short job) {
        return isDemon(job) || isAngelicBuster(job) || isZero(job) || isKinesis(job) || isKanna(job);
    }

    public boolean isWarriorEquipJob(short jobID) {
        return isAdventurerWarrior(jobID) || isPinkBean(jobID) || isDawnWarrior(jobID) || isMihile(jobID) ||
                isAran(jobID) || isKaiser(jobID) || isBlaster(jobID) || isDemon(jobID) || isHayato(jobID) ||
                isZero(jobID);

    }

    public boolean isMageEquipJob(short jobID) {
        return isBeastTamer(jobID) || isKinesis(jobID) || isAdventurerMage(jobID) || isBlazeWizard(jobID) ||
                isEvan(jobID) || isLuminous(jobID) || isBattleMage(jobID) || isKanna(jobID);
    }

    public boolean isArcherEquipJob(short jobID) {
        return isAdventurerArcher(jobID) || isWindArcher(jobID) || isMercedes(jobID) || isWildHunter(jobID);
    }

    public boolean isThiefEquipJob(short jobID) {
        return isAdventurerThief(jobID) || isNightWalker(jobID) || isPhantom(jobID) || isXenon(jobID);
    }

    public boolean isPirateEquipJob(short jobID) {
        return isAdventurerPirate(jobID) || isThunderBreaker(jobID) || isShade(jobID) || isAngelicBuster(jobID) ||
                isXenon(jobID) || isMechanic(jobID) || isJett(jobID);
    }


}
