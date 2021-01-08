package im.cave.ms.network.packet;

import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 1/1 16:54
 */
public class AndroidPacket {

    public static OutPacket created(Android android) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.ANDROID_CREATED.getValue());
        out.writeInt(android.getOwner().getId());
        android.encode(out);
        return out;
    }

    public static OutPacket remove(int charId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.ANDROID_REMOVED.getValue());
        out.writeInt(charId);
        return out;
    }

    public static OutPacket actionSet(Android android, int action, int emotion) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.ANDROID_ACTION_SET.getValue());
        out.writeInt(android.getOwner().getId());
        out.write(action);
        out.write(emotion);
        return out;
    }

    public static OutPacket modified(Android android) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.ANDROID_MODIFIED.getValue());
        out.writeInt(android.getOwner().getId());

        return out;

    }

    public static OutPacket move(Android android, MovementInfo mi) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.ANDROID_MOVE.getValue());
        out.writeInt(android.getOwner().getId());
        mi.encode(out);
        return out;
    }
}
