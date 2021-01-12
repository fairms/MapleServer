package im.cave.ms.client.field.obj.npc.shop;


import im.cave.ms.connection.netty.OutPacket;

import java.util.HashSet;
import java.util.Set;


public class BuyLimitInfo {

    private int type;
    private Set<Long> dates = new HashSet<>();

    public void encode(OutPacket out) {
        out.write(getType());
        if (getType() == 1 || getType() == 3 || getType() == 4) {
            out.writeInt(getDates().size());
            for (Long ft : getDates()) {
                out.writeLong(ft);
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
