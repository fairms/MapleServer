package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;


public class MovementAngle extends MovementBase {
    public MovementAngle(InPacket inPacket, byte command) {
        super();
        this.command = command;

        short x = inPacket.readShort();
        short y = inPacket.readShort();
        position = new Position(x, y);

        short xv = inPacket.readShort();
        short xy = inPacket.readShort();
        vPosition = new Position(xv, xy);

        fh = inPacket.readShort();

        moveAction = inPacket.readByte();
        elapse = inPacket.readShort();
        forcedStop = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.writePosition(getPosition());
        outPacket.writePosition(getVPosition());
        outPacket.writeShort(getFh());
        outPacket.write(getMoveAction());
        outPacket.writeShort(getDuration());
        outPacket.write(getForcedStop());
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
