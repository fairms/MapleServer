package im.cave.ms.client.field;

import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.FieldEffectType;
import im.cave.ms.enums.GreyFieldType;
import im.cave.ms.tools.Util;


public class FieldEffect {

    private FieldEffectType fieldEffectType;
    private String string;
    private String string2;
    private String string3;
    private int arg1;
    private int arg2;
    private int arg3;
    private int arg4;
    private int arg5;
    private int arg6;
    private int arg7;
    private int arg8;
    private int arg9;

    public void encode(OutPacket out) {
        out.write(getFieldEffectType().getVal());
        switch (getFieldEffectType()) {
            case Summon:
                out.write(getArg1());// nType
                out.writeInt(getArg2());// x1
                out.writeInt(getArg3());// y1
                break;
            case Tremble:
                out.write(getArg1());  // 1
                out.write(getArg2());  // 1
                out.writeInt(getArg3()); // duration
                out.writeShort(getArg4()); // level
                break;
            case ObjectStateByString:
            case TopScreen:
            case Screen:
                out.writeMapleAsciiString(getString());// sName
                break;
            case DisableEffectObject:
                out.writeMapleAsciiString(getString());// sName
                out.write(getArg1());    // bCheckPreWord
                break;
            // String
            case PlaySound:
            case BackScreen:
                out.writeMapleAsciiString(getString());
                out.writeInt(getArg1());
                break;
            case TopScreenEffect:                   // Goes over other effects
            case ScreenEffect:
                out.writeMapleAsciiString(getString());// Sound
                out.writeInt(getArg1());// Volume
                break;
            case MobHPTag:
                out.writeInt(getArg1());     // Mob Template ID
                out.writeLong(getArg2());     // Mob HP
                out.writeLong(getArg3());     // Mob max HP
                out.write(getArg4());    // HP Tag Colour
                out.write(getArg5());    // HP Tab BG Colour
                break;
            case ChangeBGM:
                out.writeMapleAsciiString(getString());// sound
                out.writeInt(getArg1());// start time
                out.writeInt(getArg2());// unk
                break;
            case BGMVolumeOnly:
                out.write(getArg1());// m_bBGMVolumeOnly
                break;
            case SetBGMVolume:
                out.writeInt(getArg1());// m_uBGMVolume
                out.writeInt(getArg2());// uFadingDuration
                break;
            case RewardRoulette:
                out.writeInt(getArg1());     // Reward Job ID
                out.writeInt(getArg2());     // Reward Part ID
                out.writeInt(getArg3());     // Reward Level ID
                break;
            // Directory to the Effect
            // Path to the Effect
            case ScreenFloatingEffect:
                out.writeMapleAsciiString(getString());
                out.write(getArg1());
                out.write(getArg2());
                break;
            case Blind:
                out.write(getArg1());// bEnable
                out.writeShort(getArg2());// x
                out.writeShort(getArg3());
                out.writeShort(getArg4());
                out.writeShort(getArg5());
                out.writeInt(getArg6());
                out.writeInt(getArg7());
                break;
            case SetGrey:
                out.writeShort(getArg1());   // GreyField Type
                out.write(getArg2());    // boolean: ON/OFF
                break;
            case OnOffLayer:
                out.write(getArg1());// type
                out.writeInt(getArg2());
                out.writeMapleAsciiString(getString());
                if (getArg1() == 0) {
                    out.writeInt(getArg3());
                    out.writeInt(getArg4());
                    out.writeInt(getArg5());
                    out.writeMapleAsciiString(getString2());
                    out.writeInt(getArg6());
                    out.write(getArg7());
                    out.writeInt(getArg8());
                    out.write(getArg9());
                } else if (getArg1() == 1) {
                    out.writeInt(getArg3());
                    out.writeInt(getArg4());
                } else if (getArg1() == 2) {
                    out.write(getArg3());
                }
                break;
            case OverlapScreen:                    // Takes a Snapshot of the Client and slowly fades away
            case StageClearExpOnly:
            case RemoveOverlapScreen:
                out.writeInt(getArg1());     // Duration of the overlap (ms)
                break;
            case OverlapScreenDetail:
                out.writeInt(getArg1());     // Fade In
                out.writeInt(getArg2());     // wait time
                out.writeInt(getArg3());     // Fade Out
                out.write(getArg4());    // some boolean
                break;
            // Fade Out duration
            case ChangeColor:
                out.writeShort(getArg1());   // GreyField Type (but doesn't contain Reactor
                out.writeShort(getArg2());   // red      (250 is normal value)
                out.writeShort(getArg3());   // green    (250 is normal value)
                out.writeShort(getArg4());   // blue     (250 is normal value)
                out.writeInt(getArg5());     // time in ms, that it takes to transition from old colours to the new colours
                out.writeInt(0);          // is in queue
                if (getArg1() == 4) {
                    out.writeInt(0);
                }
                break;
            // Exp Number given
            case SpineScreen:
                out.write(getArg1());// bBinary
                out.write(getArg2());// bLoop
                out.write(getArg3());// bPostRender
                out.writeInt(getArg4());// tEndDelay
                out.writeMapleAsciiString(getString());// sPath
                out.writeMapleAsciiString(getString2());// Animation Name

                out.writeBool(getString3() != null);
                if (getString3() != null) {
                    out.writeMapleAsciiString(getString3());// sKeyName
                }
                break;
            case OffSpineScreen:
                /*enum $886F2E0581A468BCCA02C1C9C4415C7C
                {
                    OffSpineScr_Immediate = 0x0,
                    OffSpineScr_Alpha = 0x1,
                    OffSpineScr_Ani = 0x2,
                };*/
                out.writeMapleAsciiString(getString());// pLayer
                out.writeInt(getArg1());// nType
                if (getArg1() == 1) {// PROCESS_HITPARTS
                    out.writeInt(getArg2());// tAlpha
                } else if (getArg1() == 2) {// PROCESS_SKELETON
                    out.writeMapleAsciiString(getString2());// Animation Name
                }
                break;
        }
    }

