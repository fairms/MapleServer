package im.cave.ms.client.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



/**
 * Created on 1/2/2018.
 */
public class MovementJump extends MovementBase {

    public MovementJump(InPacket inPacket, byte command) {
        super();
        this.command = command;

        short vx = inPacket.readShort();
        short vy = inPacket.readShort();
        position = new Position(vx, vy);

        if (command == 21 || command == 22) {
            footStart = inPacket.readShort();
        }
        if (command == 60) {
            short xoffset = inPacket.readShort();
            short yoffset = inPacket.readShort();
            offset = new Position(xoffset, yoffset);
        }
        moveAction = inPacket.readByte();
        elapse = inPacket.readShort();
        forcedStop = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.writePos(getVPosition());
        if (getCommand() == 21 || getCommand() == 22) {
            outPacket.writeShort(getFootStart());
        }
        if (getCommand() == 60) {
            outPacket.writePos(getOffset());
        }
        outPacket.write(getMoveAction());
        outPacket.writeShort(getDuration());
        outPacket.write(getForcedStop());
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
