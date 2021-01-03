package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementTeleport extends MovementBase {
    public MovementTeleport(InPacket inPacket, byte command) {
        super();
        this.command = command;

        short x = inPacket.readShort();
        short y = inPacket.readShort();
        position = new Position(x, y);

        fh = inPacket.readShort();
        inPacket.readInt(); // unk
        moveAction = inPacket.readByte();
        elapse = inPacket.readShort();
        forcedStop = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.writePosition(getPosition());
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
    public void applyTo(MapleMapObj life) {
        life.setPosition(getPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }

}
