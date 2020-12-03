package im.cave.ms.client.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Created on 1/2/2018.
 */
public class MovementJump extends MovementBase {

    public MovementJump(SeekableLittleEndianAccessor slea, byte command) {
        super();
        this.command = command;

        short vx = slea.readShort();
        short vy = slea.readShort();
        position = new Position(vx, vy);

        if (command == 21 || command == 22) {
            footStart = slea.readShort();
        }
        if (command == 60) {
            short xoffset = slea.readShort();
            short yoffset = slea.readShort();
            offset = new Position(xoffset, yoffset);
        }
        moveAction = slea.readByte();
        elapse = slea.readShort();
        forcedStop = slea.readByte();
    }

    @Override
    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.write(getCommand());
        mplew.writePos(getVPosition());
        if (getCommand() == 21 || getCommand() == 22) {
            mplew.writeShort(getFootStart());
        }
        if (getCommand() == 60) {
            mplew.writePos(getOffset());
        }
        mplew.write(getMoveAction());
        mplew.writeShort(getDuration());
        mplew.write(getForcedStop());
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
