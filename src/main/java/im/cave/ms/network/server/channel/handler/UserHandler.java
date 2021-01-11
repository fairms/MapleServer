package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.DamageSkinSaveData;
import im.cave.ms.client.character.Macro;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleKeyMap;
import im.cave.ms.client.character.Stat;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.WishedItem;
import im.cave.ms.client.character.job.MapleJob;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.potential.CharacterPotentialMan;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.HitInfo;
import im.cave.ms.client.character.skill.MobAttackInfo;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.social.party.PartyMember;
import im.cave.ms.client.storage.Locker;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.CashItemType;
import im.cave.ms.enums.CashShopCurrencyType;
import im.cave.ms.enums.CharPotGrade;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.DamageSkinType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.MapTransferType;
import im.cave.ms.enums.ServerMsgType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.CashShopPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.network.packet.UserRemote;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.network.server.Server;
import im.cave.ms.network.server.cashshop.CashShopServer;
import im.cave.ms.network.server.channel.MapleChannel;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.AndroidInfo;
import im.cave.ms.provider.info.CashItemInfo;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static im.cave.ms.constants.GameConstants.QUICKSLOT_SIZE;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_NICK_ITEM;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_SOUL_EFFECT;
import static im.cave.ms.constants.ServerConstants.ONE_DAY_TIMES;
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

    public static void handleHit(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        HitInfo hitInfo = new HitInfo();
        in.skip(8);
        player.setTick(in.readInt());
        short unk = in.readShort(); // FF 00
        int damage = in.readInt();
        hitInfo.hpDamage = damage;
        if (JobConstants.isGmJob(player.getJob())) {
            return;
        }
        in.skip(2);
        if (in.available() >= 13) {
            hitInfo.mobID = in.readInt();
            hitInfo.templateID = in.readInt();
            in.skip(4);   //objId
            if (in.available() >= 1) {
                hitInfo.specialEffectSkill = in.readByte();
            }
        }
        HashMap<Stat, Long> stats = new HashMap<>();
        int curHp = (int) player.getStat(Stat.HP);
        int newHp = curHp - damage;
        if (newHp <= 0) {
            newHp = 0;
            c.announce(UserPacket.sendRebirthConfirm(true, false,
                    false, false
                    , false, 0, 0));
        }
        player.setStat(Stat.HP, newHp);
        stats.put(Stat.HP, (long) newHp);
        c.announce(UserPacket.updatePlayerStats(stats, player));
        player.getMap().broadcastMessage(player, UserRemote.hit(player, hitInfo), false);

    }

    public static void handleAttack(InPacket in, MapleClient c, RecvOpcode opcode) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        AttackInfo attackInfo = new AttackInfo();
        attackInfo.attackHeader = opcode;
        if (opcode == RANGED_ATTACK) {
            attackInfo.boxAttack = in.readByte() != 0;
        }
        attackInfo.fieldKey = in.readByte(); //map key
        byte mask = in.readByte();
        attackInfo.hits = (byte) (mask & 0xF);
        attackInfo.mobCount = (mask >>> 4) & 0xF;
        in.readInt(); //00 00 00 00
        attackInfo.skillId = in.readInt();
        attackInfo.skillLevel = in.readInt();
        in.readLong(); // crc
        if (attackInfo.attackHeader != MAGIC_ATTACK) {
            in.readByte();//unk
        }
        in.skip(18);
        short pX = in.readShort(); // int?
        in.readShort();
        short pY = in.readShort();
        in.readShort();

        in.readInt(); // 00 00 00 00
        in.readInt(); // DF 2B 9E 22 固定的
        in.skip(3);
        if (attackInfo.attackHeader == RANGED_ATTACK) {
            in.readInt();
            in.readByte();
        }
        attackInfo.attackAction = in.readByte();
        attackInfo.direction = in.readByte();
        attackInfo.requestTime = in.readInt();
        attackInfo.attackActionType = in.readByte(); // 武器类型
        attackInfo.attackSpeed = in.readByte();
        player.setTick(in.readInt());
        in.readInt();
        if (attackInfo.attackHeader == CLOSE_RANGE_ATTACK) {
            in.readInt();
        }
        if (attackInfo.attackHeader == RANGED_ATTACK) {
            in.readInt(); // 00
            in.readShort(); // 00
            in.readByte(); // 30
            attackInfo.rect = in.readShortRect();
        }
        for (int i = 0; i < attackInfo.mobCount; i++) {
            MobAttackInfo mobAttackInfo = new MobAttackInfo();
            mobAttackInfo.objectId = in.readInt();
            mobAttackInfo.hitAction = in.readByte();
            in.readShort();
            mobAttackInfo.left = in.readByte();
            in.readByte();
            mobAttackInfo.templateID = in.readInt();
            mobAttackInfo.calcDamageStatIndex = in.readByte();
            mobAttackInfo.hitX = in.readShort();
            mobAttackInfo.hitY = in.readShort();
            in.readShort(); //x
            in.readShort(); //y
            if (attackInfo.attackHeader == MAGIC_ATTACK) {
                mobAttackInfo.hpPerc = in.readByte();
                short unk = in.readShort(); //unk
            } else {
                byte unk1 = in.readByte();
                byte unk2 = in.readByte(); //1 正常 2 趴着
            }
            in.readLong(); // 00
            mobAttackInfo.damages = new long[attackInfo.hits];
            for (byte j = 0; j < attackInfo.hits; j++) {
                mobAttackInfo.damages[j] = in.readLong();
            }
            in.readInt(); // 00 00 00 00
            in.readInt(); // crc E7 DA 52 9A
            mobAttackInfo.type = in.readByte();
            if (mobAttackInfo.type == 1) {
                mobAttackInfo.currentAnimationName = in.readMapleAsciiString();
                in.readMapleAsciiString();
                mobAttackInfo.animationDeltaL = in.readInt();
                mobAttackInfo.hitPartRunTimesSize = in.readInt();
                mobAttackInfo.hitPartRunTimes = new String[mobAttackInfo.hitPartRunTimesSize];
                for (int j = 0; j < mobAttackInfo.hitPartRunTimesSize; j++) {
                    mobAttackInfo.hitPartRunTimes[j] = in.readMapleAsciiString();
                }
            } else if (mobAttackInfo.type == 2) {
                player.dropMessage("mobAttackInfo.type == 2 !!!");
            }
            in.skip(14); //unk pos
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
    }

    public static void handlePlayerMove(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        in.skip(1);    //unknown
        in.skip(4);    //map relate
        in.skip(4);    //tick
        in.skip(1);    //unknown
        MovementInfo movementInfo = new MovementInfo(in);
        movementInfo.applyTo(player);
        player.chatMessage(ChatType.Tip, player.getPosition().toString());
        player.getMap().sendMapObjectPackets(player);
        player.getMap().broadcastMessage(player, UserPacket.move(player, movementInfo), false);
    }

    //todo 优先使用道具 -》 免费 超时空卷 -》 点券 超时空卷
    public static void handleWorldMapTransfer(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        int mapId = in.readInt();
        MapleChannel channel = c.getMapleChannel();
        MapleMap map = channel.getMap(mapId);
        if (map == null) {
            player.announce(WorldPacket.mapTransferResult(MapTransferType.TargetNotExist, (byte) 0, null));
            return;
        } else if (map == player.getMap()) {
            player.announce(WorldPacket.mapTransferResult(MapTransferType.AlreadyInMap, (byte) 0, null));
            return;
        }
        player.announce(UserPacket.remainingMapTransferCoupon(player));
        player.changeMap(map.getId());
    }

    //打开角色的信息面板
    public static void handleCharInfoReq(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        int charId = in.readInt();
        MapleCharacter chr = player.getMap().getCharById(charId);
        if (chr == null) {
            c.announce(WorldPacket.serverMsg("角色不存在", ServerMsgType.ALERT));
            return;
        }
        c.announce(UserRemote.charInfo(chr));
    }

    //取消椅子
    public static void handleUserSitRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        short fieldSeatId = in.readShort();
        player.setChairId(fieldSeatId);
        c.announce(UserPacket.sitResult(player.getId(), fieldSeatId));
        player.getMap().broadcastMessage(player, UserRemote.remoteSetActivePortableChair(player.getId(), 0, 0, (short) 0, 0, (byte) 0), false);
    }

    public static void handleUserPortableChairSitRequest(InPacket in, MapleClient c) {
        int mapId = in.readInt();
        int chairId = in.readInt();
        int pos = in.readByte();
        boolean textChair = in.readInt() != 0;
        Position position = in.readPosInt();
        in.readInt();
        int unk1 = in.readInt();
        short unk2 = in.readShort();
        in.skip(3);
        int unk3 = in.readInt();
        byte unk4 = in.readByte();
        c.announce(UserPacket.enableActions());
        c.announce(UserPacket.userSit());
        MapleCharacter player = c.getPlayer();
        player.getMap().broadcastMessage(player, UserRemote.remoteSetActivePortableChair(player.getId(), chairId, unk1, unk2, unk3, unk4), false);
    }

    /*
        技能开始
     */
    public static void handleSkillUp(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        int skillId = in.readInt();
        int level = in.readInt();
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
        Map<Stat, Long> stats;
        int rootId = skill.getRootId();
        if ((!JobConstants.isBeginnerJob((short) rootId) && !SkillConstants.isMatching(rootId, player.getJob())) || SkillConstants.isSkillFromItem(skillId)) {
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
        } else if (JobConstants.isExtendSpJob(player.getJob())) {
            List<Integer> remainingSp = player.getRemainingSp();
            Integer sp = remainingSp.get(jobLevel - 1);
            if (sp >= level) {
                int curLevel = curSkill == null ? 0 : curSkill.getCurrentLevel();
                int max = curSkill == null ? skill.getMaxLevel() : curSkill.getMaxLevel();
                int newLevel = Math.min(curLevel + level, max);
                skill.setCurrentLevel(newLevel);
                player.addSp(-level, jobLevel);
                stats = new HashMap<>();
                stats.put(Stat.AVAILABLESP, (long) 1);
            } else {
                log.error(String.format("Character %d tried adding a skill without having the required amount of sp" +
                                " (required %d, has %d)",
                        player.getId(), level, sp));
                return;
            }
        } else {
            Integer currentSp = player.getRemainingSp().get(0);
            if (currentSp >= level) {
                int curLevel = curSkill == null ? 0 : curSkill.getCurrentLevel();
                int max = curSkill == null ? skill.getMaxLevel() : curSkill.getMaxLevel();
                int newLevel = Math.min(curLevel + level, max);
                skill.setCurrentLevel(newLevel);
                player.addSp(-level, 1);
                stats = new HashMap<>();
                stats.put(Stat.AVAILABLESP, (long) 1);
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

    public static void handleUseSkill(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        in.readInt(); //crc
        int skillId = in.readInt();
        int skillLevel = in.readInt();
        if (player.applyMpCon(skillId, skillLevel) && player.isSkillInCd(skillId)) {
            player.getMap().broadcastMessage(UserRemote.effect(player.getId(), Effect.skillUse(skillId, (byte) skillLevel, 0)));
            SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
            MapleJob sourceJobHandler = player.getJobHandler();
            if (sourceJobHandler.isBuff(skillId) && skillInfo.isMassSpell()) {
                Rect rect = skillInfo.getFirstRect();
                if (rect != null) {
                    Rect rectAround = player.getRectAround(rect);
                    for (PartyMember pm : player.getParty().getOnlineMembers()) {
                        if (pm.getChr() != null
                                && pm.getMapId() == player.getMapId()
                                && rectAround.hasPositionInside(pm.getChr().getPosition())) {
                            MapleCharacter ptChr = pm.getChr();
                            Effect effect = Effect.skillAffected(skillId, skillLevel, 0);
                            if (ptChr != player) { // Caster shouldn't get the Affected Skill Effect
                                ptChr.getMap().broadcastMessage(ptChr,
                                        UserRemote.effect(ptChr.getId(), effect)
                                        , false);
                                ptChr.announce(UserPacket.effect(effect));
                            }
                            sourceJobHandler.handleSkill(pm.getChr().getClient(), skillId, skillLevel, in);
                        }
                    }
                }
                sourceJobHandler.handleSkill(c, skillId, skillLevel, in);
            } else {
                sourceJobHandler.handleSkill(c, skillId, skillLevel, in);
            }
        }
    }

    public static void handleCancelBuff(InPacket in, MapleClient c) {
        int skillId = in.readInt();
        in.readByte();
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        tsm.removeStatsBySkill(skillId);
    }

    /*
        技能结束
     */

    //拾取
    public static void handlePickUp(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        byte mapKey = in.readByte();
        player.setTick(in.readInt());
        Position position = in.readPos();
        int dropId = in.readInt();
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
        player.getMap().broadcastMessage(UserRemote.hiddenEffectEquips(player));
    }

    //自动回复
    public static void handleChangeStatRequest(InPacket in, MapleClient c) {

        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        long mask = in.readLong();
        List<Stat> stats = Stat.getStatsByMask(mask);
        HashMap<Stat, Long> updatedStats = new HashMap<>();
        for (Stat stat : stats) {
            updatedStats.put(stat, (long) in.readShort());
        }
        if (updatedStats.containsKey(Stat.HP)) {
            player.heal(Math.toIntExact(updatedStats.get(Stat.HP)));
        }
        if (updatedStats.containsKey(Stat.MP)) {
            player.healMP(Math.toIntExact(updatedStats.get(Stat.MP)));
        }
    }

    public static void handleChangeQuickSlot(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        ArrayList<Integer> aKeys = new ArrayList<>();
        if (in.available() == QUICKSLOT_SIZE * 4) {
            for (int i = 0; i < QUICKSLOT_SIZE; i++) {
                aKeys.add(in.readInt());
            }
        }
        player.setQuickslots(aKeys);
    }

    public static void handleChangeKeyMap(InPacket in, MapleClient c) {
        in.skip(4);
        int size = in.readInt();
        MapleKeyMap keyMap = c.getPlayer().getKeyMap();
        if (keyMap == null) {
            keyMap = new MapleKeyMap(false);
        }
        for (int i = 0; i < size; i++) {
            int key = in.readInt();
            byte type = in.readByte();
            int action = in.readInt();
            keyMap.putKeyBinding(key, type, action);
        }
        c.getPlayer().setKeyMap(keyMap);
    }

    public static void handleAPUpdateRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getRemainingAp() <= 0) {
            return;
        }
        player.setTick(in.readInt());
        short stat = in.readShort();
        Stat charStat = Stat.getByValue(stat);
        if (charStat == null) {
            return;
        }
        int amount = 1;
        if (charStat == Stat.MAXMP || charStat == Stat.MAXHP) {
            amount = 20;
        }
        player.addStat(charStat, amount);
        player.addStat(Stat.AVAILABLEAP, (short) -1);
        Map<Stat, Long> stats = new HashMap<>();
        stats.put(charStat, player.getStat(charStat));
        stats.put(Stat.AVAILABLEAP, player.getStat(Stat.AVAILABLEAP));
        c.announce(UserPacket.updatePlayerStats(stats, true, player));
    }

    public static void handleAPMassUpdateRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getRemainingAp() <= 0) {
            return;
        }
        player.setTick(in.readInt());
        int type = in.readInt();
        int amount;
        Stat charStat = null;
        if (type == 1) {
            charStat = Stat.getByValue(in.readLong());
        } else if (type == 2) {
            in.readInt();
            in.readInt();
            in.readInt();
            charStat = Stat.getByValue(in.readLong());
        }
        if (charStat == null) {
            return;
        }
        amount = in.readInt();
        int addStat = amount;
        if (player.getRemainingAp() < amount) {
            return;
        }
        if (charStat == Stat.MAXMP || charStat == Stat.MAXHP) {
            addStat *= 20;
        }
        player.addStat(charStat, addStat);
        player.addStat(Stat.AVAILABLEAP, -amount);
        Map<Stat, Long> stats = new HashMap<>();
        stats.put(charStat, player.getStat(charStat));
        stats.put(Stat.AVAILABLEAP, player.getStat(Stat.AVAILABLEAP));
        c.announce(UserPacket.updatePlayerStats(stats, true, player));
    }

    //内在能力
    public static void handleUserRequestCharacterPotentialSkillRandSetUi(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        int cost = GameConstants.CHAR_POT_RESET_COST;
        int rate = in.readInt();
        int size = in.readInt();
        Set<Integer> lockedLines = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lockedLines.add(in.readInt());
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

    public static void handleUserDamageSkinSaveRequest(InPacket in, MapleClient c) {
        byte b = in.readByte(); //unk
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

    public static void handleUserActivateNickItem(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int itemId = in.readInt();
        short pos = in.readShort();
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
        player.addQuestExAndSendPacket(QUEST_EX_NICK_ITEM, value);
    }

    public static void handleUserActivateDamageSkin(InPacket in, MapleClient c) {
        int damageSkinId = in.readInt();
        MapleCharacter chr = c.getPlayer();
        chr.setDamageSkin(chr.getDamageSkinBySkinId(damageSkinId));
        chr.getMap().broadcastMessage(chr, UserRemote.setDamageSkin(chr), true);
    }

    /*
        切换地图
     */
    public static void handleChangeMapRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        if (in.available() == 0) {
            c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
            player.changeChannel((byte) player.getChannel());
            return;
        }
        if (in.available() != 0) {
            byte type = in.readByte();
            int targetId = in.readInt();
            String portalName = in.readMapleAsciiString();
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

    public static void handleUserEnterPortalSpecialRequest(InPacket in, MapleClient c) {
        byte type = in.readByte();
        String portalName = in.readMapleAsciiString();
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


    //给其他角色增加人气
    public static void handleUserAddFameRequest(InPacket in, MapleClient c) {
        int charId = in.readInt();
        MapleCharacter player = c.getPlayer();
        MapleCharacter other = player.getMap().getCharById(charId);
        if (other == null) {
            player.chatMessage("找不到角色");
            return;
        }
        byte mode = in.readByte();
        int fameChange = mode == 0 ? -1 : 1;
        other.addStatAndSendPacket(Stat.FAME, fameChange);
        player.announce(UserPacket.addFameResponse(other, mode, other.getFame()));
        other.announce(UserPacket.receiveFame(mode, player.getName()));
    }

    //角色表情
    public static void handleCharEmotion(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int emotion = in.readInt();
        int duration = in.readInt();
        boolean byItemOption = in.readByte() != 0;
        if (GameConstants.isValidEmotion(emotion)) {
            player.getMap().broadcastMessage(player, UserRemote.emotion(player.getId(), emotion, duration, byItemOption), false);
        }
    }

    /*
        超级技能/属性
     */
    public static void handleUserHyperUpRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        int skillId = in.readInt();
        SkillInfo si = SkillData.getSkillInfo(skillId);
        if (si == null) {
            player.chatMessage("attempted assigning hyper SP to a skill with null");
            return;
        }
        if (si.getHyper() == 0 && si.getHyperStat() == 0) {
            log.error(String.format("Character %d attempted assigning hyper SP to a wrong skill (skill id %d, player job %d)", player.getId(), skillId, player.getJob()));
            return;
        }
        Skill skill = player.getSkill(skillId, true);
        if (si.getHyper() != 0) { //超级技能
            if (si.getHyper() == 1) {
                int totalSp = SkillConstants.getTotalHyperPassiveSkillSp(player.getLevel());
                int spentSp = player.getSpentHyperPassiveSkillSp();
                int availableSp = totalSp - spentSp;
                if (availableSp <= 0 || skill.getCurrentLevel() != 0) {
                    return;
                }
            } else if (si.getHyper() == 2) {
                int totalSp = SkillConstants.getTotalHyperActiveSkillSp(player.getLevel());
                int spentSp = player.getSpentHyperActiveSkillSp();
                int availableSp = totalSp - spentSp;
                if (availableSp <= 0 || skill.getCurrentLevel() != 0) {
                    return;
                }
            }
        } else if (si.getHyperStat() != 0) { //超级属性
            int totalHyperSp = SkillConstants.getHyperStatSpByLv((short) player.getLevel());
            int spentSp = player.getSpentHyperStatSp();
            int availableSp = totalHyperSp - spentSp;
            int neededSp = SkillConstants.getNeededSpForHyperStatSkill(skill.getCurrentLevel() + 1);
            if (skill.getCurrentLevel() >= skill.getMaxLevel() || availableSp < neededSp) {
                return;
            }
        } else {
            log.error(String.format("Character %d attempted assigning hyper stat to an improper skill. (%d, job %d)", player.getId(), skillId, player.getJob()));
            return;
        }
        player.removeFromBaseStatCache(skill);
        skill.setCurrentLevel(skill.getCurrentLevel() + 1);
        player.addToBaseStatCache(skill);
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        player.addSkill(skill);
        player.announce(UserPacket.changeSkillRecordResult(skills, true, false, false, false));
    }

    public static void handleUserHyperSkillResetRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        if (player.getMeso() < GameConstants.HYPER_SKILL_RESET_COST) {
            player.chatMessage("Not enough money for this operation.");
        } else {
            player.deductMoney(GameConstants.HYPER_SKILL_RESET_COST);
            List<Skill> skills = new ArrayList<>();
            for (int skillId = 80000400; skillId <= 80000418; skillId++) {
                Skill skill = player.getSkill(skillId);
                if (skill != null) {
                    skill.setCurrentLevel(0);
                    skills.add(skill);
                    player.addSkill(skill);
                }
            }
            player.announce(UserPacket.changeSkillRecordResult(skills, true, false, false, false));
        }
    }

    public static void handleUserHyperStatResetRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        if (player.getMeso() < GameConstants.HYPER_STAT_RESET_COST) {
            player.chatMessage("Not enough money for this operation.");
        } else {
            player.deductMoney(GameConstants.HYPER_STAT_RESET_COST);
            List<Skill> skills = new ArrayList<>();
            int skillBaseId = player.getJob() * 10000 + 31;
            for (int skillId = skillBaseId; skillId <= skillBaseId + 100; skillId++) {
                Skill skill = player.getSkill(skillId);
                if (skill != null) {
                    skill.setCurrentLevel(0);
                    skills.add(skill);
                    player.addSkill(skill);
                }
            }
            player.announce(UserPacket.changeSkillRecordResult(skills, true, false, false, false));
        }
    }

    // 商城操作
    public static void handleCashShopCashItemRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Account account = player.getAccount();
        Locker locker = account.getLocker();
        byte type = in.readByte();
        CashItemType cit = CashItemType.getRequestTypeByVal(type);
        CashShopServer cashShop = Server.getInstance().getCashShop(player.getWorld());
        if (cit == null) {
            log.error("Unhandled cash shop cash item request " + type);
            player.enableAction();
            return;
        }
        switch (cit) {
            case Req_SetCart: {
                in.readByte();
                List<WishedItem> wishedItems = new ArrayList<>();
                while (in.available() >= 4) {
                    int itemId = in.readInt();
                    WishedItem item = new WishedItem(in.readInt());
                    if (itemId != 0) {
                        wishedItems.add(item);
                    }
                }
                player.getWishedItems().clear();
                player.getWishedItems().addAll(wishedItems);
                break;
            }
            case Req_Buy: {
                CashShopCurrencyType currencyType = CashShopCurrencyType.getByVal(in.readByte());
                if (currencyType == null) {
                    player.chatMessage(ChatType.Notice, "暫不支持的貨幣類型");
                    return;
                }
                in.readShort(); // 00 00
                int sn = in.readInt();
                int quantity = in.readInt();
                CashItemInfo cashItemInfo = ItemData.getCashItemInfo(sn);
                if (cashItemInfo == null) {
                    player.enableAction();
                    return;
                }
                int currency;
                switch (currencyType) {
                    case Cash:
                        currency = account.getCash();
                        break;
                    case Voucher:
                        currency = account.getVoucher();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currencyType);
                }
                int price = cashItemInfo.getPrice();
                int cost = price * quantity;
                if (cost > currency) {
                    player.announce(CashShopPacket.buyFailed(CashItemType.FailReason_NoRemainCash));
                    return;
                }
                player.addCurrency(currencyType, -cost);
                Item itemCopy = ItemData.getItemCopy(cashItemInfo.getItemId(), false);
                itemCopy.setCashItemSerialNumber(cashShop.getNextSerialNumber());
                locker.putItem(itemCopy, 1);
                player.announce(CashShopPacket.buyDone(account, itemCopy));
                player.announce(CashShopPacket.queryCashResult(account));
                break;
            }
            case Req_EnableEquipSlotExt: {
                in.readByte();
                int sn = in.readInt();
                CashItemInfo cashItemInfo = ItemData.getCashItemInfo(sn);
                int extendDay = 0;
                if (cashItemInfo.getItemId() == 5550001) {
                    extendDay = 7;
                } else if (cashItemInfo.getItemId() == 5550000) {
                    extendDay = 30;
                }
                long maxTime = LocalDateTime.now().plusDays(364).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                if (player.getExtendedPendant() < DateUtil.getFileTime(System.currentTimeMillis())) { //已过期或还未购买过
                    long expiredTime = LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    player.setExtendedPendant(DateUtil.getFileTime(expiredTime));
                } else if (player.getExtendedPendant() > DateUtil.getFileTime(maxTime)) {
                    player.announce(CashShopPacket.buyFailed(CashItemType.FailReason_Max_Time_Limit));
                    return;
                } else {
                    player.setExtendedPendant(player.getExtendedPendant() + ONE_DAY_TIMES * extendDay * 10000);
                }
                player.announce(CashShopPacket.enableEquipSlotExtDone(extendDay));
                break;
            }
            case Req_MoveLtoS: { // 保管箱-》背包
                long serialNumber = in.readLong();
                int itemId = in.readInt();
                byte val = in.readByte(); //invType
                short pos = in.readShort(); //toPos
                InventoryType inventoryType = InventoryType.getTypeById(val);
                Item item = locker.getItemBySerialNumber(serialNumber);
                if (item.getItemId() != itemId || inventoryType == null) {
                    return;
                }
                item.setPos(pos);
                locker.removeItemBySerialNumber(serialNumber);
                player.getInventory(inventoryType).addItem(item);
                player.announce(CashShopPacket.moveLtoSDone(item));
                break;
            }
            case Req_MoveStoL: { //背包-》保管箱
                long serialNumber = in.readLong();
                int itemId = in.readInt();
                byte val = in.readByte();
                short pos = in.readShort();
                InventoryType inventoryType = InventoryType.getTypeById(val);
                if (inventoryType == null) {
                    return;
                }
                Item item = player.getInventory(inventoryType).getItem(pos);
                if (item.getItemId() != itemId || item.getCashItemSerialNumber() != serialNumber) {
                    return;
                }
                item.setInvType(null);
                locker.putItem(item, 1);
                player.announce(CashShopPacket.moveStoLDone(account, item));
                break;
            }
            case Req_Destroy: {
                long serialNumber = in.readLong();
                locker.removeItemBySerialNumber(serialNumber);
                player.announce(CashShopPacket.rebateDone(serialNumber));
                break;
            }
            case Req_BuyPackage: {
                in.readByte();
                long sn = in.readInt();
                int itemCount = in.readInt();
                player.announce(CashShopPacket.buyPackageDone(new ArrayList<>(), account));
                break;
            }
            case Req_BuyNormal: {
                int sn = in.readInt();
                //todo
                //0x6f add
                //0x7c0 updatePlayerStat 00 00 04 00 00 00 00 00 B1 8A 2D 00 00 00 00 00
                //Res_BuyNormal_Done 4B 01 00 00 00 01 00 06 00 D7 82 3D 00
                break;
            }
            case Req_Rebate: {
                short i = in.readShort();
                in.readByte();
                long serialNumber = in.readLong();
                Item item = locker.getItemBySerialNumber(serialNumber);
                int sn = ItemData.getSn(item.getItemId());
                CashItemInfo cashItemInfo = ItemData.getCashItemInfo(sn);
                int price = cashItemInfo.getPrice();
                account.addVoucher((int) (price * 0.3));
                locker.removeItemBySerialNumber(serialNumber);
                player.announce(CashShopPacket.destroyDone(serialNumber));
                player.announce(CashShopPacket.queryCashResult(account));
                break;
            }
            default:
                player.announce(CashShopPacket.buyFailed(CashItemType.FailReason_Max_Time_Limit));
                break;
        }
    }

    // todo 点击机器人打开商店
    public static void handleUserSelectAndroid(InPacket in, MapleClient c) {
        in.readInt(); //charId
        int type = in.readInt();
        Position position = in.readPosInt();
        MapleCharacter player = c.getPlayer();
        Android android = player.getAndroid();
        AndroidInfo androidInfo = ItemData.getAndroidInfoByType(type);
        if (androidInfo == null || android == null) {
            player.enableAction();
            return;
        }
        if (androidInfo.isShopUsable()) {
            player.dropMessage("机器人商店");
            player.enableAction();
        }
    }

    public static void handleUserSoulEffectRequest(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        boolean set = in.readByte() != 0;
        HashMap<String, String> options = new HashMap<>();
        options.put("effect", set ? "1" : "0");
        chr.addQuestExAndSendPacket(QUEST_EX_SOUL_EFFECT, options);
        chr.getMap().broadcastMessage(UserRemote.setSoulEffect(chr.getId(), set));
    }

    public static void handleUserMacroSysDataModified(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        List<Macro> macros = new ArrayList<>();
        byte size = in.readByte();
        for (byte i = 0; i < size; i++) {
            Macro macro = new Macro();
            macro.setName(in.readMapleAsciiString());
            macro.setMuted(in.readByte() != 0);
            for (int j = 0; j < 3; j++) {
                macro.setSkillAtPos(j, in.readInt());
            }
            macros.add(macro);
        }
        player.getMacros().clear();
        player.getMacros().addAll(macros); // don't set macros directly, as a new row will be made in the DB
    }

    public static void handleUserActivateEffectItem(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int i = in.readInt();
        //todo
    }
}
