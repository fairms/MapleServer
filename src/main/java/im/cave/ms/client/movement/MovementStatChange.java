package im.cave.ms.client.movement;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;



/**
 * Created on 1/2/2018.
 */
public class MovementStatChange extends MovementBase {
    public MovementStatChange(InPacket inPacket, byte command) {
        super();
        this.command = command;
        this.position = new Position(0, 0);

        this.stat = inPacket.readByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.write(getCommand());
        outPacket.write(getStat());
    }

    @Override
    public void applyTo(MapleCharacter chr) {

    }

    @Override
    public void applyTo(MapleMapObj life) {

    }

}
