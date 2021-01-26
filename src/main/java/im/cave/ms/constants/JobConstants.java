package im.cave.ms.constants;


import im.cave.ms.enums.JobEnum;

import java.util.Arrays;
import java.util.List;

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
        return id == JobEnum.ANGELIC_BUSTER.getJob() || id / 100 == 65;
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
        return id == JobEnum.KANNA.getJob() || id / 100 == 42;
    }

    public static boolean isHayato(short id) {
        return id == JobEnum.HAYATO.getJob() || id / 100 == 41;
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
        return id >= JobEnum.MECHANIC_1.getJob() && id <= JobEnum.MECHANIC_4.getJob();
    }

    public static boolean isBattleMage(short id) {
        return id >= JobEnum.BATTLE_MAGE_1.getJob() && id <= JobEnum.BATTLE_MAGE_4.getJob();
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
        return jobId == JobEnum.KAISER.getJob() || jobId / 100 == 61;
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


    public static int getJobReqLev(int jobId) {
        if (isDualBlade((short) jobId)) {
            switch (jobId) {
                case 430:
                    return 20;
                case 431:
                    return 30;
                case 432:
                    return 45;
                case 433:
                    return 60;
                case 434:
                    return 100;
                default:
                    return 0;
            }
        }
        int jobLevel = getJobLevel((short) jobId);
        switch (jobLevel) {
            case 1:
                return 10;
            case 2:
                return 30;
            case 3:
                return 60;
            case 4:
                return 100;
        }
        return 0;
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
