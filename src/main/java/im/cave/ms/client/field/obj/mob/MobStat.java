package im.cave.ms.client.field.obj.mob;

import im.cave.ms.client.character.temp.CharacterTemporaryStat;

public enum MobStat {
    PAD(0),
    PDR(1),
    MAD(2),
    MDR(3),
    ACC(4),
    EVA(5),
    Speed(6),
    Stun(11), // 虎咆啸 ...

    Freeze(12), //艾尔达诺巴 ...
    Poison(13),
    Seal(14),
    Darkness(15),
    PowerUp(16),
    MagicUp(17),
    PGuardUp(18),
    MGuardUp(19),

    PImmune(20),
    MImmune(21),
    Web(22),
    HardSkin(23),
    Ambush(24),
    Venom(25),
    Blind(26),//checked 致盲
    SealSkill(27),

    Dazzle(28),
    PCounter(29), // nOption = % of dmg, mOption = % chance
    MCounter(30),
    RiseByToss(31),
    BodyPressure(32),
    Weakness(33),
    Showdown(34),
    MagicCrash(35),

    DamagedElemAttr(36),
    Dark(37),
    Mystery(38),
    AddDamParty(39),
    HitCriDamR(40),
    Fatality(41),
    Lifting(42),
    LucidNightmare(43), //路西德之噩梦
    DeadlyCharge(43),
    Smite(40),
    AddDamSkill(41),
    Incizing(42),
    DodgeBodyAttack(43),
    DebuffHealing(44),
    AddDamSkill2(45),
    BodyAttack(46),
    TempMoveAbility(47),

    FixDamRBuff(48),
    ElementDarkness(49),
    AreaInstallByHit(50),
    BMageDebuff(51),
    JaguarProvoke(52),
    JaguarBleeding(53),
    DarkLightning(54),
    PinkBeanFlowerPot(55),

    BattlePvPHelenaMark(56),
    PsychicLock(57),
    PsychicLockCoolTime(58),
    PsychicGroundMark(59),

    PowerImmune(56),
    PsychicForce(61),
    MultiPMDR(62),
    ElementResetBySummon(63),

    BahamutLightElemAddDam(64),
    BossPropPlus(65),
    MultiDamSkill(66),
    RWLiftPress(67),
    RWChoppingHammer(68),
    TimeBomb(69),
    Treasure(70),
    AddEffect(71),

    Unknown1(72),
    Unknown2(73),
    Invincible(74),
    Explosion(75),
    HangOver(76),
    BurnedInfo(86),// v202.3
    InvincibleBalog(78),
    ExchangeAttack(79),

    ExtraBuffStat(89),// v200.3
    LinkTeam(81),
    SoulExplosion(82),
    SeperateSoulP(83),
    SeperateSoulC(84),
    Ember(85),
    TrueSight(86),
    Laser(87),
    ;

    private final int val;
    private final int pos;
    private int bitPos;

    MobStat(int val, int pos) {
        this.val = val;
        this.pos = pos;
    }

    MobStat(int bitPos) {
        this.bitPos = bitPos;
        this.val = 1 << (31 - bitPos % 32);
        this.pos = bitPos / 32;
    }

    public int getPos() {
        return pos;
    }

    public int getVal() {
        if (this == BurnedInfo) {
            return 0x40000;
        }
        return val;
    }

    public boolean isMovementAffectingStat() {
        switch (this) {
            case Speed:
            case Stun:
            case Freeze:
            case RiseByToss:
            case Lifting:
            case Smite:
            case TempMoveAbility:
            case RWLiftPress:
                return true;
            default:
                return false;
        }
    }

    public int getBitPos() {
        return bitPos;
    }

    public static void main(String[] args) {
        for (MobStat stat : MobStat.values()) {
            System.out.println(stat.toString() + " " + stat.getBitPos() + " " + Integer.toHexString(stat.getVal()) + " " + stat.getPos());
        }
    }
}
