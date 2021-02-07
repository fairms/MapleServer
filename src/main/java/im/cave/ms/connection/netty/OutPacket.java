package im.cave.ms.connection.netty;

import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 12/13 14:33
 */
public class OutPacket {
    private static final Logger log = LoggerFactory.getLogger(OutPacket.class);
    private static final Charset ASCII = Charset.forName("GBK");

    private final ByteBuf byteBuf;

    public OutPacket() {
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
    }

    public OutPacket(SendOpcode opcode) {
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeShortLE(opcode.getValue());
    }

    public OutPacket(ByteBuf byteBuf) {
        this.byteBuf = byteBuf.copy();
    }

    public void write(byte b) {
        byteBuf.writeByte(b);
    }

    public void write(byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }

    public void write(int i) {
        byteBuf.writeByte(i);
    }

    public void writeShort(short s) {
        byteBuf.writeShortLE(s);
    }

    public void writeShort(int i) {
        byteBuf.writeShortLE(i);
    }

    public void writeInt(int i) {
        byteBuf.writeIntLE(i);
    }

    public void writeInt(boolean b) {
        byteBuf.writeIntLE(b ? 1 : 0);
    }

    public void writeLong(long l) {
        byteBuf.writeLongLE(l);
    }

    public void writeReversedLong(final long l) {
        byteBuf.writeByte((byte) ((l >>> 32) & 0xFF));
        byteBuf.writeByte((byte) ((l >>> 40) & 0xFF));
        byteBuf.writeByte((byte) ((l >>> 48) & 0xFF));
        byteBuf.writeByte((byte) ((l >>> 56) & 0xFF));
        byteBuf.writeByte((byte) (l & 0xFF));
        byteBuf.writeByte((byte) ((l >>> 8) & 0xFF));
        byteBuf.writeByte((byte) ((l >>> 16) & 0xFF));
        byteBuf.writeByte((byte) ((l >>> 24) & 0xFF));
    }

    public void writeZeroBytes(int len) {
        byte[] bytes = new byte[len];
        write(bytes);
    }

    public void writeAsciiString(String s, int len) {
        if (s == null) {
            s = "";
        }
        if (s.length() > 0) {
            write(s.getBytes(ASCII));
        }
        for (int i = s.getBytes(ASCII).length; i < len; i++) {
            write(0);
        }
    }

    public void writeMapleAsciiString(String s) {
        if (s == null) {
            s = "";
        }
        if (s.length() > Short.MAX_VALUE) {
            log.error("Tried to encode a string that is too big.");
            return;
        }
        writeShort((short) s.getBytes(ASCII).length);
        writeAsciiString(s, s.getBytes(ASCII).length);
    }

    public void writePosition(Position position) {
        if (position == null) {
            writeShort(0);
            writeShort(0);
        } else {
            writeShort(position.getX());
            writeShort(position.getY());
        }
    }

    public void writePositionInt(Position position) {
        if (position == null) {
            writeInt(0);
            writeInt(0);
        } else {
            writeInt(position.getX());
            writeInt(position.getY());
        }
    }

    public void writeBool(boolean bool) {
        byteBuf.writeByte(bool ? 1 : 0);
    }

    public byte[] getData() {
        if (byteBuf.hasArray()) {
            return byteBuf.array();
        } else {
            byte[] arr = new byte[byteBuf.writerIndex()];
            byteBuf.nioBuffer().get(arr, 0, byteBuf.writerIndex());
            return arr;
        }
    }

    @Override
    public String toString() {
        if (byteBuf.writerIndex() == 0) {
            return "Blank OutPacket";
        }
        short op = byteBuf.readShortLE();
        byteBuf.readerIndex(0); //important
        return String.format("%s, %s/0x%s\t| %s", SendOpcode.getByValue(op), op, Integer.toHexString(op).toUpperCase()
                , Util.readableByteArray(Arrays.copyOfRange(getData(), 2, getData().length)));
    }

    public void release() {
        ReferenceCountUtil.release(byteBuf);
    }

    public void writeRect(Rect rect) {
        writeShort(rect.getLeft());
        writeShort(rect.getTop());
        writeShort(rect.getRight());
        writeShort(rect.getBottom());
    }
}
