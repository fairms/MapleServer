package im.cave.ms.provider.info;

import lombok.Data;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 12/19 18:48
 */
@Data
public class FamiliarInfo {
    private int familiarId;
    private int mobId;
    private int range;
    private int skillId;
    private int effectAfter;
    private boolean boos;
}
