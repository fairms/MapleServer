package im.cave.ms.client.character.temp;

import im.cave.ms.client.character.Option;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum CharacterTemporaryStat implements Comparator<CharacterTemporaryStat> {
    IndiePAD(0), //攻击力
    IndieMAD(1),
    IndiePDD(2),
    IndieMDD(-1),
    IndieMHP(3), //最大HP
    IndieMHPR(4),
    IndieMMP(5), //最大MP
    IndieMMPR(6),

    IndieACC(7), //命中
    IndieEVA(8), //回避
    IndieJump(9),
    IndieSpeed(10),
    IndieAllStat(12), //所有冒险岛勇士增加的属性
    IndieDodgeCriticalTime(12),

    IndieEXP(14),
    IndieBooster(15),//攻击速度 绿药

    IndieFixedDamageR(16),
    PyramidStunBuff(17),
    PyramidFrozenBuff(18),
    PyramidFireBuff(19),
    PyramidBonusDamageBuff(20),
    IndieRelaxEXP(21),
    IndieSTR(22),
    IndieDEX(23),

    IndieINT(24),
    IndieLUK(25),
    IndieDamR(26), //伤害
    IndieScriptBuff(27),
    IndieMDF(28),
    IndieMaxDamageOver(-1),
    IndieAsrR(29), //异常抗性
    IndieTerR(30),//属性抗性

    IndieCr(31), //暴击率
    IndiePDDR(32), //防御力百分比
    IndieCrDam(33), //爆伤
    IndieBDR(34), //BOSS伤
    IndieStatR(35),
    IndieStance(36), //稳如泰山
    IndieIgnoreMobpdpR(37), //无视
    IndieEmpty(49),

    IndiePADR(39), //攻击力百分比
    IndieMADR(40),
    IndieCrDamR(41),
    IndieEVAR(42),
    IndieMDDR(-1),
    IndieDrainHP(-1),
    IndiePMdR(43),//最终伤害 重击研究Ⅱ
    IndieMaxDamageOverR(-1),

    IndieForceJump(44),
    IndieForceSpeed(45),
    IndieQrPointTerm(46),
    IndieUnk1(47),
    IndieUnk2(48),
    IndieUnk3(49),
    IndieUnk4(50),
    IndieUnk5(51), //释放过程中无敌
    IndieUnk6(52),
    IndieUnk7(53), //烟幕弹 樱花结界 范围减伤
    IndieUnk8(54),
    IndieUnk9(55),
    IndieUnk10(56),
    IndieUnk11(57),//减少受到的伤害?
    IndieUnk12(58),
    IndieUnk13(59),
    IndieUnk14(60),
    IndieUnk15(61),
    IndieUnk16(62),
    IndieUnk17(63), //dropRate 神圣祈祷
    IndieStatCount(71),

    PAD(78),
    PDD(79),
    MAD(80),
    ACC(81),
    EVA(82),
    Craft(83),
    Speed(84),
    Jump(85),
    MagicGuard(86),//魔法盾
    DarkSight(87), //隐身术
    Booster(88), //快速武器
    PowerGuard(89), //愤怒之火减伤
    MaxHP(90),
    MaxMP(91),
    Invincible(92),
    SoulArrow(93), //无形箭
    Stun(94),
    Unk82(95),
    Unk83(96),
    Unk84(97),
    Poison(98),
    ComboCounter(98),//斗气集中
    unk99(99),
    Seal(100), //祝福之锤
    Darkness(101),
    WeaponCharge(102), //武器充能 冰雪矛
    HolySymbol(103), //神圣祈祷
    MesoUp(104),
    ShadowPartner(105), //影分身
    PickPocket(106), //敛财术
    MesoGuard(107), //金钱盾
    Thaw(108),
    Weakness(109),
    Curse(110),
    Slow(111),
    Morph(112),
    Regen(113), //团队治疗
    BasicStatUp(114), //冒险岛勇士
    Stance(115), //稳如泰山
    SharpEyes(116), //火眼晶晶
    ManaReflection(117),
    Attract(118),
    NoBulletConsume(119), //无限子弹
    Infinity(120),//终极无限
    AdvancedBless(121), //进阶祝福
    @Deprecated
    IllusionStep(122),
    Blind(123),
    Concentration(124),
    BanMap(125),
    MaxLevelBuff(126),
    Unk114(122),
    Unk115(123),
    MesoUpByItem(124),
    Ghost(125),
    Barrier(126),
    ReverseInput(127),
    ItemUpByItem(128),
    RespectPImmune(129),
    RespectMImmune(130),
    DefenseAtt(123),

    DefenseState(124),
    DojangBerserk(125),
    DojangInvincible(126),
    DojangShield(127),
    SoulMasterFinal(128),
    WindBreakerFinal(142),//隐形剑
    ElementalReset(143),//自然力重置
    HideAttack(144),
    EventRate(145),
    ComboAbilityBuff(146),
    ComboDrain(147),
    ComboBarrier(148),
    BodyPressure(149), //抗压
    RepeatEffect(150),
    ExpBuffRate(151), //经验倍率
    StopPortion(152),
    StopMotion(153),
    Fear(154),
    HiddenPieceOn(155),
    MagicShield(156),
    MagicResistance(157),
    SoulStone(158),
    Flying(159),
    Frozen(160),
    AssistCharge(161),
    Enrage(162),//葵花宝典 限制攻击个数都是这个
    DrawBack(163),
    NotDamaged(164),//无敌 龙神
    FinalCut(165), //终极斩
    HowlingAttackDamage(166),
    BeastFormDamageUp(167),
    Dance(168),
    EMHP(169),
    EMMP(170),
    EPAD(171),
    EMAD(172),
    EPDD(173),
    EMDD(-1),
    Guard(174),//完美机甲
    Unk162(170),
    Unk163(171),
    Cyclone(172),
    Unk165(173),
    HowlingCritical(-1),
    HowlingMaxMP(-1),
    HowlingDefence(-1),
    HowlingEvasion(-1),
    Conversion(174),
    Revive(175),
    PinkbeanMinibeenMove(176),
    Sneak(178),//隐匿  \潜入
    Mechanic(179), //金属机甲
    BeastFormMaxHP(180),
    Dice(181), //幸运骰子
    BlessingArmor(182),
    DamR(183),
    TeleportMasteryOn(184),//快速移动精通
    CombatOrders(185),//战斗命令
    Beholder(186), //灵魂助力
    DispelItemOption(187),
    Inflation(188), //巨人药水
    OnixDivineProtection(189),
    Web(190),
    Bless(191),//祝福
    TimeBomb(191),
    DisOrder(192),
    Thread(193),

    Team(194),
    Explosion(195),
    BuffLimit(196),
    STR(198),
    INT(199),
    DEX(200),
    LUK(201),
    DispelItemOptionByField(201),

    DarkTornado(202),
    PVPDamage(195),
    PvPScoreBonus(196),
    PvPInvincible(197),
    PvPRaceEffect(198),
    WeaknessMdamage(199),
    Frozen2(200),
    PVPDamageSkill(201),

    AmplifyDamage(202),
    IceKnight(-1),
    Shock(203),
    InfinityForce(204),
    IncMaxHP(205),
    IncMaxMP(206),
    HolyMagicShell(216),//神圣魔法盾
    KeyDownTimeIgnore(217), //圣光普照
    ArcaneAim(218),
    MasterMagicOn(219),
    AsrR(220), //异常抗性 水盾
    TerR(221),//属性抗性 水盾
    DamAbsorbShield(222), //伤害吸收 水盾 双重防御
    DevilishPower(223),//海蛇螺旋
    Roulette(224), //随机橡木桶
    SpiritLink(225),
    AsrRByItem(226),
    Event(227),
    CriticalBuff(228), //暴击率
    DropRate(229),//爆率
    PlusExpRate(230),
    ItemInvincible(231),
    Awake(223),
    ItemCritical(224),

    ItemEvade(225),
    Event2(226),
    VampiricTouch(227),
    DDR(237), //防御力百分比
    IncCriticalDamMin(-1),
    IncCriticalDamMax(-1),
    IncTerR(238),
    IncAsrR(239),

    DeathMark(240),
    UsefulAdvancedBless(241),
    Lapidification(242),
    VenomSnake(243),
    CarnivalAttack(244),
    CarnivalDefence(245),
    CarnivalExp(246),
    SlowAttack(247),

    PyramidEffect(248),
    KillingPoint(249), //侠盗本能击杀点数
    HollowPointBullet(250),
    KeyDownMoving(251), //暴风类技能
    IgnoreTargetDEF(252), //无视防御力 \龙之献祭 元素：闪电
    ReviveOnce(253), //免死一次 神秘的运气  \时光逆转
    Invisible(254),  //幻影屏障 不可见
    EnrageCr(255),//葵花宝典
    EnrageCrDam(256),//葵花宝典暴击伤害 极限弩暴击伤害...
    Judgement(248),
    DojangLuckyBonus(249),
    PainMark(250),
    Magnet(251),
    MagnetArea(252),
    Unk253(253),
    Unk254(254),
    Unk255(255),
    Unk256(256),
    Unk257(257),
    TideOfBattle(258),
    GrandGuardian(259),
    DropPer(260),
    VampDeath(261),
    GuidedArrow(262), //向导之箭
    BlessingArmorIncPAD(263),
    KeyDownAreaMoving(264),
    Larkness(265), //残影之矢
    StackBuff(274), //双重防御
    BlessOfDarkness(266),
    AntiMagicShell(276),//神圣保护 元素配合
    AntiMagicShellBool(267),
    LifeTidal(268),
    HitCriDamR(269),
    SmashStack(270),

    PartyBarrier(271),
    ReshuffleSwitch(272),
    SpecialAction(273),
    VampDeathSummon(274),
    StopForceAtomInfo(275),
    SoulGazeCriDamR(276),
    SoulRageCount(277),
    PowerTransferGauge(278),

    AffinitySlug(279),
    Trinity(280),
    IncMaxDamage(281),
    BossShield(282),
    MobZoneState(283),
    GiveMeHeal(284),
    TouchMe(285),
    Contagion(286),

    ComboUnlimited(287),
    SoulExalt(295),
    IgnorePCounter(296),
    IgnoreAllCounter(297),
    IgnorePImmune(298), //免疫 至圣领域
    IgnoreAllImmune(299),
    Unk293(300),
    FinalJudgement(301),
    IceAura(302),//寒冰灵气
    FireAura(303),//火焰灵气
    VengeanceOfAngel(304),//天使复仇
    HeavensDoor(305),//天堂之门
    Preparation(306),
    BullsEye(307),//鹰眼
    IncEffectHPPotion(308),
    IncEffectMPPotion(309),
    BleedingToxin(310), //流血剧毒
    IgnoreMobDamR(311), //无视怪物伤害百分比？
    Asura(312), //阿修罗
    Unk306(313),
    FlipTheCoin(314), //幸运钱 & 能量激发
    UnityOfPower(315), //混元归一
    Stimulate(316),// 能量激发?
    ReturnTeleport(317),
    DropRIncrease(318),
    IgnoreMobpdpR(319),
    BdR(320), //BOSS伤 龙之献祭
    CapDebuff(321),
    Exceed(322),
    DiabolicRecovery(323),
    FinalAttackProp(324),
    ExceedOverload(325),
    OverloadCount(326),
    BuckShot(327),//霰弹炮 月光洒落 双倍攻击段数
    FireBomb(328),
    HalfstatByDebuff(329),
    SurplusSupply(330), //尖兵能量
    SetBaseDamage(331),
    EVAR(332),
    NewFlying(333), //心魂漫步 自由飞行 空中悬浮
    AmaranthGenerator(334),//永动引擎
    CygnusElementSkill(335), //元素风
    OnCapsule(336),
    StrikerHyperElectric(337), //开天劈比
    EventPointAbsorb(338),
    EventAssemble(339),
    StormBringer(340), //暴风灭世
    ACCR(-1),
    DEXR(-1),
    LightOfSpirit(341), //灵魂之剑
    UNK342(342),
    Albatross(343), //信天翁
    Translucence(344),
    PoseType(345), //月光洒落 旭日
    PoseTypeBool(346),
    //    LightOfSpirit(346),
    ElementSoul(347), //灵魂：元素
    GlimmeringTime(348), //日月轮转
    TrueSight(349),
    SoulExplosion(350),
    SoulMP(351),
    FullSoulMP(352), //灵魂武器MAX
    SoulSkillDamageUp(353),
    ElementalCharge(354), //元素冲击
    Restoration(355), //元气恢复
    CrossOverChain(356), //交叉锁链
    ChargeBuff(357),
    Reincarnation(358), //重生
    KnightsAura(-1),
    ChillingStep(359),//寒冰步
    DotBasedBuff(360),
    BlessEnsenble(361),//幽冥气息
    ComboCostInc(362),
    ExtremeArchery(363),//极限：弓 极限：弩
    NaviFlying(364),
    QuiverCartridge(365),//三彩箭矢
    AdvancedQuiver(366),//进阶箭筒

    UserControlMob(361),
    ImmuneBarrier(362),
    ArmorPiercing(363),
    ZeroAuraStr(364),
    ZeroAuraSpd(365),
    CriticalGrowing(370),//名流暴击 暴击率
    QuickDraw(367),
    BowMasterConcentration(373), //集中精神

    TimeFastABuff(369),
    TimeFastBBuff(370),
    GatherDropR(371),
    AimBox2D(372),
    IncMonsterBattleCaptureRate(373),
    CursorSniping(374),
    DebuffTolerance(375),
    Unk376(376),
    DotHealHPPerSecond(377),

    SpiritGuard(383),//招魂结界
    Unk379(379),
    PreReviveOnce(380),
    SetBaseDamageByBuff(381), //祈祷 回蓝
    LimitMP(382), //祈祷 回血
    ReflectDamR(383),
    ComboTempest(384),
    MHPCutR(385),
    MMPCutR(386),

    SelfWeakness(387),
    ElementDarkness(392), //元素：黑暗
    FlareTrick(393),
    Ember(394),//引燃
    Dominion(395), //黑暗领地
    SiphonVitality(396), //体力汲取
    DarknessAscension(397),
    BossWaitingLinesBuff(398),

    DamageReduce(399),
    ShadowServant(400), //影子侍从
    ShadowIllusion(401), //黑暗幻影
    KnockBack(398),
    AddAttackCount(399),
    ComplusionSlant(400),
    JaguarSummoned(401),
    JaguarCount(402),

    SSFShootingAttack(403), //火焰咆哮
    DevilCry(407),
    ShieldAttack(408),
    BMageAura(409),
    DarkLighting(410),
    AttackCountX(411),
    BMageDeath(412),
    BombTime(413), //轰炸时间
    NoDebuff(414),
    BattlePvPMikeShield(415),
    BattlePvPMikeBugle(416),
    XenonAegisSystem(417), //宙斯盾系统
    AngelicBursterSoulSeeker(418),
    HiddenPossession(419),//灵狐
    NightWalkerBat(420),//影子蝙蝠
    NightLordMark(421),//刺客标记
    WizardIgnite(422),//燎原之火
    FireBarrier(420),
    ChangeFoxMan(421),
    DivineEcho(422),
    Unk423(423), //紫炎结界
    Unk424(424), //影朋：小白
    Unk425(425), //神圣归一
    RIFT_OF_DAMNATION(426),
    Unk427(427),
    Unk428(428),
    Unk429(429),
    Unk430(430), //全箭发射
    Unk431(431),
    BattlePvPHelenaMark(432),
    BattlePvPHelenaWindSpirit(433),
    BattlePvPLangEProtection(433),
    BattlePvPLeeMalNyunScaleUp(434),
    BattlePvPRevive(435),
    PinkbeanAttackBuff(437),
    PinkbeanRelax(438),
    PinkbeanRollingGrade(439),
    PinkbeanYoYoStack(440),
    RandAreaAttack(440),
    Unk442(445),
    NextAttackEnhance(446),
    AranBeyonderDamAbsorb(447),
    AranCombotempastOption(448),
    NautilusFinalAttack(448),//诺特勒斯战舰
    ViperTimeLeap(449),//伺机待发

    RoyalGuardState(450),
    RoyalGuardPrepare(451),
    MichaelSoulLink(452), //灵魂链接
    MichaelStanceLink(453),
    TriflingWhimOnOff(454), //狂风肆虐：Ⅰ
    AddRangeOnOff(455),

    KinesisPsychicPoint(456), //心魂点
    KinesisPsychicOver(457), //心魂附体
    KinesisPsychicShield(458), //心魂之盾
    KinesisIncMastery(459),
    KinesisPsychicEnergyShield(460), //心魂本能
    BladeStance(461),
    DebuffActiveSkillHPCon(462),
    DebuffIncHP(463),

    BowMasterMortalBlow(464), //贯穿箭
    AngelicBusterSoulResonance(465),
    Fever(-1),
    IgnisRore(466),
    RpSiksin(467),
    TeleportMasteryRange(468),
    FixCoolTime(469),
    IncMobRateDummy(470),

    AdrenalinBoost(471),//激素引擎
    AranSmashSwing(472),
    AranDrain(-1),
    AttackRecovery(473), //黑暗饥渴 生命吸收 攻击时恢复最大血量%
    AranBoostEndHunt(472),
    HiddenHyperLinkMaximization(473),
    RWCylinder(474),
    RWCombination(475),
    Unk476(476),
    RWMagnumBlow(477),

    RWBarrier(478),
    RWBarrierHeal(478),
    RWMaximizeCannon(480),
    RWOverHeat(481),
    UsingScouter(482),
    RWMovingEvar(483),
    Stigma(484),
    Unk485(485),
    Unk486(486),
    Unk487(487),
    Unk488(488),
    Unk489(489),
    Unk490(490),
    Unk491(491), //精准火箭
    Unk492(492),
    LightningCascade(493),
    BulletBarrage(494),
    Unk495(495),
    AuraScythe(496),
    Unk497(497), //祈祷
    Unk498(498),
    Unk499(499),
    Unk500(500),
    Unk501(501),//灵魂武器
    ManaOverload(502),
    Unk503(502),
    Unk504(504),
    SpreadThrow(505),
    WindEnergy(506), //呼啸风暴
    MassDestructionRockets(507),//神明惩戒 怒涛拍岸 蓄能类技能
    ShadowAssault(508),
    Unk509(509),
    Unk510(510),
    Unk511(511),
    BlitzShield(512), //召唤式神
    Unk513(513),
    FreudWisdom(514),
    CoreOverload(515),
    Spotlight(516),
    Unk517(517),
    Unk518(518),
    CrystallineWings(519), //功势之盾
    Unk520(520),
    Unk521(521),
    Overdrive(522),
    EtherealForm(523),
    LastResort(524),
    Unk525(525),
    Unk526(526),
    Unk527(527),
    Unk528(528),
    Unk529(529),
    Unk530(530),
    Unk531(531), //暴击强化 nrt
    Unk532(532),
    Unk533(533),
    Unk534(534),
    Unk535(535),
    Unk536(536),
    Unk537(537),
    Unk538(538),
    Unk539(539),
    Unk540(539),//+5
    Unk541(541),
    SpecterEnergy(542),
    SpecterState(543),
    BasicCast(544),
    ScarletCast(545),
    GustCast(546),
    AbyssalCast(547),
    ImpendingDeath(548),
    AbyssalRecall(549),
    ChargeSpellAmplifier(550),
    InfinitySpell(551),
    ConversionOverdrive(552),
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
    Unk567, //斗争本能
    Unk568, //风墙
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
    HayatoStance(580),
    HayatoStanceBonus(581),
    Unk582,
    Unk583,
    Unk584,
    Unk585,//破坏之黑暗剑灵
    Unk586, //创造之光明剑灵
    Unk587,
    EyeForEye(588),
    WillowDodge(588),
    Unk465(590),
    HayatoPAD(591),
    HayatoHPR(592), //上古指引
    HayatoMPR(593),
    HayatoBooster(594),
    Unk595,
    Unk596,
    Jinsoku(597),
    HayatoCr(598),
    HakuBlessing(599),
    HayatoBoss(600),
    BattoujutsuAdvance(601),
    Unk602,
    Unk603,
    BlackHeartedCurse(604),
    BeastMode(605),
    TeamRoar(606),
    Unk607(607),
    Unk608(608),
    Unk609(609),
    Unk610(610),
    Unk611(611),
    Unk612(612),
    Unk613(613),
    Unk614(614),
    Unk615(615),
    Unk616(616),
    Unk617(617),
    Unk618(618), //御剑屏障
    Unk619(619),
    Unk620, //苍空之子
    Unk621, //缔造
    Unk622, //御剑屏障
    Unk623, //出神
    Unk624,
    Unk625, //贵族精神
    Unk626, //共振
    EnergyCharged(627),
    DashSpeed(628),
    DashJump(629),

    RideVehicle(630), //黄色灵气
    PartyBooster(631), //红色灵气
    GuidedBullet(632), //蓝色灵气
    Undead(633), //黑暗灵气
    RideVehicleExpire(634), //减益灵气
    UNK635(635),//结合灵气
    UNK636(636),
    UNK637(637), //抗震防御
    UNK638(638), //圣洁之力
    UNK639(639), //神圣迅捷
    UNK640(640),
    UNK641(641),
    UNK642(642),
    UNK643(643), //幻影魔迹
    UNK644(644),
    UNK645(645),
    unk670(670),
    unk671(671),
    unk672(672),
    unk673(673),
    unk674(674),
    unk675(675),
    unk676(676),
    unk677(677),
    unk678(678),
    unk679(679),
    unk680(680),
    unk681(681), //破魔阵
    unk682(682), //拔刀姿势
    unk683(683),
    unk684(684), //拔刀术加成
    unk685(685), //避柳
    unk686(686), //武神归来-MHP%
    unk687(687), //武神归来-MMP%
    unk688(688), //武神归来-攻击力
    unk689(689), //迅速
    unk690(690), //一闪
    unk691(691), //小白的祝福
    unk692(692), //灯笼结界
    unk693(693), //厚积薄发
    unk694(694), //晓月流基本技能
    unk695(695),
    unk696(696),
    unk697(697),
    unk698(698),
    unk699(699),
    unk700(700),
    unk701(701),
    unk702(702),
    unk703(703),
    unk704(704), //黎明的aegis
    unk705(705),
    unk706(706),
    unk707(707),
    unk708(708),
    unk709(709),
    unk710(710),
    unk711(711),
    unk712(712), //气魄
    unk713(713),
    unk714(714), //秘技：护身罡气
    unk715(715),
    unk716(716),
    unk717(717),
    unk718(718), //能量获得
    unk719(719), //疾驰 Speed
    unk720(720),//疾驰 Jump
    unk721(721),
    unk722(722),//急速领域
    unk723(723),
    unk724(724),
    unk725(725),
    unk726(726), //遗物充能
    unk727(727),

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
            Exceed, DiabolicRecovery, FinalAttackProp, ExceedOverload, DevilishPower,
            OverloadCount, BuckShot, FireBomb, HalfstatByDebuff, SurplusSupply, SetBaseDamage, AmaranthGenerator,
            StrikerHyperElectric, EventPointAbsorb, EventAssemble, StormBringer, ACCR, DEXR, Albatross, Translucence,
            PoseType, LightOfSpirit, ElementSoul, GlimmeringTime, Restoration, ComboCostInc, ChargeBuff,
            TrueSight, CrossOverChain, ChillingStep, Reincarnation, DotBasedBuff, BlessEnsenble, ExtremeArchery,
            QuiverCartridge, AdvancedQuiver, UserControlMob, ImmuneBarrier, ArmorPiercing, ZeroAuraStr, ZeroAuraSpd,
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
            KinesisPsychicPoint, KinesisPsychicOver, KinesisPsychicShield, KinesisIncMastery, KinesisPsychicEnergyShield,
            BladeStance, DebuffActiveSkillHPCon, DebuffIncHP, BowMasterMortalBlow, AngelicBusterSoulResonance, Fever,
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
            Albatross, Translucence, PoseTypeBool, LightOfSpirit, ElementSoul, GlimmeringTime, Reincarnation, Beholder, QuiverCartridge, ArmorPiercing,
            ZeroAuraStr, ZeroAuraSpd, ImmuneBarrier, FullSoulMP, AntiMagicShellBool, Dance, Unk379, Unk425, ComboTempest, HalfstatByDebuff,
            ComplusionSlant, JaguarSummoned, BMageAura, BombTime, Unk491, Unk492, LightningCascade, BulletBarrage, Unk495, AuraScythe,
            Unk497, DarkLighting, AttackCountX, FireBarrier, KeyDownMoving, MichaelSoulLink, KinesisPsychicEnergyShield, BladeStance,
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
        spawnBuffs.put(IndieUnk3, null);
        spawnBuffs.put(IndieUnk4, null);
        spawnBuffs.put(IndieUnk6, null);
        spawnBuffs.put(IndieUnk10, null);
        spawnBuffs.put(PyramidEffect, null);
        spawnBuffs.put(KillingPoint, null);
        spawnBuffs.put(BattlePvPLangEProtection, null);
        spawnBuffs.put(BattlePvPRevive, null);
        spawnBuffs.put(RandAreaAttack, null);
        spawnBuffs.put(AranSmashSwing, null);
        spawnBuffs.put(RWBarrierHeal, null);
        spawnBuffs.put(Unk503, null);
        spawnBuffs.put(Unk540, null);
        spawnBuffs.put(Unk583, null);
        spawnBuffs.put(WillowDodge, null);


        //twoStat
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
            case QuiverCartridge:
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
            case NoBulletConsume:
            case RespectPImmune:
            case RespectMImmune:
            case DefenseAtt:
            case DefenseState:
            case MagicShield:
            case PyramidEffect:
            case BlessOfDarkness:
            case Unk306:
            case ImmuneBarrier:
            case Dance:
            case Unk379:
            case Unk425:
            case SpiritGuard:
            case KinesisPsychicEnergyShield:
            case AdrenalinBoost:
            case RWBarrier:
            case Unk476:
            case RWMagnumBlow:
            case DivineEcho:
            case Unk503:
            case Unk531:
            case Unk612:
            case Unk613:
            case Unk614:
            case Unk617:
            case Unk618:
            case HayatoStance:

            case Unk488:
            case Unk489:
                return true;
            default:
                return false;
        }
    }

    public boolean isRemoteEncode1() {
        switch (this) {
            case Speed:
            case Poison:
            case Seal:
            case Shock:
            case Team:
            case Cyclone:
            case OnCapsule:
            case KillingPoint:
            case PinkbeanRollingGrade:
            case ReturnTeleport:
            case FireBomb:
            case SurplusSupply:
            case Unk585:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotEncodeReason() {
        switch (this) {
            case Speed:
            case Poison:
            case Seal:
            case ElementalCharge:
            case Shock:
            case Team:
            case Ghost:
            case NoBulletConsume:
            case RespectPImmune:
            case RespectMImmune:
            case DefenseAtt:
            case DefenseState:
            case MagicShield:
            case Cyclone:
            case OnCapsule:
            case PyramidEffect:
            case KillingPoint:
            case PinkbeanRollingGrade:
            case StackBuff:
            case BlessOfDarkness:
            case SurplusSupply:
            case ImmuneBarrier:
            case AdrenalinBoost:
            case RWBarrier:
            case Unk476:
            case RWMagnumBlow:
            case Unk504:
            case Unk485:
            case ManaOverload:
            case Unk530:
            case Unk613:
            case Unk614:
            case Unk617:
            case Unk618:
            case HayatoStance:
            case Unk488:
            case Unk489:

                return true;
            default:
                return false;
        }
    }

    public boolean isNotEncodeAnything() {
        switch (this) {

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

            case FullSoulMP:
            case AntiMagicShellBool:
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
