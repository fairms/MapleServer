package im.cave.ms.client.field.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;



/**
 * Created by MechAviv on 2/2/2019.
 */
public class MovementUNK extends MovementBase {
    public MovementUNK(InPacket in, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        short xv = in.readShort();
        short xy = in.readShort();
        vPosition = new Position(xv, xy);

        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.writePosition(getVPosition());
        out.write(getMoveAction());
        out.writeShort(getDuration());
        out.write(getForcedStop());
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
