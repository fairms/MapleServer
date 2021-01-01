package im.cave.ms.client;

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

    public void encode(OutPacket outPacket) {
        outPacket.write(type.getVal());
        switch (type) {
            case ForcedAction:
                outPacket.writeInt(arg1); // nAction
                if (arg1 <= 1895) {
                    outPacket.writeInt(arg2); // nDuration
                }
                break;
            case Delay:
                outPacket.writeInt(arg1); // nDelay
                break;
            case EffectPlay:
                outPacket.writeMapleAsciiString(str); // sEffectUOL
                outPacket.writeInt(arg1); // tDuration (0 => take effect's duration)
                outPacket.writePositionInt(pos);
                outPacket.writeBool(arg2 >= -1);
                if (arg2 >= -1) {
                    outPacket.writeInt(arg2); // z
                }
                outPacket.writeBool(arg3 >= -1);
                if (arg3 >= -1) {
                    outPacket.writeInt(arg3); // dwNpcID (for CNpcPool::GetNpcForExtend)
                    outPacket.write(arg4);
                    outPacket.write(arg5);
                    outPacket.write(arg6);
                }
                break;
            case ForcedInput:
                outPacket.writeInt(arg1); // nForcedInput
                break;
            case PatternInputRequest:
                outPacket.writeMapleAsciiString(str); // sPattern
                outPacket.writeInt(arg1); // nAct
                outPacket.writeInt(arg2); // nRequestCount
                outPacket.writeInt(arg3); // nTime
                break;
            case CameraMove:
                outPacket.write(arg1); // bBack
                outPacket.writeInt(arg2); // nPixelPerSec
                if (arg1 == 0) {
                    outPacket.writePositionInt(pos);
                } else {
                    outPacket.write(0);// not sure if really exists but in GMS(v198) there is extra 0 byte
                }
                break;
            case CameraOnCharacter:
                outPacket.writeInt(arg1); // dwNpcID
                break;
            case CameraZoom:
                outPacket.writeInt(arg1); // nTime
                outPacket.writeInt(arg2); // nScale
                outPacket.writeInt(arg3); // nTimePos
                outPacket.writePositionInt(pos);
                break;
            case CameraReleaseFromUserPoint:
                break;
            case VansheeMode:
                outPacket.write(arg1); // bVanshee
                break;
            case FaceOff:
                outPacket.writeInt(arg1); // nFaceItemID
                break;
            case Monologue:
                outPacket.writeMapleAsciiString(str); // sStr
                outPacket.write(arg1); // bIsEnd
                break;
            case MonologueScroll:
                outPacket.writeMapleAsciiString(str); // sStr
                outPacket.write(arg1); // stayModal
                outPacket.writeShort(arg2); // nAlign
                outPacket.writeInt(arg3); // nUpdateSpeedTime
                outPacket.writeInt(arg4); // nDecTic
                break;
            case AvatarLookSet:
                outPacket.write(arr.length);
                for (int itemID : arr) {
                    outPacket.writeInt(itemID);
                }
                break;
            case RemoveAdditionalEffect:
                break;
            case ForcedMove:
                outPacket.writeInt(arg1); // nDir
                outPacket.writeInt(arg2); // nSpeed
                break;
            case ForcedFlip:
                outPacket.writeInt(arg1); // nForcedFlip
                break;
            case InputUI:
                outPacket.write(arg1); // nIdx
                break;
        }
    }


}
