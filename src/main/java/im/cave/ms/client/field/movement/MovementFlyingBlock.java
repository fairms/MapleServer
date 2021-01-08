package im.cave.ms.client.field.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;

public class MovementFlyingBlock extends MovementBase {
    public MovementFlyingBlock(InPacket in, byte command) {
        super();
        this.command = command;

        short x = in.readShort();
        short y = in.readShort();
        position = new Position(x, y);

        short vx = in.readShort();
        short vy = in.readShort();
        vPosition = new Position(vx, vy);

        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.writePosition(getPosition());
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
