package im.cave.ms.network.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.social.miniroom.TradeRoom;
import im.cave.ms.enums.MiniRoomType;
import im.cave.ms.enums.RoomLeaveType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 12/29 14:37
 */
public class MiniRoomPacket {
    public static OutPacket tradeInvite(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.TradeInviteRequest.getVal());
        out.write(4);
        out.writeMapleAsciiString(player.getName());
        out.writeInt(0);
        out.writeInt(player.getId());
        return out;
    }

    public static OutPacket cancelTrade(int user) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.ExitTrade.getVal());
        out.write(user);
        out.write(RoomLeaveType.MRLeave_Closed.getVal());
        return out;
    }

    public static OutPacket enterTrade(TradeRoom tradeRoom, int user) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.EnterTrade.getVal());
        out.write(4);
        out.write(2);
        out.write(user);
        tradeRoom.encodeChar(out);
        return out;
    }

    public static OutPacket acceptTradeInvite(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.Accept.getVal());
        out.write(1);
        player.getCharLook().encode(out);
        out.writeShort(player.getJob());
        return out;
    }

    public static OutPacket chat(int pos, String msg, MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.Chat.getVal());
        out.write(25);
        out.write(pos);
        out.writeMapleAsciiString(msg);
        out.writeInt(chr.getId());
        out.writeMapleAsciiString(chr.getName());
        out.writeMapleAsciiString(msg);
        out.writeZeroBytes(13);
        return out;
    }

    public static OutPacket putItem(int user, byte tradePos, Item item) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.PlaceItem.getVal());
        out.write(user);
        out.write(tradePos);
        item.encode(out);
        return out;
    }

    public static OutPacket putMeso(int user, long meso) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.SetMesos.getVal());
        out.write(user);
        out.writeLong(meso);
        return out;
    }

    public static OutPacket tradeConfirm() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.Trade.getVal());
        return out;
    }

    public static OutPacket tradeComplete(int user) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM.getValue());
        out.write(MiniRoomType.ExitTrade.getVal());
        out.write(user); // other user cancelled
        out.write(RoomLeaveType.TRLeave_TradeDone.getVal());
        return out;
    }
}
