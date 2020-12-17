package im.cave.ms.scripting.item;

import im.cave.ms.client.MapleClient;
import im.cave.ms.scripting.AbstractPlayerInteraction;
import im.cave.ms.tools.Randomizer;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.portal
 * @date 11/28 14:16
 */

public class ItemScriptAction extends AbstractPlayerInteraction {

    private final int itemId;
    private final int npcId;

    public ItemScriptAction(MapleClient c, int itemId, int npcId) {
        super(c);
        this.itemId = itemId;
        this.npcId = npcId;
    }

    public void addHonerPoint() {
        switch (itemId) {
            case 2431174:
                c.getPlayer().addHonerPoint(Randomizer.rand(20, 120));
                break;
            case 9999999:
                //todo
                break;
            default:
                break;
        }
    }
}
