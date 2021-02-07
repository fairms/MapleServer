package im.cave.ms.client.character.job.adventurer;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Option;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;

import java.util.Arrays;

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.Booster;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.ElementalReset;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.IndieMAD;
import static im.cave.ms.client.character.temp.CharacterTemporaryStat.MagicGuard;
import static im.cave.ms.enums.SkillStat.indieMad;
import static im.cave.ms.enums.SkillStat.time;
import static im.cave.ms.enums.SkillStat.x;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.job.adventurer
 * @date 12/28 14:25
 */
public class Archer extends Beginner {
    public Archer(MapleCharacter chr) {
        super(chr);
    }
}
