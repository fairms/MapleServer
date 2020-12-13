package im.cave.ms.net.netty;

import im.cave.ms.tools.Position;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 12/13 14:33
 */
public class InPacket {
    private final ByteBuf byteBuf;

    public InPacket(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }


    public InPacket(byte[] data) {
        this(Unpooled.copiedBuffer(data));
    }

    public InPacket() {
        this(Unpooled.buffer());
    }

    public void release() {
        byteBuf.release();
    }


    public byte readByte() {
        return byteBuf.readByte();
    }

    public short readUByte() {
        return byteBuf.readUnsignedByte();
    }

    public short readShort() {
        return byteBuf.readShortLE();
    }

    public int readInt() {
        return byteBuf.readIntLE();
    }

    public long readLong() {
        return byteBuf.readLongLE();
    }

    public String readAsciiString(int length) {
        byte[] bytes = read(length);
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) bytes[i];
        }
        return String.valueOf(chars);
    }


    public String readMapleAsciiString() {
        int len = readShort();
        return readAsciiString(len);
    }

    public byte[] read(int amount) {
        byte[] arr = new byte[amount];
        for (int i = 0; i < amount; i++) {
            arr[i] = byteBuf.readByte();
        }
        return arr;
    }

    public Position readPos() {
        return new Position(readShort(), readShort());
    }

    public Position readPosInt() {
        return new Position(readInt(), readInt());
    }

    public int available() {
        return byteBuf.readableBytes();
    }

    public void skip(int len) {
        byteBuf.skipBytes(len);
    }
}
