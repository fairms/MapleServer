package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.mob.MobSkillAttackInfo;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.MobPacket;
import im.cave.ms.tools.Position;
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

    //todo
    public static void handleMobMove(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int objectId = in.readInt();
        MapleMap map = player.getMap();
        MapleMapObj obj = map.getObj(objectId);
        if (!(obj instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) obj;
        if (mob.getHp() == 0) {
            return;
        }
        MobSkillAttackInfo msai = new MobSkillAttackInfo();
        short moveId = in.readShort();
        msai.actionAndDirMask = in.readByte();
        byte action = in.readByte(); //FF
        msai.action = (byte) (action >> 1);
        mob.setMoveAction(action);
//        int skillId = msai.action - 30;
//        int skillSN = skillId;
        int slv = 0;
        msai.targetInfo = in.readLong();
        in.read(6);
        boolean useSkill = action != -1;
        byte multiTargetForBallSize = in.readByte();
        for (int i = 0; i < multiTargetForBallSize; i++) {
            Position pos = in.readPos(); // list of ball positions
            msai.multiTargetForBalls.add(pos);
        }
        in.skip(18);
        MovementInfo movementInfo = new MovementInfo(in);
        movementInfo.applyTo(mob);
        player.announce(MobPacket.mobCtrlAck(objectId, moveId, useSkill, (int) mob.getMp(), 0, (short) 0));
        player.getMap().broadcastMessage(player, MobPacket.moveMobRemote(mob, msai, movementInfo), false);
    }
}