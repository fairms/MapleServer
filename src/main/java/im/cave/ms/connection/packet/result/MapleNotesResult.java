package im.cave.ms.connection.packet.result;

import im.cave.ms.client.multiplayer.MapleNotes;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet.result
 * @date 1/20 22:39
 */
@Data
public class MapleNotesResult {
    private int arg1;
    private List<MapleNotes> notes;
    private MapleNotes note;
}
