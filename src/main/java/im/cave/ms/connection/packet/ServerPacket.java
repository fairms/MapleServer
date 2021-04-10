package im.cave.ms.connection.packet;

import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.constants.ServerConstants;

public class ServerPacket {
    public static OutPacket securityPacket(int code) {
        OutPacket out = new OutPacket(SendOpcode.SECURITY_PACKET);

        out.write(code);

        return out;
    }

    public static OutPacket serverStateResult() {
        OutPacket out = new OutPacket(SendOpcode.SERVER_STATE_RESULT);

        out.writeInt(3);

        out.writeInt(1);
        out.writeInt(0x15);
        out.writeInt(1);
        out.writeMapleAsciiString("all");
        out.writeInt(2);
        out.writeInt(0x19);
        out.writeInt(1);
        out.writeMapleAsciiString("all");
        out.writeInt(3);
        out.writeInt(24);
        out.writeInt(1);
        out.writeMapleAsciiString("all");

        return out;
    }

    public static OutPacket serverKeyValue() {
        OutPacket out = new OutPacket(SendOpcode.SERVER_KEY_VALUE);

        out.writeInt(1);
        out.writeInt(1);
        out.writeInt(10000);

        out.writeMapleAsciiString(String.format("version=v%d_%s;pointUpdateBlock=0", ServerConstants.VERSION, ServerConstants.PATH));
        out.writeMapleAsciiString("all");

        return out;
    }
}
