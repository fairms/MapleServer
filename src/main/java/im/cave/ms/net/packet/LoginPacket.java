package im.cave.ms.net.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.config.ServerConfig;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.login.LoginServer;
import im.cave.ms.net.server.world.World;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.LoginType;
import im.cave.ms.enums.ServerType;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static im.cave.ms.constants.ServerConstants.PATH;
import static im.cave.ms.constants.ServerConstants.VERSION;
import static im.cave.ms.enums.ServerType.LOGIN;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 17:07
 */
public class LoginPacket {


    public static MaplePacketLittleEndianWriter clientAuth() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(0x2C);
        mplew.write(0);
        return mplew;
    }


    public static MaplePacketLittleEndianWriter getHello(int sendIv, int recvIv, ServerType type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (type == LOGIN) {
            mplew.writeShort(0x1E);
            for (int i = 0; i < 2; i++) {
                mplew.writeShort(VERSION);
                mplew.writeMapleAsciiString(PATH);
                mplew.writeInt(recvIv);
                mplew.writeInt(sendIv);
                mplew.writeShort(4);
            }
        } else if (type == ServerType.CHANNEL) {
            mplew.writeShort(0x0E);
            mplew.writeShort(VERSION);
            mplew.writeMapleAsciiString(PATH);
            mplew.writeInt(recvIv);
            mplew.writeInt(sendIv);
            mplew.write(4);
        }
        return mplew;
    }


    public static MaplePacketLittleEndianWriter getOpenCreateChar() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.OPEN_CREATE_CHAR.getValue());
        mplew.write(4);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter checkNameResponse(String name, byte state) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(name);
        mplew.write(state);
        return mplew;
    }

    private static int num = 1;

    public static MaplePacketLittleEndianWriter loginResult(MapleClient c, LoginType result) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.LOGIN_STATUS.getValue());
        if (result == LoginType.Success) {
            mplew.write(0);
            mplew.writeMapleAsciiString(c.getAccount().getAccount());
            mplew.writeLong(0);
            mplew.writeInt(c.getAccount().getId());
            mplew.writeBool(false);
            mplew.writeLong(128);
            mplew.writeShort(3);
            mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
            mplew.writeZeroBytes(11);
            mplew.writeShort(8449);
            for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
                mplew.write(ServerConfig.config.CLOSED_JOBS.contains(job.getBeginJob().getJobId()) ? 0 : job.getFlag());
                mplew.writeShort(1);
            }
            mplew.write(0);
            mplew.writeInt(-1);
            mplew.writeBool(true);
            mplew.writeShort(0);
            mplew.writeMapleAsciiString(c.getAccount().getAccount());
            mplew.write(1);
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);
        } else {
            mplew.write(result.getValue());
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter serverListBg() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SERVER_LIST_BG.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString("default");
        mplew.write(1);
        mplew.writeLong(0);
        return mplew;
    }

    public static List<MaplePacketLittleEndianWriter> serverList() {
        Server server = Server.getInstance();
        List<World> worlds = server.getWorlds();
        List<MaplePacketLittleEndianWriter> serverList = new ArrayList<>();
        for (World world : worlds) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendOpcode.SERVERLIST.getValue());
            mplew.writeShort(world.getId());
            mplew.writeMapleAsciiString("World-" + world.getId());
            mplew.writeZeroBytes(8);
            mplew.writeShort(512);
            mplew.writeMapleAsciiString(world.getEventMessage());
            mplew.write(world.getChannelsSize());
            mplew.writeInt(500);
            List<MapleChannel> worldChannels = world.getChannels();
            for (MapleChannel ch : worldChannels) {
                mplew.writeMapleAsciiString("World-" + world.getId() + "-" + ch.getChannelId());
                mplew.writeInt(ch.getChannelCapacity());
                mplew.write(world.getId());
                mplew.writeShort(ch.getChannelId());
            }
            mplew.writeZeroBytes(12);
            serverList.add(mplew);
        }
        return serverList;

    }

    public static MaplePacketLittleEndianWriter serverListEnd() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SERVERLIST.getValue());
        mplew.writeShort(-1);
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        mplew.writeLong(0);
        mplew.writeShort(0);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter serverStatus(int worldId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SERVERSTATUS.getValue());
        mplew.write(0);
        mplew.writeInt(worldId);
        mplew.writeInt(2);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter account(Account account) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.ACCOUNT.getValue());
        mplew.writeLong(0);
        mplew.writeMapleAsciiString(account.getAccount());
        return mplew;
    }

    public static MaplePacketLittleEndianWriter charList(MapleClient c, List<MapleCharacter> characters, int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHARLIST.getValue());
        mplew.write(status);
        mplew.writeMapleAsciiString("Normal");
        mplew.writeMapleAsciiString("Normal");
        mplew.writeZeroBytes(6);
        List<MapleCharacter> deletedChars = characters.stream().filter(MapleCharacter::isDeleted).collect(Collectors.toList());
        mplew.writeInt(deletedChars.size());
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        for (MapleCharacter deletedChar : deletedChars) {
            mplew.writeInt(deletedChar.getId());
            mplew.writeLong(DateUtil.getFileTime(deletedChar.getDeleteTime()));
        }
        mplew.write(0);
        mplew.writeInt(characters.size());
        for (MapleCharacter character : characters) {
            mplew.writeInt(character.getId());
        }
        mplew.writeZeroBytes(9);
        mplew.write(characters.size());
        characters.forEach(chr -> PacketHelper.addCharEntry(mplew, chr));
        mplew.writeShort(0);
        mplew.writeLong(c.getAccount().getCharacterSlots());
        mplew.writeInt(-1);
        mplew.writeReversedLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        mplew.write(0);
        mplew.write(1);
        mplew.write(0);
        mplew.writeZeroBytes(8);
        mplew.writeInt(327680);
        mplew.writeInt(553713664);
        for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
            mplew.write(ServerConfig.config.CLOSED_JOBS.contains(job.getBeginJob().getJobId()) ? 0 : job.getFlag());
            mplew.writeShort(1);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter selectCharacterResult(LoginType loginType, byte errorCode, int port, int charId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SERVER_IP.getValue());
        mplew.write(loginType.getValue());
        mplew.write(errorCode);
        if (loginType.equals(LoginType.Success)) {
            mplew.write(ServerConstants.NEXON_IP);
            mplew.writeShort(port);
            mplew.writeInt(0);
            mplew.writeInt(charId);
            mplew.writeInt(0);
            mplew.writeShort(5120);
            mplew.writeShort(1000);
            mplew.writeZeroBytes(6);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter ping(ServerType type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (type == LOGIN) {
            mplew.writeShort(SendOpcode.PING.getValue());
        } else {
            mplew.writeShort(SendOpcode.CPING.getValue());
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter changePlayer(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        char[] ss = new char[256];
        int i = 0;
        while (i < ss.length) {
            int f = (int) (Math.random() * 3);
            if (f == 0) {
                ss[i] = (char) ('A' + Math.random() * 26);
            } else if (f == 1) {
                ss[i] = (char) ('a' + Math.random() * 26);
            } else {
                ss[i] = (char) ('0' + Math.random() * 10);
            }
            i++;
        }
        String key = new String(ss);
        LoginServer.getInstance().putLoginAuthKey(key, c.getAccount().getAccount(), c.getChannel());
        mplew.writeShort(SendOpcode.CHANGE_CHAR_KEY.getValue());
        mplew.writeMapleAsciiString(key);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter authSuccess(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.AUTH_SUCCESS.getValue());
        mplew.write(0);
        mplew.writeInt(c.getAccount().getId());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(3);
        mplew.write(0);
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        mplew.writeLong(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(c.getAccount().getAccount());
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(33);
        for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
            mplew.write(ServerConfig.config.CLOSED_JOBS.contains(job.getBeginJob().getJobId()) ? 0 : job.getFlag());
            mplew.writeShort(1);
        }
        mplew.write(0);
        mplew.writeInt(-1);
        mplew.write(1);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter deleteTime(int charId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.DELETE_CHAR_TIME.getValue());
        mplew.writeInt(charId);
        mplew.write(0);
        long deleteTime = LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        mplew.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        mplew.writeLong(DateUtil.getFileTime(deleteTime));
        return mplew;
    }

    public static MaplePacketLittleEndianWriter cancelDeleteChar(int charId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CANCEL_DELETE_CHAR.getValue());
        mplew.writeInt(charId);
        mplew.write(0);
        return mplew;
    }
}
