package im.cave.ms.client.character.job;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.Stat;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.exception.SkillException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class MapleJob {
    protected MapleCharacter chr;
    protected MapleClient c;

    public static final int MULTIPLE_PET = 8;

    public static final int MONOLITH = 80011261;
    public static final int ELEMENTAL_SYLPH = 80001518;
    public static final int FLAME_SYLPH = 80001519;
    public static final int THUNDER_SYLPH = 80001520;
    public static final int ICE_SYLPH = 80001521;
    public static final int EARTH_SYLPH = 80001522;
    public static final int DARK_SYLPH = 80001523;
    public static final int HOLY_SYLPH = 80001524;
    public static final int SALAMANDER_SYLPH = 80001525;
    public static final int ELECTRON_SYLPH = 80001526;
    public static final int UNDINE_SYLPH = 80001527;
    public static final int GNOME_SYLPH = 80001528;
    public static final int DEVIL_SYLPH = 80001529;
    public static final int ANGEL_SYLPH = 80001530;

    public static final int ELEMENTAL_SYLPH_2 = 80001715;
    public static final int FLAME_SYLPH_2 = 80001716;
    public static final int THUNDER_SYLPH_2 = 80001717;
    public static final int ICE_SYLPH_2 = 80001718;
    public static final int EARTH_SYLPH_2 = 80001719;
    public static final int DARK_SYLPH_2 = 80001720;
    public static final int HOLY_SYLPH_2 = 80001721;
    public static final int SALAMANDER_SYLPH_2 = 80001722;
    public static final int ELECTRON_SYLPH_2 = 80001723;
    public static final int UNDINE_SYLPH_2 = 80001724;
    public static final int GNOME_SYLPH_2 = 80001725;
    public static final int DEVIL_SYLPH_2 = 80001726;
    public static final int ANGEL_SYLPH_2 = 80001727;

    public static final int WHITE_ANGELIC_BLESSING = 80000155;
    public static final int WHITE_ANGELIC_BLESSING_2 = 80001154;
    public static final int LIGHTNING_GOD_RING = 80001262;
    public static final int LIGHTNING_GOD_RING_2 = 80011178;
    public static final int GUARD_RING = 80011149;
    public static final int SUN_RING = 80010067;
    public static final int RAIN_RING = 80010068;
    public static final int RAINBOW_RING = 80010069;
    public static final int SNOW_RING = 80010070;
    public static final int LIGHTNING_RING = 80010071;
    public static final int WIND_RING = 80010072;

    public static final int BOSS_SLAYERS = 91001022;
    public static final int UNDETERRED = 91001023;
    public static final int FOR_THE_GUILD = 91001024;

    public static final int REBOOT = 80000186;
    public static final int REBOOT2 = 80000187;

    public static final int MAPLERUNNER_DASH = 80001965;

    public static final int[] REMOVE_ON_STOP = new int[]{
            MAPLERUNNER_DASH
    };

    public static final int[] REMOVE_ON_WARP = new int[]{
            MAPLERUNNER_DASH
    };

    private final int[] buffs = new int[]{
            BOSS_SLAYERS,
            UNDETERRED,
            FOR_THE_GUILD,
            MAPLERUNNER_DASH
    };

    public MapleJob(MapleCharacter chr) {
        this.chr = chr;
        this.c = chr.getClient();
    }

    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        MapleCharacter chr = c.getPlayer();
//        TemporaryStatManager tsm = chr.getTemporaryStatManager();
//        Skill skill = SkillData.getSkillDeepCopyById(attackInfo.skillId);
//        int skillID = 0;
//        SkillInfo si = null;
//        boolean hasHitMobs = attackInfo.mobAttackInfo.size() > 0;
//        byte slv = 0;
//        if (skill != null) {
//            si = SkillData.getSkillInfoById(skill.getSkillId());
//            slv = (byte) skill.getCurrentLevel();
//            skillID = skill.getSkillId();
//        }
//
//        // Recovery Rune  HP Recovery
//        if (tsm.getOptByCTSAndSkill(IgnoreMobDamR, RuneStone.LIBERATE_THE_RECOVERY_RUNE) != null) {
//            SkillInfo recoveryRuneInfo = SkillData.getSkillInfoById(RuneStone.LIBERATE_THE_RECOVERY_RUNE);
//            byte recoveryRuneSLV = 1; //Hardcode Skill Level to 1
//            int healrate = recoveryRuneInfo.getValue(dotHealHPPerSecondR, recoveryRuneSLV);
//            int healing = chr.getMaxHP() / (100 / healrate);
//            chr.heal(healing);
//        }
//
//
//        Option o1 = new Option();
//        Option o2 = new Option();
//        Option o3 = new Option();
//        Option o4 = new Option();
//        switch (skillID) {
//            case RuneStone.LIBERATE_THE_DESTRUCTIVE_RUNE:
//                // Attack of the Rune
//                AffectedArea aa = AffectedArea.getAffectedArea(chr, attackInfo);
//                aa.setMobOrigin((byte) 0);
//                aa.setPosition(chr.getPosition());
//                aa.setRect(aa.getPosition().getRectAround(si.getRects().get(0)));
//                chr.getField().spawnAffectedArea(aa);
//
//                skill.setCurrentLevel(1);
//                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
//                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
//                    MobTemporaryStat mts = mob.getTemporaryStat();
//                    mts.createAndAddBurnedInfo(chr, skill);
//                }
//
//                // Buff of the Rune
//                si = SkillData.getSkillInfoById(RuneStone.LIBERATE_THE_DESTRUCTIVE_RUNE_BUFF); //Buff Info
//                slv = (byte) skill.getCurrentLevel();
//                o1.nReason = RuneStone.LIBERATE_THE_DESTRUCTIVE_RUNE_BUFF;
//                o1.nValue = si.getValue(indieDamR, slv); //50% DamR
//                o1.tStart = (int) System.currentTimeMillis();
//                o1.tTerm = si.getValue(time, slv);
//                tsm.putMapleCharacteracterStatValue(IndieDamR, o1);
//
//                tsm.sendSetStatPacket();
//                break;
//        }
    }

    /*
        全职业通用技能
     */
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket in) throws SkillException, Exception {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        MapleCharacter chr = c.getPlayer();
        SkillInfo si = SkillData.getSkillInfo(skillId);
        int cooltime = si.getValue(SkillStat.cooltime, slv);
        if (!chr.hasSkill(skillId)) {
            chr.enableAction();
            throw new SkillException("技能不存在");
        }
        if (cooltime > 0) {
            //处理冷却时间减少
            HashMap<Integer, Integer> cooltimes = new HashMap<>();
            chr.addSkillCooltime(skillId, cooltime * 1000);
            cooltimes.put(skillId, cooltime * 1000);
            c.announce(UserPacket.skillCoolTimeSet(cooltimes));
            //todo
            EventManager.addEvent(() -> c.announce(UserPacket.skillCoolDown(skillId)), cooltime, TimeUnit.SECONDS);
        }
        if (in != null && isBuff(skillId)) {
            handleBuff(c, in, skillId, slv);
        }
    }

    public void handleBuff(MapleClient c, InPacket in, int skillId, int slv) {
        MapleCharacter player = c.getPlayer();
        SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        Option option = new Option();
        long curTime = System.currentTimeMillis();
        boolean sendStat = true;
        switch (skillId) {
            case 10000000:
                player.dropMessage("10000000");
                break;
            case 200000000:
                player.dropMessage("20000000");
                break;
            default:
                player.dropMessage("unhandled buff" + skillId);
                sendStat = false;
        }

        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }

