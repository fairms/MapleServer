package im.cave.ms.connection.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.multiplayer.miniroom.ChatRoom;
import im.cave.ms.client.multiplayer.miniroom.TradeRoom;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.ChatRoomType;
import im.cave.ms.enums.RoomLeaveType;
import im.cave.ms.enums.MiniRoomType;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 12/29 14:37
 */
public class MiniRoomPacket {

    /*
        Invite
        type 4:trade
             3: RPS
     */
    public static OutPacket tradeInvite(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.TradeInviteRequest.getVal());
        out.write(4); //type
        out.writeMapleAsciiString(player.getName());
        out.writeInt(0);
        out.writeInt(player.getId());
        return out;
    }

    public static OutPacket cancelTrade(int user) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.ExitTrade.getVal());
        out.write(user);
        out.write(RoomLeaveType.MRLeave_Closed.getVal());
        return out;
    }

    public static OutPacket enterTrade(TradeRoom tradeRoom, int user) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.EnterTrade.getVal());
        out.write(4); //type
        out.write(2);
        out.write(user);
        tradeRoom.encodeChar(out);
        return out;
    }

    public static OutPacket acceptTradeInvite(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.Accept.getVal());
        out.write(1);
        player.getCharLook().encode(out);
        out.writeShort(player.getJob());
        return out;
    }

    public static OutPacket chat(int pos, String msg, MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
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
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.PlaceItem.getVal());
        out.write(user);
        out.write(tradePos);
        item.encode(out);
        return out;
    }

    public static OutPacket putMeso(int user, long meso) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.SetMesos.getVal());
        out.write(user);
        out.writeLong(meso);
        return out;
    }

    public static OutPacket tradeConfirm() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.Trade.getVal());
        return out;
    }

    public static OutPacket tradeComplete(int user) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MINI_ROOM_DUAL.getValue());
        out.write(MiniRoomType.ExitTrade.getVal());
        out.write(user); // other user cancelled
        out.write(RoomLeaveType.TRLeave_TradeDone.getVal());
        return out;
    }


    /*
        End Trade
     */


    /*
        Begin Chat
     */
    public static OutPacket chatRoomInvite(String name) {
        OutPacket out = new OutPacket(SendOpcode.MINI_ROOM_MULTI);
        out.write(ChatRoomType.Res_ChatInviteRequest.getVal());
        out.writeMapleAsciiString(name);
        out.writeInt(0); //unk
        out.writeInt(0); //unk
        out.writeShort(0); //unk
        return out;
    }

    public static OutPacket chatRoomInviteTip(String name, boolean self) {
        OutPacket out = new OutPacket(SendOpcode.MINI_ROOM_MULTI);
        out.write(ChatRoomType.Res_ChatInviteTip.getVal());
        out.writeMapleAsciiString(name);
        out.writeBool(self);
        return out;
    }


    public static OutPacket enterChatRoom(ChatRoom cr, MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.MINI_ROOM_MULTI);
        out.write(ChatRoomType.Req_Join.getVal());
        out.write(1); //unk
        out.writeInt(cr.getChatRoomId());
        chr.getCharLook().encode(out);
        out.writeMapleAsciiString(chr.getName());
        out.write(0);
        out.write(1);
        out.writeInt(0);
        // 00 00 00 00 00 00  6个位置?
        return out;
    }
}
