package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.net.netty.InPacket;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.tools.Position;



/**
 * Created on 1/2/2018.
 */
public class MovementFlyingBlock extends MovementBase {
    public MovementFlyingBlock(InPacket inPacket, byte command) {
        super();
        this.command = command;

        short x = inPacket.readShort();
        short y = inPacket.readShort();
        position = new Position(x, y);

        short vx = inPacket.readShort();
        short vy = inPacket.readShort();
        vPosition = new Position(vx, vy);

        moveAction = inPacket.readByte();
        elapse = inPacket.readShort();
        forcedStop = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.writePos(getPosition());
        outPacket.writePos(getVPosition());
        outPacket.write(getMoveAction());
        outPacket.writeShort(getDuration());
        outPacket.write(getForcedStop());
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
