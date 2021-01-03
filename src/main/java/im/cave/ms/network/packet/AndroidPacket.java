package im.cave.ms.network.packet;

import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.movement.MovementInfo;
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
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ANDROID_CREATED.getValue());
        outPacket.writeInt(android.getOwner().getId());
        android.encode(outPacket);
        return outPacket;
    }

    public static OutPacket remove(int charId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ANDROID_REMOVED.getValue());
        outPacket.writeInt(charId);
        return outPacket;
    }

    public static OutPacket actionSet(Android android, int action, int emotion) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ANDROID_ACTION_SET.getValue());
        outPacket.writeInt(android.getOwner().getId());
        outPacket.write(action);
        outPacket.write(emotion);
        return outPacket;
    }

    public static OutPacket modified(Android android) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ANDROID_MODIFIED.getValue());
        outPacket.writeInt(android.getOwner().getId());

        return outPacket;

    }

    public static OutPacket move(Android android, MovementInfo mi) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ANDROID_MOVE.getValue());
        outPacket.writeInt(android.getOwner().getId());
        mi.encode(outPacket);
        return outPacket;
    }
}