//    public int alterCooldownSkill(int skillId) {
//        Skill skill = chr.getSkill(skillId);
//        if (skill == null) {
//            return -1;
//        }
//        SkillInfo si = SkillData.getSkillInfoById(skillId);
//        byte slv = (byte) skill.getCurrentLevel();
//        int cdInSec = si.getValue(SkillStat.cooltime, slv);
//        int cdInMillis = cdInSec > 0 ? cdInSec * 1000 : si.getValue(SkillStat.cooltimeMS, slv);
//        int cooldownReductionR = chr.getHyperPsdSkillsCooltimeR().getOrDefault(skillId, 0);
//        if (cooldownReductionR > 0) {
//            return (int) (cdInMillis - ((double) (cdInMillis * cooldownReductionR) / 100));
//        }
//        return -1;
//    }


    /**
     * Gets called when MapleCharacteracter receives a debuff from a Mob Skill
     *
     * @param chr The MapleCharacteracter
     */

    public void handleMobDebuffSkill(MapleCharacter chr) {

    }

    /**
     * Used for Classes that have timers, to cancel the timer after changing channel
     *
     * @param chr The MapleCharacteracter
     */
    public void handleCancelTimer(MapleCharacter chr) {

    }

//    public void handleJoblessBuff(MapleClient c, InPacket in, int skillID, byte slv) {
//        MapleCharacter chr = c.getChr();
//        SkillInfo si = SkillData.getSkillInfoById(skillID);
//        TemporaryStatManager tsm = c.getChr().getTemporaryStatManager();
//        Option o1 = new Option();
//        Option o2 = new Option();
//        Option o3 = new Option();
//        Option o4 = new Option();
//        Option o5 = new Option();
//        Summon summon;
//        Field field;
//        int curTime = (int) System.currentTimeMillis();
//        boolean sendStat = true;
//        switch (skillID) {
//            case BOSS_SLAYERS:
//                o1.nReason = skillID;
//                o1.nValue = si.getValue(indieBDR, slv);
//                o1.tStart = curTime;
//                o1.tTerm = si.getValue(time, slv);
//                tsm.putMapleCharacteracterStatValue(IndieBDR, o1);
//                break;
//            case UNDETERRED:
//                o1.nReason = skillID;
//                o1.nValue = si.getValue(indieIgnoreMobpdpR, slv);
//                o1.tStart = curTime;
//                o1.tTerm = si.getValue(time, slv);
//                tsm.putMapleCharacteracterStatValue(IndieIgnoreMobpdpR, o1);
//                break;
//            case FOR_THE_GUILD:
//                o1.nReason = skillID;
//                o1.nValue = si.getValue(indieDamR, slv);
//                o1.tStart = curTime;
//                o1.tTerm = si.getValue(time, slv);
//                tsm.putMapleCharacteracterStatValue(IndieDamR, o1);
//                break;
//            case MAPLERUNNER_DASH:
//                o1.nReason = o2.nReason = skillID;
//                o1.tStart = o2.tStart = curTime;
//                o1.tTerm = o2.tTerm = si.getValue(time, slv);
//                o1.nValue = si.getValue(indieForceJump, slv);
//                tsm.putMapleCharacteracterStatValue(IndieForceJump, o1);
//                o2.nValue = si.getValue(indieForceSpeed, slv);
//                tsm.putMapleCharacteracterStatValue(IndieForceSpeed, o2);
//                break;
//            default:
//                sendStat = false;
//        }
//        if (sendStat) {
//            tsm.sendSetStatPacket();
//        }
//}


