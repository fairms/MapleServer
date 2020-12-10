package im.cave.ms.net.packet;

import im.cave.ms.client.character.ExpIncreaseInfo;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.items.Item;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.DropEnterType;
import im.cave.ms.enums.DropLeaveType;
import im.cave.ms.enums.ServerMsgType;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Randomizer;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import static im.cave.ms.enums.DropEnterType.*;
import static im.cave.ms.enums.MessageType.*;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 11/20 21:58
 */
public class ChannelPacket {
    public static MaplePacketLittleEndianWriter getWarpToMap(MapleCharacter player, boolean firstLoggedIn) {
        return getWarpToMap(player, true, null, 0, firstLoggedIn);
    }

    public static MaplePacketLittleEndianWriter getWarpToMap(MapleCharacter player, MapleMap to, int spawnPoint) {
        return getWarpToMap(player, false, to, spawnPoint, false);
    }

    public static MaplePacketLittleEndianWriter getWarpToMap(MapleCharacter chr, boolean load, MapleMap to, int spawnPoint, boolean firstLoggedIn) {

        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SET_MAP.getValue());
        mplew.writeShort(1);
        mplew.writeLong(1);
        mplew.writeInt(chr.getClient().getChannel());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeShort(Math.min(chr.getVisitedMapCount(), 255));
        mplew.writeInt(!load ? to.getFieldType().getVal() : 0);
        mplew.writeInt(chr.getMap().getWidth());
        mplew.writeInt(chr.getMap().getHeight());
        mplew.writeBool(load);
        mplew.writeShort(0);
        if (load) {
            for (int i = 0; i < 3; i++) {
                mplew.writeInt(Randomizer.nextInt());
            }
            PacketHelper.addCharInfo(mplew, chr);
        } else {
            mplew.write(0); // usingBuffProtector
            mplew.writeInt(to.getId()); //地图ID
            mplew.write(spawnPoint);
            mplew.writeInt(chr.getStats().getHp()); // 角色HP
            mplew.writeZeroBytes(4);
        }
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        mplew.writeInt(100); //mobStatAdjustRate
        mplew.writeShort(0); //hasFieldCustom + is pvp map
        mplew.write(1); //canNotifyAnnouncedQuest
        mplew.writeShort(0);  //stackEventGauge >= 0 + Star planet

        //过图加载怪怪信息
        if (!load) {
            mplew.writeInt(360);
            addUnkData(mplew, chr);
        } else {
            mplew.writeLong(4);
        }
        //下面是固定的 unk
        mplew.writeZeroBytes(16);
        mplew.write(1);
        mplew.writeInt(-1);
        mplew.writeLong(0);
        mplew.writeInt(999999999);
        mplew.writeInt(999999999);
        mplew.writeInt(0);
        mplew.writeZeroBytes(3);
        mplew.write(1);
        mplew.writeInt(0);