    public static FieldEffect mobHPTagFieldEffect(Mob mob) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.MobHPTag);
        fieldEffect.setArg1(mob.getTemplateId());
        int maxHP = Util.maxInt(mob.getMaxHp());
        double ratio = mob.getMaxHp() / (double) Integer.MAX_VALUE;
        fieldEffect.setArg2(ratio > 1 ? (int) (mob.getHp() / ratio) : (int) mob.getHp());
        fieldEffect.setArg3(maxHP);
        fieldEffect.setArg4(mob.getHpTagColor());
        fieldEffect.setArg5(mob.getHpTagBgcolor());
        return fieldEffect;
    }

    public static FieldEffect getFieldEffectFromWz(String dir, int delay) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.ScreenEffect);

        fieldEffect.setString(dir);
        fieldEffect.setArg1(delay);

        return fieldEffect;
    }

    public static FieldEffect getFieldBackgroundEffectFromWz(String dir, int delay) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.BackScreen);

        fieldEffect.setString(dir);
        fieldEffect.setArg1(delay);

        return fieldEffect;
    }

    public static FieldEffect getOffFieldEffectFromWz(String dir, int delay) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.TopScreenEffect);

        fieldEffect.setString(dir);
        fieldEffect.setArg1(delay);

        return fieldEffect;
    }

    public static FieldEffect setFieldGrey(GreyFieldType greyFieldType, boolean setGrey) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.SetGrey);

        fieldEffect.setArg1(greyFieldType.getVal());
        fieldEffect.setArg2(setGrey ? 1 : 0);

        return fieldEffect;
    }

    public static FieldEffect setFieldColor(GreyFieldType colorFieldType, short red, short green, short blue, int time) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.ChangeColor);

        fieldEffect.setArg1(colorFieldType.getVal());
        fieldEffect.setArg2(red);
        fieldEffect.setArg3(green);
        fieldEffect.setArg4(blue);
        fieldEffect.setArg5(time);

        return fieldEffect;
    }

    public static FieldEffect showClearStageExpWindow(int expNumber) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.StageClearExpOnly);

        fieldEffect.setArg1(expNumber);

        return fieldEffect;
    }

    public static FieldEffect takeSnapShotOfClient(int duration) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OverlapScreen);

        fieldEffect.setArg1(duration);

        return fieldEffect;
    }

    public static FieldEffect takeSnapShotOfClient2(int transitionDurationToSnapShot, int inBetweenDuration, int transitionBack, boolean someBoolean) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OverlapScreenDetail);

        fieldEffect.setArg1(transitionDurationToSnapShot);
        fieldEffect.setArg2(inBetweenDuration);
        fieldEffect.setArg3(transitionBack);
        fieldEffect.setArg4(someBoolean ? 1 : 0);

        return fieldEffect;
    }

    public static FieldEffect removeOverlapScreen(int fadeOutDuration) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.RemoveOverlapScreen);

        fieldEffect.setArg1(fadeOutDuration);

        return fieldEffect;
    }

    public static FieldEffect playSound(String sound, int vol) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.PlaySound);

        fieldEffect.setString(sound);
        fieldEffect.setArg1(vol);

        return fieldEffect;
    }

    public static FieldEffect blind(int enable, int x, int color, int unk1, int unk2, int time, int unk3) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.Blind);

        fieldEffect.setArg1(enable);
        fieldEffect.setArg2(x);
        fieldEffect.setArg3(color);
        fieldEffect.setArg4(unk1);
        fieldEffect.setArg5(unk2);
        fieldEffect.setArg6(time);
        fieldEffect.setArg7(unk3);
        return fieldEffect;
    }

    public static FieldEffect OnOffLayer_On(int term, String key, int unk1, int unk2, int z, String path, int origin, int unk5, int unk6, int unk7) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OnOffLayer);

        fieldEffect.setArg1(0);// type0
        fieldEffect.setArg2(term);
        fieldEffect.setArg3(unk1);
        fieldEffect.setArg4(unk2);
        fieldEffect.setArg5(z);
        fieldEffect.setArg6(origin);
        fieldEffect.setArg7(unk5);
        fieldEffect.setArg8(unk6);
        fieldEffect.setArg9(unk7);
        fieldEffect.setString(key);
        fieldEffect.setString2(path);

        return fieldEffect;
    }

    public static FieldEffect OnOffLayer_Move(int term, String key, int dx, int dy) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OnOffLayer);

        fieldEffect.setArg1(1);
        fieldEffect.setArg2(term);
        fieldEffect.setArg3(dx);
        fieldEffect.setArg4(dy);
        fieldEffect.setString(key);

        return fieldEffect;
    }

    public static FieldEffect OnOffLayer_Off(int term, String key, int unk) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OnOffLayer);

        fieldEffect.setArg1(2);// type0
        fieldEffect.setArg2(term);
        fieldEffect.setArg3(unk);
        fieldEffect.setString(key);

        return fieldEffect;
    }

    public static FieldEffect changeBGM(String sound, int startTime, int unk) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.ChangeBGM);

        fieldEffect.setString(sound);
        fieldEffect.setArg1(startTime);// type0
        fieldEffect.setArg2(unk);

        return fieldEffect;
    }

    public static FieldEffect tremble(int bHeavyNShortTremble, int tDelay, int unk) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.Tremble);

        fieldEffect.setArg1(bHeavyNShortTremble);
        fieldEffect.setArg2(tDelay);
        fieldEffect.setArg3(unk);

        return fieldEffect;
    }

    public static FieldEffect spineScreen(boolean binary, boolean loop, boolean postRender, int endDelay, String path, String animation, String keyName) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.SpineScreen);

        fieldEffect.setArg1(binary ? 1 : 0);
        fieldEffect.setArg2(loop ? 1 : 0);
        fieldEffect.setArg3(postRender ? 1 : 0);
        fieldEffect.setArg4(endDelay);
        fieldEffect.setString(path);
        fieldEffect.setString2(animation);
        fieldEffect.setString3(keyName);

        return fieldEffect;
    }

    public static FieldEffect offSpineScreenImmediate(String layer) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OffSpineScreen);

        fieldEffect.setArg1(0);// OffSpineScr_Immediate = 0x0
        fieldEffect.setString(layer);

        return fieldEffect;
    }

    public static FieldEffect offSpineScreenAlpha(String layer, int alpha) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OffSpineScreen);

        fieldEffect.setArg1(1);// OffSpineScr_Alpha = 0x1
        fieldEffect.setString(layer);
        fieldEffect.setArg2(alpha);

        return fieldEffect;
    }

    public static FieldEffect offSpineScreenAni(String layer, String path) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.OffSpineScreen);

        fieldEffect.setArg1(2);// OffSpineScr_Ani = 0x2
        fieldEffect.setString(layer);
        fieldEffect.setString2(path);
        return fieldEffect;
    }

    public static FieldEffect setBGMVolume(int bgmVolume, int fadingDuration) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.SetBGMVolume);

        fieldEffect.setArg1(bgmVolume);// type0
        fieldEffect.setArg2(fadingDuration);

        return fieldEffect;
    }

    public static FieldEffect objectStateByString(String name) {
        FieldEffect fieldEffect = new FieldEffect();
        fieldEffect.setFieldEffectType(FieldEffectType.ObjectStateByString);

        fieldEffect.setString(name);

        return fieldEffect;
    }

    public FieldEffectType getFieldEffectType() {
        return fieldEffectType;
    }

    public void setFieldEffectType(FieldEffectType fieldEffectType) {
        this.fieldEffectType = fieldEffectType;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public int getArg2() {
        return arg2;
    }

    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }

    public int getArg3() {
        return arg3;
    }

    public void setArg3(int arg3) {
        this.arg3 = arg3;
    }

    public int getArg4() {
        return arg4;
    }

    public void setArg4(int arg4) {
        this.arg4 = arg4;
    }

    public int getArg5() {
        return arg5;
    }

    public void setArg5(int arg5) {
        this.arg5 = arg5;
    }

    public int getArg6() {
        return arg6;
    }

    public void setArg6(int arg6) {
        this.arg6 = arg6;
    }

    public int getArg7() {
        return arg7;
    }

    public void setArg7(int arg7) {
        this.arg7 = arg7;
    }

    public int getArg8() {
        return arg8;
    }

    public void setArg8(int arg8) {
        this.arg8 = arg8;
    }

    public int getArg9() {
        return arg9;
    }

    public void setArg9(int arg9) {
        this.arg9 = arg9;
    }

    public String getString3() {
        return string3;
    }

    public void setString3(String string3) {
        this.string3 = string3;
    }
}
