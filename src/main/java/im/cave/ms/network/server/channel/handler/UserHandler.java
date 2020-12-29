package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.DamageSkinSaveData;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleKeyMap;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.potential.CharacterPotentialMan;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.job.MapleJob;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.client.skill.HitInfo;
import im.cave.ms.client.skill.MobAttackInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.CharPotGrade;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.DamageSkinType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.ServerMsgType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.network.packet.UserRemote;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.network.server.CommandHandler;
import im.cave.ms.network.server.channel.MapleChannel;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static im.cave.ms.constants.GameConstants.QUICKSLOT_SIZE;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_NICK_ITEM;
import static im.cave.ms.network.packet.opcode.RecvOpcode.CLOSE_RANGE_ATTACK;
import static im.cave.ms.network.packet.opcode.RecvOpcode.MAGIC_ATTACK;
import static im.cave.ms.network.packet.opcode.RecvOpcode.RANGED_ATTACK;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 12/1 14:59
 */
public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    public static void handleHit(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        HitInfo hitInfo = new HitInfo();
        inPacket.skip(8);
        player.setTick(inPacket.readInt());
        short unk = inPacket.readShort(); // FF 00
        int damage = inPacket.readInt();
        hitInfo.hpDamage = damage;
        if (JobConstants.isGmJob(player.getJobId())) {
            return;
        }
        inPacket.skip(2);
        if (inPacket.available() >= 13) {
            hitInfo.mobID = inPacket.readInt();
            hitInfo.templateID = inPacket.readInt();
            inPacket.skip(4);   //objId
            if (inPacket.available() >= 1) {
                hitInfo.specialEffectSkill = inPacket.readByte();
            }
        }
        HashMap<MapleStat, Long> stats = new HashMap<>();
        int curHp = (int) player.getStat(MapleStat.HP);
        int newHp = curHp - damage;
        if (newHp <= 0) {
            newHp = 0;
            c.announce(UserPacket.sendRebirthConfirm(true, false,
                    false, false
                    , false, 0, 0));
        }
        player.setStat(MapleStat.HP, newHp);
        stats.put(MapleStat.HP, (long) newHp);
        c.announce(UserPacket.updatePlayerStats(stats, player));
        player.getMap().broadcastMessage(player, UserRemote.hit(player, hitInfo), false);

    }

    public static void handleAttack(InPacket inPacket, MapleClient c, RecvOpcode opcode) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        AttackInfo attackInfo = new AttackInfo();
        attackInfo.attackHeader = opcode;
        if (opcode == RANGED_ATTACK) {
            attackInfo.boxAttack = inPacket.readByte() != 0;
        }
        attackInfo.fieldKey = inPacket.readByte(); //map key
        byte mask = inPacket.readByte();
        attackInfo.hits = (byte) (mask & 0xF);
        attackInfo.mobCount = (mask >>> 4) & 0xF;
        inPacket.readInt(); //00 00 00 00
        attackInfo.skillId = inPacket.readInt();
        attackInfo.skillLevel = inPacket.readInt();
        inPacket.readLong(); // crc
        if (attackInfo.attackHeader != MAGIC_ATTACK) {
            inPacket.readByte();//unk
        }
        inPacket.skip(18);
        short pX = inPacket.readShort(); // int?
        inPacket.readShort();
        short pY = inPacket.readShort();
        inPacket.readShort();

        inPacket.readInt(); // 00 00 00 00
        inPacket.readInt(); // DF 2B 9E 22 固定的
        inPacket.skip(3);
        if (attackInfo.attackHeader == RANGED_ATTACK) {
            inPacket.readInt();
            inPacket.readByte();
        }
        attackInfo.attackAction = inPacket.readByte();
        attackInfo.direction = inPacket.readByte();
        attackInfo.requestTime = inPacket.readInt();
        attackInfo.attackActionType = inPacket.readByte(); // 武器类型
        attackInfo.attackSpeed = inPacket.readByte();
        player.setTick(inPacket.readInt());
        inPacket.readInt();
        if (attackInfo.attackHeader == CLOSE_RANGE_ATTACK) {
            inPacket.readInt();
        }
        if (attackInfo.attackHeader == RANGED_ATTACK) {
            inPacket.readInt(); // 00
            inPacket.readShort(); // 00
            inPacket.readByte(); // 30
            attackInfo.rect = inPacket.readShortRect();
        }
        for (int i = 0; i < attackInfo.mobCount; i++) {
            MobAttackInfo mobAttackInfo = new MobAttackInfo();
            mobAttackInfo.objectId = inPacket.readInt();
            mobAttackInfo.hitAction = inPacket.readByte();
            inPacket.readShort();
            mobAttackInfo.left = inPacket.readByte();
            inPacket.readByte();
            mobAttackInfo.templateID = inPacket.readInt();
            mobAttackInfo.calcDamageStatIndex = inPacket.readByte();
            mobAttackInfo.hitX = inPacket.readShort();
            mobAttackInfo.hitY = inPacket.readShort();
            inPacket.readShort(); //x
            inPacket.readShort(); //y
            if (attackInfo.attackHeader == MAGIC_ATTACK) {
                mobAttackInfo.hpPerc = inPacket.readByte();
                short unk = inPacket.readShort(); //unk
            } else {
                byte unk1 = inPacket.readByte();
                byte unk2 = inPacket.readByte(); //1 正常 2 趴着
            }
            inPacket.readLong(); // 00
            mobAttackInfo.damages = new long[attackInfo.hits];
            for (byte j = 0; j < attackInfo.hits; j++) {
                mobAttackInfo.damages[j] = inPacket.readLong();
            }
            inPacket.readInt(); // 00 00 00 00
            inPacket.readInt(); // crc E7 DA 52 9A
            mobAttackInfo.type = inPacket.readByte();
            if (mobAttackInfo.type == 1) {
                mobAttackInfo.currentAnimationName = inPacket.readMapleAsciiString();
                inPacket.readMapleAsciiString();
                mobAttackInfo.animationDeltaL = inPacket.readInt();
                mobAttackInfo.hitPartRunTimesSize = inPacket.readInt();
                mobAttackInfo.hitPartRunTimes = new String[mobAttackInfo.hitPartRunTimesSize];
                for (int j = 0; j < mobAttackInfo.hitPartRunTimesSize; j++) {
                    mobAttackInfo.hitPartRunTimes[j] = inPacket.readMapleAsciiString();
                }
            } else if (mobAttackInfo.type == 2) {
                player.dropMessage("mobAttackInfo.type == 2 !!!");
            }
            inPacket.skip(14); //unk pos
            attackInfo.mobAttackInfo.add(mobAttackInfo);
        }
        player.getJobHandler().handleAttack(c, attackInfo);
        handleAttack(c, attackInfo);
    }

    public static void handleAttack(MapleClient c, AttackInfo attackInfo) {
        int killedCount = 0;
        int lastKilledMob = 0;
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        int skillId = attackInfo.skillId;
        if (!player.applyMpCon(skillId, attackInfo.skillLevel)) {
            return;
        }

        if (attackInfo.attackHeader != null) {
            switch (attackInfo.attackHeader) {
//                case SUMMONED_ATTACK:
//                    chr.getField().broadcastPacket(Summoned.summonedAttack(chr.getId(), attackInfo, false), chr);
//                    break;
//                case FAMILIAR_ATTACK:
//                    chr.getField().broadcastPacket(CFamiliar.familiarAttack(chr.getId(), attackInfo), chr);
//                    break;
                default:
                    player.getMap().broadcastMessage(player, UserRemote.attack(player, attackInfo), false);
            }
        }
        for (MobAttackInfo mobAttackInfo : attackInfo.mobAttackInfo) {
            MapleMap map = player.getMap();
            Mob mob = (Mob) map.getObj(mobAttackInfo.objectId);
            if (mob == null) {
                player.dropMessage("unhandled mob is null");
            } else if (mob.getHp() > 0) {
                long totalDamage = Arrays.stream(mobAttackInfo.damages).sum();
                mob.damage(player, totalDamage);
                if (mob.getHp() <= 0) {
                    killedCount++;
                    lastKilledMob = mob.getObjectId();
                    if (player.getLevel() - mob.getForcedMobStat().getLevel() <= 15) {
                        player.addDailyMobKillCount();
                    }
                }
                //todo handle reflect
            }
            if (mob != null && mob.getHp() < 0) {
                log.warn("mob was dead");
            }
        }
        if (killedCount > 0) {
            if (System.currentTimeMillis() - player.getLastKill() < 10000) {
                player.comboKill(lastKilledMob);
            } else {
                player.setCombo(0);
            }
            player.setLastKill(System.currentTimeMillis());
        }
        if (killedCount >= 3) {
            //todo
            player.announce(UserPacket.stylishKillMessage(1000, killedCount));
            player.addExp(1000, null);
        }

        if (attackInfo.attackHeader != null) {
            switch (attackInfo.attackHeader) {
                //todo
//                case SUMMONED_ATTACK:
//                    chr.getField().broadcastPacket(Summoned.summonedAttack(chr.getId(), attackInfo, false), chr);
//                    break;
//                case FAMILIAR_ATTACK:
//                    chr.getField().broadcastPacket(CFamiliar.familiarAttack(chr.getId(), attackInfo), chr);
//                    break;
                default:
                    player.getMap().broadcastMessage(player, UserRemote.attack(player, attackInfo), false);
            }
        }
    }

    public static void handlePlayerMove(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        inPacket.skip(1);    //unknown
        inPacket.skip(4);    //map relate
        inPacket.skip(4);    //tick
        inPacket.skip(1);    //unknown
        MovementInfo movementInfo = new MovementInfo(inPacket);
        movementInfo.applyTo(player);
        player.chatMessage(ChatType.Tip, player.getPosition().toString());
        player.getMap().sendMapObjectPackets(player);
        player.getMap().broadcastMessage(player, UserPacket.move(player, movementInfo), false);
    }

    public static void handleWorldMapTransfer(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        int mapId = inPacket.readInt();
        MapleChannel channel = c.getMapleChannel();
        MapleMap map = channel.getMap(mapId);
        if (map == null) {
            return;
        }
        player.changeMap(map, map.getDefaultPortal() == null ? 0 : map.getDefaultPortal().getId());
    }

    public static void handleCharInfoReq(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        int charId = inPacket.readInt();
        MapleCharacter chr = player.getMap().getCharById(charId);
        if (chr == null) {
            c.announce(WorldPacket.serverMsg("角色不存在", ServerMsgType.ALERT));
            return;
        }
        c.announce(UserRemote.charInfo(chr));
    }

    /*
    取消椅子/城镇椅子
     */
    public static void cancelChair(InPacket inPacket, MapleClient c) {
        short id = inPacket.readShort();
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setChairId(id);
        c.announce(UserPacket.cancelChair(player.getId(), id));
    }

    public static void handleUserSitRequest(InPacket inPacket, MapleClient c) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeInt(SendOpcode.SIT_RESULT.getValue());
        outPacket.writeInt(0);
        c.announce(outPacket);
        c.announce(UserPacket.enableActions());
    }

    public static void handlePickUp(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        byte mapKey = inPacket.readByte();
        player.setTick(inPacket.readInt());
        Position position = inPacket.readPos();
        int dropId = inPacket.readInt();
        MapleMap map = player.getMap();
        MapleMapObj obj = map.getObj(dropId);
        if (obj instanceof Drop) {
            Drop drop = (Drop) obj;
            player.addDrop(drop);
            map.removeDrop(dropId, DropLeaveType.CharPickup1, player.getId(), false);
        }
    }

    public static void handleEquipEffectOpt(int pos, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        Inventory inventory = player.getEquippedInventory();
        Equip equip = (Equip) inventory.getItem((short) pos);
        if (equip == null) {
            c.announce(UserPacket.enableActions());
            return;
        }
        equip.setShowEffect(!equip.isShowEffect());
        c.announce(UserPacket.hiddenEffectEquips(player));
    }

    public static void handleSkillUp(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        int skillId = inPacket.readInt();
        int level = inPacket.readInt();
        if (level < 1) {
            c.close();
            return;
        }
        Skill skill = SkillData.getSkill(skillId);
        if (skill == null) {
            return;
        }
        Skill curSkill = player.getSkill(skill.getSkillId());
        byte jobLevel = (byte) JobConstants.getJobLevel((short) skill.getRootId());
        if (JobConstants.isZero((short) skill.getRootId())) {
            jobLevel = JobConstants.getJobLevelByZeroSkillID(skillId);
        }
        Map<MapleStat, Long> stats;
        int rootId = skill.getRootId();
        if ((!JobConstants.isBeginnerJob((short) rootId) && !SkillConstants.isMatching(rootId, player.getJobId())) || SkillConstants.isSkillFromItem(skillId)) {
            log.error(String.format("Character %d tried adding an invalid skill (job %d, skill id %d)",
                    player.getId(), skillId, rootId));
            return;
        }
        if (JobConstants.isBeginnerJob((short) rootId)) {
            stats = new HashMap<>();
            int spentSp = player.getSkills().stream()
                    .filter(s -> JobConstants.isBeginnerJob((short) s.getRootId()))
                    .mapToInt(Skill::getCurrentLevel).sum();
            int totalSp;
            if (JobConstants.isResistance((short) skill.getRootId())) {
                totalSp = Math.min(player.getLevel(), GameConstants.RESISTANCE_SP_MAX_LV) - 1; // sp gained from 2~10
            } else {
                totalSp = Math.min(player.getLevel(), GameConstants.BEGINNER_SP_MAX_LV) - 1; // sp gained from 2~7
            }
            if (totalSp - spentSp >= level) {
                int curLevel = curSkill == null ? 0 : curSkill.getCurrentLevel();
                int max = curSkill == null ? skill.getMaxLevel() : curSkill.getMaxLevel();
                int newLevel = Math.min(curLevel + level, max);
                skill.setCurrentLevel(newLevel);
            }
        } else if (JobConstants.isExtendSpJob(player.getJobId())) {
            int[] remainingSps = player.getRemainingSps();
            int remainingSp = remainingSps[jobLevel - 1];
            if (remainingSp >= level) {
                int curLevel = curSkill == null ? 0 : curSkill.getCurrentLevel();
                int max = curSkill == null ? skill.getMaxLevel() : curSkill.getMaxLevel();
                int newLevel = Math.min(curLevel + level, max);
                skill.setCurrentLevel(newLevel);
                remainingSps[jobLevel - 1] = remainingSp - level;
                player.setRemainingSp(remainingSps);
                stats = new HashMap<>();
                stats.put(MapleStat.AVAILABLESP, (long) 1);
            } else {
                log.error(String.format("Character %d tried adding a skill without having the required amount of sp" +
                                " (required %d, has %d)",
                        player.getId(), remainingSp, level));
                return;
            }
        } else {
            int[] remainingSps = player.getRemainingSps();
            int currentSp = remainingSps[0];
            if (currentSp >= level) {
                int curLevel = curSkill == null ? 0 : curSkill.getCurrentLevel();
                int max = curSkill == null ? skill.getMaxLevel() : curSkill.getMaxLevel();
                int newLevel = Math.min(curLevel + level, max);
                skill.setCurrentLevel(newLevel);
                remainingSps[0] = currentSp - level;
                player.setRemainingSp(remainingSps);
                stats = new HashMap<>();
                stats.put(MapleStat.AVAILABLESP, (long) 1);
            } else {
                log.error(String.format("Character %d tried adding a skill without having the required amount of sp" +
                                " (required %d, has %d)",
                        player.getId(), currentSp, level));
                return;
            }
        }

        c.announce(UserPacket.updatePlayerStats(stats, player));
        player.addSkill(skill);
        c.announce(UserPacket.changeSkillRecordResult(skill));

    }

    //自动回复
    public static void handleChangeStatRequest(InPacket inPacket, MapleClient c) {

        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        long mask = inPacket.readLong();
        List<MapleStat> stats = MapleStat.getStatsByMask(mask);
        HashMap<MapleStat, Long> updatedStats = new HashMap<>();
        for (MapleStat stat : stats) {
            updatedStats.put(stat, (long) inPacket.readShort());
        }
        if (updatedStats.containsKey(MapleStat.HP)) {
            player.heal(Math.toIntExact(updatedStats.get(MapleStat.HP)));
        }
        if (updatedStats.containsKey(MapleStat.MP)) {
            player.healMP(Math.toIntExact(updatedStats.get(MapleStat.MP)));
        }
//        c.announce(UserPacket.updatePlayerStats(updatedStats, player));
    }

    public static void handleChangeQuickslot(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        ArrayList<Integer> aKeys = new ArrayList<>();
        if (inPacket.available() == QUICKSLOT_SIZE * 4) {
            for (int i = 0; i < QUICKSLOT_SIZE; i++) {
                aKeys.add(inPacket.readInt());
            }
        }
        player.setQuickslots(aKeys);
    }

    public static void handleChangeKeyMap(InPacket inPacket, MapleClient c) {
        inPacket.skip(4);
        int size = inPacket.readInt();
        MapleKeyMap keyMap = c.getPlayer().getKeyMap();
        if (keyMap == null) {
            keyMap = new MapleKeyMap();
            keyMap.setDefault(false);
        }
        for (int i = 0; i < size; i++) {
            int key = inPacket.readInt();
            byte type = inPacket.readByte();
            int action = inPacket.readInt();
            keyMap.putKeyBinding(key, type, action);
        }
        c.getPlayer().setKeyMap(keyMap);
    }

    public static void handleUseSkill(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        inPacket.readInt(); //crc
        int skillId = inPacket.readInt();
        int skillLevel = inPacket.readInt();
        if (player.applyMpCon(skillId, skillLevel) && player.isSkillInCd(skillId)) {
            player.getMap().broadcastMessage(UserRemote.effect(player.getId(), Effect.skillUse(skillId, (byte) skillLevel, 0)));
            SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
            MapleJob sourceJobHandler = player.getJobHandler();
            if (sourceJobHandler.isBuff(skillId) && skillInfo.isMassSpell()) {
                Rect rect = skillInfo.getFirstRect();
                if (rect != null) {
                    Rect rectAround = player.getRectAround(rect);
//                    for (PartyMember pm : chr.getParty().getOnlineMembers()) {
//                        if (pm.getChr() != null
//                                && pm.getFieldID() == chr.getFieldID()
//                                && rectAround.hasPositionInside(pm.getChr().getPosition())) {
//                            Char ptChr = pm.getChr();
//                            Effect effect = Effect.skillAffected(skillID, slv, 0);
//                            if (ptChr != chr) { // Caster shouldn't get the Affected Skill Effect
//                                chr.getField().broadcastPacket(
//                                        UserPacket.effect(ptChr.getId(), effect)
//                                        , ptChr);
//                                ptChr.write(User.effect(effect));
//                            }
//                            sourceJobHandler.handleSkill(pm.getChr().getClient(), skillID, slv, inPacket);
//                        }
//                    }
                }
                sourceJobHandler.handleSkill(c, skillId, skillLevel, inPacket);

            } else {
                sourceJobHandler.handleSkill(c, skillId, skillLevel, inPacket);
            }

        }
    }

    public static void handleCancelBuff(InPacket inPacket, MapleClient c) {
        int skillId = inPacket.readInt();
        inPacket.readByte();
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        tsm.removeStatsBySkill(skillId);
    }

    public static void handleAPUpdateRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getRemainingAp() <= 0) {
            return;
        }
        player.setTick(inPacket.readInt());
        short stat = inPacket.readShort();
        MapleStat charStat = MapleStat.getByValue(stat);
        if (charStat == null) {
            return;
        }
        int amount = 1;
        if (charStat == MapleStat.MAXMP || charStat == MapleStat.MAXHP) {
            amount = 20;
        }
        player.addStat(charStat, amount);
        player.addStat(MapleStat.AVAILABLEAP, (short) -1);
        Map<MapleStat, Long> stats = new HashMap<>();
        stats.put(charStat, player.getStat(charStat));
        stats.put(MapleStat.AVAILABLEAP, player.getStat(MapleStat.AVAILABLEAP));
        c.announce(UserPacket.updatePlayerStats(stats, true, player));
    }

    public static void handleAPMassUpdateRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getRemainingAp() <= 0) {
            return;
        }
        player.setTick(inPacket.readInt());
        int type = inPacket.readInt();
        int amount;
        MapleStat charStat = null;
        if (type == 1) {
            charStat = MapleStat.getByValue(inPacket.readLong());
        } else if (type == 2) {
            inPacket.readInt();
            inPacket.readInt();
            inPacket.readInt();
            charStat = MapleStat.getByValue(inPacket.readLong());
        }
        if (charStat == null) {
            return;
        }
        amount = inPacket.readInt();
        int addStat = amount;
        if (player.getRemainingAp() < amount) {
            return;
        }
        if (charStat == MapleStat.MAXMP || charStat == MapleStat.MAXHP) {
            addStat *= 20;
        }
        player.addStat(charStat, addStat);
        player.addStat(MapleStat.AVAILABLEAP, -amount);
        Map<MapleStat, Long> stats = new HashMap<>();
        stats.put(charStat, player.getStat(charStat));
        stats.put(MapleStat.AVAILABLEAP, player.getStat(MapleStat.AVAILABLEAP));
        c.announce(UserPacket.updatePlayerStats(stats, true, player));
    }

    public static void handleUserRequestCharacterPotentialSkillRandSetUi(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        int cost = GameConstants.CHAR_POT_RESET_COST;
        int rate = inPacket.readInt();
        int size = inPacket.readInt();
        Set<Integer> lockedLines = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lockedLines.add(inPacket.readInt());
            if (lockedLines.size() == 0) {
                cost += GameConstants.CHAR_POT_LOCK_1_COST;
            } else {
                cost += GameConstants.CHAR_POT_LOCK_2_COST;
            }
        }
        boolean locked = rate > 0;
        if (locked) {
            cost += GameConstants.CHAR_POT_GRADE_LOCK_COST;
        }
        if (cost > player.getHonerPoint()) {
            player.chatMessage("You do not have enough honor exp for that action.");
            return;
        }
        player.addHonerPoint(-cost);

        CharacterPotentialMan cpm = player.getPotentialMan();
        boolean gradeUp = !locked && Util.succeedProp(GameConstants.BASE_CHAR_POT_UP_RATE);
        boolean gradeDown = !locked && Util.succeedProp(GameConstants.BASE_CHAR_POT_DOWN_RATE);
        byte grade = cpm.getGrade();
        // update grades
        if (grade < CharPotGrade.Legendary.ordinal() && gradeUp) {
            grade++;
        } else if (grade > CharPotGrade.Rare.ordinal() && gradeDown) {
            grade--;
        }
        // set new potentials that weren't locked
        for (CharacterPotential cp : player.getPotentials()) {
            cp.setGrade(grade);
            if (!lockedLines.contains((int) cp.getKey())) {
                cpm.addPotential(cpm.generateRandomPotential(cp.getKey()));
            }
        }
        c.announce(UserPacket.noticeMsg("内在能力重新设置成功。"));
    }

    public static void handleUserDamageSkinSaveRequest(InPacket inPacket, MapleClient c) {
        byte b = inPacket.readByte(); //unk
        MapleCharacter player = c.getPlayer();
        DamageSkinSaveData damageSkin = player.getDamageSkin();
        DamageSkinType error = null;
        if (player.getDamageSkins().size() >= GameConstants.DAMAGE_SKIN_MAX_SIZE) {
            error = DamageSkinType.DamageSkinSave_Fail_SlotCount;
        }
        if (error != null) {
            player.announce(UserPacket.damageSkinSaveResult(DamageSkinType.DamageSkinSaveReq_Reg, error, null));
        } else {
            player.getDamageSkinByItemID(damageSkin.getItemID()).setNotSave(false);
            player.announce(UserPacket.damageSkinSaveResult(DamageSkinType.DamageSkinSaveReq_Active,
                    DamageSkinType.DamageSkinSave_Success, player));
        }
    }

    public static void handleUserActivateNickItem(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int itemId = inPacket.readInt();
        short pos = inPacket.readShort();
        Item item = player.getInstallInventory().getItem(pos);
        if ((item == null || item.getItemId() != itemId) && itemId != 0) {
            return;
        }
        String date;
        String expired;
        if (itemId == 0) {
            date = "0";
            expired = "1";
        } else {
            date = "2079/01/01 00:00:00:000";
            expired = "0";
        }
        HashMap<String, String> value = new HashMap<>();
        value.put("id", String.valueOf(itemId));
        value.put("date", date);
        value.put("expired", expired);
        player.addQuestEx(QUEST_EX_NICK_ITEM, value);
        c.announce(UserPacket.message(MessageType.QUEST_RECORD_EX_MESSAGE, QUEST_EX_NICK_ITEM, player.getQuestsExStorage().get(QUEST_EX_NICK_ITEM), (byte) 0));
    }

    public static void handleUserActivateDamageSkin(InPacket inPacket, MapleClient c) {
        int damageSkinId = inPacket.readInt();
        MapleCharacter chr = c.getPlayer();
        chr.setDamageSkin(chr.getDamageSkinBySkinId(damageSkinId));
        chr.announce(UserPacket.setDamageSkin(chr));
    }

    public static void handleUserEnterPortalRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        if (inPacket.available() != 0) {
            byte type = inPacket.readByte();
            int targetId = inPacket.readInt();
            String portalName = inPacket.readMapleAsciiString();
            if (portalName != null && !"".equals(portalName)) {
                Portal portal = player.getMap().getPortal(portalName);
                portal.enterPortal(c);
            } else if (player.getHp() <= 0) {
                int returnMap = player.getMap().getReturnMap();
                player.changeMap(returnMap);
                player.heal(50);
            }
        }
    }

    public static void handleUserGeneralChat(InPacket inPacket, MapleClient c) {
        int tick = inPacket.readInt();
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            c.close();
            return;
        }
        player.setTick(tick);
        String content = inPacket.readMapleAsciiString();

        if (content.startsWith("@")) {
            CommandHandler.handle(c, content);
            return;
        }

        player.getMap().broadcastMessage(player, UserRemote.getChatText(player, content), true);
    }

    public static void handleUserEnterPortalSpecialRequest(InPacket inPacket, MapleClient c) {
        byte type = inPacket.readByte();
        String portalName = inPacket.readMapleAsciiString();
        Portal portal = c.getPlayer().getMap().getPortal(portalName);
        if (portal == null) {
            c.announce(UserPacket.enableActions());
            return;
        }
        if (c.getPlayer().isChangingChannel()) {
            c.announce(UserPacket.enableActions());
            return;
        }

        portal.enterPortal(c);

    }

    public static void handleUserAddFameRequest(InPacket inPacket, MapleClient c) {
        int charId = inPacket.readInt();
        MapleCharacter player = c.getPlayer();
        MapleCharacter other = player.getMap().getCharById(charId);
        if (other == null) {
            player.chatMessage("找不到角色");
            return;
        }
        byte mode = inPacket.readByte();
        int fameChange = mode == 0 ? -1 : 1;
        other.addStatAndSendPacket(MapleStat.FAME, fameChange);
        player.announce(UserPacket.addFameResponse(player, mode, other.getFame()));
        other.announce(UserPacket.receiveFame(mode, player.getName()));
    }
}
