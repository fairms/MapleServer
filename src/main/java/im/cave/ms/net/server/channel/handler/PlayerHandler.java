package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.Job.MapleJob;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleKeyMap;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.client.skill.MobAttackInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.ServerMsgType;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.packet.PlayerPacket;
import im.cave.ms.net.packet.opcode.RecvOpcode;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static im.cave.ms.constants.GameConstants.QUICKSLOT_SIZE;
import static im.cave.ms.net.packet.opcode.RecvOpcode.CLOSE_RANGE_ATTACK;
import static im.cave.ms.net.packet.opcode.RecvOpcode.MAGIC_ATTACK;
import static im.cave.ms.net.packet.opcode.RecvOpcode.RANGED_ATTACK;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 12/1 14:59
 */
public class PlayerHandler {
    private static final Logger log = LoggerFactory.getLogger(PlayerHandler.class);

    public static void handleHit(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        slea.skip(4);
        slea.skip(4);
        player.setTick(slea.readInt());
        byte type = slea.readByte();
        byte element = slea.readByte();
        int damage = slea.readInt();
        if (JobConstants.isGmJob(player.getJobId())) {
            return;
        }
        slea.skip(2);
        if (slea.available() >= 13) {
            int objId = slea.readInt();
            int mobId = slea.readInt();
            slea.skip(4);   //objId
        }
        HashMap<MapleStat, Long> stats = new HashMap<>();
        int curHp = player.getStat(MapleStat.HP);
        int newHp = curHp - damage;
        if (newHp < 0) {
            newHp = 0;
            c.announce(PlayerPacket.sendRebirthConfirm(true, false,
                    false, false
                    , false, 0, 0));
        }
        player.setStat(MapleStat.HP, newHp);
        stats.put(MapleStat.HP, (long) newHp);
        c.announce(MaplePacketCreator.updatePlayerStats(stats, player));
    }

    public static void handleAttack(SeekableLittleEndianAccessor slea, MapleClient c, RecvOpcode opcode) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        AttackInfo attackInfo = new AttackInfo();
        attackInfo.attackHeader = opcode;
        if (opcode == RANGED_ATTACK) {
            attackInfo.boxAttack = slea.readByte() != 0;
        }
        attackInfo.fieldKey = slea.readByte(); //map key
        byte mask = slea.readByte();
        attackInfo.hits = (byte) (mask & 0xF);
        attackInfo.mobCount = (mask >>> 4) & 0xF;
        slea.readInt(); //00 00 00 00
        attackInfo.skillId = slea.readInt();
        attackInfo.skillLevel = slea.readInt();
        slea.readLong(); // crc
        if (attackInfo.attackHeader != MAGIC_ATTACK) {
            slea.readByte();//unk
        }
        slea.skip(18);
        short pX = slea.readShort(); // int?
        slea.readShort();
        short pY = slea.readShort();
        slea.readShort();

