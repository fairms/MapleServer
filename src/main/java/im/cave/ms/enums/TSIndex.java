package im.cave.ms.enums;


import im.cave.ms.client.character.temp.CharacterTemporaryStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public enum TSIndex {
    EnergyCharged(0),
    DashSpeed(1),
    DashJump(2),
    RideVehicle(3),
    PartyBooster(4),
    GuidedBullet(5),
    Undead(6);
    private final int index;

    TSIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static TSIndex getTSEByIndex(int index) {
        return Arrays.stream(TSIndex.values()).filter(tse -> tse.getIndex() == index).findFirst().orElse(null);
    }

    public static CharacterTemporaryStat getCTSFromTwoStatIndex(int index) {
        switch (index) {
            case 0:
                return CharacterTemporaryStat.EnergyCharged;
            case 1:
                return CharacterTemporaryStat.DashSpeed;
            case 2:
                return CharacterTemporaryStat.DashJump;
            case 3:
                return CharacterTemporaryStat.RideVehicle;
            case 4:
                return CharacterTemporaryStat.PartyBooster;
            case 5:
                return CharacterTemporaryStat.GuidedBullet;
            case 6:
                return CharacterTemporaryStat.Undead;
            default:
                return null;
        }
    }

    public static TSIndex getTSEFromCTS(CharacterTemporaryStat cts) {
        switch (cts) {
            case EnergyCharged:
                return EnergyCharged;
            case DashJump:
                return DashJump;
            case DashSpeed:
                return DashSpeed;
            case RideVehicle:
                return RideVehicle;
            case PartyBooster:
                return PartyBooster;
            case GuidedBullet:
                return GuidedBullet;
            case Undead:
                return Undead;
        }
        return null;
    }

    public static boolean isTwoStat(CharacterTemporaryStat cts) {
        return getTSEFromCTS(cts) != null;
    }


    public static List<CharacterTemporaryStat> getAllCTS() {
        List<CharacterTemporaryStat> characterTemporaryStats = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            characterTemporaryStats.add(getCTSFromTwoStatIndex(i));
        }
        return characterTemporaryStats;
    }
}
