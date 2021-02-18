package im.cave.ms.connection.server;

import im.cave.ms.connection.netty.ServerAcceptor;
import im.cave.ms.enums.ServerType;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer
 * @date 11/19 16:23
 */
public abstract class AbstractServer {
    protected ServerType type;
    protected int port;
    protected int worldId;
    protected int channelId;
    protected ServerAcceptor acceptor;

    public AbstractServer(int worldId, int channelId) {
        this.worldId = worldId;
        this.channelId = channelId;
    }


    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }


    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }
}