        slea.readInt(); // 00 00 00 00
        slea.readInt(); // DF 2B 9E 22 固定的
        slea.skip(3);
        if (attackInfo.attackHeader == RANGED_ATTACK) {
            slea.readInt();
            slea.readByte();
        }
        attackInfo.attackAction = slea.readByte();
        attackInfo.direction = slea.readByte();
        attackInfo.requestTime = slea.readInt();
        attackInfo.attackActionType = slea.readByte(); // 武器类型
        attackInfo.attackSpeed = slea.readByte();
        player.setTick(slea.readInt());
        slea.readInt();
        if (attackInfo.attackHeader == CLOSE_RANGE_ATTACK) {
            slea.readInt();
        }
        if (attackInfo.attackHeader == RANGED_ATTACK) {
            slea.readInt(); // 00
            slea.readShort(); // 00
            slea.readByte(); // 30
            attackInfo.rect = slea.readShortRect();
        }
        for (int i = 0; i < attackInfo.mobCount; i++) {
            MobAttackInfo mobAttackInfo = new MobAttackInfo();
            mobAttackInfo.objectId = slea.readInt();
            mobAttackInfo.hitAction = slea.readByte();
            slea.readShort();
            mobAttackInfo.left = slea.readByte();
            slea.readByte();
            mobAttackInfo.templateID = slea.readInt();
            mobAttackInfo.calcDamageStatIndex = slea.readByte();
            mobAttackInfo.hitX = slea.readShort();
            mobAttackInfo.hitY = slea.readShort();
            slea.readShort(); //x
            slea.readShort(); //y
            if (attackInfo.attackHeader == MAGIC_ATTACK) {
                mobAttackInfo.hpPerc = slea.readByte();
                short unk = slea.readShort(); //unk
            } else {
                byte unk1 = slea.readByte();
                byte unk2 = slea.readByte(); //1 正常 2 趴着
            }
            slea.readLong(); // 00
            mobAttackInfo.damages = new long[attackInfo.hits];
            for (byte j = 0; j < attackInfo.hits; j++) {
                mobAttackInfo.damages[j] = slea.readLong();
            }
            slea.readInt(); // 00 00 00 00
            slea.readInt(); // crc E7 DA 52 9A
            mobAttackInfo.type = slea.readByte();
            if (mobAttackInfo.type == 1) {
                mobAttackInfo.currentAnimationName = slea.readMapleAsciiString();
                slea.readMapleAsciiString();
                mobAttackInfo.animationDeltaL = slea.readInt();
                mobAttackInfo.hitPartRunTimesSize = slea.readInt();
                mobAttackInfo.hitPartRunTimes = new String[mobAttackInfo.hitPartRunTimesSize];
                for (int j = 0; j < mobAttackInfo.hitPartRunTimesSize; j++) {
                    mobAttackInfo.hitPartRunTimes[j] = slea.readMapleAsciiString();
                }
            } else if (mobAttackInfo.type == 2) {
                player.dropMessage("mobAttackInfo.type == 2 !!!");
            }
            slea.skip(14); //unk pos
            attackInfo.mobAttackInfo.add(mobAttackInfo);
        }
        player.getJobHandler().handleAttack(c, attackInfo);
        handleAttack(c, attackInfo);
    }

    public static void handleAttack(MapleClient c, AttackInfo attackInfo) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        int skillId = attackInfo.skillId;
        if (!player.applyMpCon(skillId, attackInfo.skillLevel)) {
            return;
        }
        for (MobAttackInfo mobAttackInfo : attackInfo.mobAttackInfo) {
            MapleMap map = player.getMap();
            Mob mob = (Mob) map.getObj(mobAttackInfo.objectId);
            if (mob == null) {
                player.dropMessage("unhandled mob is null");
            } else if (mob.getHp() > 0) {
                long totalDamage = Arrays.stream(mobAttackInfo.damages).sum();
                mob.damage(player, totalDamage);
                //todo handle reflect
            }

            if (mob != null && mob.getHp() < 0) {
                log.warn("mob was dead");
            }
        }
    }

    public static void handlePlayerMove(SeekableLittleEndianAccessor slea, MapleClient c) {

        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        slea.skip(1);    //unknown
        slea.skip(4);    //map relate
        slea.skip(4);    //tick
        slea.skip(1);    //unknown
        MovementInfo movementInfo = new MovementInfo(slea);
        movementInfo.applyTo(player);
        player.chatMessage(ChatType.Tip, player.getPosition().toString() + ", fh:" + player.getFoothold());
        player.getMap().broadcastMessage(player, PlayerPacket.move(player, movementInfo), true);
    }


    public static void handleWorldMapTransfer(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(slea.readInt());
        int mapId = slea.readInt();
        MapleChannel channel = c.getMapleChannel();
        MapleMap map = channel.getMap(mapId);
        player.changeMap(map, map.getDefaultPortal());
    }

    public static void handleCharInfoReq(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(slea.readInt());
        int charId = slea.readInt();
        MapleCharacter chr = player.getMap().getPlayer(charId);
        if (chr == null) {
            c.announce(ChannelPacket.serverMsg("角色不存在", ServerMsgType.ALERT));
            return;
        }
        c.announce(PlayerPacket.charInfo(chr));
    }

    /*
    取消椅子/城镇椅子
     */
    public static void cancelChair(SeekableLittleEndianAccessor slea, MapleClient c) {
        short id = slea.readShort();
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setChairId(id);
        c.announce(PlayerPacket.cancelChair(player.getId(), id));
    }

    public static void handleUseChair(SeekableLittleEndianAccessor slea, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeInt(SendOpcode.SIT_RESULT.getValue());
        mplew.writeInt(0);
        c.announce(mplew);
        c.announce(MaplePacketCreator.enableActions());
    }

    public static void handlePickUp(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        byte mapKey = slea.readByte();
        player.setTick(slea.readInt());
        Position position = slea.readPos();
        int dropId = slea.readInt();
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
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        equip.setShowEffect(!equip.isShowEffect());
        c.announce(PlayerPacket.hiddenEffectEquips(player));
    }

    public static void handleSkillUp(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(slea.readInt());
        int skillId = slea.readInt();
        int level = slea.readInt();
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

        c.announce(MaplePacketCreator.updatePlayerStats(stats, player));
        player.addSkill(skill);
        c.announce(PlayerPacket.changeSkillRecordResult(skill));

    }


    //自动回复
    public static void handleChangeStatRequest(SeekableLittleEndianAccessor slea, MapleClient c) {

        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(slea.readInt());
        long mask = slea.readLong();
        List<MapleStat> stats = MapleStat.getStatsByMask(mask);
        HashMap<MapleStat, Long> updatedStats = new HashMap<>();
        for (MapleStat stat : stats) {
            updatedStats.put(stat, (long) slea.readShort());
        }
        if (updatedStats.containsKey(MapleStat.HP)) {
            player.heal(Math.toIntExact(updatedStats.get(MapleStat.HP)));
        }
        if (updatedStats.containsKey(MapleStat.MP)) {
            player.healMP(Math.toIntExact(updatedStats.get(MapleStat.MP)));
        }
//        c.announce(MaplePacketCreator.updatePlayerStats(updatedStats, player));
    }

    public static void handleChangeQuickslot(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        ArrayList<Integer> aKeys = new ArrayList<>();
        if (slea.available() == QUICKSLOT_SIZE * 4) {
            for (int i = 0; i < QUICKSLOT_SIZE - 1; i++) {
                aKeys.add(slea.readInt());
            }
        }
        player.setQuickslots(aKeys);
    }

    public static void handleChangeKeyMap(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.skip(4);
        int size = slea.readInt();
        MapleKeyMap keyMap = c.getPlayer().getKeyMap();
        if (keyMap == null) {
            keyMap = new MapleKeyMap();
            keyMap.setDefault(false);
        }
        for (int i = 0; i < size; i++) {
            int key = slea.readInt();
            byte type = slea.readByte();
            int action = slea.readInt();
            keyMap.putKeyBinding(key, type, action);
        }
        c.getPlayer().setKeyMap(keyMap);
    }

    public static void handleUseSkill(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        slea.readInt(); //crc
        int skillId = slea.readInt();
        int skillLevel = slea.readInt();
        if (player.applyMpCon(skillId, skillLevel) && player.isSkillInCd(skillId)) {
            SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
            MapleJob sourceJobHandler = player.getJobHandler();
            if (sourceJobHandler.isBuff(skillId) && skillInfo.isMassSpell()) {
                Rect rect = skillInfo.getFirstRect();
//                if (r != null) {
//                    playxer.getRectAround(rect);
//                }
                //组队BUFF
                sourceJobHandler.handleSkill(c, skillId, skillLevel, slea);

            } else {
                sourceJobHandler.handleSkill(c, skillId, skillLevel, slea);
            }

        }
    }

    public static void handleCancelBuff(SeekableLittleEndianAccessor slea, MapleClient c) {
        int skillId = slea.readInt();
        slea.readByte();
        MapleCharacter player = c.getPlayer();
        TemporaryStatManager tsm = player.getTemporaryStatManager();
        tsm.removeStatsBySkill(skillId);
    }

    public static void handleAPUpdateRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getRemainingAp() <= 0) {
            return;
        }
        player.setTick(slea.readInt());
        short stat = slea.readShort();
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
        stats.put(charStat, (long) player.getStat(charStat));
        stats.put(MapleStat.AVAILABLEAP, (long) player.getStat(MapleStat.AVAILABLEAP));
        c.announce(MaplePacketCreator.updatePlayerStats(stats, true, player));
    }

    public static void handleAPMassUpdateRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getRemainingAp() <= 0) {
            return;
        }
        player.setTick(slea.readInt());
        int type = slea.readInt();
        int amount;
        MapleStat charStat = null;
        if (type == 1) {
            charStat = MapleStat.getByValue(slea.readLong());
        } else if (type == 2) {
            slea.readInt();
            slea.readInt();
            slea.readInt();
            charStat = MapleStat.getByValue(slea.readLong());
        }
        if (charStat == null) {
            return;
        }
        amount = slea.readInt();
        int addStat = amount;
        if (player.getRemainingAp() < amount) {
            return;
        }
        if (charStat == MapleStat.MAXMP || charStat == MapleStat.MAXHP) {
            addStat *= 20;
        }
        player.setStat(charStat, addStat);
        player.setStat(MapleStat.AVAILABLEAP, amount);
        Map<MapleStat, Long> stats = new HashMap<>();
        stats.put(charStat, (long) player.getStat(charStat));
        stats.put(MapleStat.AVAILABLEAP, (long) player.getStat(MapleStat.AVAILABLEAP));
        c.announce(MaplePacketCreator.updatePlayerStats(stats, player));
    }
}
