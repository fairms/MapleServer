package im.cave.ms.connection.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.AuctionRecord;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.AuctionAction;

import java.util.List;

import static im.cave.ms.constants.ServerConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 2/5 10:28
 */
public class AuctionPacket {

    public static OutPacket getWrapToAuction(MapleCharacter player) {
        OutPacket out = new OutPacket(SendOpcode.SET_AUCTION);
        PacketHelper.addCharInfo(out, player);
        return out;
    }

    public static OutPacket quotation(List<AuctionRecord> records) {
        OutPacket out = new OutPacket(SendOpcode.AUCTION);

        out.writeInt(AuctionAction.QuickSearch.getVal());
        out.writeLong(1000); //mask
        out.write(1); //金币
        out.write(0); //抵用券
        out.writeInt(records.size());
        for (AuctionRecord record : records) {
            record.encode(out);
        }

        return out;
    }


    public static OutPacket pullOnSellDone(AuctionRecord record) {
        OutPacket out = new OutPacket(SendOpcode.AUCTION);

        out.writeInt(AuctionAction.Put_On_Sell.getVal());
        out.writeInt(0);
        out.writeInt(record.getId());
        out.write(1);
        record.encode(out);

        return out;
    }
}
