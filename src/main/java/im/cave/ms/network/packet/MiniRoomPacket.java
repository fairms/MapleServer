package im.cave.ms.network.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.miniroom.TradeRoom;
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
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.MINI_ROOM.getValue());
        outPacket.write(MiniRoomType.TradeInviteRequest.getVal());
        outPacket.write(4);
        outPacket.writeMapleAsciiString(player.getName());
        outPacket.writeInt(0);
        outPacket.writeInt(player.getId());
        return outPacket;
    }

    public static OutPacket cancelTrade(boolean sponsor) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.MINI_ROOM.getValue());
        outPacket.write(MiniRoomType.ExitTrade.getVal());
        outPacket.writeBool(!sponsor); // other user cancelled
        outPacket.write(RoomLeaveType.TRLeave_TradeFail_Denied.getVal());
        return outPacket;
    }

    public static OutPacket enterTrade(TradeRoom tradeRoom, MapleCharacter player) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.MINI_ROOM.getValue());
        outPacket.write(MiniRoomType.EnterTrade.getVal());
        outPacket.write(4);
        outPacket.write(2);
        outPacket.write(0);
        MapleCharacter other = tradeRoom.getOtherChar(player);
        if (other == null) {
            outPacket.writeShort(0);
        }

        outPacket.write(0);
        PacketHelper.addCharLook(outPacket, other, true, false);
        outPacket.writeMapleAsciiString(player.getName());
        outPacket.writeShort(player.getJobId());
        outPacket.write(-1);
        return outPacket;

    }
}
