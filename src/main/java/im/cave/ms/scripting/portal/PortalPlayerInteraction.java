package im.cave.ms.scripting.portal;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.Portal;
import im.cave.ms.scripting.AbstractPlayerInteraction;
import im.cave.ms.scripting.map.MapScriptManager;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.portal
 * @date 11/28 14:16
 */

public class PortalPlayerInteraction extends AbstractPlayerInteraction {
    private final Portal portal;

    public PortalPlayerInteraction(MapleClient c, Portal portal) {
        super(c);
        this.portal = portal;
    }

    public void runMapScript() {
        MapScriptManager msm = MapScriptManager.getInstance();
        msm.runMapScript(c, "onUserEnter/" + portal.getScript(), false);
    }

}
