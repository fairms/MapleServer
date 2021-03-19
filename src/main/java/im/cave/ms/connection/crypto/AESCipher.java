package im.cave.ms.connection.crypto;

import im.cave.ms.constants.ServerConstants;

public class AESCipher {
    private final AES pCipher;

    public static final short nVersion = ServerConstants.VERSION;
    private static final byte[] aKey = new byte[]{
            (byte) 0xCD, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x87, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x5A, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x99, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xAD, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x67, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };


    public AESCipher() {
        pCipher = new AES();
        pCipher.setKey(aKey);
    }

    public void Crypt(byte[] aData, int pSrc) {
        byte[] pdwKey = new byte[]{
                (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };
        Crypt(aData, pdwKey);
    }

    public void Crypt(byte[] aData, byte[] aSeqKey) {
        int a = aData.length;
        int b = 0x5B0;
        int c = 0;
        while (a > 0) {
            byte[] d = multiplyBytes(aSeqKey, 4, 4);
            if (a < b) {
                b = a;
            }
            for (int e = c; e < (c + b); e++) {
                if ((e - c) % d.length == 0) {
                    pCipher.encrypt(d);
                }
                aData[e] ^= d[(e - c) % d.length];
            }
            c += b;
            a -= b;
            b = 0x5B4;
        }
    }

    public byte[] multiplyBytes(byte[] iv, int i, int i0) {
        byte[] ret = new byte[i * i0];
        for (int x = 0; x < ret.length; x++) {
            ret[x] = iv[x % i];
        }
        return ret;
    }
}
