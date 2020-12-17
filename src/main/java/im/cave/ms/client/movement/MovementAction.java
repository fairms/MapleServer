package im.cave.ms.client.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



/**
 * Created on 1/2/2018.
 */
public class MovementAction extends MovementBase {
    public MovementAction(InPacket inPacket, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        moveAction = inPacket.readByte();
        elapse = inPacket.readShort();
        forcedStop = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.write(getMoveAction());
        outPacket.writeShort(getDuration());
        outPacket.write(getForcedStop());
    }

    @Override
    public void applyTo(MapleCharacter chr) {
        chr.setMoveAction(moveAction);
    }

    @Override
    public void applyTo(MapleMapObj life) {
        life.setMoveAction(moveAction);
    }


}
