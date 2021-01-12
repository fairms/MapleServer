package im.cave.ms.client.field.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;


public class MovementStatChange extends MovementBase {
    public MovementStatChange(InPacket in, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        this.stat = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.write(getStat());
    }

    @Override
    public void applyTo(MapleCharacter chr) {

    }

    @Override
    public void applyTo(MapleMapObj life) {

    }

}
