package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Created on 1/2/2018.
 */
public class MovementFlyingBlock extends MovementBase {
    public MovementFlyingBlock(SeekableLittleEndianAccessor slea, byte command) {
        super();
        this.command = command;

        short x = slea.readShort();
        short y = slea.readShort();
        position = new Position(x, y);

        short vx = slea.readShort();
        short vy = slea.readShort();
        vPosition = new Position(vx, vy);

        moveAction = slea.readByte();
        elapse = slea.readShort();
        forcedStop = slea.readByte();
    }

    @Override
    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.write(getCommand());
        mplew.writePos(getPosition());
        mplew.writePos(getVPosition());
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
    public void applyTo(MapleMapObj life) {
        life.setPosition(getPosition());
        life.setVPosition(getVPosition());
        life.setMoveAction(getMoveAction());
    }

}
