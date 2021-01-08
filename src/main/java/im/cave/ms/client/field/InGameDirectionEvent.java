package im.cave.ms.client.field;

import im.cave.ms.enums.InGameDirectionEventType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.tools.Position;

public class InGameDirectionEvent {
    private InGameDirectionEventType type;
    private int arg1, arg2, arg3, arg4, arg5, arg6;
    private String str;
    private Position pos;
    private int[] arr;

    public InGameDirectionEvent(InGameDirectionEventType type) {
        this.type = type;
    }

    public static InGameDirectionEvent forcedAction(int action, int duration) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.ForcedAction);

        igdr.arg1 = action;
        igdr.arg2 = duration;

        return igdr;
    }

    public static InGameDirectionEvent delay(int delay) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.Delay);

        igdr.arg1 = delay;

        return igdr;
    }

    public static InGameDirectionEvent effectPlay(String effectUOL, int duration, Position position, int z,
                                                  int npcIdForExtend, boolean onUser, int idk2) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.EffectPlay);

        igdr.str = effectUOL;
        igdr.arg1 = duration;
        igdr.pos = position;
        igdr.arg2 = z;
        igdr.arg3 = npcIdForExtend;
        igdr.arg4 = onUser ? 0 : 1;
        igdr.arg5 = idk2; // flip with user

        return igdr;
    }

    public static InGameDirectionEvent forcedInput(int forcedInput) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.ForcedInput);

        igdr.arg1 = forcedInput;

        return igdr;
    }

    public static InGameDirectionEvent patternInputRequest(String pattern, int act, int requestCount, int time) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.PatternInputRequest);

        igdr.str = pattern;
        igdr.arg1 = act;
        igdr.arg2 = requestCount;
        igdr.arg3 = time;

        return igdr;
    }

    /**
     * Moves the camera.
     *
     * @param back        if the camera move should go back to its original state
     * @param pixelPerSec speed of camera movement
     * @param pos         position the camera should move to (not applicable if back is true)
     * @return the event
     */
    public static InGameDirectionEvent cameraMove(boolean back, int pixelPerSec, Position pos) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.CameraMove);

        igdr.arg1 = back ? 1 : 0;
        igdr.arg2 = pixelPerSec;
        igdr.pos = pos;

        return igdr;
    }

    /**
     * Sets a camera on a character or npc.
     *
     * @param npcID the npc's id on which the camera should focus on, or 0 if the camera should focus on the player
     * @return the event
     */
    public static InGameDirectionEvent cameraOnCharacter(int npcID) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.CameraOnCharacter);

        igdr.arg1 = npcID;

        return igdr;
    }

    public static InGameDirectionEvent cameraZoom(int time, int scale, int timePos, Position position) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.CameraZoom);

        igdr.arg1 = time;
        igdr.arg2 = scale;
        igdr.arg3 = timePos;
        igdr.pos = position;

        return igdr;
    }

    public static InGameDirectionEvent cameraReleaseFromUserPoint() {
        return new InGameDirectionEvent(InGameDirectionEventType.CameraReleaseFromUserPoint);
    }

    public static InGameDirectionEvent vansheeMode(boolean vansheeMode) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.VansheeMode);

        igdr.arg1 = vansheeMode ? 1 : 0;

        return igdr;
    }

    public static InGameDirectionEvent faceOff(int faceItemID) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.FaceOff);

        igdr.arg1 = faceItemID;

        return igdr;
    }

    public static InGameDirectionEvent monologue(String msg, boolean isEnd) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.Monologue);

        igdr.str = msg;
        igdr.arg1 = isEnd ? 1 : 0;

        return igdr;
    }

    public static InGameDirectionEvent monologueScroll(String msg, boolean stayModal, short align, int updateSpeedTime,
                                                       int decTic) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.MonologueScroll);

        igdr.str = msg;
        igdr.arg1 = stayModal ? 1 : 0;
        igdr.arg2 = align;
        igdr.arg3 = updateSpeedTime;
        igdr.arg4 = decTic;

        return igdr;
    }

    public static InGameDirectionEvent avatarLookSet(int[] equipIDs) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.AvatarLookSet);

        igdr.arr = equipIDs;

        return igdr;
    }

    public static InGameDirectionEvent removeAdditionalEffect() {
        return new InGameDirectionEvent(InGameDirectionEventType.RemoveAdditionalEffect);
    }

    public static InGameDirectionEvent forcedMove(boolean left, int distance) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.ForcedMove);

        igdr.arg1 = left ? 1 : 2; // left = 1, right = 2
        igdr.arg2 = distance;

        return igdr;
    }

    public static InGameDirectionEvent forcedFlip(boolean left) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.ForcedFlip);

        igdr.arg1 = left ? -1 : 1; // left = -1, right = 1

        return igdr;
    }

    public static InGameDirectionEvent inputUI(int idx) {
        InGameDirectionEvent igdr = new InGameDirectionEvent(InGameDirectionEventType.ForcedAction);

        igdr.arg1 = idx;

        return igdr;
    }

    public void encode(OutPacket out) {
        out.write(type.getVal());
        switch (type) {
            case ForcedAction:
                out.writeInt(arg1); // nAction
                if (arg1 <= 1895) {
                    out.writeInt(arg2); // nDuration
                }
                break;
            case Delay:
                out.writeInt(arg1); // nDelay
                break;
            case EffectPlay:
                out.writeMapleAsciiString(str); // sEffectUOL
                out.writeInt(arg1); // tDuration (0 => take effect's duration)
                out.writePositionInt(pos);
                out.writeBool(arg2 >= -1);
                if (arg2 >= -1) {
                    out.writeInt(arg2); // z
                }
                out.writeBool(arg3 >= -1);
                if (arg3 >= -1) {
                    out.writeInt(arg3); // dwNpcID (for CNpcPool::GetNpcForExtend)
                    out.write(arg4);
                    out.write(arg5);
                    out.write(arg6);
                }
                break;
            case ForcedInput:
                out.writeInt(arg1); // nForcedInput
                break;
            case PatternInputRequest:
                out.writeMapleAsciiString(str); // sPattern
                out.writeInt(arg1); // nAct
                out.writeInt(arg2); // nRequestCount
                out.writeInt(arg3); // nTime
                break;
            case CameraMove:
                out.write(arg1); // bBack
                out.writeInt(arg2); // nPixelPerSec
                if (arg1 == 0) {
                    out.writePositionInt(pos);
                } else {
                    out.write(0);// not sure if really exists but in GMS(v198) there is extra 0 byte
                }
                break;
            case CameraOnCharacter:
                out.writeInt(arg1); // dwNpcID
                break;
            case CameraZoom:
                out.writeInt(arg1); // nTime
                out.writeInt(arg2); // nScale
                out.writeInt(arg3); // nTimePos
                out.writePositionInt(pos);
                break;
            case CameraReleaseFromUserPoint:
                break;
            case VansheeMode:
                out.write(arg1); // bVanshee
                break;
            case FaceOff:
                out.writeInt(arg1); // nFaceItemID
                break;
            case Monologue:
                out.writeMapleAsciiString(str); // sStr
                out.write(arg1); // bIsEnd
                break;
            case MonologueScroll:
                out.writeMapleAsciiString(str); // sStr
                out.write(arg1); // stayModal
                out.writeShort(arg2); // nAlign
                out.writeInt(arg3); // nUpdateSpeedTime
                out.writeInt(arg4); // nDecTic
                break;
            case AvatarLookSet:
                out.write(arr.length);
                for (int itemID : arr) {
                    out.writeInt(itemID);
                }
                break;
            case RemoveAdditionalEffect:
                break;
            case ForcedMove:
                out.writeInt(arg1); // nDir
                out.writeInt(arg2); // nSpeed
                break;
            case ForcedFlip:
                out.writeInt(arg1); // nForcedFlip
                break;
            case InputUI:
                out.write(arg1); // nIdx
                break;
        }
    }


}
