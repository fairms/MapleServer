package im.cave.ms.network.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.Trunk;
import im.cave.ms.client.character.ExpIncreaseInfo;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.QuickMoveInfo;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.party.PartyResult;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.DimensionalMirror;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.ServerMsgType;
import im.cave.ms.enums.TrunkOpType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Randomizer;

import java.util.List;
import java.util.Map;

import static im.cave.ms.constants.GameConstants.ZERO_TIME;
import static im.cave.ms.constants.ServerConstants.NEXON_IP;
import static im.cave.ms.enums.DropEnterType.Instant;
import static im.cave.ms.enums.MessageType.DROP_PICKUP_MESSAGE;
import static im.cave.ms.enums.MessageType.INC_EXP_MESSAGE;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 11/20 21:58
 */
public class WorldPacket {

    public static OutPacket getWarpToMap(MapleCharacter player, boolean firstLoggedIn) {
        return getWarpToMap(player, true, null, 0, firstLoggedIn);
    }

    public static OutPacket getWarpToMap(MapleCharacter player, MapleMap to, int spawnPoint) {
        return getWarpToMap(player, false, to, spawnPoint, false);
    }

    public static OutPacket getWarpToMap(MapleCharacter chr, boolean load, MapleMap to, int spawnPoint, boolean firstLoggedIn) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SET_MAP.getValue());
        outPacket.writeShort(1);
        outPacket.writeLong(1);
        outPacket.writeInt(chr.getClient().getChannel());
        outPacket.write(0);
        outPacket.writeInt(0);
        outPacket.write(0);
        outPacket.writeShort(Math.min(chr.getVisitedMapCount(), 255));
        outPacket.writeInt(!load ? to.getFieldType().getVal() : 0);
        outPacket.writeInt(chr.getMap().getWidth());
        outPacket.writeInt(chr.getMap().getHeight());
        outPacket.writeBool(load);
        outPacket.writeShort(0);
        if (load) {
            for (int i = 0; i < 3; i++) {
                outPacket.writeInt(Randomizer.nextInt());
            }
            PacketHelper.addCharInfo(outPacket, chr);
            outPacket.write(1);
            outPacket.write(0);
            outPacket.writeLong(ZERO_TIME);
            outPacket.writeZeroBytes(16);
        } else {
            outPacket.write(0); // usingBuffProtector
            outPacket.writeInt(to.getId()); //地图ID
            outPacket.write(spawnPoint);
            outPacket.writeInt(chr.getStats().getHp()); // 角色HP
            outPacket.writeZeroBytes(4);
        }
        outPacket.write(0);
        outPacket.write(0);
        outPacket.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        outPacket.writeInt(100); //mobStatAdjustRate
        outPacket.writeShort(0); //hasFieldCustom + is pvp map
        outPacket.write(1); //canNotifyAnnouncedQuest
        outPacket.writeShort(0);  //stackEventGauge >= 0 + Star planet

        //过图加载怪怪信息
        if (!load) {
            outPacket.writeInt(360);
            addUnkData(outPacket, chr);
        } else {
            outPacket.writeLong(4);
        }
        //下面是固定的 unk
        outPacket.writeZeroBytes(16);
        outPacket.write(1);
        outPacket.writeInt(-1);
        outPacket.writeLong(0);
        outPacket.writeInt(999999999);
        outPacket.writeInt(999999999);
        outPacket.writeInt(0);
        outPacket.writeZeroBytes(3);
        outPacket.write(1);
        outPacket.writeInt(0);

        return outPacket;
    }

    public static OutPacket userLeaveMap(int charId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.USER_LEAVE_FIELD.getValue());
        outPacket.writeInt(charId);
        return outPacket;
    }

    public static OutPacket initFamiliar(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.FAMILIAR_OPERATION.getValue());
        outPacket.write(7);
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(1);
        outPacket.writeInt(0x9947A4C2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeZeroBytes(21);
        outPacket.writeShort(2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        outPacket.writeInt(4);
        outPacket.writeShort(0);
        outPacket.writeShort(5);
        outPacket.writeInt(0);
        outPacket.writeInt(7);
        outPacket.writeShort(8);
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        outPacket.writeInt(2);
        outPacket.writeInt(3);
        outPacket.writeShort(0);
        outPacket.writeShort(0x0b);
        outPacket.writeInt(0x44f9930f);
        outPacket.write(0x0c);
        outPacket.writeLong(0);
        outPacket.writeInt(2);
        outPacket.writeInt(0x825ad512);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeZeroBytes(21);
        outPacket.writeShort(2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        outPacket.writeShort(0);
        outPacket.write(0);
        outPacket.writeInt(3);
        outPacket.writeInt(0xb529d96e);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeZeroBytes(21);
        outPacket.writeShort(2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        byte[] bytes = new byte[]{0x4, 0x5, 0x6, 0x7, 0x8, 0x9,
                0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x19, 0x1E,
                0x1F, 0x20, 0x21, 0x24, 0x26, 0x27, 0x28, 0x29};
        for (byte b : bytes) {
            outPacket.writeShort(b);
            if (b == 0x20) {
                outPacket.writeInt(0);
            }
            outPacket.writeInt(0);
        }
        outPacket.writeZeroBytes(3);
        return outPacket;
    }

    public static void addUnkData(OutPacket outPacket, MapleCharacter chr) {
        outPacket.writeInt(1);
        outPacket.writeInt(0x9947A4C2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeZeroBytes(21);
        outPacket.writeShort(2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        outPacket.writeInt(4);
        outPacket.writeShort(0);
        outPacket.writeShort(5);
        outPacket.writeInt(0);
        outPacket.writeInt(7);
        outPacket.writeShort(8);
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        outPacket.writeInt(2);
        outPacket.writeInt(3);
        outPacket.writeShort(0);
        outPacket.writeShort(0x0b);
        outPacket.writeInt(0x44f9930f);
        outPacket.write(0x0c);
        outPacket.writeLong(0);
        outPacket.writeInt(2);
        outPacket.writeInt(0x825ad512);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeZeroBytes(21);
        outPacket.writeShort(2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        outPacket.writeShort(0);
        outPacket.write(0);
        outPacket.writeInt(3);
        outPacket.writeInt(0xb529d96e);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeZeroBytes(21);
        outPacket.writeShort(2);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeShort(3);
        outPacket.writeInt(1);
        byte[] bytes = new byte[]{0x4, 0x5, 0x6, 0x7, 0x8, 0x9,
                0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x19, 0x1E,
                0x1F, 0x20, 0x21, 0x24, 0x26, 0x27, 0x28, 0x29};
        for (byte b : bytes) {
            outPacket.writeShort(b);
            if (b == 0x20) {
                outPacket.writeInt(0);
            }
            outPacket.writeInt(0);
        }
        outPacket.writeZeroBytes(7);
    }

    public static OutPacket chatMessage(String content, ChatType type) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHAT_MSG.getValue());
        outPacket.writeShort(type.getVal());
        outPacket.writeMapleAsciiString(content);
        return outPacket;
    }

    public static OutPacket serverMsg(String content, ServerMsgType type) {
        return serverMsg(content, type, null);
    }

    public static OutPacket serverMsgWithItem(String content, ServerMsgType type, Item item) {
        return serverMsg(content, type, item);
    }

    public static OutPacket serverMsg(String content, ServerMsgType type, Item item) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SERVER_MSG.getValue());
        outPacket.write(type.getVal());
        switch (type) {
            case NOTICE:
                break;
            case ALERT:
            case EVENT:
                outPacket.writeMapleAsciiString(content);
                break;
            case SLIDE:
                outPacket.write(1);
                outPacket.writeMapleAsciiString(content);
                break;
            case NOTICE_WITH_OUT_PREFIX:
                outPacket.writeMapleAsciiString(content);
                outPacket.write(0);
                break;
            case PICKUP_ITEM_WORLD:
                outPacket.writeMapleAsciiString(content);
                outPacket.write(item.getItemId());
                PacketHelper.addItemInfo(outPacket, item);
                break;
            case WITH_ITEM:
                outPacket.writeMapleAsciiString(content);
                outPacket.writeInt(item.getItemId());
                outPacket.writeInt(3);
                outPacket.writeInt(3);
                outPacket.write(0x2E);
                PacketHelper.addItemInfo(outPacket, item);
                break;
        }
        return outPacket;
    }

    public static OutPacket debugMsg(String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.DEBUG_MSG.getValue());
        outPacket.writeInt(3); //unk
        outPacket.writeInt(14); //unk
        outPacket.writeInt(14); //unk
        outPacket.writeInt(0); //unk
        outPacket.write(0); //unk
        outPacket.writeMapleAsciiString(content);
        return outPacket;
    }

    public static OutPacket serverNotice(String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SERVER_NOTICE.getValue());
        outPacket.writeMapleAsciiString(content);
        return outPacket;
    }

    public static OutPacket fullscreenMessage(String content) {
        OutPacket outPacket = new OutPacket();
        outPacket.write(0x0c);
        outPacket.writeMapleAsciiString(content);
        outPacket.write(1);
        return outPacket;
    }

    public static OutPacket dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom) {
        return dropEnterField(drop, dropEnterType, posFrom, posFrom);
    }

    public static OutPacket dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom, Position posTo) {
        return dropEnterField(drop, dropEnterType, posFrom, posTo, 0, false);
    }

    public static OutPacket dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom, Position posTo, int delay, boolean canBePickByPet) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.DROP_ENTER_FIELD.getValue());
        outPacket.writeBool(false);
        outPacket.write(dropEnterType.getVal());
        outPacket.writeInt(drop.getObjectId());
        outPacket.writeBool(drop.isMoney());
        outPacket.writeInt(0); //getDropMotionType
        outPacket.writeInt(0); //dropSpeed
        outPacket.writeInt(0);  //rand?
        outPacket.writeInt(!drop.isMoney() ? drop.getItem().getItemId() : drop.getMoney());
        outPacket.writeInt(drop.getOwnerID());
        outPacket.write(2); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
        outPacket.writePosition(posTo);
        outPacket.writeInt(0); // drop from id
        outPacket.writeZeroBytes(42);
        if (dropEnterType != Instant) {
            outPacket.writePosition(posFrom);
            outPacket.writeInt(delay); //delay
        }
        outPacket.write(0); //unk
        if (!drop.isMoney()) {
            outPacket.writeLong(drop.getExpireTime());
        }
        outPacket.writeBool(drop.isCanBePickedUpByPet() && canBePickByPet);
        outPacket.writeZeroBytes(14); //unk
        return outPacket;
    }

    public static OutPacket dropLeaveField(DropLeaveType type, int charId, int dropId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.PICK_UP_DROP.getValue());
        outPacket.write(type.getVal());
        outPacket.writeInt(dropId);
        outPacket.writeInt(charId);
        return outPacket;
    }

    public static OutPacket incExpMessage(ExpIncreaseInfo expIncreaseInfo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(INC_EXP_MESSAGE.getVal());
        expIncreaseInfo.encode(outPacket);
        outPacket.writeInt(0);
        return outPacket;
    }

    public static OutPacket dropPickupMessage(int money, short internetCafeExtra, short smallChangeExtra) {
        return dropPickupMessage(money, (byte) 1, false, internetCafeExtra, smallChangeExtra, (short) 0);
    }

    public static OutPacket dropPickupMessage(Item item, boolean bag, short quantity) {
        return dropPickupMessage(item.getItemId(), (byte) 0, bag, (short) 0, (short) 0, quantity);
    }

    public static OutPacket dropPickupMessage(int i, byte type, boolean bag,
                                              short internetCafeExtra,
                                              short smallChangeExtra,
                                              short quantity) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(DROP_PICKUP_MESSAGE.getVal());
        outPacket.writeInt(0);
        outPacket.write(0);
        if (internetCafeExtra > 0) type = 8;
        outPacket.write(type);
        // also error (?) codes -2, ,-3, -4, -5, <default>
        switch (type) {
            case -10:
                outPacket.writeInt(100);// nItemID
                break;
            case 0: // item
                outPacket.writeInt(i);
                outPacket.writeInt(quantity); // ?
                outPacket.write(0);
                break;
            case 1: // Mesos
                outPacket.writeBool(false); // boolean: portion was lost after falling to the ground
                outPacket.writeInt(i); // Mesos
                outPacket.writeShort(smallChangeExtra); // Spotting small change
                break;
            case 2: // ?
                outPacket.writeInt(100);// nItemID
                outPacket.writeLong(0);
                break;
            case 8:
                outPacket.writeInt(i); // Mesos
                outPacket.writeShort(internetCafeExtra); // Internet cafe
                break;
        }
        return outPacket;
    }

    public static OutPacket resultInstanceTable(String requestStr, int type, int subType, int value) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.RESULT_INSTANCE_TABLE.getValue());
        outPacket.writeMapleAsciiString(requestStr);
        outPacket.writeInt(type);
        outPacket.writeInt(subType);
        outPacket.writeBool(type < 41);
        outPacket.writeInt(value);
        return outPacket;
    }

    public static OutPacket unityPortal() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.OPEN_UNITY_PORTAL.getValue());
        outPacket.writeInt(DimensionalMirror.values().length);
        for (DimensionalMirror unityPortal : DimensionalMirror.values()) {

            outPacket.writeMapleAsciiString(unityPortal.getName());
            outPacket.writeMapleAsciiString(unityPortal.getDesc());
            outPacket.writeInt(unityPortal.getReqLevel());
            outPacket.writeInt(0); //type?
            outPacket.writeInt(unityPortal.getId());
            outPacket.writeInt(unityPortal.getReqQuest()); // 00 00 00 00
            outPacket.writeInt(unityPortal.getQuestToSave()); //00 00 00 00
            outPacket.writeInt(unityPortal.getQuestToSave()); //00 00 00 00
            outPacket.writeMapleAsciiString("");
            outPacket.writeBool(unityPortal.isSquad()); //00
            outPacket.writeInt(unityPortal.getRewards().length);
            for (Integer reward : unityPortal.getRewards()) {
                outPacket.writeInt(reward);
            }
        }
        return outPacket;
    }

    public static OutPacket quickMove(boolean town) {
        OutPacket outPacket = new OutPacket();
        List<QuickMoveInfo> quickMoveInfos = GameConstants.getQuickMoveInfos();
        outPacket.writeShort(SendOpcode.QUICK_MOVE.getValue());
        if (town) {
            outPacket.write(quickMoveInfos.size());
            for (int i = 0; i < quickMoveInfos.size(); i++) {
                outPacket.writeInt(i);
                outPacket.writeInt(quickMoveInfos.get(i).getTemplateID());
                outPacket.writeInt(quickMoveInfos.get(i).getCode().getVal());
                outPacket.writeInt(quickMoveInfos.get(i).getLevelMin());
                outPacket.writeMapleAsciiString(quickMoveInfos.get(i).getMsg());
                outPacket.writeLong(quickMoveInfos.get(i).getStart());
                outPacket.writeLong(quickMoveInfos.get(i).getEnd());
            }
        } else {
            outPacket.writeBool(false);
        }
        return outPacket;
    }

    /*
        仓库操作 开始
     */

    public static OutPacket openTrunk(int npcId, Account account) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        outPacket.write(TrunkOpType.TrunkRes_OpenTrunkDlg.getVal());
        outPacket.writeInt(npcId);
        Trunk trunk = account.getTrunk();
        trunk.encode(outPacket, 0x7E);
        outPacket.writeInt(0);
        return outPacket;
    }

    public static OutPacket trunkMsg(TrunkOpType trunkOpType) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        outPacket.write(trunkOpType.getVal());
        return outPacket;
    }

    public static OutPacket getMoneyFromTrunk(long mount, Trunk trunk) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        outPacket.write(TrunkOpType.TrunkRes_MoneySuccess.getVal());
        outPacket.write(trunk.getSlotCount());
        outPacket.writeLong(0x02);
        outPacket.writeLong(trunk.getMoney());
        return outPacket;
    }

    public static OutPacket getItemFromTrunk(Trunk trunk, InventoryType type) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        outPacket.write(TrunkOpType.TrunkRes_GetSuccess.getVal());
        trunk.encode(outPacket, type.getBitfieldEncoding());
        return outPacket;
    }

    public static OutPacket putItemToTrunk(Trunk trunk, InventoryType inventoryType) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        outPacket.write(TrunkOpType.TrunkRes_PutSuccess.getVal());
        trunk.encode(outPacket, inventoryType.getBitfieldEncoding());
        return outPacket;
    }

    public static OutPacket sortedTrunkItems(Trunk trunk) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        outPacket.write(TrunkOpType.TrunkRes_SortItem.getVal());
        trunk.encode(outPacket, 0x7C);
        outPacket.writeInt(0);
        return outPacket;
    }

        /*
        仓库操作 结束
     */


    public static OutPacket eventMessage(String msg, int type, int duration) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.EVENT_MESSAGE.getValue());
        outPacket.writeMapleAsciiString(msg);
        outPacket.writeInt(type); //type
        outPacket.writeInt(duration); //duration
        outPacket.write(1); //unk  !=
        return outPacket;
    }

    public static OutPacket userEnterMap(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        outPacket.writeShort(SendOpcode.USER_ENTER_FIELD.getValue());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getLevel());
        outPacket.writeMapleAsciiString(chr.getName());
        outPacket.writeZeroBytes(22);   //todo
        outPacket.write(chr.getGender());
        outPacket.writeZeroBytes(17); //todo
        Map<CharacterTemporaryStat, List<Option>> spawnBuffs = CharacterTemporaryStat.getSpawnBuffs();
        spawnBuffs.putAll(tsm.getCurrentStats());
        tsm.encodeForRemote(outPacket, spawnBuffs);
        outPacket.writeInt(chr.getJobId()); //short jobId + short subJobId
        outPacket.writeInt(chr.getTotalChuc());
        outPacket.writeInt(0);
        PacketHelper.addCharLook(outPacket, chr, false, false);

        outPacket.writeInt(0); // int or short
        outPacket.write(0xFF);
        outPacket.writeInt(0);
        outPacket.write(0xFF);
        outPacket.writeInt(0);

        outPacket.writeZeroBytes(70);
        for (int i = 0; i < 6; i++) {
            outPacket.write(-1); // unk
        }
        outPacket.writeZeroBytes(14);
        outPacket.writePosition(chr.getPosition());
        outPacket.write(chr.getMoveAction());
        outPacket.writeShort(chr.getFoothold());
        outPacket.writeZeroBytes(3); //unk
        outPacket.write(1);
        outPacket.writeZeroBytes(28);
        for (int i = 0; i < 5; i++) {
            outPacket.write(-1);
        }
        outPacket.writeInt(0);
        outPacket.write(0);
        outPacket.writeZeroBytes(20);
        outPacket.write(1);
        outPacket.write(1);
        outPacket.write(0);
        outPacket.write(0);
        outPacket.writeInt(1051291);
        outPacket.writeZeroBytes(31);
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

    public static OutPacket startBattleAnalysis() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.BATTLE_ANALYSIS.getValue());
        outPacket.write(1);
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

    public static OutPacket partyResult(PartyResult partyResult) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.PARTY_RESULT.getValue());
        partyResult.encode(outPacket);
        return outPacket;
    }

    public static OutPacket elfTip(int elf, int duration, String msg) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ELF_TIP.getValue());
        outPacket.writeInt(elf);
        outPacket.writeInt(duration);
        outPacket.writeMapleAsciiString(msg);
        outPacket.write(0);
        outPacket.write(0);
        outPacket.write(0);
        return outPacket;
    }


    public static OutPacket queryCashPointResult(Account account) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CASH_POINT_RESULT.getValue());
        outPacket.writeInt(account.getPoint());
        outPacket.writeInt(account.getVoucher());
        return outPacket;
    }
}
