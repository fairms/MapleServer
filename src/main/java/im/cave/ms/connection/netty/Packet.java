package im.cave.ms.connection.netty;

import im.cave.ms.tools.Util;

public class Packet implements Cloneable {

    private byte[] data;

    public Packet(byte[] data) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public int getLength() {
        if (data != null) {
            return data.length;
        }
        return 0;
    }

    public int getHeader() {
        if (data.length < 2) {
            return 0xFFFF;
        }
        return (data[0] + (data[1] << 8));
    }

    public void release() {
        this.data = null;
    }

    @Override
    public String toString() {
        if (data == null) return "";
        return "[Packet] | " + Util.readableByteArray(data);
    }

    @Override
    public Packet clone() {
        return new Packet(data);
    }

    public byte[] getData() {
        return data;

    }

    public void setData(byte[] data) {
        this.data = data;
    }
}