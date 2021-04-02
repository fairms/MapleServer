package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.ForceAtomInfo;
import im.cave.ms.client.character.skill.MobAttackInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobTemporaryStat;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.enums.ForceAtomEnum;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;

import java.util.Random;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.AdvancedQuiver;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.EPAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ExtremeArchery;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePDDR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndiePMdR;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.NoBulletConsume;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.QuiverCatridge;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.SharpEyes;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.SoulArrow;
import static im.cave.ms.enums.SkillStat.epad;
import static im.cave.ms.enums.SkillStat.indiePMdR;
import static im.cave.ms.enums.SkillStat.time;
import static im.cave.ms.enums.SkillStat.u;
import static im.cave.ms.enums.SkillStat.w;
import static im.cave.ms.enums.SkillStat.x;
import static im.cave.ms.enums.SkillStat.y;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.job.adventurer
 * @date 12/28 14:25
 */
public class Archer extends Beginner {
    public static final int BOW_SOUL_ARROW = 3101004;
    public static final int BOW_HURRICANE = 3121020;
    public static final int BOW_QUIVER_CARTRIDGE = 3101009;
    public static final int BOW_QUIVER_CARTRIDGE_ATOM = 3100010;
    public static final int BOW_ENCHANTED_QUIVER = 3121016;
    public static final int BOW_RECKLESS_HUNT = 3111011;
    public static final int BOW_ARROW_PLATTER = 3111013;
    public static final int BOW_ILLUSION_STEP = 3121007;
    public static final int BOW_SHARP_EYE = 3121002;

    public Archer(MapleCharacter chr) {
        super(chr);
    }

    private QuiverCartridge quiverCartridge;

    @Override
    public void handleSkill(MapleClient c, int skillId, int slv, InPacket in) throws Exception {
        super.handleSkill(c, skillId, slv, in);

        switch (skillId) {


        }
    }


    @Override
    public void handleAttack(MapleClient c, AttackInfo attackInfo) {
        MapleCharacter chr = c.getPlayer();
        Skill skill = chr.getSkill(attackInfo.skillId);
        SkillInfo si = null;
        int skillId = 0;
        boolean hasHitMobs = attackInfo.mobAttackInfo.size() > 0;
        int slv = 0;
        if (skill != null) {
            si = SkillData.getSkillInfo(skill.getSkillId());
            slv = skill.getCurrentLevel();
            skillId = skill.getSkillId();
        }
        if (hasHitMobs) {
            quiverCartridge(chr.getTemporaryStatManager(), attackInfo, slv);
//            incrementFocusedFury();
//            incrementMortalBlow();
//            giveAggressiveResistanceBuff(attackInfo);
//            procArmorBreak(attackInfo);
        }

        super.handleAttack(c, attackInfo);

    }

