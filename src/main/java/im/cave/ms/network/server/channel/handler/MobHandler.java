package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.MobPacket;
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

    public static void handleMobMove(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int objectId = inPacket.readInt();
        MapleMap map = player.getMap();
        MapleMapObj obj = map.getObj(objectId);
        if (!(obj instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) obj;
        if (mob.getHp() == 0) {
            return;
        }
        short moveId = inPacket.readShort();
        boolean useSkill = (inPacket.readByte() & 0xFF) > 0;
        byte mode = (byte) (inPacket.readByte() >> 1);
        inPacket.skip(60);
        inPacket.skip(13);
        MovementInfo movementInfo = new MovementInfo(inPacket);
        movementInfo.applyTo(mob);
        map.broadcastMessage(player,
                MobPacket.moveMob(objectId, moveId, useSkill, (int) mob.getMp(), 0, (short) 0),
                true);
    }
}
