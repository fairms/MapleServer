package im.cave.ms.client.character.potential;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.network.packet.PlayerPacket;
import im.cave.ms.tools.Util;

import java.util.Set;

/**
 * Created on 5/27/2018.
 */
public class CharacterPotentialMan {

    private final MapleCharacter chr;

    public CharacterPotentialMan(MapleCharacter chr) {
        this.chr = chr;
    }

    private Set<CharacterPotential> getPotentials() {
        return chr.getPotentials();
    }

    public CharacterPotential getPotentialByKey(byte key) {
        return getPotentials().stream().filter(pot -> pot.getKey() == key).findAny().orElse(null);
    }

    /**
     * Adds a potential to the char's potential list. Will override the old one with the same key if one exists.
     * Also sends a packet to the client to indicate the change.
     *
     * @param potential The potential to add
     */
    public void addPotential(CharacterPotential potential) {
        getPotentials().add(potential);
        chr.announce(PlayerPacket.characterPotentialSet(potential));
    }

    /**
     * Removes a potential from the char's potential list by key. Will do nothing if there is no such potential.
     * Also sends a packet to the client to indicate the change.
     *
     * @param key the potential's key to remove
     */
    public void removePotential(byte key) {
        CharacterPotential cp = getPotentialByKey(key);
        if (cp != null) {
            getPotentials().remove(cp);
//            chr.announce(WvsContext.characterPotentialReset(PotentialResetType.Pos, cp.getKey()));
        }
    }

    /**
     * Returns the current grade of a Char's potential, which is equivalent to the highest potential of the Char.
     *
     * @return the current grade of a Char's potential
     */
    public byte getGrade() {
        int max = 0;
        for (CharacterPotential cp : getPotentials()) {
            if (cp.getGrade() > max) {
                max = cp.getGrade();
            }
        }
        return (byte) max;
    }

    /**
     * Generates a new CharacterPotential, based off of the current grade.
     *
     * @param key the key (line number, 1-3) the generated potential should have
     * @return the generated CharacterPotential.
     */
    public CharacterPotential generateRandomPotential(byte key) {
        // slv are 1-40 inclusive, split up into 4 "tiers" of 10 slv per grade (0-3 inclusive)
        byte grade = getGrade();
        int baseSlv = grade * 10;
        int maxSlv = baseSlv + 10;
        int slv = 1 + Util.getRandom(baseSlv, maxSlv);
        int skillID = Util.getRandom(GameConstants.CHAR_POT_BASE_ID, GameConstants.CHAR_POT_END_ID + 1);
        return new CharacterPotential(key, skillID, (byte) slv, grade);
    }

}
