package im.cave.ms.client.field.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementTeleport extends MovementBase {
    public MovementTeleport(InPacket in, byte command) {
        super();
        this.command = command;

        short x = in.readShort();
        short y = in.readShort();
        position = new Position(x, y);

        fh = in.readShort();
        in.readInt(); // unk
        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.writePosition(getPosition());
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
    public void applyTo(MapleMapObj life) {
        life.setPosition(getPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }

}
