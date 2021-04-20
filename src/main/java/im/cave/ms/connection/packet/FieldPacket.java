package im.cave.ms.connection.packet;

import im.cave.ms.client.field.AffectedArea;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.constants.SkillConstants;

public class FieldPacket {
    public static OutPacket affectedAreaCreated(AffectedArea aa) {
        OutPacket out = new OutPacket(SendOpcode.AFFECTED_AREA_CREATED);
        out.writeInt(aa.getObjectId());
        out.write(aa.getMobOrigin());
        out.writeInt(aa.getCharID());
        out.writeInt(aa.getSkillID());
        out.writeShort(aa.getSlv());
        out.writeShort(aa.getDelay());
//        aa.getRect().encode(outPacket);
        out.writeInt(aa.getElemAttr());
        out.writeInt(aa.getElemAttr()); // ?
        out.writePosition(aa.getPosition());
        out.writeInt(aa.getForce());
        out.writeInt(aa.getOption());
        out.writeBool(aa.getOption() != 0);
        out.writeInt(aa.getIdk()); // ?

        if (SkillConstants.isFlipAffectAreaSkill(aa.getSkillID())) {
            out.writeBool(aa.isFlip());
        }
        out.write(0); // ?
        out.write(0);


        return out;
    }

    public static OutPacket affectedAreaRemoved(AffectedArea aa, boolean mistEruption) {
        OutPacket out = new OutPacket(SendOpcode.AFFECTED_AREA_REMOVED);

        out.writeInt(aa.getObjectId());
        if (aa.getSkillID() == 2111003) {
            out.writeBool(mistEruption);
        }

        return out;

    }
}
