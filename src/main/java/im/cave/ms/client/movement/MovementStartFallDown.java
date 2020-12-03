package im.cave.ms.client.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Created on 1/2/2018.
 */
public class MovementStartFallDown extends MovementBase {
    public MovementStartFallDown(SeekableLittleEndianAccessor slea, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        short xv = slea.readShort();
        short xy = slea.readShort();
        vPosition = new Position(xv, xy);

        // I'm not sure about this, it' needs testing. <- mushy
        // Should be fallStart (foothold fall start)?
        footStart = slea.readShort();

        moveAction = slea.readByte();
        elapse = slea.readShort();
        forcedStop = slea.readByte();
    }

    @Override
    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.write(getCommand());
        mplew.writePos(getVPosition());
        mplew.writeShort(getFootStart());
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
        life.setVPosition(getVPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }

}
