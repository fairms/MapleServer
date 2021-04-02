package im.cave.ms.client.field;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.FieldAttackObjPacket;
import im.cave.ms.tools.Position;

public class FieldAttackObj extends MapleMapObj {

    private int ownerID;
    private int reserveID;

    public FieldAttackObj(int templateId) {
        super(templateId);
    }

    public FieldAttackObj(int templateId, int ownerID, Position position, boolean flip) {
        super(templateId);
        this.ownerID = ownerID;
        setPosition(position);
        setFlip(flip);
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getReserveID() {
        return reserveID;
    }

    public void setReserveID(int reserveID) {
        this.reserveID = reserveID;
    }


    public void encode(OutPacket out) {
        out.writeInt(getObjectId());
        out.writeInt(getTemplateId());
        out.writeInt(getOwnerID());
        out.writeInt(getReserveID());
        out.write(0);
        out.writePositionInt(getPosition());
        out.writeBool(isFlip());
    }


    @Override
    public void sendLeavePacket(MapleCharacter chr) {
        chr.announce(FieldAttackObjPacket.objRemoveList(getTemplateId(), getObjectId()));
    }

    @Override
    public void sendSpawnPacket(MapleCharacter chr) {
        chr.announce(FieldAttackObjPacket.objCreate(this));
    }

}