//    public void handleHit(MapleClient c, InPacket in) {
//        int idk1 = in.decodeInt();
//        in.decodeInt(); // tick
//        byte idk2 = in.decodeByte(); // -1?
//        byte idk3 = in.decodeByte();
//        int damage = in.decodeInt();
//        short idk4 = in.decodeShort();
//        int templateID = 0;
//        int mobID = 0;
//        if (in.getUnreadAmount() >= 13) {
//            templateID = in.decodeInt();
//            mobID = in.decodeInt();
//            boolean left = in.decodeByte() != 0;
//            int skillID = in.decodeInt();
//        }
//
//        HitInfo hitInfo = new HitInfo();
//        hitInfo.hpDamage = damage;
//        hitInfo.templateID = templateID;
//        hitInfo.mobID = mobID;
//
//        if (chr.isInvincible()) {
//            return;
//        }
//
//        handleHit(c, in, hitInfo);
//        handleHit(c, hitInfo);
//    }


//    public void handleHit(MapleClient c, HitInfo hitInfo) {
//        MapleCharacter chr = c.getChr();
//        hitInfo.hpDamage = Math.max(0, hitInfo.hpDamage); // to prevent -1 (dodges) healing the player.
//
//        if (chr.getStat(Stat.hp) <= hitInfo.hpDamage) {
//            TemporaryStatManager tsm = chr.getTemporaryStatManager();
//
//            // Global Revives ---------------------------------------
//
//            // Global - Door (Bishop)
//            if (tsm.hasStatBySkillId(Magician.HEAVENS_DOOR)) {
//                Magician.reviveByHeavensDoor(chr);
//            }
//
//            // Global - Shade Link Skill (Shade)
//            // TODO
//
//
//            // Class Revives ----------------------------------------
//
//            // Dark Knight - Final Pact
//            else if (JobConstants.isDarkKnight(chr.getJob()) && chr.hasSkill(Warrior.FINAL_PACT_INFO) && Warrior.isFinalPactAvailable(chr)) {
//                Warrior.reviveByFinalPact(chr);
//            }
//
//            // Night Walker - Darkness Ascending
//            else if (tsm.getOptByCTSAndSkill(ReviveOnce, NightWalker.DARKNESS_ASCENDING) != null) {
//                NightWalker.reviveByDarknessAscending(chr);
//            }
//
//            // Blaze Wizard - Phoenix Run
//            else if (tsm.getOptByCTSAndSkill(ReviveOnce, BlazeWizard.PHOENIX_RUN) != null) {
//                BlazeWizard.reviveByPhoenixRun(chr);
//            }
//
//            // Shade - Summon Other Spirit
//            else if (tsm.getOptByCTSAndSkill(ReviveOnce, Shade.SUMMON_OTHER_SPIRIT) != null) {
//                Shade.reviveBySummonOtherSpirit(chr);
//            }
//
//            // Beast Tamer - Bear Reborn		TODO
//            else if (tsm.getOptByCTSAndSkill(ReviveOnce, BeastTamer.BEAR_REBORN) != null) {
//                BeastTamer.reviveByBearReborn(chr);
//            }
//
//            // Zero - Rewind
//            else if (tsm.getOptByCTSAndSkill(ReviveOnce, Zero.REWIND) != null) {
//                Zero.reviveByRewind(chr);
//            }
//
//            // Phantom - Final Feint
//            else if (tsm.getOptByCTSAndSkill(ReviveOnce, Phantom.FINAL_FEINT) != null) {
//                Phantom.reviveByFinalFeint(chr);
//            }
//
//
//        }
//        int curHP = chr.getStat(Stat.hp);
//        int newHP = curHP - hitInfo.hpDamage;
//        if (newHP <= 0) {
//            curHP = 0;
//        } else {
//            curHP = newHP;
//        }
//        Map<Stat, Object> stats = new HashMap<>();
//        chr.setStat(Stat.hp, curHP);
//        stats.put(Stat.hp, curHP);
//
//        int curMP = chr.getStat(Stat.mp);
//        int newMP = curMP - hitInfo.mpDamage;
//        if (newMP < 0) {
//            // should not happen
//            curMP = 0;
//        } else {
//            curMP = newMP;
//        }
//        chr.setStat(Stat.mp, curMP);
//        stats.put(Stat.mp, curMP);
//        c.write(WvsContext.statChanged(stats));
//        chr.getField().broadcastPacket(UserRemote.hit(chr, hitInfo), chr);
//        if (chr.getParty() != null) {
//            chr.getParty().broadcast(UserPacket.receiveHP(chr), chr);
//        }
//        if (curHP <= 0) {
//            // TODO Add more items for protecting exp and whatnot
//            c.write(UserLocal.openUIOnDead(true, chr.getBuffProtectorItem() != null,
//                    false, false, false,
//                    ReviveType.NORMAL.getVal(), 0));
//        }
//    }


    //    public void handleHit(MapleClient c, InPacket in, HitInfo hitInfo) {
