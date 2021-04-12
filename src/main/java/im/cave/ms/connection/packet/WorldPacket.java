package im.cave.ms.connection.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.ForceAtom;
import im.cave.ms.client.HotTimeReward;
import im.cave.ms.client.character.ExpIncreaseInfo;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.skill.ForceAtomInfo;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.QuickMoveInfo;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.client.field.obstacleatom.ObstacleAtomInfo;
import im.cave.ms.client.field.obstacleatom.ObstacleInRowInfo;
import im.cave.ms.client.field.obstacleatom.ObstacleRadianInfo;
import im.cave.ms.client.multiplayer.Express;
import im.cave.ms.client.multiplayer.friend.Friend;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.party.PartyResult;
import im.cave.ms.client.storage.Trunk;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.connection.packet.result.ExpressResult;
import im.cave.ms.connection.packet.result.GuildResult;
import im.cave.ms.connection.packet.result.HotTimeRewardResult;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.*;
import im.cave.ms.tools.*;

import java.util.*;

import static im.cave.ms.constants.ServerConstants.NEXON_IP;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;
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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_MAP.getValue());
        out.writeShort(1);
        out.writeLong(1);
        out.writeInt(chr.getClient().getChannelId());
        out.write(0);
        out.writeInt(0);
        out.write(0);
        out.writeShort(Math.min(chr.getVisitedMapCount(), 255));
        out.writeInt(!load ? to.getFieldType().getVal() : 0);
        out.writeInt(chr.getMap().getWidth());
        out.writeInt(chr.getMap().getHeight());
        out.writeBool(load);
        out.writeShort(0);
        if (load) {
            for (int i = 0; i < 3; i++) {
                out.writeInt(Randomizer.nextInt());
            }
            PacketHelper.addCharInfo(out, chr);
            out.write(1);
            out.write(0);
            out.writeLong(ZERO_TIME);
            out.write(0);
            out.writeLong(ZERO_TIME);
            out.writeZeroBytes(16);
        } else {
            out.write(0); // usingBuffProtector
            out.writeInt(to.getId()); //地图ID
            out.write(spawnPoint);
            out.writeInt(chr.getStats().getHp()); // 角色HP
            out.writeZeroBytes(4);
        }
        out.write(0);
        out.write(0);
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.writeInt(100); //mobStatAdjustRate
        out.writeShort(0); //hasFieldCustom + is pvp map
        out.write(1); //canNotifyAnnouncedQuest
        out.writeShort(0);  //stackEventGauge >= 0 + Star planet

        //过图加载怪怪信息
        if (!load) {
            out.writeInt(360);
            addUnkData(out, chr); //怪怪
        } else {
            out.writeLong(4);
        }
        //下面是固定的 unk
        out.writeZeroBytes(14);
        out.write(1);
        out.writeInt(-1);
        out.writeLong(0);
        out.writeInt(999999999);
        out.writeInt(999999999);
        out.writeInt(0);
        out.writeZeroBytes(3);
        out.write(1);
        out.writeInt(0);

        return out;
    }

    public static OutPacket userLeaveMap(int charId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.USER_LEAVE_FIELD.getValue());
        out.writeInt(charId);
        return out;
    }


    public static void addUnkData(OutPacket out, MapleCharacter chr) {
        out.writeInt(1);
        out.writeInt(0x9947A4C2);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.write(1);
        out.write(1);
        out.writeZeroBytes(21);
        out.writeShort(2);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.writeShort(3);
        out.writeInt(1);
        out.writeInt(4);
        out.writeShort(0);
        out.writeShort(5);
        out.writeInt(0);
        out.writeInt(7);
        out.writeShort(8);
        out.writeShort(3);
        out.writeInt(1);
        out.writeInt(2);
        out.writeInt(3);
        out.writeShort(0);
        out.writeShort(0x0b);
        out.writeInt(0x44f9930f);
        out.write(0x0c);
        out.writeLong(0);
        out.writeInt(2);
        out.writeInt(0x825ad512);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.write(1);
        out.write(1);
        out.writeZeroBytes(21);
        out.writeShort(2);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.writeShort(3);
        out.writeInt(1);
        out.writeShort(0);
        out.write(0);
        out.writeInt(3);
        out.writeInt(0xb529d96e);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.write(1);
        out.write(1);
        out.writeZeroBytes(21);
        out.writeShort(2);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.writeShort(3);
        out.writeInt(1);
        byte[] bytes = new byte[]{0x4, 0x5, 0x6, 0x7, 0x8, 0x9,
                0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x19, 0x1E,
                0x1F, 0x20, 0x21, 0x24, 0x26, 0x27, 0x28, 0x29};
        for (byte b : bytes) {
            out.writeShort(b);
            if (b == 0x20) {
                out.writeInt(0);
            }
            out.writeInt(0);
        }
        out.writeZeroBytes(7);
    }

    public static OutPacket chatMessage(String content, ChatType type) {
        OutPacket out = new OutPacket(SendOpcode.CHAT_MSG);

        out.writeShort(type.getVal());
        out.writeMapleAsciiString(content);

        return out;
    }

    public static OutPacket progressMessageFont(int fontNameType, int fontSize, int fontColorType, int fadeOutDelay, String message) {
        OutPacket out = new OutPacket(SendOpcode.PROGRESS_MESSAGE_FONT);

        out.writeInt(fontNameType);
        out.writeInt(fontSize);
        out.writeInt(fontColorType);
        out.writeInt(fadeOutDelay);
        out.write(0); //unk
        out.writeMapleAsciiString(message);

        return out;
    }

    public static OutPacket scriptProgressMessage(String content) {
        OutPacket out = new OutPacket(SendOpcode.SCRIPT_PROGRESS_MESSAGE);

        out.writeMapleAsciiString(content);

        return out;
    }

    //todo Opcode忘记了
    public static OutPacket fullscreenMessage(String content) {
        OutPacket out = new OutPacket();
        out.write(0x0c);
        out.writeMapleAsciiString(content);
        out.write(1);
        return out;
    }

    public static OutPacket dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom) {
        return dropEnterField(drop, dropEnterType, posFrom, posFrom);
    }

    public static OutPacket dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom, Position posTo) {
        return dropEnterField(drop, dropEnterType, posFrom, posTo, 0, false);
    }

    public static OutPacket dropEnterField(Drop drop, DropEnterType dropEnterType,
                                           Position posFrom, Position posTo, int delay, boolean canBePickByPet) {
        OutPacket out = new OutPacket(SendOpcode.DROP_ENTER_FIELD);

        out.writeBool(false);
        out.write(dropEnterType.getVal());
        out.writeInt(drop.getObjectId());
        out.writeBool(drop.isMoney());
        out.writeInt(0); //getDropMotionType
        out.writeInt(0); //dropSpeed
        out.writeInt(0);  //rand?
        out.writeInt(!drop.isMoney() ? drop.getItem().getItemId() : drop.getMoney());
        out.writeInt(drop.getOwnerId());
        out.write(drop.getTimeoutStrategy()); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
        out.writePosition(posTo);
        out.writeInt(0); // drop from id
        out.writeZeroBytes(42);
        if (dropEnterType != Instant) {
            out.writePosition(posFrom);
            out.writeInt(delay); //delay
        }
        out.write(0); //unk
        if (!drop.isMoney()) {
            out.writeLong(drop.getExpireTime());
        }
        out.writeBool(drop.isCanBePickedUpByPet() && canBePickByPet);
        out.writeZeroBytes(14); //unk

        return out;
    }


    public static OutPacket dropLeaveField(int charId, int dropId) {
        return dropLeaveField(DropLeaveType.CharPickup1, charId, dropId, (short) 0, 0, 0);
    }

    public static OutPacket dropLeaveField(DropLeaveType type, int charId, int dropId) {
        return dropLeaveField(type, charId, dropId, (short) 0, 0, 0);
    }

    public static OutPacket dropLeaveField(DropLeaveType type, int charId, int dropId, short delay, int petId, int key) {
        OutPacket out = new OutPacket(SendOpcode.DROP_LEAVE_FIELD);
        out.write(type.getVal());
        out.writeInt(dropId);
        switch (type) {
            case CharPickup1:
            case CharPickup2:
                out.writeInt(charId);
                break;
            case PetPickup:
                out.writeInt(charId);
                out.writeInt(petId);
                break;
            case DelayedPickup:
                out.writeShort(delay);
                break;
            case Absorb:
                out.writeInt(key);
                break;
        }
        return out;
    }


    public static OutPacket incExpMessage(ExpIncreaseInfo expIncreaseInfo) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MESSAGE.getValue());
        out.write(INC_EXP_MESSAGE.getVal());
        expIncreaseInfo.encode(out);
        out.writeInt(0);
        return out;
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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MESSAGE.getValue());
        out.write(DROP_PICKUP_MESSAGE.getVal());
        out.writeInt(0);
        out.write(0);
        if (internetCafeExtra > 0) type = 8;
        out.write(type);
        // also error (?) codes -2, ,-3, -4, -5, <default>
        switch (type) {
            case -10:
                out.writeInt(100);// nItemID
                break;
            case 0: // item
                out.writeInt(i);
                out.writeInt(quantity); // ?
                out.write(0);
                break;
            case 1: // Mesos
                out.writeBool(false); // boolean: portion was lost after falling to the ground
                out.writeInt(i); // Mesos
                out.writeShort(smallChangeExtra); // Spotting small change
                break;
            case 2: // ?
                out.writeInt(100);// nItemID
                out.writeLong(0);
                break;
            case 8:
                out.writeInt(i); // Mesos
                out.writeShort(internetCafeExtra); // Internet cafe
                break;
        }
        return out;
    }

    public static OutPacket resultInstanceTable(String requestStr, int type, int subType, int value) {
        OutPacket out = new OutPacket(SendOpcode.RESULT_INSTANCE_TABLE);
        out.writeMapleAsciiString(requestStr);
        out.writeInt(type);
        out.writeInt(subType);
        out.writeBool(type < 41);
        out.writeInt(value);
        return out;
    }

    //todo 未完成
    public static OutPacket dojoRankResult(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.DOJO_RANK_RESULT);
        out.write(0);
        out.writeInt(chr.getJob());
        return out;
    }


    public static OutPacket unityPortal() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.OPEN_UNITY_PORTAL.getValue());
        out.writeInt(DimensionalMirror.values().length);
        for (DimensionalMirror unityPortal : DimensionalMirror.values()) {

            out.writeMapleAsciiString(unityPortal.getName());
            out.writeMapleAsciiString(unityPortal.getDesc());
            out.writeInt(unityPortal.getReqLevel());
            out.writeInt(0); //type?
            out.writeInt(unityPortal.getId());
            out.writeInt(unityPortal.getReqQuest()); // 00 00 00 00
            out.writeInt(unityPortal.getQuestToSave()); //00 00 00 00
            out.writeInt(unityPortal.getQuestToSave()); //00 00 00 00
            out.writeMapleAsciiString("");
            out.writeBool(unityPortal.isSquad()); //00
            out.writeInt(unityPortal.getRewards().length);
            for (Integer reward : unityPortal.getRewards()) {
                out.writeInt(reward);
            }
        }
        return out;
    }

    public static OutPacket quickMove(boolean town) {
        OutPacket out = new OutPacket();
        List<QuickMoveInfo> quickMoveInfos = GameConstants.getQuickMoveInfos();
        out.writeShort(SendOpcode.QUICK_MOVE.getValue());
        if (town) {
            out.write(quickMoveInfos.size());
            for (int i = 0; i < quickMoveInfos.size(); i++) {
                out.writeInt(i);
                out.writeInt(quickMoveInfos.get(i).getTemplateID());
                out.writeInt(quickMoveInfos.get(i).getCode().getVal());
                out.writeInt(quickMoveInfos.get(i).getLevelMin());
                out.writeMapleAsciiString(quickMoveInfos.get(i).getMsg());
                out.writeLong(quickMoveInfos.get(i).getStart());
                out.writeLong(quickMoveInfos.get(i).getEnd());
            }
        } else {
            out.writeBool(false);
        }
        return out;
    }

    /*
        仓库操作 开始
     */

    public static OutPacket openTrunk(int npcId, Account account) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        out.write(TrunkOpType.TrunkRes_OpenTrunkDlg.getVal());
        out.writeInt(npcId);
        Trunk trunk = account.getTrunk();
        trunk.encode(out, Trunk.Mask.ALL);
        out.writeInt(0);
        return out;
    }

    public static OutPacket trunkMsg(TrunkOpType trunkOpType) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        out.write(trunkOpType.getVal());
        return out;
    }

    public static OutPacket getMoneyFromTrunk(long mount, Trunk trunk) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        out.write(TrunkOpType.TrunkRes_MoneySuccess.getVal());
        out.write(trunk.getSlots());
        out.writeLong(0x02);
        out.writeLong(trunk.getMeso());
        return out;
    }

    public static OutPacket getItemFromTrunk(Trunk trunk, InventoryType type) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        out.write(TrunkOpType.TrunkRes_GetSuccess.getVal());
        trunk.encode(out, type);
        return out;
    }

    public static OutPacket putItemToTrunk(Trunk trunk, InventoryType inventoryType) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        out.write(TrunkOpType.TrunkRes_PutSuccess.getVal());
        trunk.encode(out, inventoryType);
        return out;
    }

    public static OutPacket sortedTrunkItems(Trunk trunk) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.TRUNK_OPERATION.getValue());
        out.write(TrunkOpType.TrunkRes_SortItem.getVal());
        trunk.encode(out, Trunk.Mask.ITEM);
        out.writeInt(0);
        return out;
    }

        /*
        仓库操作 结束
     */


    public static OutPacket weatherEffectNotice(WeatherEffNoticeType type, String text, int duration) {
        OutPacket out = new OutPacket(SendOpcode.WEATHER_EFFECT_NOTICE);

        out.writeMapleAsciiString(text);
        out.writeInt(type.getVal()); //type
        out.writeInt(duration); //duration
        out.write(1); //Forced Notice

        return out;
    }

    public static OutPacket userEnterMap(MapleCharacter chr) {
        OutPacket out = new OutPacket();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        out.writeShort(SendOpcode.USER_ENTER_FIELD.getValue());
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.writeInt(chr.getId());
        out.writeInt(chr.getLevel());
        out.writeMapleAsciiString(chr.getName());
        out.writeMapleAsciiString("");
        if (chr.getGuild() != null) {
            chr.getGuild().encodeForRemote(out);
        } else {
            Guild.defaultEncodeForRemote(out);
        }
        out.writeZeroBytes(8);   //可能和家族相关
        out.write(chr.getGender());
        out.writeInt(chr.getFame());
        out.writeZeroBytes(13); //todo
        Map<CharacterTemporaryStat, List<Option>> spawnBuffs = CharacterTemporaryStat.getSpawnBuffs();
        spawnBuffs.putAll(tsm.getCurrentStats());
        tsm.encodeForRemote(out, spawnBuffs);
        out.writeShort(chr.getJob());
        out.writeShort(chr.getSubJob());
        out.writeInt(chr.getTotalChuc()); //星之力
        out.writeInt(0);
        chr.getCharLook().encode(out);
        out.writeInt(0); // int or short
        out.write(0xFF);
        out.writeInt(0);
        out.write(0xFF);
        out.writeZeroBytes(32);
//        out.writeInt(chr.getActiveEffectItemID());
//        out.writeInt(chr.getMonkeyEffectItemID());
        out.writeInt(chr.getActiveNickItemId());
        out.write(0);
        out.writeInt(chr.getDamageSkin().getDamageSkinID());
        out.writeZeroBytes(33);
        for (int i = 0; i < 6; i++) {
            out.write(-1); // unk
        }
        out.writeZeroBytes(14); //椅子
        //charId int
        //0 int
        //10 byte
        //01 byte
        // CE 9D E1 00 int
        // C7 44 97 00
        out.writePosition(chr.getPosition());
        out.write(chr.getMoveAction());
        out.writeShort(chr.getFoothold());
        boolean hasPortableChairMsg = chr.getPortableChairMsg() != null;
        out.writeBool(hasPortableChairMsg); //hasPortableChairMsg
        if (hasPortableChairMsg) {
            out.writeMapleAsciiString(chr.getPortableChairMsg());
            out.writeMapleAsciiString(chr.getName());
            out.writeMapleAsciiString(chr.getPortableChairMsg());
        }
        /*
            string:msg
            string:name
            string:msg
         */
        for (Pet pet : chr.getPets()) {
            if (pet.getId() == 0) {
                continue;
            }
            out.write(1);
            out.writeInt(pet.getIdx());
            pet.encode(out);
        }

        out.write(0);
        out.write(0);
        out.write(1);
        out.writeZeroBytes(28);
        for (int i = 0; i < 5; i++) {
            out.write(-1);
        }
        out.writeInt(0);
        out.write(0);
        out.writeZeroBytes(20);
        out.write(1);
        out.write(1);
        out.write(0);
        out.write(0);
        out.writeInt(1051291);
        out.writeZeroBytes(29);
        out.writeMapleAsciiString(chr.getWorld() + "-" + StringUtil.getLeftPaddedStr(String.valueOf(chr.getId()), '0', 6));
        out.writeInt(0); // 如果是5 则有怪怪  应该是MASK

        return out;
    }

    public static OutPacket getChannelChange(boolean success, int port) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHANGE_CHANNEL.getValue());
        out.writeBool(success);
        if (success) {
            out.write(NEXON_IP);
            out.writeShort(port);
            out.write(0);
        }
        return out;
    }

    public static OutPacket startBattleAnalysis() {
        OutPacket out = new OutPacket(SendOpcode.BATTLE_ANALYSIS);

        out.write(1);

        return out;
    }

    public static OutPacket fieldEffect(FieldEffect effect) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.FIELD_EFFECT.getValue());
        effect.encode(out);
        return out;
    }

    public static OutPacket fieldMessage(int itemId, String message, int duration) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.FIELD_MESSAGE.getValue());
        out.write(0);
        out.writeInt(itemId);
        if (itemId != 0) {
            out.writeMapleAsciiString(message);
            out.writeInt(duration);
            out.write(0); // bool
        }
        return out;
    }

    //1530619  illustration2
    //1530060
    public static OutPacket illustrationMsg(int npcId, int duration, String msg) {
        OutPacket out = new OutPacket(SendOpcode.ILLUSTRATION_MSG);
        out.writeInt(npcId);
        out.writeInt(duration);
        out.writeMapleAsciiString(msg);
        out.write(0);
        out.write(0);
        out.write(0);
        return out;
    }

    public static OutPacket partyResult(PartyResult partyResult) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.PARTY_RESULT.getValue());
        partyResult.encode(out);
        return out;
    }

    //组队推荐玩家
    public static OutPacket partyMemberCandidateResult(List<MapleCharacter> players) {
        OutPacket out = new OutPacket(SendOpcode.PARTY_MEMBER_CANDIDATE_RESULT);
        out.write(players.size());
        for (MapleCharacter player : players) {
            out.writeInt(player.getId());
            out.writeMapleAsciiString(player.getName());
            out.writeInt(player.getJob());
            out.writeInt(player.getLevel());
        }
        return out;
    }


    public static OutPacket queryCashPointResult(Account account) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CASH_POINT_RESULT.getValue());
        out.writeInt(account.getMaplePoint());
        out.writeInt(account.getPoint());
        return out;
    }

    public static OutPacket openUI(UIType uiType) {
        return openUI(uiType.getVal());
    }


    public static OutPacket openUI(int uiId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.OPEN_UI.getValue());
        out.writeInt(uiId);
        return out;
    }

    public static OutPacket openUIWithOption(UIType uiType, int option) {
        return openUIWithOption(uiType.getVal(), option);
    }

    // 00 00 00 00 10 00 00 00 背包 - 装备栏
    public static OutPacket openUIWithOption(int uiId, int option) {
        OutPacket out = new OutPacket(SendOpcode.OPEN_UI_WITH_OPTION);
        out.writeInt(uiId);
        out.writeInt(option);
        out.writeInt(0); //minigame options
        return out;
    }

    public static OutPacket mapTransferResult(MapTransferType mapTransferType, byte itemType, int[] hyperRockFields) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MAP_TRANSFER_RESULT.getValue());
        out.write(mapTransferType.getVal());
        out.write(itemType);
        if (mapTransferType == MapTransferType.DeleteListSend || mapTransferType == MapTransferType.RegisterListSend) {
            for (int mapId : hyperRockFields) {
                out.writeInt(mapId); // Target Field ID
            }
        }
        return out;
    }

    public static OutPacket friendResult(FriendType type, List<Friend> friends) {
        return friendResult(type, null, friends);
    }


    public static OutPacket friendResult(FriendType type, MapleCharacter chr, List<Friend> friends) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.FRIEND_RESULT.getValue());
        out.write(type.getVal());
        switch (type) {
            case FriendRes_SetFriend_UnknownUser:
                break;
            case FriendRes_SendSingleFriendInfo:
                friends.get(0).encode(out);
                break;
            case FriendRes_SetFriend_Done:
                out.writeMapleAsciiString(friends.get(0).getName());
                break;
            case FriendRes_Invite: {
                Friend friend = friends.get(0);
                out.writeBool(friend.getFriendAccountId() != 0);
                out.writeInt(friend.getFriendId());
                out.writeInt(friend.getFriendAccountId());
                out.writeMapleAsciiString(friend.getName());
                out.writeInt(friend.getChar().getLevel());
                out.writeInt(friend.getChar().getJob());
                out.writeInt(0);
                friend.encode(out);
                out.write(0);
                break;
            }
            case FriendRes_DeleteFriend_Done:
                out.write(0); //是否为账号好友?
                out.writeInt(friends.get(0).getId());
                break;
            case FriendRes_LoadFriend_Done: {
                out.writeInt(friends.size());
                for (Friend friend : friends) {
                    friend.encode(out);
                }
                out.write(0);
                break;
            }
            case FriendRes_IncMaxCount_Done: {
                out.write(chr.getBuddyCapacity());
            }
            case FriendRes_Notify: {//todo
                out.writeInt(chr.getId()); //friendId
                out.writeInt(chr.getAccId()); //accId
                out.write(0); //
                out.writeInt(chr.getChannel()); //20商城 -1离线
                out.write(1); //是否是账号好友
                out.write(1);
                out.writeMapleAsciiString(chr.getName()); //账号好友才有
                break;
            }
            case FriendRes_NotifyChange_FriendInfo: {
                Friend friend = friends.get(0);
                out.writeInt(friend.getFriendId());
                out.writeInt(friend.getFriendAccountId());
                friend.encode(out);
                break;
            }
        }
        return out;
    }

    public static OutPacket receiveHP(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.REMOTE_RECEIVE_HP);

        out.writeInt(chr.getId());
        out.writeInt(chr.getHp());
        out.writeInt(chr.getMaxHP());

        return out;
    }

    public static OutPacket expressResult(ExpressResult result) {
        OutPacket out = new OutPacket(SendOpcode.EXPRESS);
        out.write(result.getAction().getVal());
        switch (result.getAction()) {
            case Res_New_Msg:
                out.writeMapleAsciiString(result.getFromName());
                out.write(result.getArg1());
                break;
            case Res_Open_Dialog:
                out.write(result.getArg1());
                break;
            case Res_Init_Locker:
                MapleCharacter chr = result.getChr();
                out.write(0);
                out.write(chr.getExpresses().size());
                for (Express express : chr.getExpresses()) {
                    express.encode(out);
                }
                out.write(0);
                break;
            case Res_Remove_Done:
                out.writeInt(result.getArg1());
                out.write(result.getArg2());
                break;
        }
        return out;
    }

    public static OutPacket checkTrickOrTreatResult(boolean b) {
        OutPacket out = new OutPacket(SendOpcode.CHECK_TRICK_OR_TREAT_RESULT);
        out.writeBool(b);
        return out;
    }

    public static OutPacket hotTimeRewardResult(HotTimeRewardResult result) {
        OutPacket out = new OutPacket(SendOpcode.HOTTIME_REWARD_RESULT);
        out.write(result.getType().getVal());
        switch (result.getType()) {
            case LIST:
                List<HotTimeReward> rewards = result.getRewards();
                out.writeInt(rewards.size());
                for (HotTimeReward reward : rewards) {
                    reward.encode(out);
                }
                break;
            case GET_MAPLE_POINT:
                HotTimeReward reward = result.getReward();
                out.writeInt(reward.getSort());
                out.writeInt(reward.getMaplePoint());
                out.writeInt(0);
                break;
            case GET_ITEM:
                reward = result.getReward();
                out.writeInt(reward.getSort());
                out.writeInt(0);
                break;
        }
        return out;
    }

    public static OutPacket npcDisableInfo(int[] npcs) {
        OutPacket out = new OutPacket(SendOpcode.LIMITED_NPC_DISABLE_INFO);
        out.write(npcs.length);
        for (int npc : npcs) {
            out.writeInt(npc);
        }
        return out;
    }

    public static OutPacket guildSearchResult(byte searchType, boolean exact, String searchTerm, Collection<Guild> guilds) {
        OutPacket out = new OutPacket(SendOpcode.GUILD_SEARCH_RESULT);

        out.writeShort(searchType);
        out.writeMapleAsciiString(searchTerm);
        out.writeShort(exact ? 1 : 0);
        out.writeShort(1);
        out.writeInt(0);

        out.writeInt(guilds.size());
        for (Guild g : guilds) {
            out.writeInt(g.getId());
            out.write(g.getLevel());
            out.writeMapleAsciiString(g.getName());
            out.writeMapleAsciiString(g.getGuildLeader().getName());
            out.writeShort(g.getMembers().size());
            out.writeInt(g.getAverageMemberLevel());
            out.writeZeroBytes(7);
            out.write(1);
            out.writeZeroBytes(15);
        }
        return out;
    }

    public static OutPacket guildResult(GuildResult gri) {
        OutPacket out = new OutPacket(SendOpcode.GUILD_RESULT);

        gri.encode(out);

        return out;
    }

    public static OutPacket guildContentResult(List<Guild> ggpWeaklyRank, List<Guild> captureTheFlagGameRank, List<Guild> undergroundWaterwayRank) {
        OutPacket out = new OutPacket(SendOpcode.GUILD_CONTENT_RESULT);

        out.writeBool(true);
        out.writeInt(ggpWeaklyRank.size());
        for (Guild guild : ggpWeaklyRank) {
            out.writeInt(guild.getId());
            out.writeInt(guild.getGgp());
            out.writeLong(DateUtil.getFileTime(System.currentTimeMillis())); //可能是GGP最后更新时间
            out.writeMapleAsciiString(guild.getName());
        }

        out.writeInt(captureTheFlagGameRank.size());
        for (Guild guild : captureTheFlagGameRank) {
            out.writeInt(guild.getId());
            out.writeInt(guild.getGgp()); //todo 这里应该变一下
            out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
            out.writeMapleAsciiString(guild.getName());
        }

        out.writeInt(undergroundWaterwayRank.size());
        for (Guild guild : undergroundWaterwayRank) {
            out.writeInt(guild.getId());
            out.writeInt(guild.getGgp()); //todo 这里应该变一下
            out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
            out.writeMapleAsciiString(guild.getName());
        }

        return out;
    }

    //todo move to CField
    public static OutPacket blowWeather(int itemId, String message) {
        return null;
    }

    public static OutPacket hourChange() {
        OutPacket out = new OutPacket(SendOpcode.HOUR_CHANGE);

        out.writeShort(5); //unk
        out.writeShort(DateUtil.now().getHour());

        return out;
    }

    public static OutPacket miniMapOnOff(boolean opt) {
        OutPacket out = new OutPacket(SendOpcode.MINIMAP_ON_OFF);

        out.writeBool(opt);

        return out;

    }

    public static OutPacket createForceAtom(boolean byMob, int userOwner, int targetID, int forceAtomType,
                                            boolean toMob, int targets, int skillID,
                                            ForceAtomInfo fai, Rect rect, int arriveDir, int arriveRange,
                                            Position forcedTargetPos, int bulletID, Position pos) {

        return createForceAtom(byMob, userOwner, targetID, ForceAtomEnum.getByVal(forceAtomType), toMob, targets, skillID, fai, rect, arriveDir, arriveRange, forcedTargetPos, bulletID, pos);
    }

    public static OutPacket createForceAtom(boolean byMob, int userOwner, int targetID, ForceAtomEnum forceAtomType, boolean toMob,
                                            int targets, int skillID, ForceAtomInfo fai, Rect rect, int arriveDir, int arriveRange,
                                            Position forcedTargetPos, int bulletID, Position pos) {
        List<Integer> integers = new ArrayList<>();
        integers.add(targets);
        List<ForceAtomInfo> forceAtomInfos = new ArrayList<>();
        forceAtomInfos.add(fai);
        return createForceAtom(byMob, userOwner, targetID, forceAtomType, toMob, integers, skillID, forceAtomInfos,
                rect, arriveDir, arriveRange, forcedTargetPos, bulletID, pos);
    }

    public static OutPacket createForceAtom(boolean byMob, int userOwner, int charID, ForceAtomEnum forceAtomType, boolean toMob,
                                            List<Integer> targets, int skillID, List<ForceAtomInfo> faiList, Rect rect, int arriveDir, int arriveRange,
                                            Position forcedTargetPos, int bulletID, Position pos) {
        return createForceAtom(new ForceAtom(byMob, userOwner, charID, forceAtomType, toMob, targets, skillID, faiList, rect, arriveDir, arriveRange, forcedTargetPos, bulletID, pos));
    }

    public static OutPacket createForceAtom(ForceAtom fa) {
        OutPacket out = new OutPacket(SendOpcode.CREATE_FORCE_ATOM);
        fa.encode(out);
        return out;
    }

    public static OutPacket createObstacle(ObstacleAtomCreateType oact, ObstacleInRowInfo oiri, ObstacleRadianInfo ori,
                                           Set<ObstacleAtomInfo> atomInfos) {
        OutPacket out = new OutPacket(SendOpcode.CREATE_OBSTACLE);
        out.writeInt(0); // ? gets used in 1 function, which forwards it to another, which does nothing with it
        out.writeInt(atomInfos.size());
        out.write(oact.getVal());
        if (oact == ObstacleAtomCreateType.IN_ROW) {
            oiri.encode(out);
        } else if (oact == ObstacleAtomCreateType.RADIAL) {
            ori.encode(out);
        }
        for (ObstacleAtomInfo atomInfo : atomInfos) {
            out.writeBool(true); // false -> no encode
            atomInfo.encode(out);
            if (oact == ObstacleAtomCreateType.DIAGONAL) {
                atomInfo.getObtacleDiagonalInfo().encode(out);
            }
        }

        return out;
    }
}
