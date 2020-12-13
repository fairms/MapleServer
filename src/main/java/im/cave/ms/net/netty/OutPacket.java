package im.cave.ms.net.netty;

import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 12/13 14:33
 */
public class OutPacket {
    private static final Logger log = LoggerFactory.getLogger(OutPacket.class);

    private final ByteBuf byteBuf;

    public OutPacket() {
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
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

    public void writeChar(char c) {
        byteBuf.writeByte(c);
    }

    public void writeInt(int i) {
        byteBuf.writeIntLE(i);
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
            for (char c : s.toCharArray()) {
                writeChar(c);
            }
        }
        for (int i = s.length(); i < len; i++) {
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
        writeShort((short) s.length());
        writeAsciiString(s, s.length());
    }

    public void writePos(Position position) {
        if (position == null) {
            writeShort(0);
            writeShort(0);
        } else {
            writeShort(position.getX());
            writeShort(position.getY());
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
        short op = byteBuf.readShortLE();
        return String.format("%s, %s/0x%s\t| %s", SendOpcode.getByValue(op), op, Integer.toHexString(op).toUpperCase()
                , Util.readableByteArray(Arrays.copyOfRange(getData(), 2, getData().length)));
    }
}
