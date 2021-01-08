package im.cave.ms.client.field.movement;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MovementInfo {
    private static final Logger log = LoggerFactory.getLogger(MovementInfo.class);

    private long encodedGatherDuration;
    private Position oldPos;
    private Position oldVPos;
    private List<Movement> movements = new ArrayList<>();

    public MovementInfo(Position oldPos, Position oldVPos) {
        this.oldPos = oldPos;
        this.oldVPos = oldVPos;
    }

    public MovementInfo(InPacket in) {
        decode(in);
    }

    public void applyTo(MapleCharacter chr) {
        for (Movement m : getMovements()) {
            m.applyTo(chr);
        }
    }

    public void applyTo(MapleMapObj obj) {
        for (Movement m : getMovements()) {
            m.applyTo(obj);
        }
    }

//    public void applyTo(Dragon dragon) {
//        for (Movement m : getMovements()) {
//            m.applyTo(dragon);
//        }
//    }

    public void decode(InPacket in) {
        encodedGatherDuration = in.readLong();
        oldPos = in.readPos();
        oldVPos = in.readPos();
        movements = parseMovement(in);
    }


    public void encode(OutPacket out) {
        out.writeLong(encodedGatherDuration);
        out.writePosition(oldPos);
        out.writePosition(oldVPos);
        out.write(movements.size());
        for (Movement m : movements) {
            m.encode(out);
        }
    }

    private static List<Movement> parseMovement(InPacket in) {
        // Taken from mushy when my IDA wasn't able to show this properly
        // Made by Maxcloud
        List<Movement> res = new ArrayList<>();
        byte size = in.readByte();
        for (int i = 0; i < size; i++) {
            byte type = in.readByte();
            switch (type) {
                case 0:
                case 8:
                case 15:
                case 17:
                case 19:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 92:
                    res.add(new MovementNormal(in, type));
                    break;
                case 1:
                case 2:
                case 18:
                case 21:
                case 22:
                case 24:
                case 28:
                case 60:
                case 62:
                case 64:
                case 65:
                case 66:
                case 67:
                    res.add(new MovementJump(in, type));
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 26:
                case 27:
                case 52:
                case 53:
                case 54:
                case 63:
                case 81:
                case 82:
                case 83:
                case 85:
                case 87:
                case 96:
                    res.add(new MovementTeleport(in, type));
                    break;
                case 12:
                    res.add(new MovementStatChange(in, type));
                    break;
                case 14:
                case 16:
                    res.add(new MovementStartFallDown(in, type));
                    break;
                case 23:
                    res.add(new MovementFlyingBlock(in, type));
                    break;
                case 29:
                    res.add(new MovementUNK(in, type));
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 50:
                case 51:
                case 55:
                case 57:
                case 58:
                case 59:
                case 61:
                case 75:
                case 76:
                case 77:
                case 79:
                case 84:
                case 86:
                case 88:
                case 89:
                case 90:
                case 91:
                    res.add(new MovementAction(in, type));
                    break;
                case 56:
                case 68:
                case 95:
                    res.add(new MovementAngle(in, type)); // probably not a good name
                    break;
                default:
                    log.warn(String.format("Unhandled move path attribute %s.", type));
                    break;
            }
        }
        return res;
    }

    public List<Movement> getMovements() {
        return movements;
    }
}
