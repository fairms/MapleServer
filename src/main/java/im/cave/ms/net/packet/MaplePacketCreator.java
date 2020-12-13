package im.cave.ms.net.packet;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.net.crypto.TripleDESCipher;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.net.packet.opcode.RecvOpcode;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.Randomizer;

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

    public static OutPacket encodeOpcodes(MapleClient client) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.OPCODE_TABLE.getValue());
        outPacket.writeInt(4); //block size
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
            outPacket.writeInt(buffer.length);
            outPacket.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
        }
        return outPacket;
    }

    /*
     * 00 00 FF 00 00 FF 00 00 FF 00 00 FF 00 00 FF
     * 限制称号显示数量
     */
    public static OutPacket cancelTitleEffect() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CANCEL_TITLE_EFFECT.getValue());
        for (int i = 0; i < 5; i++) {
            outPacket.writeShort(0);
            outPacket.write(-1);
        }
        return outPacket;
    }

    public static OutPacket openWorldMap() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.OPEN_WORLDMAP.getValue());
        outPacket.writeInt(0);
        return outPacket;
    }

    public static OutPacket getChatText(MapleCharacter player, String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHATTEXT.getValue());
        outPacket.writeInt(player.getId());
        outPacket.writeBool(true);
        outPacket.writeMapleAsciiString(content);
        outPacket.writeMapleAsciiString(player.getName());
        outPacket.writeMapleAsciiString(content);
        outPacket.writeLong(0);
        outPacket.write(1);
        outPacket.writeInt(1);
        outPacket.write(3);
        outPacket.write(1);
        outPacket.write(0xFF);
        return outPacket;
    }

    public static OutPacket blackboard(MapleCharacter chr, boolean show, String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeInt(chr.getId());
        outPacket.writeBool(show);
        if (show) {
            outPacket.writeMapleAsciiString(content);
        }
        return outPacket;
    }


    public static OutPacket enableActions() {
        return updatePlayerStats(EMPTY_STATUS, true, null);
    }

    public static OutPacket updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static OutPacket updatePlayerStats(Map<MapleStat, Long> stats, boolean enableActions, MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.UPDATE_STATS.getValue());
        outPacket.write(enableActions ? 1 : 0);

        outPacket.write(0); //unk

        long mask = 0;
        for (MapleStat stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        outPacket.writeLong(mask);
        Comparator<MapleStat> comparator = Comparator.comparingLong(MapleStat::getValue);
        TreeMap<MapleStat, Long> sortedStats = new TreeMap<>(comparator);
        sortedStats.putAll(stats);
        for (Map.Entry<MapleStat, Long> entry : sortedStats.entrySet()) {
            MapleStat stat = entry.getKey();
            long value = entry.getValue();
            switch (stat) {
                case SKIN:
                    outPacket.write((byte) value);
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
                    outPacket.writeInt((int) value);
                    break;
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                case FATIGUE:
                    outPacket.writeShort((int) value);
                    break;
                case AVAILABLESP:
                    PacketHelper.addCharSP(outPacket, chr);
                    break;
                case EXP:
                case MESO:
                    outPacket.writeLong(value);
                    break;
                case TODAYS_TRAITS:
                    outPacket.writeZeroBytes(21); //限制
                    break;
            }
        }
        outPacket.write(chr != null ? chr.getHairColorBase() : -1);
        outPacket.write(chr != null ? chr.getHairColorMixed() : 0);
        outPacket.write(chr != null ? chr.getHairColorProb() : 0);


        outPacket.write(0);
        if (mask == 0 || !enableActions) { //unknown
            outPacket.write(0);
        }
        outPacket.write(0);

        return outPacket;

    }

    public static OutPacket getChannelChange(int port) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHANGE_CHANNEL.getValue());
        outPacket.write(1);
        outPacket.write(NEXON_IP);
        outPacket.writeShort(port);
        return outPacket;
    }

    public static OutPacket spawnNpc(Npc npc) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_NPC.getValue());
        outPacket.writeInt(npc.getObjectId());
        outPacket.writeInt(npc.getTemplateId());
        npc.encode(outPacket);
        return outPacket;

    }

    public static OutPacket spawnNpcController(Npc npc) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        outPacket.write(1);
        outPacket.writeInt(npc.getObjectId());
        outPacket.writeInt(npc.getTemplateId());
        npc.encode(outPacket);
        return outPacket;
    }

    public static OutPacket spawnMob(Mob mob) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_MOB.getValue());
        outPacket.writeBool(mob.isSealedInsteadDead());
        outPacket.writeInt(mob.getObjectId());
        outPacket.write(mob.getCalcDamageIndex());
        outPacket.writeInt(mob.getTemplateId());
        //unk
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0x20);
        outPacket.writeShort(0);

        outPacket.writeShort(mob.getPosition().getX());
        outPacket.writeShort(mob.getPosition().getY());
        outPacket.write(mob.getMoveAction());
        outPacket.writeShort(mob.getFh());
        outPacket.writeShort(mob.getFh());
        outPacket.writeShort(mob.getAppearType());
        outPacket.write(mob.getTeamForMCarnival());
        outPacket.writeLong(mob.getMaxHp());

        outPacket.writeInt(mob.getEffectItemID());
        outPacket.writeInt(mob.getPhase());
        outPacket.writeInt(mob.getCurZoneDataType());
        outPacket.writeInt(mob.getRefImgMobID());
        outPacket.writeInt(0);

        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.write(0);
        outPacket.writeInt(mob.getScale());
        outPacket.writeInt(-1);
        outPacket.writeZeroBytes(42);
        return outPacket;
    }

    public static OutPacket mobChangeController(Mob mob) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
        outPacket.write(1);
        outPacket.writeInt(mob.getObjectId());
        outPacket.write(0);
        outPacket.writeInt(mob.getTemplateId());

        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0x20);
        outPacket.writeShort(0);

        outPacket.writeShort(mob.getPosition().getX());
        outPacket.writeShort(mob.getPosition().getY());
        outPacket.write(mob.getMoveAction());
        outPacket.writeShort(mob.getFh());
        outPacket.writeShort(mob.getFh());
        outPacket.writeShort(mob.getAppearType());
        outPacket.write(mob.getTeamForMCarnival());
        outPacket.writeLong(mob.getMaxHp());

        outPacket.writeInt(mob.getEffectItemID());
        outPacket.writeInt(mob.getPhase());
        outPacket.writeInt(mob.getCurZoneDataType());
        outPacket.writeInt(mob.getRefImgMobID());
        outPacket.writeInt(0);

        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.write(0);
        outPacket.writeInt(mob.getScale());
        outPacket.writeInt(-1);
        outPacket.writeZeroBytes(42);
        return outPacket;
    }

    public static OutPacket startBattleAnalysis() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.BATTLE_ANALYSIS.getValue());
        outPacket.write(1);
        return outPacket;
    }

    public static OutPacket keymapInit(MapleCharacter character) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.KEYMAP_INIT.getValue());
        character.getKeyMap().encode(outPacket);
        return outPacket;
    }

    public static OutPacket quickslotInit(MapleCharacter player) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.QUICKSLOT_INIT.getValue());
        boolean edited = player.getQuickslots() != null && player.getQuickslots().size() == 32;
        outPacket.writeBool(edited);
        if (player.getQuickslots() != null) {
            for (Integer key : player.getQuickslots()) {
                outPacket.writeInt(key);
            }
        }
        return outPacket;
    }

    public static OutPacket fieldEffect(FieldEffect effect) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.FIELD_EFFECT.getValue());
        effect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket fieldMessage(int itemId, String message, int duration) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.FIELD_MESSAGE.getValue());
        outPacket.write(0);
        outPacket.writeInt(itemId);
        outPacket.writeMapleAsciiString(message);
        outPacket.writeInt(duration);
        outPacket.write(0);
        return outPacket;
    }

}
