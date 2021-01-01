package im.cave.ms.client.party;


import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;

import static im.cave.ms.constants.GameConstants.NO_MAP_ID;

/**
 * Created on 3/19/2018.
 */
public class TownPortal {

    private int townID = NO_MAP_ID;
    private int fieldID = NO_MAP_ID;
    private int skillID;
    private Position fieldPortal;

    public int getTownID() {
        return townID;
    }

    public void setTownID(int townID) {
        this.townID = townID;
    }

    public int getFieldID() {
        return fieldID;
    }

    public void setFieldID(int fieldID) {
        this.fieldID = fieldID;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public Position getFieldPortal() {
        return fieldPortal;
    }

    public void setFieldPortal(Position fieldPortal) {
        this.fieldPortal = fieldPortal;
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getTownID());
        outPacket.writeInt(getFieldID());
        outPacket.writeInt(getSkillID());
        outPacket.writePosition(getFieldPortal());
    }

}
