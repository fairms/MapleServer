package im.cave.ms.connection.packet;

import im.cave.ms.client.field.obj.Reactor;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 2/7 11:58
 */
public class ReactorPacket {
    public static OutPacket reactorEnterField(Reactor reactor) {
        OutPacket out = new OutPacket(SendOpcode.REACTOR_ENTER_FIELD);

        out.writeInt(reactor.getObjectId());
        out.writeInt(reactor.getTemplateId());
        out.write(reactor.getState());
        out.writePosition(reactor.getPosition());
        out.writeBool(reactor.isFlip());
        out.writeMapleAsciiString(reactor.getName());

        return out;
    }


    public static OutPacket reactorChangeState(Reactor reactor, short delay, byte stateLength) {
        OutPacket out = new OutPacket(SendOpcode.REACTOR_CHANGE_STATE);

        out.writeInt(reactor.getObjectId());
        out.write(reactor.getState());
        out.writePosition(reactor.getPosition());
        out.writeShort(delay);
        out.write(reactor.getProperEventIdx());
        out.writeInt(stateLength);
        out.writeInt(reactor.getOwnerId());

        return out;
    }

    public static OutPacket remove(Reactor reactor) {
        OutPacket out = new OutPacket(SendOpcode.REACTOR_REMOVE);

        out.writeInt(reactor.getObjectId());

        return out;
    }
}
