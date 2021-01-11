package im.cave.ms.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 1/7 10:05
 */
public class Test {
    public static void main(String[] args) {
        long timestamp = DateUtil.getTimestamp(132551914159460000L);
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
        long l = LocalDateTime.of(2021, 4, 5, 22, 16, 39, 32).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println(l);
        System.out.println(DateUtil.getFileTime(l));
        long fileTime = DateUtil.getFileTime(System.currentTimeMillis());
        long timestamp2 = DateUtil.getTimestamp(fileTime);
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault()));

    }
}
