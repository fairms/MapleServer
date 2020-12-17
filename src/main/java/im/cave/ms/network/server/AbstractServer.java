package im.cave.ms.network.server;

import im.cave.ms.enums.ServerType;
import im.cave.ms.network.netty.ServerAcceptor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.abstractServer
 * @date 11/19 16:23
 */
public abstract class AbstractServer {
    protected ServerType type;
    protected int port;
    protected int worldId = 0;
    protected int channelId = 0;
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
