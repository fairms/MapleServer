package im.cave.ms.net.packet;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.net.crypto.TripleDESCipher;
import im.cave.ms.net.packet.opcode.RecvOpcode;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.Randomizer;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static im.cave.ms.constants.ServerConstants.DESKEY;
import static im.cave.ms.constants.ServerConstants.NEXON_IP;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/25 8:48
 */
public class MaplePacketCreator {
    public static final Map<MapleStat, Long> EMPTY_STATUS = Collections.emptyMap();

    public static MaplePacketLittleEndianWriter encodeOpcodes(MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.OPCODE_TABLE.getValue());
        mplew.writeInt(4); //block size
        List<Integer> used = new ArrayList<>();
        StringBuilder sOpcodes = new StringBuilder();
        for (int i = RecvOpcode.BEGIN.getValue(); i < RecvOpcode.END.getValue(); i++) {
            int opcode = Randomizer.rand(RecvOpcode.BEGIN.getValue(), 9999);
            while (used.contains(opcode)) {
                opcode = Randomizer.rand(RecvOpcode.BEGIN.getValue(), 9999);
            }
            String sOpcode = String.format("%04d", opcode);
            if (!used.contains(opcode)) {
                client.mEncryptedOpcode.put(opcode, i);
                used.add(opcode);
                sOpcodes.append(sOpcode);
            }
        }
        used.clear();

