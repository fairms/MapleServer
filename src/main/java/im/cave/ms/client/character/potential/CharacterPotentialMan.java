package im.cave.ms.client.character.potential;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.CharPotGrade;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.Randomizer;
import im.cave.ms.tools.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CharacterPotentialMan {

    private final MapleCharacter chr;

    public CharacterPotentialMan(MapleCharacter chr) {
        this.chr = chr;
    }

    private Set<CharacterPotential> temp = new HashSet<>();


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
        chr.announce(UserPacket.characterPotentialSet(potential));
    }


    public void addPotential(CharacterPotential potential, boolean updatePassive) {
        getPotentials().add(potential);
        chr.announce(UserPacket.characterPotentialSet(potential, updatePassive));
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
//            chr.announce(UserPacket.characterPotentialReset(PotentialResetType.Pos, cp.getKey()));
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

    public static CharacterPotential generateRandomPotential(byte key, byte grade) {
        List<Integer> skills = GameConstants.getCharPotentialIDByGrade(grade);
        Integer skill = Util.getRandomFromCollection(skills);
        SkillInfo si = SkillData.getSkillInfo(skill);
        int maxLevel = si.getMaxLevel();
        byte baseGrade = GameConstants.getLeastReqGradeOfSkill(skill);
        int trie = CharPotGrade.Legendary.ordinal() - baseGrade + 1;
        int levelPerTrie = maxLevel / trie;
        int lLevel = levelPerTrie * (grade - baseGrade) + 4;
        int hLevel = levelPerTrie * (grade - baseGrade + 1);
        int slv = 1 + Util.getRandom(lLevel, hLevel);
        return new CharacterPotential(key, skill, (byte) slv, grade);
    }

    //simplify
    public Set<CharacterPotential> randomizer(Set<Byte> lockedLines, int minGrade) {
        Set<CharacterPotential> changedPotential = new HashSet<>();
        Set<CharacterPotential> potentials = getPotentials();
        if (lockedLines != null) {
            CharacterPotential firstPotential = getPotentialByKey((byte) 1);
            byte maxGrade = firstPotential.getGrade();
            for (CharacterPotential potential : potentials) {
                if (lockedLines.contains(potential.getKey())) {
                    continue;
                }
                byte key = potential.getKey();
                byte grade = potential.getGrade();
                boolean gradeUp = Randomizer.isSuccess(GameConstants.getBaseCharPotUpRate(potential.getGrade()));
                boolean gradeDown = Randomizer.isSuccess(GameConstants.getBaseCharPotDownRate(potential.getGrade()));
                if (gradeUp && key != 1 && grade < CharPotGrade.Legendary.ordinal() && grade < maxGrade - 1) {
                    grade++;
                } else if (gradeDown && key != 1 && grade > CharPotGrade.Rare.ordinal() && grade > minGrade) {
                    grade--;
                }
                CharacterPotential p = generateRandomPotential(key, grade);
                changedPotential.add(p);
            }
        } else {
            int maxGrade = CharPotGrade.Legendary.ordinal();
            for (CharacterPotential potential : potentials) {
                byte key = potential.getKey();
                byte grade = potential.getGrade();
                boolean gradeUp = Randomizer.isSuccess(GameConstants.getBaseCharPotUpRate(potential.getGrade()));
                boolean gradeDown = Randomizer.isSuccess(GameConstants.getBaseCharPotDownRate(potential.getGrade()));
                if (gradeUp && grade < CharPotGrade.Legendary.ordinal() && grade < maxGrade) {
                    grade++;
                } else if (gradeDown && grade > CharPotGrade.Rare.ordinal() && grade > minGrade) {
                    grade--;
                }
                if (key == 1) {
                    maxGrade = grade - 1;
                }
                CharacterPotential p = generateRandomPotential(key, grade);
                changedPotential.add(p);
            }
        }

        return changedPotential;
    }

    public Set<CharacterPotential> getTemp() {
        return temp;
    }

    public void setTemp(Set<CharacterPotential> temp) {
        this.temp = temp;
    }
}
