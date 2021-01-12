package im.cave.ms.client.field.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementNormal extends MovementBase {
    public MovementNormal(InPacket in, byte command) {
        super();
        this.command = command;

        short x = in.readShort();
        short y = in.readShort();
        position = new Position(x, y);

        short xv = in.readShort();
        short yv = in.readShort();
        vPosition = new Position(xv, yv);

        fh = in.readShort();

        if (command == 15 || command == 17) {
            footStart = in.readShort();
        }

        short xoffset = in.readShort();
        short yoffset = in.readShort();
        offset = new Position(xoffset, yoffset);

        unk = in.readShort();

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
        if (getCommand() == 15 || getCommand() == 17) {
            out.writeShort(getFootStart());
        }
        out.writePosition(getOffset());
        out.writeShort(getUNK());
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
    public void applyTo(MapleMapObj life) {
        life.setPosition(getPosition());
        life.setVPosition(getVPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }

}
