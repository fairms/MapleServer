package im.cave.ms.client.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementStartFallDown extends MovementBase {
    public MovementStartFallDown(InPacket inPacket, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        short xv = inPacket.readShort();
        short xy = inPacket.readShort();
        vPosition = new Position(xv, xy);

        // I'm not sure about this, it' needs testing. <- mushy
        // Should be fallStart (foothold fall start)?
        footStart = inPacket.readShort();

        moveAction = inPacket.readByte();
        elapse = inPacket.readShort();
        forcedStop = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.writePosition(getVPosition());
        outPacket.writeShort(getFootStart());
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
    public void applyTo(MapleMapObj life) {
        life.setPosition(getPosition());
        life.setVPosition(getVPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }

}
