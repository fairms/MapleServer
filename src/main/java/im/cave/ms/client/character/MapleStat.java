/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package im.cave.ms.client.character;

public enum MapleStat {

    SKIN(0x1),
    FACE(0x2),
    HAIR(0x4),
    LEVEL(0x10),
    JOB(0x20),
    STR(0x40),
    DEX(0x80),
    INT(0x100),
    LUK(0x200),
    HP(0x400),
    MAXHP(0x800),
    MP(0x1000),
    MAXMP(0x2000),
    AVAILABLEAP(0x4000),
    AVAILABLESP(0x8000),
    EXP(0x10000),
    FAME(0x20000),
    MESO(0x40000),
    PET(0x180008),
    GACHAEXP(0x200000),
    FATIGUE(0x80000), //疲劳
    CHARISMA(0x100000), //领袖
    INSIGHT(0x200000), //洞察
    WILL(0x400000), //意志
    CRAFT(0x800000), //手技
    SENSE(0x1000000), //感性
    CHARM(0x2000000),//魅力
    TODAYS_TRAITS(0x4000000), //今日获得
    TRAIT_LIMIT(0x8000000),
    BATTLE_EXP(0x10000000),
    BATTLE_RANK(0x20000000),
    BATTLE_POINTS(0x40000000),
    ICE_GAGE(0x80000000L),
    VIRTUE(0x100000000L),
    GREAD(0x200000000L);

    private final long i;

    private MapleStat(long i) {
        this.i = i;
    }

    public long getValue() {
        return i;
    }

    public static MapleStat getByValue(long value) {
        for (MapleStat stat : MapleStat.values()) {
            if (stat.getValue() == value) {
                return stat;
            }
        }
        return null;
    }

    public static MapleStat getBy5ByteEncoding(int encoded) {
        switch (encoded) {
            case 64:
                return STR;
            case 128:
                return DEX;
            case 256:
                return INT;
            case 512:
                return LUK;
        }
        return null;
    }

    public static MapleStat getByString(String type) {
        if (type.equals("SKIN")) {
            return SKIN;
        } else if (type.equals("FACE")) {
            return FACE;
        } else if (type.equals("HAIR")) {
            return HAIR;
        } else if (type.equals("LEVEL")) {
            return LEVEL;
        } else if (type.equals("JOB")) {
            return JOB;
        } else if (type.equals("STR")) {
            return STR;
        } else if (type.equals("DEX")) {
            return DEX;
        } else if (type.equals("INT")) {
            return INT;
        } else if (type.equals("LUK")) {
            return LUK;
        } else if (type.equals("HP")) {
            return HP;
        } else if (type.equals("MAXHP")) {
            return MAXHP;
        } else if (type.equals("MP")) {
            return MP;
        } else if (type.equals("MAXMP")) {
            return MAXMP;
        } else if (type.equals("AVAILABLEAP")) {
            return AVAILABLEAP;
        } else if (type.equals("AVAILABLESP")) {
            return AVAILABLESP;
        } else if (type.equals("EXP")) {
            return EXP;
        } else if (type.equals("FAME")) {
            return FAME;
        } else if (type.equals("MESO")) {
            return MESO;
        } else if (type.equals("PET")) {
            return PET;
        }
        return null;
    }
}
