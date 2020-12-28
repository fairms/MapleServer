package im.cave.ms.client.field.obj.npc.shop;


import im.cave.ms.network.netty.OutPacket;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 3/27/2018.
 */
public class BuyLimitInfo {

    private int type;
    private Set<Long> dates = new HashSet<>();

    public void encode(OutPacket outPacket) {
        outPacket.write(getType());
        if (getType() == 1 || getType() == 3 || getType() == 4) {
            outPacket.writeInt(getDates().size());
            for (Long ft : getDates()) {
                outPacket.writeLong(ft);
            }
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<Long> getDates() {
        return dates;
    }

    public void addDate(Long fileTime) {
        getDates().add(fileTime);
    }

    public void removeDate(Long fileTime) {
        getDates().remove(fileTime);
    }
}
