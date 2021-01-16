package im.cave.ms.constants;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.constant
 * @date 11/19 21:48
 */
public class ServerConstants {
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long ONE_DAY_TIMES = 60 * 60 * 24 * 1000L;
    public static final short VERSION = 177;
    public static final String PATH = "1";
    public static final int LOGIN_PORT = 8484;
    public static final int BCRYPT_ITERATIONS = 10;
    public static final String DIR = System.getProperty("user.dir");
    public static final String WZ_DIR = DIR + "/wz/" + VERSION;
    public static final String SCRIPT_DIR = DIR + "/scripts";
    public static final byte[] NEXON_IP = new byte[]{(byte) 0xDD, (byte) 0xE7, (byte) 0x82, (byte) 0x46};
    public final static byte[] DESKEY = new byte[]{0x4d, 0x40, 0x50, 0x6c, 0x65, 0x53, 0x74, 0x6f, 0x72, 0x79, 0x4d, 0x61, 0x50, 0x4c, 0x65, 0x21, 0x4d, 0x40, 0x50, 0x6c, 0x65, 0x53, 0x74, 0x6f};
}
