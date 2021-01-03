package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



public class MovementNormal extends MovementBase {
    public MovementNormal(InPacket inPacket, byte command) {
        super();
        this.command = command;

        short x = inPacket.readShort();
        short y = inPacket.readShort();
        position = new Position(x, y);

        short xv = inPacket.readShort();
        short yv = inPacket.readShort();
        vPosition = new Position(xv, yv);

        fh = inPacket.readShort();

        if (command == 15 || command == 17) {
            footStart = inPacket.readShort();
        }

        short xoffset = inPacket.readShort();
        short yoffset = inPacket.readShort();
        offset = new Position(xoffset, yoffset);

        unk = inPacket.readShort();

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
        if (getCommand() == 15 || getCommand() == 17) {
            outPacket.writeShort(getFootStart());
        }
        outPacket.writePosition(getOffset());
        outPacket.writeShort(getUNK());
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
