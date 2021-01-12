package im.cave.ms.client.field.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.tools.Position;


public interface Movement {
    void encode(OutPacket out);

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
