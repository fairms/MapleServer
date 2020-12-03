package im.cave.ms.client.character;

import im.cave.ms.enums.MapleTraitType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 11/21 16:42
 */
@Data
@Entity
public class MapleTrait {
    private MapleTraitType type;
    private int totalExp = 0, localTotalExp = 0;
    private short exp = 0;
    private byte level = 0;
    private Integer charId;

    public MapleTrait() {

    }

    public MapleTrait(MapleTraitType type) {
        this.type = type;
    }

    public void setCharId(Integer charId) {
        this.charId = charId;
    }

    @Id
    public Integer getCharId() {
        return charId;
    }
}
