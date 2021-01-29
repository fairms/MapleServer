package im.cave.ms.client.field;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.server.Server;
import im.cave.ms.connection.server.channel.MapleChannel;
import im.cave.ms.enums.PortalType;
import im.cave.ms.scripting.portal.PortalScriptManager;
import lombok.Getter;
import lombok.Setter;

;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.map
 * @date 11/25 9:08
 */
@Getter
@Setter
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
    private byte id;


    public Portal(byte id, PortalType type, String name, int targetMapId, String targetPortalName, int x, int y,
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

    public Portal(byte portalId) {
        id = portalId;
    }

    public void enterPortal(MapleClient c) {
        boolean changed = false;
        if (script.length() != 0) {
            PortalScriptManager psm = PortalScriptManager.getInstance();
            changed = psm.executePortalScript(this, c);
        } else if (targetMapId != 999999999) {
            MapleChannel channel = Server.getInstance().getWorldById(c.getWorldId()).getChannel(c.getChannelId());
            MapleMap toMap = channel.getMap(targetMapId);
            if (toMap == null) {
                return;
            }
            Portal toPortal = toMap.getPortal(getTargetPortalName());
            if (toPortal == null) {
                toPortal = toMap.getPortal("sp");
            }
            MapleCharacter player = c.getPlayer();
            player.changeMap(toMap, toPortal.getId());
        }

        if (!changed) {
            c.announce(UserPacket.enableActions());
        }
    }
}