        return mplew;
    }


    public static void addUnkData(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(1);
        mplew.writeInt(0x9947A4C2);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.write(1);
        mplew.writeZeroBytes(21);
        mplew.writeShort(2);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.writeShort(3);
        mplew.writeInt(1);
        mplew.writeInt(4);
        mplew.writeShort(0);
        mplew.writeShort(5);
        mplew.writeInt(0);
        mplew.writeInt(7);
        mplew.writeShort(8);
        mplew.writeShort(3);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(3);
        mplew.writeShort(0);
        mplew.writeShort(0x0b);
        mplew.writeInt(0x44f9930f);
        mplew.write(0x0c);
        mplew.writeLong(0);
        mplew.writeInt(2);
        mplew.writeInt(0x825ad512);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.write(1);
        mplew.writeZeroBytes(21);
        mplew.writeShort(2);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.writeShort(3);
        mplew.writeInt(1);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(3);
        mplew.writeInt(0xb529d96e);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.write(1);
        mplew.writeZeroBytes(21);
        mplew.writeShort(2);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.writeShort(3);
        mplew.writeInt(1);
        byte[] bytes = new byte[]{0x4, 0x5, 0x6, 0x7, 0x8, 0x9,
                0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x19, 0x1E,
                0x1F, 0x20, 0x21, 0x24, 0x26, 0x27, 0x28, 0x29};
        for (byte b : bytes) {
            mplew.writeShort(b);
            if (b == 0x20) {
                mplew.writeInt(0);
            }
            mplew.writeInt(0);
        }
        mplew.writeZeroBytes(7);

    }

    public static MaplePacketLittleEndianWriter chatMessage(String content, ChatType type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHAT_MSG.getValue());
        mplew.writeShort(type.getVal());
        mplew.writeMapleAsciiString(content);
        return mplew;
    }


    public static MaplePacketLittleEndianWriter serverMsg(String content, ServerMsgType type) {
        return serverMsg(content, type, null);
    }

    public static MaplePacketLittleEndianWriter serverMsg(String content, ServerMsgType type, Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SERVER_MSG.getValue());
        mplew.write(type.getVal());
        switch (type) {
            case NOTICE:
                break;
            case ALERT:
            case EVENT:
                mplew.writeMapleAsciiString(content);
                break;
            case SLIDE:
                mplew.write(1);
                mplew.writeMapleAsciiString(content);
                break;
            case NOTICE_WITH_OUT_PREFIX:
                mplew.writeMapleAsciiString(content);
                mplew.write(0);
                break;
            case PICKUP_ITEM_WORLD:
                mplew.writeMapleAsciiString(content);
                mplew.write(item.getItemId());
                PacketHelper.addItemInfo(mplew, item);
                break;
        }
        return mplew;
    }


    public static MaplePacketLittleEndianWriter debugMsg(String content) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.DEBUG_MSG.getValue());
        mplew.writeInt(3); //unk
        mplew.writeInt(14); //unk
        mplew.writeInt(14); //unk
        mplew.writeInt(0); //unk
        mplew.write(0); //unk
        mplew.writeMapleAsciiString(content);
        return mplew;
    }


    public static MaplePacketLittleEndianWriter serverNotice(String content) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SERVER_NOTICE.getValue());
        mplew.writeMapleAsciiString(content);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter fullscreenMessage(String content) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(0x0c);
        mplew.writeMapleAsciiString(content);
        mplew.write(1);
        return mplew;
    }


    public static MaplePacketLittleEndianWriter dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom) {
        return dropEnterField(drop, dropEnterType, posFrom, posFrom);
    }

    public static MaplePacketLittleEndianWriter dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom, Position posTo) {
        return dropEnterField(drop, dropEnterType, posFrom, posTo, 0, false);
    }


    public static MaplePacketLittleEndianWriter dropEnterField(Drop drop, DropEnterType dropEnterType, Position posFrom, Position posTo, int delay, boolean canBePickByPet) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.DROP_ENTER_FIELD.getValue());
        mplew.writeBool(drop.isMeso());
        mplew.write(dropEnterType.getVal());
        mplew.writeInt(drop.getObjectId());
        mplew.writeBool(drop.isMeso());
        mplew.writeInt(0); //getDropMotionType
        mplew.writeInt(0); //dropSpeed
        mplew.writeInt(0);  //rand?
        mplew.writeInt(!drop.isMeso() ? drop.getItem().getItemId() : drop.getMoney());
        mplew.writeInt(drop.getOwnerID());
        mplew.write(2); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
        mplew.writePos(posTo);
        mplew.writeInt(0); // drop from id
        mplew.writeZeroBytes(42);
        if (dropEnterType != Instant) {
            mplew.writePos(posFrom);
            mplew.writeInt(delay); //delay
        }
        mplew.write(0); //unk
        if (!drop.isMeso()) {
            mplew.writeLong(drop.getExpireTime());
        }
        mplew.writeBool(drop.isCanBePickedUpByPet() && canBePickByPet);
        mplew.writeZeroBytes(14); //unk
        return mplew;
    }

    public static MaplePacketLittleEndianWriter dropLeaveField(DropLeaveType type, int charId, int dropId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.PICK_UP_DROP.getValue());
        mplew.write(type.getVal());
        mplew.writeInt(dropId);
        mplew.writeInt(charId);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter incExpMessage(ExpIncreaseInfo expIncreaseInfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(INC_EXP_MESSAGE.getVal());
        expIncreaseInfo.encode(mplew);
        mplew.writeInt(0);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter dropPickupMessage(int money, short internetCafeExtra, short smallChangeExtra) {
        return dropPickupMessage(money, (byte) 1, internetCafeExtra, smallChangeExtra, (short) 0);
    }

    public static MaplePacketLittleEndianWriter dropPickupMessage(Item item, short quantity) {
        return dropPickupMessage(item.getItemId(), (byte) 0, (short) 0, (short) 0, quantity);
    }

    public static MaplePacketLittleEndianWriter dropPickupMessage(int i, byte type, short internetCafeExtra, short smallChangeExtra, short quantity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(DROP_PICKUP_MESSAGE.getVal());
        mplew.writeInt(0);
        mplew.write(0);
        if (internetCafeExtra > 0) type = 8;
        mplew.write(type);
        // also error (?) codes -2, ,-3, -4, -5, <default>
        switch (type) {
            case -10:
                mplew.writeInt(100);// nItemID
                break;
            case 0: // item
                mplew.writeInt(i);
                mplew.writeInt(quantity); // ?
                mplew.write(0);
                break;
            case 1: // Mesos
                mplew.writeBool(false); // boolean: portion was lost after falling to the ground
                mplew.writeInt(i); // Mesos
                mplew.writeShort(smallChangeExtra); // Spotting small change
                break;
            case 2: // ?
                mplew.writeInt(100);// nItemID
                mplew.writeLong(0);
                break;
            case 8:
                mplew.writeInt(i); // Mesos
                mplew.writeShort(internetCafeExtra); // Internet cafe
                break;
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter effect(int charId) {
        return null;
    }

    public static MaplePacketLittleEndianWriter resultInstanceTable(String requestStr, int type, int subType, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.RESULT_INSTANCE_TABLE.getValue());
        mplew.writeMapleAsciiString(requestStr);
        mplew.writeInt(type);
        mplew.writeInt(subType);
        mplew.writeBool(type < 41);
        mplew.writeInt(value);
        return mplew;
    }
}