//        MapleCharacter chr = c.getChr();
//        TemporaryStatManager tsm = chr.getTemporaryStatManager();
//
//        // If no job specific skills already nullified the dmg taken
//        if (hitInfo.hpDamage != 0) {
//
//            // Bishop - Holy Magic Shell
//            if (tsm.hasStat(MapleCharacteracterTemporaryStat.HolyMagicShell)) {
//                if (Magician.hmshits < Magician.getHolyMagicShellMaxGuards(chr)) {
//                    Magician.hmshits++;
//                } else {
//                    Magician.hmshits = 0;
//                    tsm.removeStatsBySkill(Magician.HOLY_MAGIC_SHELL);
//                }
//            }
//
//            // Mihile - Soul Link
//            else if (tsm.hasStat(MichaelSoulLink) && chr.getId() != tsm.getOption(MichaelSoulLink).cOption) {
//                Party party = chr.getParty();
//
//                PartyMember mihileInParty = party.getPartyMemberByID(tsm.getOption(MichaelSoulLink).cOption);
//                if (mihileInParty != null) {
//                    MapleCharacter mihileChr = mihileInParty.getChr();
//                    Skill skill = mihileChr.getSkill(SOUL_LINK);
//                    SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
//                    byte slv = (byte) skill.getCurrentLevel();
//
//                    int hpDmg = hitInfo.hpDamage;
//                    int mihileDmgTaken = (int) (hpDmg * ((double) si.getValue(q, slv) / 100));
//
//                    hitInfo.hpDamage = hitInfo.hpDamage - mihileDmgTaken;
//                    mihileChr.damage(mihileDmgTaken);
//                } else {
//                    tsm.removeStatsBySkill(SOUL_LINK);
//                    tsm.removeStatsBySkill(ROYAL_GUARD);
//                    tsm.removeStatsBySkill(ENDURING_SPIRIT);
//                    tsm.sendResetStatPacket();
//                }
//            }
//
//            // Paladin - Parashock Guard
//            else if (tsm.hasStat(KnightsAura) && chr.getId() != tsm.getOption(KnightsAura).nOption) {
//                Party party = chr.getParty();
//
//                PartyMember paladinInParty = party.getPartyMemberByID(tsm.getOption(KnightsAura).nOption);
//                if (paladinInParty != null) {
//                    MapleCharacter paladinChr = paladinInParty.getChr();
//                    Skill skill = paladinChr.getSkill(PARASHOCK_GUARD);
//                    SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
//                    byte slv = (byte) skill.getCurrentLevel();
//
//                    int dmgReductionR = si.getValue(y, slv);
//                    int dmgReduceAmount = (int) (hitInfo.hpDamage * ((double) dmgReductionR / 100));
//                    hitInfo.hpDamage = hitInfo.hpDamage - dmgReduceAmount;
//                }
//            }
//        }
//    }
    public abstract boolean isHandlerOfJob(short id);

    public SkillInfo getInfo(int skillId) {
        return SkillData.getSkillInfo(skillId);
    }

    protected MapleCharacter getMapleCharacter() {
        return chr;
    }

    public abstract int getFinalAttackSkill();

    /**
     * Called when a player is right-clicking a buff, requesting for it to be disabled.
     *
     * @param c       The MapleClient
     * @param skillID The skill that the player right-clicked
     */
    public void handleSkillRemove(MapleClient c, int skillID) {
        // nothing here yet, @Override to make use of it
    }

