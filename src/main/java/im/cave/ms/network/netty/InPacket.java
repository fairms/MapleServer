package im.cave.ms.network.netty;

import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 12/13 14:33
 */
public class InPacket implements Cloneable {
    private ByteBuf byteBuf;

    public InPacket(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }


    public InPacket(byte[] data) {
        this(Unpooled.copiedBuffer(data));
    }

    public InPacket() {
        this(Unpooled.buffer());
    }

    @Override
    public InPacket clone() {
        return new InPacket(byteBuf.copy());
    }

    public void release() {
        if (byteBuf != null) {
            ReferenceCountUtil.release(byteBuf);
            byteBuf = null;
        }
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

    public String readAsciiString(int len) {
        byte[] ret = new byte[len];
        for (int x = 0; x < len; x++) {
            ret[x] = readByte();
        }
        try {
            return new String(ret, "gbk");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    // 我服了
    public void skip(int len) {
        byteBuf.readBytes(len).release();
    }

    public Rect readShortRect() {
        return new Rect(readPos(), readPos());
    }

    public byte[] getData() {
        return byteBuf.array();
    }

    @Override
    public String toString() {
        return Util.readableByteArray(Arrays.copyOfRange(getData(), getData().length - available(), getData().length)); // Substring after copy of range xd
    }
}
