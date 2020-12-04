package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.world.World;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/25 8:17
 */
public class PlayerLoggedinHandler {

    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readInt();
        int charId = slea.readInt();
        byte[] machineId = slea.read(16);
        c.setMachineID(machineId);
        World world = Server.getInstance().getWorldById(c.getWorld());
        MapleChannel channel = world.getChannel(c.getChannel());
        MapleCharacter player = channel.getPlayer(charId);
        if (player == null) {
            c.close();
            return;
        }
        player.setChannel(channel.getChannelId());
        player.setClient(c);
        c.setPlayer(player);
        //加密后的Opcode
        c.announce(MaplePacketCreator.encodeOpcodes(c));

        c.announce(MaplePacketCreator.cancelTitleEffect());
        //3.切换地图
        if (player.getHp() <= 0) {
            player.setMapId(player.getMap().getReturnMap());
            player.heal(50);
        }
        c.announce(ChannelPacket.getWarpToMap(player, true));
        player.getMap().addPlayer(player);

        c.announce(LoginPacket.account(player.getAccount()));
    }
}
