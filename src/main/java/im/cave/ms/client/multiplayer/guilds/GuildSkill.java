package im.cave.ms.client.multiplayer.guilds;

import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.multiplayer.guilds
 * @date 2/3 14:39
 */
@Entity
@Table(name = "guild_skill")
@Getter
@Setter
public class GuildSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int skillId;
    private byte level;
    private long expireDate;
    private String buyCharName;
    private String extendCharName;


    public void encode(OutPacket out) {
        out.writeShort(getLevel());
        out.writeLong(getExpireDate());
        out.writeMapleAsciiString(getBuyCharName());
        out.writeMapleAsciiString(getExtendCharName());
    }
}
