package im.cave.ms.connection.packet;

import im.cave.ms.client.field.FieldAttackObj;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 4/2 10:42
 */
public class FieldAttackObjPacket {
    public static OutPacket objCreate(FieldAttackObj fao) {
        OutPacket out = new OutPacket(SendOpcode.FIELD_ATTACK_CREATE);

        fao.encode(out);

        return out;
    }

    public static OutPacket setAttack(int objectId, int attackIdx) {
        OutPacket out = new OutPacket(SendOpcode.FIELD_ATTACK_SET_ATTACK);

        out.writeInt(objectId);
        out.writeInt(attackIdx);

        return out;
    }

    public static OutPacket objRemoveByKey(int objectID) {
        OutPacket out = new OutPacket(SendOpcode.FIELD_ATTACK_REMOVE_BY_KEY);

        out.writeInt(objectID);

        return out;
    }

    public static OutPacket objRemoveList(int templateId, int objId) {
        OutPacket out = new OutPacket(SendOpcode.FIELD_ATTACK_REMOVE_LIST);

        out.writeInt(templateId);
        out.writeInt(objId);

        return out;
    }
}
