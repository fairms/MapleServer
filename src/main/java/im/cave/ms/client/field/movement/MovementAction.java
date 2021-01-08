package im.cave.ms.client.field.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementAction extends MovementBase {
    public MovementAction(InPacket in, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.write(getMoveAction());
        out.writeShort(getDuration());
        out.write(getForcedStop());
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
