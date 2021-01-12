package im.cave.ms.client.field.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;


public class MovementJump extends MovementBase {

    public MovementJump(InPacket in, byte command) {
        super();
        this.command = command;

        short vx = in.readShort();
        short vy = in.readShort();
        position = new Position(vx, vy);

        if (command == 21 || command == 22) {
            footStart = in.readShort();
        }
        if (command == 60) {
            short xoffset = in.readShort();
            short yoffset = in.readShort();
            offset = new Position(xoffset, yoffset);
        }
        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.writePosition(getVPosition());
        if (getCommand() == 21 || getCommand() == 22) {
            out.writeShort(getFootStart());
        }
        if (getCommand() == 60) {
            out.writePosition(getOffset());
        }
        out.write(getMoveAction());
        out.writeShort(getDuration());
        out.write(getForcedStop());
    }

    @Override
    public void applyTo(MapleCharacter chr) {
        chr.setPosition(getPosition());
        chr.setMoveAction(getMoveAction());
    }

    @Override
    public void applyTo(MapleMapObj obj) {
        obj.setPosition(getPosition());
        obj.setMoveAction(getMoveAction());
    }

}
