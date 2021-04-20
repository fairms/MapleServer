package im.cave.ms.connection.packet;

import im.cave.ms.client.field.AffectedArea;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.constants.SkillConstants;

public class FieldPacket {
    /*
     神之子 时光扭曲 28 A1 07 00 00 00 00 00 70 21 00 00 ED E5 F5 05 01 00 00 00 9E FC FF FF C6 FF FF FF 00 FF FF FF 98 00 00 00 00 00 00 00 D4 FD 8E 00 00 00 00 00 00 00 00 00 00 EB 00 00 00 A0 8C 00 00 00 00 00 00 00 00 00 00 01 00
     */
    public static OutPacket affectedAreaCreated(AffectedArea aa) {
        OutPacket out = new OutPacket(SendOpcode.AFFECTED_AREA_CREATED);
        out.writeInt(aa.getObjectId());
        out.writeInt(aa.getMobOrigin());//type 2 = invincible, so put 1 for recovery aura
        out.writeInt(aa.getCharID());
        out.writeInt(aa.getSkillID());
        out.writeShort(aa.getSlv());
        out.writeShort(aa.getDelay());
        out.writeRect(aa.getRect());
        out.writeInt(aa.getElemAttr()); //subType
        out.writePosition(aa.getPosition());
        out.writeInt(aa.getForce());
        out.writeInt(aa.getOption());
        out.writeBool(aa.getOption() != 0);
        out.writeInt(aa.getIdk()); // ?
        out.writeInt(aa.getDuration());
        if (SkillConstants.isFlipAffectAreaSkill(aa.getSkillID())) {
            out.writeBool(aa.isFlip());
        }
        out.writeInt(0);
        out.writeInt(0);
        out.write(1); // ?
        out.write(0);


        return out;
    }

    public static OutPacket affectedAreaRemoved(AffectedArea aa, boolean mistEruption) {
        OutPacket out = new OutPacket(SendOpcode.AFFECTED_AREA_REMOVED);

        out.writeInt(aa.getObjectId());
        out.writeBool(mistEruption);


        return out;

    }
}
