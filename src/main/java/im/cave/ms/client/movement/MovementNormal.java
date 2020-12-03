package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Created on 1/2/2018.
 * These classes + children/parents are basically the same as Mushy, credits to @MaxCloud.
 */
public class MovementNormal extends MovementBase {
    public MovementNormal(SeekableLittleEndianAccessor slea, byte command) {
        super();
        this.command = command;

        short x = slea.readShort();
        short y = slea.readShort();
        position = new Position(x, y);

        short xv = slea.readShort();
        short yv = slea.readShort();
        vPosition = new Position(xv, yv);

        fh = slea.readShort();

        if (command == 15 || command == 17) {
            footStart = slea.readShort();
        }

        short xoffset = slea.readShort();
        short yoffset = slea.readShort();
        offset = new Position(xoffset, yoffset);

        unk = slea.readShort();

        moveAction = slea.readByte();
        elapse = slea.readShort();
        forcedStop = slea.readByte();
    }

    @Override
    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.write(getCommand());
        mplew.writePos(getPosition());
        mplew.writePos(getVPosition());
        mplew.writeShort(getFh());
        if (getCommand() == 15 || getCommand() == 17) {
            mplew.writeShort(getFootStart());
        }
        mplew.writePos(getOffset());
        mplew.writeShort(getUNK());
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
