package im.cave.ms.client.character.temp;

import im.cave.ms.client.character.Option;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum CharacterTemporaryStat implements Comparator<CharacterTemporaryStat> {
    IndiePAD(0), // Checked
    IndieMAD(1),
    IndiePDD(2), // Checked
    IndieMDD(-1),
    IndieMHP(3),
    IndieMHPR(4), // Checked
    IndieMMP(5),
    IndieMMPR(6), // Checked

    IndieACC(7),
    IndieEVA(8),
    IndieJump(9),
    IndieSpeed(10),
    IndieAllStat(11),
    IndieDodgeCriticalTime(12),

    IndieEXP(14), // Checked
    IndieBooster(15), // Checked

    IndieFixedDamageR(16),
    PyramidStunBuff(17),
    PyramidFrozenBuff(18),
    PyramidFireBuff(19),
    PyramidBonusDamageBuff(20),
    IndieRelaxEXP(21),
    IndieSTR(22),
    IndieDEX(23), // Checked

    IndieINT(24),
    IndieLUK(25),
    IndieDamR(26), //
    IndieScriptBuff(27),
    IndieMDF(28),
    IndieMaxDamageOver(-1),
    IndieAsrR(29), //异常抗性
    IndieTerR(30),

    IndieCr(31),//
    IndiePDDR(32),
    IndieCrDam(33), //暴击伤害
    IndieBDR(34), // Checked Boss伤
    IndieStatR(35),
    IndieStance(36), // Checked 稳如泰山
    IndieIgnoreMobpdpR(37), // Checked 无视
    IndieEmpty(47), // 召唤物

    IndiePADR(39),
    IndieMADR(40),
    IndieCrDamR(41),
    IndieEVAR(42),
    IndieMDDR(-1),
    IndieDrainHP(43),
    IndiePMdR(43), // Checked 最终伤害
    IndieMaxDamageOverR(-1),

    IndieForceJump(44),
    IndieForceSpeed(45),
    IndieQrPointTerm(46),
    IndieUnk1(47),
    IndieUnk2(48),
    IndieUnk3(49),
    IndieUnk4(50),
    IndieUnk5(51),
    IndieUnk6(52),
    IndieUnk7(53),
    IndieUnk8(54),
    IndieUnk9(55),
    IndieUnk10(56),
    IndieUnk11(57),
    IndieUnk12(58),
    IndieUnk13(59),
    IndieUnk14(60),
    IndieUnk15(61),
    IndieUnk16(62),
    IndieUnk17(63),
    IndieStatCount(71),

    PAD(76),// Checked 物理攻击力
    PDD(77),// Checked 防御力
    MAD(78), // Checked 魔法攻击力
    ACC(79),
    EVA(80),
    Craft(81),
    Speed(82), // Checked
    Jump(83), // Checked
    MagicGuard(84), // Checked 魔法盾
    DarkSight(85), // Checked 隐身
    Booster(86), // Checked 82
    PowerGuard(87), // Checked 反伤 (愤怒之火) 83
    MaxHP(88), // Checked
    MaxMP(89), // Checked
    Invincible(90),
    SoulArrow(91), // Checked 无形箭
    Stun(92),//
    Unk82(93),//
    Unk83(94),//
    Unk84(95),//
    Poison(96),//
    ComboCounter(96),// 斗气集中
    Seal(97),//
    Darkness(98),//
    WeaponCharge(100),//
    HolySymbol(101), // Checked 神圣祈祷
    MesoUp(102),//
    ShadowPartner(103), // Checked 镜像分身
    PickPocket(104),//
    MesoGuard(105), // Checked 金钱盾
    Thaw(106),//
    Weakness(107),//
    Curse(108),//
    Slow(109),//
    Morph(110), // Checked 变形
    Regen(111), // Checked 持续回复  + 3
    BasicStatUp(112), // Checked 基础属性百分比 (冒险岛勇士)
    Stance(113), // Checked 稳如泰山
    SharpEyes(114), // Checked 火眼晶晶
    ManaReflection(115),//
    Attract(116),//
    NoBulletConsume(117), // Checked 无限子弹
    Infinity(118),//
    AdvancedBless(116),//
    IllusionStep(117), // Checked 幻影步
    Blind(118),//
    Concentration(119),
    BanMap(120),//
    MaxLevelBuff(121), // Checked 200级技能 4% 攻击力魔力
    Unk114(122),//
    Unk115(123),//
    MesoUpByItem(124),
    Ghost(125),//
    Barrier(126),//
    ReverseInput(127),//
    ItemUpByItem(128),//
    RespectPImmune(129),//
    RespectMImmune(130),//
    DefenseAtt(123),// 

    DefenseState(124),// 
    DojangBerserk(125),// 
    DojangInvincible(126),// 
    DojangShield(127),// 
    SoulMasterFinal(128),// 
    WindBreakerFinal(129),// Checked 隐形剑
    ElementalReset(141), // Checked 无视抗性
    HideAttack(131),// 

    EventRate(132),// 
    ComboAbilityBuff(133),// 
    ComboDrain(134),// 
    ComboBarrier(135),// 
    BodyPressure(136),// 
    RepeatEffect(137),//  // 隐形剑 HideAttack
    ExpBuffRate(138),//  [Used for 2450156]
    StopPortion(139),// 

    StopMotion(140),// 
    Fear(141),// 
    HiddenPieceOn(142),// 
    MagicShield(143),// 
    MagicResistance(144),//  矛连击
    SoulStone(145),// 
    Flying(146),// 
    Frozen(147),// 

    AssistCharge(148),// 
    Enrage(160),//
    DrawBack(161), // Checked 撤步退身
    NotDamaged(162), // Checked 无敌
    FinalCut(163),// Checked 终极斩
    HowlingAttackDamage(164),
    BeastFormDamageUp(165),
    Dance(166),//

    EMHP(164),//
    EMMP(165),//
    EPAD(166),//
    EMAD(167),// Checked 魔力
    EPDD(172),// 防御力
    EMDD(-1),

    Guard(172),//
    Unk162(170),//
    Unk163(171),//
    Cyclone(172),//
    Unk165(173),//
    HowlingCritical(-1),
    HowlingMaxMP(-1),
    HowlingDefence(-1),
    HowlingEvasion(-1),
    Conversion(174),//
    Revive(175),//
    PinkbeanMinibeenMove(176),//
    Sneak(177), // Checked 潜行

    Mechanic(178),//
    BeastFormMaxHP(179),//
    Dice(180),//
    BlessingArmor(181),//
    DamR(181),//
    TeleportMasteryOn(181),//
    CombatOrders(183), // Checked 战斗命令
    Beholder(184),// Checked 灵魂助力

    DispelItemOption(186),//
    Inflation(187),//
    OnixDivineProtection(188),//
    Web(189),//
    Bless(190),//
    TimeBomb(191),//
    DisOrder(192),//
    Thread(193),//

    Team(194),//
    Explosion(195),//
    BuffLimit(196),//
    STR(197),//
    INT(198),//
    DEX(199),// Checked
    LUK(200),//
    DispelItemOptionByField(201),

    DarkTornado(202), // Cygnus Attack
    PVPDamage(195),// 
    PvPScoreBonus(196),// 
    PvPInvincible(197),// 
    PvPRaceEffect(198),// 
    WeaknessMdamage(199),// 
    Frozen2(200),// 
    PVPDamageSkill(201),// 

    AmplifyDamage(202),// 
    IceKnight(-1),
    Shock(203),// 
    InfinityForce(204),// 
    IncMaxHP(205),// 
    IncMaxMP(206),// 
    HolyMagicShell(207),// 
    KeyDownTimeIgnore(208),// 

    ArcaneAim(209),// 
    MasterMagicOn(210),// 
    AsrR(218),// 异常抗性
    TerR(219),// 属性抗性
    DamAbsorbShield(213),// 伤害吸收
    DevilishPower(214),// 
    Roulette(215),// 
    SpiritLink(216),// 

    AsrRByItem(217),// 
    Event(218),// 
    CriticalBuff(219), // Checked 暴击率
    DropRate(220),// 
    PlusExpRate(221),// 
    ItemInvincible(222),// 
    Awake(223),
    ItemCritical(224),// 

    ItemEvade(225),// 
    Event2(226),// 
    VampiricTouch(227),// 
    DDR(228),// 
    IncCriticalDamMin(-1),
    IncCriticalDamMax(-1),
    IncTerR(229),// 
    IncAsrR(230),// 

    DeathMark(231),// 
    UsefulAdvancedBless(232),// 
    Lapidification(233),// 
    VenomSnake(234),// 
    CarnivalAttack(235),// 
    CarnivalDefence(236),// 
    CarnivalExp(237),// 
    SlowAttack(238),// 

    PyramidEffect(246),//
    KillingPoint(247),//
    HollowPointBullet(249),//
    KeyDownMoving(250),// Checked 按键技能
    IgnoreTargetDEF(250),//
    ReviveOnce(521),//
    Invisible(252),// Checked 隐身（幻影屏障）
    EnrageCr(253),//

    EnrageCrDam(254),//
    Judgement(248),// 
    DojangLuckyBonus(249),// 
    PainMark(250),// 
    Magnet(251),// 
    MagnetArea(252),// 
    Unk253(253),// 
    Unk254(254),// 
    Unk255(255),// 
    Unk256(256),// 
    Unk257(257),// 
    TideOfBattle(258),// 
    GrandGuardian(259),// v203.2 Paladin VSkills
    DropPer(260),//  [Used for 2023145]
    VampDeath(261),// 
    BlessingArmorIncPAD(262),// 
    KeyDownAreaMoving(263),// 
    Larkness(264),// 
    StackBuff(265),// 
    BlessOfDarkness(266),// 
    AntiMagicShell(274),//
    AntiMagicShellBool(267),// 
    LifeTidal(268),// 
    HitCriDamR(269),// 
    SmashStack(270),// 

    PartyBarrier(271),// 
    ReshuffleSwitch(272),// 
    SpecialAction(273),// 
    VampDeathSummon(274),// 
    StopForceAtomInfo(275),// 
    SoulGazeCriDamR(276),// 
    SoulRageCount(277),// 
    PowerTransferGauge(278),// 

    AffinitySlug(279),// 
    Trinity(280),// 
    IncMaxDamage(281),// 
    BossShield(282),// 
    MobZoneState(283),// 
    GiveMeHeal(284),// 
    TouchMe(285),// 
    Contagion(286),// 

    ComboUnlimited(287),// 
    SoulExalt(288),// 
    IgnorePCounter(289),// 
    IgnoreAllCounter(290),// 
    IgnorePImmune(291),// 
    IgnoreAllImmune(292),// 
    Unk293(293),// 
    FinalJudgement(294),// 
    IceAura(295),// 

    FireAura(296),// 
    VengeanceOfAngel(297),// 
    HeavensDoor(298),// 
    Preparation(299),// 
    BullsEye(300),// 
    IncEffectHPPotion(301),// 
    IncEffectMPPotion(302),// 
    BleedingToxin(303),// 

    IgnoreMobDamR(304),// 
    Asura(305),// 
    Unk306(306),// 
    FlipTheCoin(307),// 
    UnityOfPower(308),// 
    Stimulate(309),// 
    ReturnTeleport(310),// 
    DropRIncrease(311),// 
    IgnoreMobpdpR(312),// 

    BdR(318),//
    CapDebuff(314),// 
    Exceed(315),// 
    DiabolikRecovery(316),// 
    FinalAttackProp(317),// 
    ExceedOverload(318),// 
    OverloadCount(319),// 
    BuckShot(320),// 

    FireBomb(321),// 
    HalfstatByDebuff(322),// 
    SurplusSupply(323),// 
    SetBaseDamage(324),// 
    EVAR(325),// 
    NewFlying(326),// 
    AmaranthGenerator(327),// 
    OnCapsule(328),// 

    CygnusElementSkill(329),// 
    StrikerHyperElectric(330),// 
    EventPointAbsorb(331),// 
    EventAssemble(332),// 
    StormBringer(333),// 
    ACCR(334),//  回避:命中
    DEXR(335),// 
    Albatross(342),// Checked 信天翁

    Translucence(343),// 神之子透明 343
    PoseType(344),//  日月转换 344
    PoseTypeBool(345),// 345
    LightOfSpirit(346),// Checked 元素:灵魂
    ElementSoul(347),//
    GlimmeringTime(347),//
    TrueSight(348),//
    SoulExplosion(349),//
    SoulMP(350),//

    FullSoulMP(351),//
    SoulSkillDamageUp(352),//
    Restoration(353),// Checked 元气恢复
    ElementalCharge(356),// 元素冲击
    CrossOverChain(354), // Checked 355 交叉锁链
    ChargeBuff(350),//  连环环破
    Reincarnation(351),// 
    KnightsAura(352),//  抗震防御

    ChillingStep(357),// 寒冰步
    DotBasedBuff(354),// 元素爆破?
    BlessEnsenble(355),// 祈祷众生
    ComboCostInc(356), //需要检查
    ExtremeArchery(357),// 极限射箭
    NaviFlying(358),//
    QuiverCatridge(359),// 三彩箭矢
    AdvancedQuiver(360),// 进阶箭筒

    UserControlMob(361),//
    ImmuneBarrier(362),// 
    ArmorPiercing(363),//  防甲穿透
    ZeroAuraStr(364),// 圣洁之力
    ZeroAuraSpd(365),//  神圣迅捷
    CriticalGrowing(366),//  暴击蓄能
    QuickDraw(367),//  神速衔接
    BowMasterConcentration(368), // 372  集中精神

    TimeFastABuff(369),//  提速时刻_侦查
    TimeFastBBuff(370),//  提速时刻_战斗
    GatherDropR(371),// 
    AimBox2D(372),// 
    IncMonsterBattleCaptureRate(373),// 
    CursorSniping(374),// 
    DebuffTolerance(375),// 
    Unk376(376),// 
    DotHealHPPerSecond(377),// 

    SpiritGuard(378),// 招魂结界
    Unk379(379),// 九死一生
    PreReviveOnce(380),// 
    SetBaseDamageByBuff(381),// 
    LimitMP(382),// 
    ReflectDamR(383),// 
    ComboTempest(384),// 
    MHPCutR(385),// 
    MMPCutR(386),// 

    SelfWeakness(387),// 
    ElementDarkness(388),// 
    FlareTrick(389),// 
    Ember(390),// 
    Dominion(391),// 
    SiphonVitality(392),// 
    DarknessAscension(393),// 
    BossWaitingLinesBuff(394),// 

    DamageReduce(395),// 
    ShadowServant(396),// 
    ShadowIllusion(397),// 
    KnockBack(398),// 
    AddAttackCount(399),// 
    ComplusionSlant(400),// 
    JaguarSummoned(401),// 
    JaguarCount(402),// 

    SSFShootingAttack(403),// 
    DevilCry(404),// 
    ShieldAttack(405),// 
    BMageAura(406),// 
    DarkLighting(407),// 
    AttackCountX(408),// 
    BMageDeath(409),// 
    BombTime(410),// 
    NoDebuff(411),// 
    BattlePvPMikeShield(412),// 
    BattlePvPMikeBugle(413),// 
    XenonAegisSystem(414),// 
    AngelicBursterSoulSeeker(415),// 
    HiddenPossession(416),// 
    NightWalkerBat(417),// 
    NightLordMark(418),// 
    WizardIgnite(419),// 
    FireBarrier(420),// 
    ChangeFoxMan(421),// 
    DivineEcho(422),// v203.2 (Paladin V Buff).
    Unk423(423),
    Unk424(424),
    Unk425(425),
    RIFT_OF_DAMNATION(426),// 
    Unk427(427),
    Unk428(428),
    Unk429(429),
    Unk430(430),
    Unk431(431),
    BattlePvPHelenaMark(432),// 
    BattlePvPHelenaWindSpirit(433),//
    BattlePvPLangEProtection(433),//结合灵气
    BattlePvPLeeMalNyunScaleUp(434),//
    BattlePvPRevive(435),//   SECONDARY_STAT_BattlePvP_LangE_Protection
    PinkbeanAttackBuff(437),// 
    PinkbeanRelax(438),// 
    PinkbeanRollingGrade(439),// 
    PinkbeanYoYoStack(440),// 
    RandAreaAttack(440),//  SECONDARY_STAT_PinkbeanRollingGrade
    Unk442(442),
    NextAttackEnhance(443),
    AranBeyonderDamAbsorb(444),
    AranCombotempastOption(445),
    NautilusFinalAttack(446),
    ViperTimeLeap(447),
    RoyalGuardState(448),// 
    RoyalGuardPrepare(449),// 
    MichaelSoulLink(450),// 
    MichaelStanceLink(451),// 
    TriflingWhimOnOff(452),// 
    AddRangeOnOff(453),// 

    KinesisPsychicPoint(454),// 
    KinesisPsychicOver(455),// 
    KinesisPsychicShield(456),// 
    KinesisIncMastery(457),// 泰山? 459?
    KinesisPsychicEnergeShield(458),// 
    BladeStance(459),// 
    DebuffActiveSkillHPCon(460),// 
    DebuffIncHP(461),// 

    BowMasterMortalBlow(462),//
    AngelicBursterSoulResonance(463),// 
    Fever(-1),
    IgnisRore(464),// 
    RpSiksin(465),// 
    TeleportMasteryRange(466),// 
    FixCoolTime(467),// 
    IncMobRateDummy(468),// 

    AdrenalinBoost(469),//  激素狂飙
    AranSmashSwing(469),//
    AranDrain(471),//
    AttackRecovery(471), //攻击恢复HP百分比
    AranBoostEndHunt(472),// 
    HiddenHyperLinkMaximization(473),// 
    RWCylinder(474),// 
    RWCombination(475),// 
    Unk476(476),// 
    RWMagnumBlow(477),// 

    RWBarrier(478),// 
    RWBarrierHeal(478),//  忍耐之盾
    RWMaximizeCannon(480),// 
    RWOverHeat(481),// 
    UsingScouter(482),// 
    RWMovingEvar(483),// 
    Stigma(484),// 
    Unk485(485),
    Unk486(486),
    Unk487(487),
    Unk488(488),
    Unk489(489),
    Unk490(490),
    Unk491(491),
    Unk492(492),
    LightningCascade(493),// 
    BulletBarrage(494),// 
    Unk495(495),
    AuraScythe(496),// 
    Unk497(497),
    Unk498(498),
    Unk499(499),
    Unk500(500),
    Unk501(501),
    ManaOverload(502),// 
    Unk503(502),  // SECONDARY_STAT_UNK476
    Unk504(504),
    SpreadThrow(505),
    WindEnergy(506),
    MassDestructionRockets(507),// Used for:   Cannonneer 1st V  |  Battle Mage 2nd V  |  Buccaneer 3rd V
    ShadowAssault(508),
    Unk509(509),
    Unk510(510),
    Unk511(511),
    BlitzShield(512),
    Unk513(513),
    FreudWisdom(514),//  or SKILL_COOLTIME_REDUCE_R
    CoreOverload(515),// 
    Spotlight(516),// angelic buster v skill
    Unk517(517),// 
    Unk518(518),
    CrystallineWings(519),// v203.2
    Unk520(520),
    Unk521(521),
    Overdrive(522),//  or attack power
    EtherealForm(523),// 
    LastResort(524),// 
    Unk525(525),
    Unk526(526),// 
    Unk527(527),// 
    Unk528(528),
    Unk529(529),// 
    Unk530(530),
    Unk531(531),
    Unk532(532),
    Unk533(533),
    Unk534(534),
    Unk535(535),
    Unk536(536),
    Unk537(537),
    Unk538(538),// 
    Unk539(539),// 
    Unk540(539),
    Unk541(541),
    SpecterEnergy(542),// 
    SpecterState(543),// 
    BasicCast(544),// 
    ScarletCast(545),// 
    GustCast(546),// 
    AbyssalCast(547),// 
    ImpendingDeath(548),// 
    AbyssalRecall(549),// 
    ChargeSpellAmplifier(550),// 
    InfinitySpell(551),// 
    ConversionOverdrive(552),// 
    Unk553(553),
    Unk554(554),
    Unk555(555),
    Unk556(556),
    Unk557(557),
    Unk558(558),
    Unk559(559),
    Unk560(560),
    Unk561(561),
    Unk562(562),
    Unk563(563),
    Unk564(564),
    Unk565(565),
    Unk566,
    Unk567,
    Unk568,
    Unk569,
    Unk570,
    Unk571,
    Unk572,
    Unk573,
    Unk574,
    Unk575,
    Unk576,
    Unk577,
    Unk578,
    Unk579,
    HayatoStance(580),// 
    HayatoStanceBonus(581),// 
    Unk582,
    Unk583,
    Unk584,
    Unk585,
    Unk586,
    Unk587,
    EyeForEye(588),// 
    WillowDodge(588),//
    Unk465(590),// 
    HayatoPAD(591),// 
    HayatoHPR(592),// 
    HayatoMPR(593),// 
    HayatoBooster(594),// 
    Unk595,// 
    Unk596,// 
    Jinsoku(597),// 
    HayatoCr(598),// 
    HakuBlessing(599),// 
    HayatoBoss(600),// 
    BattoujutsuAdvance(601),// 
    Unk602,// 
    Unk603,// 
    BlackHeartedCurse(604),// 
    BeastMode(605),// 
    TeamRoar(606),// 
    Unk607(607),// 
    Unk608(608),// 
    Unk609(609),// 
    Unk610(610),// 
    Unk611(611),// 
    Unk612(612),// 
    Unk613(613),// 
    Unk614(614),// 
    Unk615(615),// 
    Unk616(616),// 
    Unk617(617),// 
    Unk618(618),// 
    Unk619(619),// 
    Unk620,
    Unk621,
    Unk622,
    Unk623,
    Unk624,
    Unk625,
    Unk626,
    EnergyCharged(627),// 
    DashSpeed(628),// 
    DashJump(629),// 
    RideVehicle(630),// 
    PartyBooster(631),//  - or 631 according to my sniffs
    GuidedBullet(632),// 
    Undead(633),// 
    RideVehicleExpire(634),//
    unk690(690),
    unk691(691),
    unk692(692),
    unk693(693),
    unk694(694), //
    unk695(695),
    unk696(696), // 能量获得
    unk697(697), //疾驰速度
    unk698(698), //疾驰跳跃
    unk699(699),  //骑兽技能
    unk700(700), //极速领域
    unk701(701),// 导航辅助
    unk702(702), //SECONDARY_STAT_Undead
    unk703(703), // SECONDARY_STAT_RideVehicleExpire
    unk704(704),
    unk705(705),
    unk706(706),
    unk707(707),
    unk708(708),
    unk709(709),
    unk720(720),
    unk721(721),
    ;

    private final int bitPos;
    private final int val;
    private final int pos;
    public static final int length = 33;

    private static final List<CharacterTemporaryStat> ORDER = Arrays.asList(
            STR, INT, DEX, LUK,
            PAD, PDD, MAD, ACC, EVA, EVAR,
            Craft, Speed, Jump,
            EMHP, EMMP, EPAD, EMAD, EPDD,
            MagicGuard, DarkSight, Booster, PowerGuard, Guard,
            MaxHP, MaxMP, Invincible, SoulArrow, Stun, Shock,
            Unk82, Unk83, Unk84, Poison, Seal, Darkness, ComboCounter, WeaponCharge, ElementalCharge,
            HolySymbol, MesoUp, ShadowPartner, PickPocket, MesoGuard, Thaw, Weakness, WeaknessMdamage,
            Curse, Slow, TimeBomb, BuffLimit, Team, DisOrder, Thread,
            Morph, Ghost,
            Regen, BasicStatUp, Stance, SharpEyes, ManaReflection, Attract, NoBulletConsume, StackBuff, Trinity,
            Infinity, AdvancedBless, IllusionStep, Blind, Concentration, BanMap, MaxLevelBuff, Barrier, DojangShield, ReverseInput,
            Unk114, Unk115, MesoUpByItem, ItemUpByItem, RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, DojangBerserk, DojangInvincible, SoulMasterFinal,
            WindBreakerFinal, ElementalReset, HideAttack, EventRate, ComboAbilityBuff, ComboDrain, ComboBarrier, PartyBarrier,
            BodyPressure, RepeatEffect, ExpBuffRate, StopPortion, StopMotion, Fear, MagicShield, MagicResistance, SoulStone,
            Flying, NewFlying, NaviFlying, Frozen, Frozen2, Web,
            Enrage, NotDamaged, FinalCut, HowlingAttackDamage, BeastFormDamageUp, Dance, OnCapsule,
            Cyclone, Unk165, Conversion, Revive, PinkbeanMinibeenMove, Sneak, Mechanic, DrawBack,
            BeastFormMaxHP, Dice, BlessingArmor, BlessingArmorIncPAD,
            DamR, TeleportMasteryOn, CombatOrders, Beholder, DispelItemOption, DispelItemOptionByField,
            Inflation, OnixDivineProtection, Bless, Explosion, DarkTornado, IncMaxHP, IncMaxMP,
            PVPDamage, PVPDamageSkill, PvPScoreBonus, PvPInvincible, PvPRaceEffect,
            HolyMagicShell, InfinityForce, AmplifyDamage, KeyDownTimeIgnore, MasterMagicOn,
            AsrR, AsrRByItem, TerR, DamAbsorbShield, Roulette, Event, SpiritLink,
            CriticalBuff, DropRate, PlusExpRate, ItemInvincible, ItemCritical, ItemEvade,
            Event2, VampiricTouch, DDR, IncTerR, IncAsrR, DeathMark, PainMark,
            UsefulAdvancedBless, Lapidification, VampDeath, VampDeathSummon,
            VenomSnake, CarnivalAttack, CarnivalDefence, CarnivalExp, SlowAttack, PyramidEffect, HollowPointBullet, KeyDownMoving, KeyDownAreaMoving, CygnusElementSkill,
            IgnoreTargetDEF, Invisible, ReviveOnce, AntiMagicShell,
            EnrageCr, EnrageCrDam, BlessOfDarkness, LifeTidal,
            Judgement, DojangLuckyBonus, HitCriDamR, Larkness, SmashStack, ReshuffleSwitch, SpecialAction, ArcaneAim,
            StopForceAtomInfo, SoulGazeCriDamR, SoulRageCount, PowerTransferGauge, AffinitySlug, SoulExalt, HiddenPieceOn,
            BossShield, MobZoneState, GiveMeHeal, TouchMe, Contagion, ComboUnlimited,
            IgnorePCounter, IgnoreAllCounter, IgnorePImmune, IgnoreAllImmune, Unk293,
            FinalJudgement, KnightsAura, IceAura, FireAura, VengeanceOfAngel, HeavensDoor, Preparation, BullsEye, IncEffectHPPotion, IncEffectMPPotion,
            SoulMP, FullSoulMP, SoulSkillDamageUp,
            BleedingToxin, IgnoreMobDamR, Asura, Unk306, FlipTheCoin, UnityOfPower, Stimulate, ReturnTeleport, CapDebuff,
            DropRIncrease, IgnoreMobpdpR, BdR,
            Exceed, DiabolikRecovery, FinalAttackProp, ExceedOverload, DevilishPower,
            OverloadCount, BuckShot, FireBomb, HalfstatByDebuff, SurplusSupply, SetBaseDamage, AmaranthGenerator,
            StrikerHyperElectric, EventPointAbsorb, EventAssemble, StormBringer, ACCR, DEXR, Albatross, Translucence,
            PoseType, LightOfSpirit, ElementSoul, GlimmeringTime, Restoration, ComboCostInc, ChargeBuff,
            TrueSight, CrossOverChain, ChillingStep, Reincarnation, DotBasedBuff, BlessEnsenble, ExtremeArchery,
            QuiverCatridge, AdvancedQuiver, UserControlMob, ImmuneBarrier, ArmorPiercing, ZeroAuraStr, ZeroAuraSpd,
            CriticalGrowing, QuickDraw, BowMasterConcentration, TimeFastABuff, TimeFastBBuff, GatherDropR, AimBox2D,
            IncMonsterBattleCaptureRate, CursorSniping, DebuffTolerance, Unk376, DotHealHPPerSecond, SpiritGuard, Unk379,
            PreReviveOnce, SetBaseDamageByBuff, LimitMP, ReflectDamR, ComboTempest, MHPCutR, MMPCutR, SelfWeakness,
            ElementDarkness, FlareTrick, Ember, Dominion, SiphonVitality, DarknessAscension, BossWaitingLinesBuff,
            DamageReduce, ShadowServant, ShadowIllusion,
            AddAttackCount, ComplusionSlant, JaguarSummoned, JaguarCount, SSFShootingAttack, DevilCry, ShieldAttack, BMageAura,
            DarkLighting, AttackCountX, BMageDeath, BombTime, NoDebuff,
            XenonAegisSystem, AngelicBursterSoulSeeker, HiddenPossession, NightWalkerBat, NightLordMark, WizardIgnite,
            Unk423, Unk424,
            BattlePvPHelenaMark, BattlePvPHelenaWindSpirit, BattlePvPLangEProtection, BattlePvPLeeMalNyunScaleUp, BattlePvPRevive, PinkbeanAttackBuff,
            RandAreaAttack, Unk442, BattlePvPMikeShield, BattlePvPMikeBugle,
            PinkbeanRelax, PinkbeanYoYoStack, WindEnergy,
            NextAttackEnhance, AranBeyonderDamAbsorb, AranCombotempastOption, NautilusFinalAttack, ViperTimeLeap,
            RoyalGuardState, RoyalGuardPrepare, MichaelSoulLink, MichaelStanceLink, TriflingWhimOnOff, AddRangeOnOff,
            KinesisPsychicPoint, KinesisPsychicOver, KinesisPsychicShield, KinesisIncMastery, KinesisPsychicEnergeShield,
            BladeStance, DebuffActiveSkillHPCon, DebuffIncHP, BowMasterMortalBlow, AngelicBursterSoulResonance, Fever,
            IgnisRore, RpSiksin, TeleportMasteryRange, FireBarrier, ChangeFoxMan,
            FixCoolTime, IncMobRateDummy, AdrenalinBoost, AranSmashSwing, AranDrain, AttackRecovery, AranBoostEndHunt, HiddenHyperLinkMaximization,
            RWCylinder, RWCombination, Unk476, RWMagnumBlow, RWBarrier, RWBarrierHeal, RWMaximizeCannon, RWOverHeat,
            RWMovingEvar, Stigma, Unk485, Unk486, Unk487, Unk488, Unk489, Unk490, Unk491, Unk492,
            LightningCascade, BulletBarrage, AuraScythe, Unk497, Unk498, Unk499, Unk500, DivineEcho,
            Unk501, ManaOverload, Unk503, Unk504, SpreadThrow,
            MassDestructionRockets, ShadowAssault, Unk509, Unk510, Unk511, BlitzShield, Unk513, FreudWisdom, CoreOverload, Unk425,
            CrystallineWings, Unk520, RIFT_OF_DAMNATION,
            Unk517, Spotlight, Unk162, Unk518,
            Unk521, Overdrive, EtherealForm, LastResort, Unk525, Unk526, Unk527, Unk528, Unk529, Unk163,
            Unk530, Unk531, Unk532, Unk534, Unk535, Unk536, Unk537, Unk538, Unk539, Unk540, Unk541,
            SpecterEnergy, SpecterState, BasicCast, ScarletCast, GustCast, AbyssalCast, ImpendingDeath, AbyssalRecall, ChargeSpellAmplifier,
            InfinitySpell, ConversionOverdrive, Unk553, Unk554, Unk555, Unk556, Unk557,
            Unk559, Unk560, Unk561, Unk562, Unk427,
            Unk563, Unk428,
            Unk564, Unk565, Unk566, Unk567, Unk429, Unk430,
            Unk568, Unk569, DropPer, Unk431,
            Unk570, Unk571, Unk572, Unk573, Unk574, IncMaxDamage,
            Unk575, Unk576, Unk578, Unk579, IndieUnk13,
            HayatoStance, EyeForEye, HayatoStanceBonus,
            Unk583, Unk584, Unk585, Unk586, Unk587,
            HayatoPAD, HayatoHPR, HayatoMPR, HayatoBooster, Unk595, Unk596, Jinsoku, HayatoCr, Unk582,
            HakuBlessing, HayatoBoss, BattoujutsuAdvance, Unk602,
            TeamRoar, Unk607, Unk608, Unk609, Unk610, Unk611, Unk612, Unk613,
            Unk615, Unk616, Unk617, Unk618, Unk619
    );

    private static final List<CharacterTemporaryStat> REMOTE_ORDER = Arrays.asList(
            Speed, Poison, Seal, Darkness, WeaponCharge, ElementalCharge, Stun, Shock, Unk84, Unk83, Weakness, WeaknessMdamage,
            Curse, Slow, PvPRaceEffect, TimeBomb, Team, DisOrder, Thread, Unk82, ShadowPartner, Morph, Ghost, Attract, Magnet, MagnetArea,
            NoBulletConsume, BanMap, Barrier, DojangShield, ReverseInput, RespectPImmune, RespectMImmune, DefenseAtt, DefenseState,
            DojangBerserk, RepeatEffect, Unk602, StopPortion, StopMotion, Fear, MagicShield, Frozen, Frozen2, Web, DrawBack, FinalCut,
            OnCapsule, Mechanic, Inflation, Explosion, DarkTornado, AmplifyDamage, HideAttack, DevilishPower, SpiritLink, Event, Event2,
            DeathMark, PainMark, Lapidification, VampDeath, VampDeathSummon, VenomSnake, PyramidEffect, KillingPoint, PinkbeanRollingGrade,
            IgnoreTargetDEF, Invisible, Judgement, KeyDownAreaMoving, StackBuff, BlessOfDarkness, Larkness, ReshuffleSwitch, SpecialAction,
            StopForceAtomInfo, SoulGazeCriDamR, PowerTransferGauge, BlitzShield, AffinitySlug, SoulExalt, HiddenPieceOn, SmashStack, MobZoneState,
            GiveMeHeal, TouchMe, Contagion, ComboUnlimited, IgnorePCounter, IgnoreAllCounter, IgnorePImmune, IgnoreAllImmune, Unk293,
            FinalJudgement, KnightsAura, IceAura, FireAura, HeavensDoor, DamAbsorbShield, NotDamaged, BleedingToxin,
            WindBreakerFinal, IgnoreMobDamR, Asura, Unk306, UnityOfPower, Stimulate, ReturnTeleport, CapDebuff, OverloadCount, FireBomb,
            SurplusSupply, NewFlying, NaviFlying, AmaranthGenerator, CygnusElementSkill, StrikerHyperElectric, EventPointAbsorb, EventAssemble,
            Albatross, Translucence, PoseTypeBool, LightOfSpirit, ElementSoul, GlimmeringTime, Reincarnation, Beholder, QuiverCatridge, ArmorPiercing,
            ZeroAuraStr, ZeroAuraSpd, ImmuneBarrier, FullSoulMP, AntiMagicShellBool, Dance, Unk379, Unk425, ComboTempest, HalfstatByDebuff,
            ComplusionSlant, JaguarSummoned, BMageAura, BombTime, Unk491, Unk492, LightningCascade, BulletBarrage, Unk495, AuraScythe,
            Unk497, DarkLighting, AttackCountX, FireBarrier, KeyDownMoving, MichaelSoulLink, KinesisPsychicEnergeShield, BladeStance,
            IgnisRore, AdrenalinBoost, RWBarrier, Unk476, RWMagnumBlow, Unk253, Unk254, Unk255, Unk256, Unk257, Stigma, DivineEcho, Unk503,
            Unk504, Unk485, ManaOverload, CursorSniping, Unk517, Spotlight, CoreOverload, FreudWisdom, ComboCounter, Overdrive, EtherealForm,
            LastResort, Unk525, Unk526, Unk527, Unk528, Unk529, Unk520, Unk530, Unk531, Unk532, Unk533,
            SpecterState, ImpendingDeath, Unk556, Unk531, Unk565, GrandGuardian, HayatoStanceBonus, BeastMode, TeamRoar, Unk586,
            HayatoBooster, Unk587, HayatoPAD, HayatoHPR, HayatoMPR, HayatoCr, HayatoBoss, Stance, BattoujutsuAdvance, Unk603,
            BlackHeartedCurse, EyeForEye, Unk608, Unk612, Unk613, Unk614, Unk616, Unk617, Unk618, Unk585, Unk513
    );

    CharacterTemporaryStat(int bitPos) {
        this.bitPos = bitPos;
        this.val = 1 << (31 - bitPos % 32);
        this.pos = bitPos / 32;
    }


    CharacterTemporaryStat() {
        int bitPos = -1;
        if (name().contains("Unk")) {
            bitPos = Integer.parseInt(name().replace("Unk", ""));
        }
        this.bitPos = bitPos;
        this.val = 1 << (31 - bitPos % 32);
        this.pos = bitPos / 32;
    }

    public static Map<CharacterTemporaryStat, List<Option>> getSpawnBuffs() {
        Map<CharacterTemporaryStat, List<Option>> spawnBuffs = new HashMap<>();
        spawnBuffs.put(IndieUnk3, null); //
        spawnBuffs.put(IndieUnk4, null); //
        spawnBuffs.put(IndieUnk6, null); //
        spawnBuffs.put(IndieUnk10, null);//
        spawnBuffs.put(PyramidEffect, null); //
        spawnBuffs.put(KillingPoint, null); //
        spawnBuffs.put(BattlePvPLangEProtection, null);//
        spawnBuffs.put(BattlePvPRevive, null);//
        spawnBuffs.put(RandAreaAttack, null);//
        spawnBuffs.put(AranSmashSwing, null);//
        spawnBuffs.put(RWBarrierHeal, null);//
        spawnBuffs.put(Unk503, null); //
        spawnBuffs.put(Unk540, null); //
        spawnBuffs.put(Unk583, null); //
        spawnBuffs.put(WillowDodge, null); //

        spawnBuffs.put(unk695, null);
        spawnBuffs.put(unk696, null);
        spawnBuffs.put(unk697, null);
        spawnBuffs.put(unk698, null);
        spawnBuffs.put(unk699, null);
        spawnBuffs.put(unk700, null);
        spawnBuffs.put(unk701, null);
        spawnBuffs.put(unk702, null);
        spawnBuffs.put(unk703, null);
        spawnBuffs.put(unk704, null);


        return spawnBuffs;
    }

    public boolean isEncodeInt() {
        switch (this) {
            case CarnivalDefence:
            case SpiritLink:
            case DojangLuckyBonus:
            case SoulGazeCriDamR:
            case PowerTransferGauge:
            case ReturnTeleport:
            case ShadowPartner:
            case AranSmashSwing:
            case SetBaseDamage:
            case QuiverCatridge:
            case ImmuneBarrier:
            case NaviFlying:
            case Dance:
            case SetBaseDamageByBuff:
            case DotHealHPPerSecond:
            case SpiritGuard:
            case IncMaxDamage:
            case Unk612:
            case MagnetArea:
            case DivineEcho:
            case Unk306:
            case VampDeath:
            case BlitzShield:
            case Unk162:
            case RWBarrier:
                return true;
            default:
                return false;
        }
    }

    public boolean isIndie() {
        return getBitPos() < IndieStatCount.getBitPos();
    }

    public boolean isMovingEffectingStat() {
        switch (this) {
            case Speed:
            case Jump:
            case Stun:
            case Weakness:
            case Slow:
            case Morph:
            case Ghost:
            case BasicStatUp:
            case Attract:
            case DashSpeed:
            case DashJump:
            case Flying:
            case Frozen:
            case Frozen2:
            case Lapidification:
            case IndieSpeed:
            case IndieJump:
            case KeyDownMoving:
            case EnergyCharged:
            case Mechanic:
            case Magnet:
            case MagnetArea:
            case VampDeath:
            case VampDeathSummon:
            case GiveMeHeal:
            case DarkTornado:
            case NewFlying:
            case NaviFlying:
            case UserControlMob:
            case Dance:
            case SelfWeakness:
            case BattlePvPHelenaWindSpirit:
            case IndieUnk10:
            case BattlePvPLeeMalNyunScaleUp:
            case TouchMe:
            case IndieForceSpeed:
            case IndieForceJump:
            case RideVehicle:
            case RideVehicleExpire:
            case Unk538:
            case Unk539:
                return true;
            default:
                return false;
        }
    }

    public int getVal() {
        return val;
    }

    public int getPos() {
        return pos;
    }

    public int getOrder() {
        return ORDER.indexOf(this);
    }

    public int getRemoteOrder() {
        return REMOTE_ORDER.indexOf(this);
    }

    public boolean isRemoteEncode4() {
        switch (this) {
            case NoBulletConsume:// 
            case RespectPImmune:// 
            case RespectMImmune:// 
            case DefenseAtt:// 
            case DefenseState:// 
            case MagicShield:// 
            case PyramidEffect:// 
            case BlessOfDarkness:// 
            case Unk306:// 
            case ImmuneBarrier:// 
            case Dance:// 
            case Unk379:// 
            case Unk425:// 
            case SpiritGuard:
            case KinesisPsychicEnergeShield:// 
            case AdrenalinBoost:// 
            case RWBarrier:// 
            case Unk476:// 
            case RWMagnumBlow:// 
            case DivineEcho:// 
            case Unk503:// 
            case Unk531:// 
            case Unk612:// 
            case Unk613:// 
            case Unk614:// 
            case Unk617:// 
            case Unk618:// 
            case HayatoStance:
                //case Unk487:
            case Unk488:
            case Unk489:
                return true;
            default:
                return false;
        }
    }

    public boolean isRemoteEncode1() {
        switch (this) {
            case Speed:// 
            case Poison:// 
            case Seal:// 
            case Shock:// 
            case Team:// 
            case Cyclone:
            case OnCapsule:// 
            case KillingPoint:// 
            case PinkbeanRollingGrade:// 
            case ReturnTeleport:// 
            case FireBomb:// 
            case SurplusSupply:// 
            case Unk585:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotEncodeReason() {
        switch (this) {
            case Speed:// 
            case Poison:// 
            case Seal:// 
            case ElementalCharge:// 
            case Shock:// 
            case Team:// 
            case Ghost:// 
            case NoBulletConsume:// 
            case RespectPImmune:// 
            case RespectMImmune:// 
            case DefenseAtt:// 
            case DefenseState:// 
            case MagicShield:// 
            case Cyclone:
            case OnCapsule:// 
            case PyramidEffect:// 
            case KillingPoint:// 
            case PinkbeanRollingGrade:// 
            case StackBuff:// 
            case BlessOfDarkness:// 
            case SurplusSupply:// 
            case ImmuneBarrier:// 
            case AdrenalinBoost:// 
            case RWBarrier:// 
            case Unk476:// 
            case RWMagnumBlow:// 
            case Unk504:// 
            case Unk485:// 
            case ManaOverload:// 
            case Unk530:// 
            case Unk613:// 
            case Unk614:// 
            case Unk617:// 
            case Unk618:// 
            case HayatoStance:
            case Unk488:
            case Unk489:
                //case Unk460:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotEncodeAnything() {
        switch (this) {
            // not encoded in client
            case DarkSight:
            case SoulArrow:
            case DojangInvincible:
            case Flying:
            case Sneak:
            case BeastFormDamageUp:
            case BlessingArmor:
            case BlessingArmorIncPAD:
            case HolyMagicShell:
            case VengeanceOfAngel:
            case UserControlMob:
            case Unk565:
                // Special encoding
            case FullSoulMP:// 
            case AntiMagicShellBool:// 
            case PoseTypeBool:
                return true;
            default:
                return false;
        }
    }


    @Override
    public int compare(CharacterTemporaryStat o1, CharacterTemporaryStat o2) {
        if (o1.getPos() < o2.getPos()) {
            return -1;
        } else if (o1.getPos() > o2.getPos()) {
            return 1;
        }
        int o1Val = o1.getVal();
        if (o1Val == 0x8000_0000) {
            o1Val = Integer.MAX_VALUE;
        }
        int o2Val = o2.getVal();
        if (o2Val == 0x8000_0000) {
            o2Val = Integer.MAX_VALUE;
        }

        if (o1Val > o2Val) {
            return -1;
        } else if (o1Val < o2Val) {
            return 1;
        }
        return 0;
    }

    public int getBitPos() {
        return bitPos;
    }


    public static void main(String[] args) {
        for (CharacterTemporaryStat stat : CharacterTemporaryStat.values()) {
            System.out.println(stat.toString() + " " + stat.getBitPos() + " " + Integer.toHexString(stat.getVal()) + " " + stat.getPos());
        }
    }
}