    @Override
    public void handleBuff(MapleClient c, InPacket in, int skillId, int slv) {
        super.handleBuff(c, in, skillId, slv);
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        SkillInfo si = SkillData.getSkillInfo(skillId);
        boolean sendStat = true;
        Option o = new Option();
        Option oo = new Option();
        Option ooo = new Option();
        switch (skillId) {
            case BOW_ILLUSION_STEP:
                o.nOption = si.getValue(SkillStat.indieDex, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.DEX, o);
                oo.nOption = si.getValue(x, slv);
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.IllusionStep, oo);
                break;
            case BOW_SOUL_ARROW:
                o.nOption = si.getValue(x, slv);
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(SoulArrow, o);
                oo.nOption = si.getValue(epad, slv);
                oo.rOption = skillId;
                oo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(EPAD, oo);
                ooo.nOption = si.getValue(x, slv);
                ooo.rOption = skillId;
                ooo.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(NoBulletConsume, ooo);
                break;
            case BOW_SHARP_EYE:
                int cr = si.getValue(x, slv);
                int cd = si.getValue(y, slv);
                o.nOption = (cr << 8) + cd;
                o.rOption = skillId;
                o.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(SharpEyes, oo);
                //todo deal hyper passive
                break;
            case BOW_RECKLESS_HUNT: //todo 加的攻击力去哪了？
                if (tsm.hasStatBySkillId(skillId)) {
                    tsm.removeStatsBySkill(skillId);
                    tsm.sendResetStatPacket();
                } else {
                    o.nOption = si.getValue(x, slv);
                    o.rOption = skillId;
                    tsm.putCharacterStatValue(ExtremeArchery, o);
                    oo.nReason = skillId;
                    oo.nValue = -1 * si.getValue(x, slv);
                    int tStart = (int) System.currentTimeMillis();
                    oo.tStart = tStart;
                    tsm.putCharacterStatValue(IndiePDDR, oo);
                    ooo.nReason = skillId;
                    ooo.nValue = si.getValue(indiePMdR, slv);
                    ooo.tStart = tStart;
                    tsm.putCharacterStatValue(IndiePMdR, ooo);
                }
                break;
            case BOW_QUIVER_CARTRIDGE:
                if (quiverCartridge == null) {
                    quiverCartridge = new QuiverCartridge(chr);
                } else if (tsm.hasStat(QuiverCatridge)) {
                    quiverCartridge.incType();
                }
                o = quiverCartridge.getOption();
                tsm.putCharacterStatValue(QuiverCatridge, o);
                break;
            default:
                sendStat = false;
        }
        if (sendStat) {
            tsm.sendSetStatPacket();
        }
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return super.isHandlerOfJob(id);
    }

    @Override
    public boolean isBuff(int skillId) {
        return super.isBuff(skillId);
    }

    @Override
    public int getFinalAttackSkill() {
        return super.getFinalAttackSkill();
    }


    //箭筒
    public class QuiverCartridge {

        private int blood; // 1
        private int poison; // 2
        private int magic; // 3
        private int type;
        private final MapleCharacter chr;

        public QuiverCartridge(MapleCharacter chr) {
            blood = getMaxNumberOfArrows(QCType.BLOOD.getVal());
            poison = getMaxNumberOfArrows(QCType.POISON.getVal());
            magic = getMaxNumberOfArrows(QCType.MAGIC.getVal());
            type = 1;
            this.chr = chr;
        }

        public void decrementAmount() {
            if (chr.getTemporaryStatManager().hasStat(AdvancedQuiver)) {
                return;
            }
            switch (type) {
                case 1:
                    blood--;
                    if (blood == 0) {
                        blood = getMaxNumberOfArrows(QCType.BLOOD.getVal());
                        incType();
                    }
                    break;
                case 2:
                    poison--;
                    if (poison == 0) {
                        poison = getMaxNumberOfArrows(QCType.POISON.getVal());
                        incType();
                    }
                    break;
                case 3:
                    magic--;
                    if (magic == 0) {
                        magic = getMaxNumberOfArrows(QCType.MAGIC.getVal());
                        incType();
                    }
                    break;
            }
        }

        public void incType() {
            type = (type % 3) + 1;
        }

        public int getTotal() {
            return blood * 10000 + poison * 100 + magic;
        }

        public Option getOption() {
            Option o = new Option();
            o.nOption = getTotal();
            o.rOption = BOW_QUIVER_CARTRIDGE;
            o.xOption = type;
            return o;
        }

        public int getType() {
            return type;
        }
    }


    public enum QCType {
        BLOOD(1),
        POISON(2),
        MAGIC(3),
        ;
        private final byte val;

        QCType(int val) {
            this.val = (byte) val;
        }

        public byte getVal() {
            return val;
        }
    }


    public int getMaxNumberOfArrows(int type) {
        int num = 0;
        Skill firstSkill = chr.getSkill(BOW_QUIVER_CARTRIDGE);
        Skill secondSkill = chr.getSkill(BOW_ENCHANTED_QUIVER);
        if (secondSkill != null && secondSkill.getCurrentLevel() > 0) {
            num = 20;

        } else if (firstSkill != null && firstSkill.getCurrentLevel() > 0) {
            num = 10;
        }
        return type == 3 ? num * 2 : num; // Magic Arrow has 2x as many arrows
    }

    private void quiverCartridge(TemporaryStatManager tsm, AttackInfo attackInfo, int slv) {
        MapleCharacter chr = c.getPlayer();
        if (quiverCartridge == null) {
            return;
        }
        Skill skill = chr.hasSkill(BOW_ENCHANTED_QUIVER) ? chr.getSkill(BOW_ENCHANTED_QUIVER)
                : chr.getSkill(BOW_QUIVER_CARTRIDGE);
        SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
        for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
            Mob mob = (Mob) chr.getMap().getObj(mai.objectId);
            if (mob == null) {
                continue;
            }
            MobTemporaryStat mts = mob.getTemporaryStat();
            int mobId = mai.objectId;
            switch (quiverCartridge.getType()) {
                case 1: // Blood
                    if (Util.succeedProp(si.getValue(w, slv))) {
                        quiverCartridge.decrementAmount();
                        int healRate = si.getValue(w, slv);
                        chr.heal((int) (chr.getMaxHP() / ((double) 100 / healRate)));
                    }
                    break;
                case 2: // Poison
                    mts.createAndAddBurnedInfo(chr, skill);
                    quiverCartridge.decrementAmount();
                    break;
                case 3: // Magic
                    int num = new Random().nextInt(130) + 50;
                    if (Util.succeedProp(si.getValue(u, slv))) {
                        quiverCartridge.decrementAmount();
                        int inc = ForceAtomEnum.BM_ARROW.getInc();
                        int type = ForceAtomEnum.BM_ARROW.getForceAtomType();
                        ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 13, 12,
                                num, 0, (int) System.currentTimeMillis(), 1, 0,
                                new Position());
                        chr.getMap().broadcastMessage(WorldPacket.createForceAtom(false, 0, chr.getId(), type,
                                true, mobId, BOW_QUIVER_CARTRIDGE_ATOM, forceAtomInfo, new Rect(), 0, 300,
                                mob.getPosition(), 0, mob.getPosition()));
                    }
                    break;
            }
        }
        tsm.putCharacterStatValue(QuiverCatridge, quiverCartridge.getOption());
        tsm.sendSetStatPacket();
    }


}
