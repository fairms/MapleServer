package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.Job.JobManager;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.MapleSignIn;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.packet.PlayerPacket;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.tools.Pair;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/25 8:17
 */
public class PlayerLoggedinHandler {

    public static void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int worldId = slea.readInt();
        int charId = slea.readInt();
        byte[] machineId = slea.read(16);
        Pair<Byte, MapleClient> transInfo = Server.getInstance().getClientTransInfo(charId);
        if (transInfo == null) {
            c.close();
            return;
        }
        MapleClient oldClient = transInfo.getRight();
//        if (!Arrays.equals(oldClient.getMachineID(), machineId) ||) {
//            c.close();
//            return;
//        }
        MapleCharacter player = oldClient.getPlayer();
        if (player == null) {
            c.close();
            return;
        }
        Byte channel = transInfo.getLeft();
        c.setMachineID(machineId);
        c.setWorld(worldId);
        c.setChannel(channel);
        c.setAccount(oldClient.getAccount());
        Server.getInstance().removeTransfer(charId);
        MapleChannel mapleChannel = c.getMapleChannel();
        player.setClient(c);
        player.setAccount(c.getAccount());
        player.setChannel(channel);
        c.setPlayer(player);
        c.setLoginStatus(LoginStatus.LOGGEDIN);
        mapleChannel.addPlayer(player);
        Server.getInstance().addAccount(c.getAccount());
        player.setJobHandler(JobManager.getJobById(player.getJobId(), player));
        //加密后的Opcode
        c.announce(MaplePacketCreator.encodeOpcodes(c));
        c.announce(MaplePacketCreator.cancelTitleEffect());
        //3.切换地图
        if (player.getHp() <= 0) {
            player.setMapId(player.getMap().getReturnMap());
            player.heal(50);
        }
        player.changeMap(player.getMapId(), true);
        player.initBaseStats();
        player.buildQuestEx();
        c.announce(MapleSignIn.getRewardPacket());
        c.announce(MaplePacketCreator.keymapInit(player));
        c.announce(MaplePacketCreator.quickslotInit(player));
        c.announce(LoginPacket.account(player.getAccount()));
        c.announce(PlayerPacket.updateVoucher(player));
    }
}
