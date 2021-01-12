package im.cave.ms.client.field.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;


public class MovementAngle extends MovementBase {
    public MovementAngle(InPacket in, byte command) {
        super();
        this.command = command;

        short x = in.readShort();
        short y = in.readShort();
        position = new Position(x, y);

        short xv = in.readShort();
        short xy = in.readShort();
        vPosition = new Position(xv, xy);

        fh = in.readShort();

        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.writePosition(getPosition());
        out.writePosition(getVPosition());
        out.writeShort(getFh());
        out.write(getMoveAction());
        out.writeShort(getDuration());
        out.write(getForcedStop());
    }

    @Override
    public void applyTo(MapleCharacter chr) {
        chr.setPosition(getPosition());
        chr.setFoothold(getFh());
        chr.setMoveAction(getMoveAction());
    }

    @Override
    public void applyTo(MapleMapObj obj) {
        obj.setPosition(getPosition());
        obj.setVPosition(getVPosition());
        obj.setFh(getFh());
        obj.setMoveAction(getMoveAction());
    }

}