//    public void handleSkillPrepare(MapleCharacter chr, int skillID) {
//        Skill skill = chr.getSkill(skillID);
//        chr.getField().broadcastPacket(UserPacket.skillPrepare(chr, skillID, (byte) skill.getCurrentLevel()), chr);
//    }

    public void handleLevelUp() {
        short level = (short) chr.getLevel();
        Map<Stat, Long> stats = new HashMap<>();
        if (level > 10) {
            chr.addStat(Stat.AVAILABLEAP, 5);
            stats.put(Stat.AVAILABLEAP, chr.getStat(Stat.AVAILABLEAP));
        } else {
            if (level >= 6) {
                chr.addStat(Stat.STR, 4);
                chr.addStat(Stat.DEX, 1);
            } else {
                chr.addStat(Stat.STR, 5);
            }
            stats.put(Stat.STR, chr.getStat(Stat.STR));
            stats.put(Stat.DEX, chr.getStat(Stat.DEX));
        }

        int sp = SkillConstants.getBaseSpByLevel(level);
        if ((level % 10) % 3 == 0 && level > 100) {
            sp *= 2; // double sp on levels ending in 3/6/9
        }

        if (level >= 10) {
            chr.addSpToJobByCurrentLevel(sp);
            stats.put(Stat.AVAILABLESP, (long) 1);
        }

//        byte linkSkillLevel = (byte) SkillConstants.getLinkSkillLevelByMapleCharacterLevel(level);
//        int linkSkillID = SkillConstants.getOriginalOfLinkedSkill(SkillConstants.getLinkSkillByJob(chr.getJob()));
//        if (linkSkillID != 0 && linkSkillLevel > 0) {
//            Skill skill = chr.getSkill(linkSkillID, true);
//            if (skill.getCurrentLevel() != linkSkillLevel) {
//                chr.addSkill(linkSkillID, linkSkillLevel, 3);
//            }
//        }

        int[][] incVal = GameConstants.getIncValArray(chr.getJob());
        if (incVal != null) {
            chr.addStat(Stat.MAXHP, incVal[0][1]);
            stats.put(Stat.MAXHP, (long) chr.getMaxHP());
            if (!JobConstants.isNoManaJob(chr.getJob())) {
                chr.addStat(Stat.MAXMP, incVal[3][0]);
                stats.put(Stat.MAXMP, (long) chr.getMaxMP());
            }
        } else {
            chr.chatMessage(ChatType.Notice, "Unhandled HP/MP job " + chr.getJob());
        }
        chr.announce(UserPacket.updatePlayerStats(stats, chr));
        chr.heal(chr.getMaxHP());
        chr.healMP(chr.getMaxMP());

        switch (level) {
            case 30:
                chr.getPotentialMan().addPotential(new CharacterPotential((byte) 0, 7000000, (byte) 1, (byte) 1));
                break;
            case 50:
                chr.getPotentialMan().addPotential(new CharacterPotential((byte) 1, 7000000, (byte) 1, (byte) 1));
                break;
            case 70:
                chr.getPotentialMan().addPotential(new CharacterPotential((byte) 2, 7000000, (byte) 1, (byte) 1));
                break;
        }
    }

    public boolean isBuff(int skillId) {
        return Arrays.stream(buffs).anyMatch(b -> b == skillId);
    }

}
