package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.client.skill.SkillStat;
import im.cave.ms.enums.AssistType;
import im.cave.ms.enums.EnterType;
import im.cave.ms.enums.MoveAbility;
import im.cave.ms.network.packet.SummonPacket;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.tools.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieEmpty;


/**
 * Created on 1/6/2018.
 */
public class Summon extends MapleMapObj {

    private static final Logger log = LoggerFactory.getLogger(Summon.class);

    private MapleCharacter chr;
    private int skillID;
    private int bulletID;
    private int summonTerm;
    private byte charLevel;
    private byte slv;
    private AssistType assistType;
    private EnterType enterType;
    private byte teslaCoilState;
    private boolean flyMob;
    private boolean beforeFirstAttack;
    private boolean jaguarActive;
    private boolean attackActive;
    private short curFoothold;
    private List<Position> teslaCoilPositions = new ArrayList<>();
    private MoveAbility moveAbility;
    private int maxHP;
    private int hp;

    public Summon(int templateId) {
        super(templateId);
    }

    public MapleCharacter getChr() {
        return chr;
    }

    public void setChr(MapleCharacter chr) {
        this.chr = chr;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public byte getCharLevel() {
        return charLevel;
    }

    public void setCharLevel(byte charLevel) {
        this.charLevel = charLevel;
    }

    public byte getSlv() {
        return slv;
    }

    public void setSlv(byte slv) {
        this.slv = slv;
    }

    public int getBulletID() {
        return bulletID;
    }

    public void setBulletID(int bulletID) {
        this.bulletID = bulletID;
    }

    public int getSummonTerm() {
        return summonTerm;
    }

    public void setSummonTerm(int summonTerm) {
        this.summonTerm = 1000 * summonTerm;
    }

    public AssistType getAssistType() {
        return assistType;
    }

    public void setAssistType(AssistType assistType) {
        this.assistType = assistType;
    }

    public EnterType getEnterType() {
        return enterType;
    }

    public void setEnterType(EnterType enterType) {
        this.enterType = enterType;
    }

    public byte getTeslaCoilState() {
        return teslaCoilState;
    }

    public void setTeslaCoilState(byte teslaCoilState) {
        this.teslaCoilState = teslaCoilState;
    }

    public boolean isFlyMob() {
        return flyMob;
    }

    public void setFlyMob(boolean flyMob) {
        this.flyMob = flyMob;
    }

    public boolean isBeforeFirstAttack() {
        return beforeFirstAttack;
    }

    public void setBeforeFirstAttack(boolean beforeFirstAttack) {
        this.beforeFirstAttack = beforeFirstAttack;
    }

    public boolean isJaguarActive() {
        return jaguarActive;
    }

    public void setJaguarActive(boolean jaguarActive) {
        this.jaguarActive = jaguarActive;
    }

    public boolean isAttackActive() {
        return attackActive;
    }

    public void setAttackActive(boolean attackActive) {
        this.attackActive = attackActive;
    }

    public short getCurFoothold() {
        return curFoothold;
    }

    public void setCurFoothold(short curFoothold) {
        this.curFoothold = curFoothold;
    }

    public List<Position> getTeslaCoilPositions() {
        return teslaCoilPositions;
    }

    public void setTeslaCoilPositions(List<Position> teslaCoilPositions) {
        this.teslaCoilPositions = teslaCoilPositions;
    }

    public MoveAbility getMoveAbility() {
        return moveAbility;
    }

    public void setMoveAbility(MoveAbility moveAbility) {
        this.moveAbility = moveAbility;
    }

    public static Summon getSummonBy(MapleCharacter chr, int skillID, byte slv) {
        SkillInfo si = SkillData.getSkillInfo(skillID);
        Summon summon = new Summon(-1);
        summon.setChr(chr);
        summon.setSkillID(skillID);
        summon.setSlv(slv);
        summon.setSummonTerm(si.getValue(SkillStat.time, slv));
        summon.setCharLevel((byte) chr.getLevel());
        summon.setPosition(chr.getPosition().deepCopy());
        summon.setMoveAction((byte) 1);
        summon.setCurFoothold((short) chr.getMap().findFootHoldBelow(summon.getPosition()).getId());
        summon.setMoveAbility(MoveAbility.Walk);
        summon.setAssistType(AssistType.Attack);
        summon.setEnterType(EnterType.Animation);
        summon.setBeforeFirstAttack(true);
        summon.setTemplateId(skillID);
        summon.setAttackActive(true);

        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        o.nReason = skillID;
        o.nValue = 1;
        o.summon = summon;
        o.tStart = (int) System.currentTimeMillis();
        o.tTerm = summon.getSummonTerm() / 1000;
        tsm.putCharacterStatValue(IndieEmpty, o);
        tsm.sendSetStatPacket();
        return summon;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

//    public void onSkillUse(int skillId) {
//        switch (skillId) {
//            case Warrior.KNIGHT_EVIL_EYE:
//                ((Warrior) chr.getJobHandler()).healByEvilEye();
//                break;
//
//            case Warrior.HEX_OF_THE_EVIL_EYE:
//                ((Warrior) chr.getJobHandler()).giveHexOfTheEvilEyeBuffs();
//                break;
//
//            case Mechanic.SUPPORT_UNIT_HEX:
//            case Mechanic.ENHANCED_SUPPORT_UNIT:
//                ((Mechanic) chr.getJobHandler()).healFromSupportUnit(this);
//                break;
//            default:
//                int buffItem = SkillConstants.getBuffSkillItem(skillId);
//                if (buffItem != 0) {
//                    ItemBuffs.giveItemBuffsFromItemID(chr, chr.getTemporaryStatManager(), buffItem);
//                } else {
//                    chr.chatMessage(String.format("Unhandled Summon Skill: %d, casted by Summon: %d", skillId, getSkillID()));
//                }
//                break;
//        }
//        chr.write(User.effect(Effect.skillAffected(skillID, (byte) 1, getObjectId())));
//        chr.getField().broadcastPacket(UserRemote.effect(chr.getId(), Effect.skillAffected(skillID, (byte) 1, getObjectId())));
//    }

    public void onHit(int damage, int mobTemplateId) {
        MapleCharacter chr = getChr();
        Skill skill = chr.getSkill(getSkillID());

        if (skill == null) {
            return;
        }

        int summonHP = getHp();
        int newSummonHP = summonHP - damage;

//        switch (getSkillID()) {
//            case Thief.MIRRORED_TARGET:
//                ((Thief) chr.getJobHandler()).giveShadowMeld();
//                break;
//
//            case WindArcher.EMERALD_DUST:
//                ((WindArcher) chr.getJobHandler()).applyEmeraldDustDebuffToMob(this, mobTemplateId);
//                // Fallthrough intended
//            case WindArcher.EMERALD_FLOWER:
//                ((WindArcher) chr.getJobHandler()).applyEmeraldFlowerDebuffToMob(this, mobTemplateId);
//                break;
//
//            default:
//                log.error(String.format("Unhandled HP Summon, id = %d", getSkillID()));
//                break;
//        }

        if (newSummonHP <= 0) {
            TemporaryStatManager tsm = chr.getTemporaryStatManager();
//            chr.getMap().broadcastMessage(Summoned.summonedRemoved(this, LeaveType.ANIMATION));
            tsm.removeStatsBySkill(skill.getSkillId());
        } else {
            setHp(newSummonHP);
        }
    }


    @Override
    public void sendSpawnPacket(MapleCharacter chr) {
        chr.announce(SummonPacket.spawnSummon(chr.getId(), this));
    }
}
