package im.cave.ms.client;

import im.cave.ms.connection.netty.OutPacket;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 1/20 10:50
 */
@Data
@Entity
@Table(name = "ht_reward")
public class HotTimeReward {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int sort; //? 未知的
    private int type; //1 物品 3 MaplePoint
    private long receivedTime;
    private long expiredTime;
    private int itemId;
    private int quantity;
    private int meso;
    private int maplePoint;
    private int exp;
    private String msg;


    public void encode(OutPacket out) {
        out.writeInt(0);
        out.writeInt(getSort()); //11 12 13 14
        out.writeLong(getReceivedTime());
        out.writeLong(getExpiredTime());
        out.writeInt(getType());
        out.writeInt(getItemId());
        out.writeInt(getQuantity());
        out.writeInt(0);
        out.writeLong(MAX_TIME);
        out.writeInt(0);
        out.writeInt(getMaplePoint());
        out.writeZeroBytes(26);
        out.writeMapleAsciiString(getMsg());
    }
}
