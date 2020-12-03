package im.cave.ms.client.field;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.PortalType;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.scripting.portal.PortalScriptManager;
import lombok.Data;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.map
 * @date 11/25 9:08
 */
@Data
public class Portal {
    private PortalType type;
    private String name = "";
    private int targetMapId;
    private String targetPortalName = "";
    private int x;
    private int y;
    private int horizontalImpact;
    private int verticalImpact;
    private String script = "";
    private boolean onlyOnce;
    private boolean hideTooltip;
    private int delay;
    private int id;


    public Portal(int id, PortalType type, String name, int targetMapId, String targetPortalName, int x, int y,
                  int horizontalImpact, int verticalImpact, String script, boolean onlyOnce, boolean hideTooltip,
                  int delay) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.targetMapId = targetMapId;
        this.targetPortalName = targetPortalName;
        this.x = x;
        this.y = y;
        this.horizontalImpact = horizontalImpact;
        this.verticalImpact = verticalImpact;
        this.script = script;
        this.onlyOnce = onlyOnce;
        this.hideTooltip = hideTooltip;
        this.delay = delay;
    }

    public Portal(int portalId) {
        id = portalId;
    }

    public void enterPortal(MapleClient c) {
        boolean changed = false;
        if (script.length() != 0) {
            PortalScriptManager psm = PortalScriptManager.getInstance();
            changed = psm.executePortalScript(this, c);
        } else if (targetMapId != 999999999) {
            MapleChannel channel = Server.getInstance().getWorldById(c.getWorld()).getChannel(c.getChannel());
            MapleMap toMap = channel.getMap(targetMapId);
            if (toMap == null) {
                return;
            }
            Portal toPortal = toMap.getPortal(getTargetPortalName());
            if (toPortal == null) {
                toPortal = toMap.getPortal("sp");
            }
            MapleCharacter player = c.getPlayer();
            player.changeMap(toMap, toPortal);
        }

        if (!changed) {
            c.announce(MaplePacketCreator.enableActions());
        }
    }
}
