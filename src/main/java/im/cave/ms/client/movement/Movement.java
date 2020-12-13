package im.cave.ms.client.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.tools.Position;


/**
 * Created on 1/2/2018.
 * These classes + children/parents are basically the same as Mushy, credits to @MaxCloud.
 */
public interface Movement {
    void encode(OutPacket outPacket);

    Position getPosition();

    byte getCommand();

    byte getMoveAction();

    byte getForcedStop();

    byte getStat();

    short getFh();

    short getFootStart();

    short getDuration();

    Position getVPosition();

    Position getOffset();

    void applyTo(MapleCharacter chr);

    void applyTo(MapleMapObj obj);

}