        TripleDESCipher tripleDESCipher = new TripleDESCipher(DESKEY);
        try {
            byte[] buffer = new byte[Short.MAX_VALUE + 1];
            byte[] encrypt = tripleDESCipher.Encrypt(sOpcodes.toString().getBytes());
            System.arraycopy(encrypt, 0, buffer, 0, encrypt.length);
            for (int i = encrypt.length; i < buffer.length; i++) {
                buffer[i] = 0;
            }
            mplew.writeInt(buffer.length);
            mplew.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
        }
        return mplew;
    }

    /*
     * 00 00 FF 00 00 FF 00 00 FF 00 00 FF 00 00 FF
     * 限制称号显示数量
     */
    public static MaplePacketLittleEndianWriter cancelTitleEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CANCEL_TITLE_EFFECT.getValue());
        for (int i = 0; i < 5; i++) {
            mplew.writeShort(0);
            mplew.write(-1);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter openWorldMap() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.OPEN_WORLDMAP.getValue());
        mplew.writeInt(0);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter getChatText(MapleCharacter player, String content) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHATTEXT.getValue());
        mplew.writeInt(player.getId());
        mplew.writeBool(true);
        mplew.writeMapleAsciiString(content);
        mplew.writeMapleAsciiString(player.getName());
        mplew.writeMapleAsciiString(content);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(1);
        mplew.write(3);
        mplew.write(1);
        mplew.write(0xFF);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter blackboard(MapleCharacter chr, boolean show, String content) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeInt(chr.getId());
        mplew.writeBool(show);
        if (show) {
            mplew.writeMapleAsciiString(content);
        }
        return mplew;
    }


    public static MaplePacketLittleEndianWriter enableActions() {
        return updatePlayerStats(EMPTY_STATUS, true, null);
    }

    public static MaplePacketLittleEndianWriter updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static MaplePacketLittleEndianWriter updatePlayerStats(Map<MapleStat, Long> stats, boolean enableActions, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.UPDATE_STATS.getValue());
        mplew.write(enableActions ? 1 : 0);

        mplew.write(0); //unk

        long mask = 0;
        for (MapleStat stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeLong(mask);
        Comparator<MapleStat> comparator = Comparator.comparingLong(MapleStat::getValue);
        TreeMap<MapleStat, Long> sortedStats = new TreeMap<>(comparator);
        sortedStats.putAll(stats);
        for (Map.Entry<MapleStat, Long> entry : sortedStats.entrySet()) {
            MapleStat stat = entry.getKey();
            long value = entry.getValue();
            switch (stat) {
                case SKIN:
                    mplew.write((byte) value);
                    break;
                case FACE:
                case HAIR:
                case HP:
                case MAXHP:
                case MP:
                case MAXMP:
                case FAME:
                case CHARISMA:
                case CHARM:
                case WILL:
                case SENSE:
                case INSIGHT:
                case CRAFT:
                case LEVEL:
                case ICE_GAGE:
                case JOB:
                    mplew.writeInt((int) value);
                    break;
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                case FATIGUE:
                    mplew.writeShort((int) value);
                    break;
                case AVAILABLESP:
                    PacketHelper.addCharSP(mplew, chr);
                    break;
                case EXP:
                case MESO:
                    mplew.writeLong(value);
                    break;
                case TODAYS_TRAITS:
                    mplew.writeZeroBytes(21); //限制
                    break;
            }
        }
        mplew.write(chr != null ? chr.getHairColorBase() : -1);
        mplew.write(chr != null ? chr.getHairColorMixed() : 0);
        mplew.write(chr != null ? chr.getHairColorProb() : 0);


        mplew.write(0);
        if (mask == 0 || !enableActions) { //unknown
            mplew.write(0);
        }
        mplew.write(0);

        return mplew;

    }

    public static MaplePacketLittleEndianWriter getChannelChange(int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        mplew.write(NEXON_IP);
        mplew.writeShort(port);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter spawnNpc(Npc npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SPAWN_NPC.getValue());
        mplew.writeInt(npc.getObjectId());
        mplew.writeInt(npc.getTemplateId());
        npc.encode(mplew);
        return mplew;

    }

    public static MaplePacketLittleEndianWriter spawnNpcController(Npc npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        mplew.write(1);
        mplew.writeInt(npc.getObjectId());
        mplew.writeInt(npc.getTemplateId());
        npc.encode(mplew);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter spawnMob(Mob mob) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SPAWN_MOB.getValue());
        mplew.writeBool(mob.isSealedInsteadDead());
        mplew.writeInt(mob.getObjectId());
        mplew.write(mob.getCalcDamageIndex());
        mplew.writeInt(mob.getTemplateId());
        //unk
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0x20);
        mplew.writeShort(0);

        mplew.writeShort(mob.getPosition().getX());
        mplew.writeShort(mob.getPosition().getY());
        mplew.write(mob.getMoveAction());
        mplew.writeShort(mob.getFh());
        mplew.writeShort(mob.getFh());
        mplew.writeShort(mob.getAppearType());
        mplew.write(mob.getTeamForMCarnival());
        mplew.writeLong(mob.getMaxHp());

        mplew.writeInt(mob.getEffectItemID());
        mplew.writeInt(mob.getPhase());
        mplew.writeInt(mob.getCurZoneDataType());
        mplew.writeInt(mob.getRefImgMobID());
        mplew.writeInt(0);

        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(mob.getScale());
        mplew.writeInt(-1);
        mplew.writeZeroBytes(42);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter mobChangeController(Mob mob) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(1);
        mplew.writeInt(mob.getObjectId());
        mplew.write(0);
        mplew.writeInt(mob.getTemplateId());

        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0x20);
        mplew.writeShort(0);

        mplew.writeShort(mob.getPosition().getX());
        mplew.writeShort(mob.getPosition().getY());
        mplew.write(mob.getMoveAction());
        mplew.writeShort(mob.getFh());
        mplew.writeShort(mob.getFh());
        mplew.writeShort(mob.getAppearType());
        mplew.write(mob.getTeamForMCarnival());
        mplew.writeLong(mob.getMaxHp());

        mplew.writeInt(mob.getEffectItemID());
        mplew.writeInt(mob.getPhase());
        mplew.writeInt(mob.getCurZoneDataType());
        mplew.writeInt(mob.getRefImgMobID());
        mplew.writeInt(0);

        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(mob.getScale());
        mplew.writeInt(-1);
        mplew.writeZeroBytes(42);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter startBattleAnalysis() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.BATTLE_ANALYSIS.getValue());
        mplew.write(1);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter keymapInit(MapleCharacter character) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.KEYMAP_INIT.getValue());
        character.getKeyMap().encode(mplew);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter quickslotInit(MapleCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.QUICKSLOT_INIT.getValue());
        boolean edited = player.getQuickslots() != null && player.getQuickslots().size() == 32;
        mplew.writeBool(edited);
        if (player.getQuickslots() != null) {
            for (Integer key : player.getQuickslots()) {
                mplew.writeInt(key);
            }
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter fieldEffect(FieldEffect effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
        effect.encode(mplew);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter fieldMessage(int itemId, String message, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.FIELD_MESSAGE.getValue());
        mplew.write(0);
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(duration);
        mplew.write(0);
        return mplew;
    }

}
