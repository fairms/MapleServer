package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
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
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        switch (opcode) {
            case CLOSE_RANGE_ATTACK://近距离攻击
                PlayerHandler.closeRangeAttack(slea, c, player);
                break;
//            case RANGED_ATTACK://远距离攻击
//                PlayerHandler.rangedAttack(slea, c, chr);
//                break;
//            case MAGIC_ATTACK://魔法攻击
//                PlayerHandler.MagicDamage(slea, c, chr);
//                break;
//            case SUMMON_ATTACK://召唤兽攻击
//                SummonHandler.SummonAttack(slea, c, chr);
//                break;
//            case PASSIVE_ENERGY:
//            case CLUSTER_ATTACK:
//                PlayerHandler.passiveRangeAttack(slea, c, chr);
        }

    }


    public static void closeRangeAttack(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(1); //map key
        byte mask = slea.readByte();
        byte hits = (byte) (mask & 0xF);
        int mobCount = (mask >>> 4) & 0xF;

        slea.skip(39); //known

        short pX = slea.readShort();
        slea.readShort();
        short pY = slea.readShort();
        slea.readShort();
        slea.skip(4); //unknown
        slea.skip(4); //F0 AB C2 E3
        slea.skip(3); //00 00 00
        byte attackAction = slea.readByte();
        byte direction = slea.readByte();
        slea.readInt(); // CB 58 E0 21; fixed
        byte weaponClass = slea.readByte();
        byte attackSpeed = slea.readByte();
        chr.setTick(slea.readInt());
        slea.skip(8); //00 00 00 00 00 00 00 00

        for (int i = 0; i < mobCount; i++) {
            int objId = slea.readInt();
            byte hitAction = slea.readByte();
            slea.skip(4); //00 00 01 00
            int mobId = slea.readInt();
            byte calcDamageStatIndexAndDoomed = slea.readByte();
            short hitX = slea.readShort();
            short hitY = slea.readShort();
            short unkPosX = slea.readShort();
            short unkPosY = slea.readShort();
            slea.readShort(); //unk
            slea.skip(8); //00 00 00 00 00 00 00 00
            long[] damages = new long[hits];
            for (int j = 0; j < hits; j++) {
                damages[j] = slea.readLong();
            }
            int mobUpDownYRange = slea.readInt();
            slea.skip(4);
            byte type = slea.readByte();
            slea.skip(14); //unk
            MapleMapObj obj = chr.getMap().getObj(objId);
            if (obj instanceof Mob) {
                Mob mob = (Mob) obj;
                long totalDamage = Arrays.stream(damages).sum();
                mob.damage(chr, totalDamage);
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
        mplew.writeInt(SendOpcode.USE_CHAIR.getValue());
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
        } else if (JobConstants.isExtendSpJob((short) player.getJobId())) {
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
}
