package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.MapleSignIn;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.job.JobManager;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.MaplePacketCreator;
import im.cave.ms.network.server.Server;
import im.cave.ms.network.server.channel.MapleChannel;
import im.cave.ms.tools.Pair;

import java.util.Arrays;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/25 8:17
 */
public class PlayerLoggedinHandler {

    public static void handlePacket(InPacket inPacket, MapleClient c) {
        int worldId = inPacket.readInt();
        int charId = inPacket.readInt();
        byte[] machineId = inPacket.read(16);
        Pair<Byte, MapleClient> transInfo = Server.getInstance().getClientTransInfo(charId);
        if (transInfo == null) {
            c.close();
            return;
        }
        MapleClient oldClient = transInfo.getRight();
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
        if (!Arrays.equals(oldClient.getMachineID(), machineId)) {
            //todo
//            c.close();
//            return;
        }
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
        c.announce(MaplePacketCreator.updateEventNameTag()); //updateEventNameTag
        //3.切换地图
        if (player.getHp() <= 0) {
            player.setMapId(player.getMap().getReturnMap());
            player.heal(50);
        }
        player.changeMap(player.getMapId(), true);
        player.initBaseStats();
        player.buildQuestEx();
        c.getAccount().buildSharedQuestEx();
        c.announce(MapleSignIn.getRewardPacket());
    }
}
