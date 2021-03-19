package im.cave.ms.client.character;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 3/18 11:11
 */
@Getter
@Setter
public class PortableChair {
    int itemId;
    Set<Integer> charId;
    boolean hasPortableMsg;
    String portableMsg;
    int arg1;
    int arg2;
    int arg3;
    int arg4;
}
