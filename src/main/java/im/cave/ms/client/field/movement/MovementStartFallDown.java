package im.cave.ms.client.field.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementStartFallDown extends MovementBase {
    public MovementStartFallDown(InPacket in, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        short xv = in.readShort();
        short xy = in.readShort();
        vPosition = new Position(xv, xy);

        // I'm not sure about this, it' needs testing. <- mushy
        // Should be fallStart (foothold fall start)?
        footStart = in.readShort();

        moveAction = in.readByte();
        elapse = in.readShort();
        forcedStop = in.readByte();
    }

    @Override
    public void encode(OutPacket out) {
        out.write(getCommand());
        out.writePosition(getVPosition());
        out.writeShort(getFootStart());
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
