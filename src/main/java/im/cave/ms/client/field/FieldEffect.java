package im.cave.ms.client.field;

import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.enums.FieldEffectType;
import im.cave.ms.enums.GreyFieldType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Util;


/**
 * Created on 3/26/2018.
 */
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

    public void encode(OutPacket outPacket) {
        outPacket.write(getFieldEffectType().getVal());
        switch (getFieldEffectType()) {
            case Summon:
                outPacket.write(getArg1());// nType
                outPacket.writeInt(getArg2());// x1
                outPacket.writeInt(getArg3());// y1
                break;
            case Tremble:
                outPacket.write(getArg1());
                outPacket.write(getArg2());
                outPacket.writeInt(getArg3());
                outPacket.writeShort(getArg4());
                break;
            case ObjectStateByString:
                outPacket.writeMapleAsciiString(getString());// sName
                break;
            case DisableEffectObject:
                outPacket.writeMapleAsciiString(getString());// sName
                outPacket.write(getArg1());    // bCheckPreWord
                break;
            case Screen:
                outPacket.writeMapleAsciiString(getString());// String
                break;
            case PlaySound:
                outPacket.writeMapleAsciiString(getString());// Sound
                outPacket.writeInt(getArg1());// Volume
                break;
            case MobHPTag:
                outPacket.writeInt(getArg1());     // Mob Template ID
                outPacket.writeLong(getArg2());     // Mob HP
                outPacket.writeLong(getArg3());     // Mob max HP
                outPacket.write(getArg4());    // HP Tag Colour
                outPacket.write(getArg5());    // HP Tab BG Colour
                break;
            case ChangeBGM:
                outPacket.writeMapleAsciiString(getString());// sound
                outPacket.writeInt(getArg1());// start time
                outPacket.writeInt(getArg2());// unk
                break;
            case BGMVolumeOnly:
                outPacket.write(getArg1());// m_bBGMVolumeOnly
                break;
            case SetBGMVolume:
                outPacket.writeInt(getArg1());// m_uBGMVolume
                outPacket.writeInt(getArg2());// uFadingDuration
                break;
            case RewardRoulette:
                outPacket.writeInt(getArg1());     // Reward Job ID
                outPacket.writeInt(getArg2());     // Reward Part ID
                outPacket.writeInt(getArg3());     // Reward Level ID
                break;
            case TopScreen:
                outPacket.writeMapleAsciiString(getString());// Directory to the Effect
                break;
            case BackScreen:
                outPacket.writeMapleAsciiString(getString());// Directory to the Effect
                outPacket.writeInt(getArg1());     // Delay in ms
                break;
            case TopScreenEffect:                   // Goes over other effects
                outPacket.writeMapleAsciiString(getString());// Directory to the Effect
                outPacket.writeInt(getArg1());     // Delay in ms
                break;
            case ScreenEffect:
                outPacket.writeMapleAsciiString(getString());// Path to the Effect
                outPacket.writeInt(getArg1());     // Delay in ms
                break;
            case ScreenFloatingEffect:
                outPacket.writeMapleAsciiString(getString());
                outPacket.write(getArg1());
                outPacket.write(getArg2());
                break;
            case Blind:
                outPacket.write(getArg1());// bEnable
                outPacket.writeShort(getArg2());// x
                outPacket.writeShort(getArg3());
                outPacket.writeShort(getArg4());
                outPacket.writeShort(getArg5());
                outPacket.writeInt(getArg6());
                outPacket.writeInt(getArg7());
                break;
            case SetGrey:
                outPacket.writeShort(getArg1());   // GreyField Type
                outPacket.write(getArg2());    // boolean: ON/OFF
                break;
            case OnOffLayer:
                outPacket.write(getArg1());// type
                outPacket.writeInt(getArg2());
                outPacket.writeMapleAsciiString(getString());
                if (getArg1() == 0) {
                    outPacket.writeInt(getArg3());
                    outPacket.writeInt(getArg4());
                    outPacket.writeInt(getArg5());
                    outPacket.writeMapleAsciiString(getString2());
                    outPacket.writeInt(getArg6());
                    outPacket.write(getArg7());
                    outPacket.writeInt(getArg8());
                    outPacket.write(getArg9());
                } else if (getArg1() == 1) {
                    outPacket.writeInt(getArg3());
                    outPacket.writeInt(getArg4());
                } else if (getArg1() == 2) {
                    outPacket.write(getArg3());
                }
                break;
            case OverlapScreen:                    // Takes a Snapshot of the Client and slowly fades away
                outPacket.writeInt(getArg1());     // Duration of the overlap (ms)
                break;
            case OverlapScreenDetail:
                outPacket.writeInt(getArg1());     // Fade In
                outPacket.writeInt(getArg2());     // wait time
                outPacket.writeInt(getArg3());     // Fade Out
                outPacket.write(getArg4());    // some boolean
                break;
            case RemoveOverlapScreen:
                outPacket.writeInt(getArg1());     // Fade Out duration
                break;
            case ChangeColor:
                outPacket.writeShort(getArg1());   // GreyField Type (but doesn't contain Reactor
                outPacket.writeShort(getArg2());   // red      (250 is normal value)
                outPacket.writeShort(getArg3());   // green    (250 is normal value)
                outPacket.writeShort(getArg4());   // blue     (250 is normal value)
                outPacket.writeInt(getArg5());     // time in ms, that it takes to transition from old colours to the new colours
                outPacket.writeInt(0);          // is in queue
                if (getArg1() == 4) {
                    outPacket.writeInt(0);
                }
                break;
            case StageClearExpOnly:
                outPacket.writeInt(getArg1());     // Exp Number given
                break;
            case SpineScreen:
                outPacket.write(getArg1());// bBinary
                outPacket.write(getArg2());// bLoop
                outPacket.write(getArg3());// bPostRender
                outPacket.writeInt(getArg4());// tEndDelay
                outPacket.writeMapleAsciiString(getString());// sPath
                outPacket.writeMapleAsciiString(getString2());// Animation Name

                outPacket.writeBool(getString3() != null);
                if (getString3() != null) {
                    outPacket.writeMapleAsciiString(getString3());// sKeyName
                }
                break;
            case OffSpineScreen:
                /*enum $886F2E0581A468BCCA02C1C9C4415C7C
                {
                    OffSpineScr_Immediate = 0x0,
                    OffSpineScr_Alpha = 0x1,
                    OffSpineScr_Ani = 0x2,
                };*/
                outPacket.writeMapleAsciiString(getString());// pLayer
                outPacket.writeInt(getArg1());// nType
                if (getArg1() == 1) {// PROCESS_HITPARTS
                    outPacket.writeInt(getArg2());// tAlpha
                } else if (getArg1() == 2) {// PROCESS_SKELETON
                    outPacket.writeMapleAsciiString(getString2());// Animation Name
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
