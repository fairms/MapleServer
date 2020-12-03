package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Created on 1/2/2018.
 */
public class MovementTeleport extends MovementBase {
    public MovementTeleport(SeekableLittleEndianAccessor slea, byte command) {
        super();
        this.command = command;

        short x = slea.readShort();
        short y = slea.readShort();
        position = new Position(x, y);

        fh = slea.readShort();

        moveAction = slea.readByte();
        elapse = slea.readShort();
        forcedStop = slea.readByte();
    }

    @Override
    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.write(getCommand());
        mplew.writePos(getPosition());
        mplew.writeShort(getFh());
        mplew.write(getMoveAction());
        mplew.writeShort(getDuration());
        mplew.write(getForcedStop());
    }

    @Override
    public void applyTo(MapleCharacter chr) {
        chr.setPosition(getPosition());
        chr.setFoothold(getFh());
        chr.setMoveAction(getMoveAction());
    }

    @Override
    public void applyTo(MapleMapObj life) {
        life.setPosition(getPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }

}
