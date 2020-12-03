package im.cave.ms.net.handler.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.net.packet.MobPacket;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/29 15:52
 */
public class MobHandler {
    private static final Logger log = LoggerFactory.getLogger(MobHandler.class);

    public static void handleMoveMob(SeekableLittleEndianAccessor slea, MapleClient c) {
        int objectId = slea.readInt();
        MapleMap map = c.getPlayer().getMap();
        MapleMapObj obj = map.getObj(objectId);
        if (!(obj instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) obj;
        short moveId = slea.readShort();
        boolean useSkill = (slea.readByte() & 0xFF) > 0;
        byte mode = (byte) (slea.readByte() >> 1);
        slea.skip(60);
        slea.skip(13);
        MovementInfo movementInfo = new MovementInfo(slea);
        movementInfo.applyTo(mob);
        c.announce(MobPacket.mobMoveResponse(objectId, moveId, useSkill, (int) mob.getMp(), 0, (short) 0));
    }
}
