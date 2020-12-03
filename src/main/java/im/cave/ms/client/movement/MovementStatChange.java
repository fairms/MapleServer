package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Created on 1/2/2018.
 */
public class MovementStatChange extends MovementBase {
    public MovementStatChange(SeekableLittleEndianAccessor slea, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        this.stat = slea.readByte();
    }

    @Override
    public void encode(MaplePacketLittleEndianWriter mplew) {
        mplew.write(getCommand());
        mplew.write(getStat());
    }

    @Override
    public void applyTo(MapleCharacter chr) {

    }

    @Override
    public void applyTo(MapleMapObj life) {

    }

}
