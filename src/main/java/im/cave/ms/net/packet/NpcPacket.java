package im.cave.ms.net.packet;

import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.scripting.npc.NpcScriptInfo;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/30 21:20
 */
public class NpcPacket {

    public static OutPacket npcTalk(NpcMessageType type, NpcScriptInfo nsi) {
        OutPacket outPacket = new OutPacket();
        int overrideTemplate = nsi.getOverrideSpeakerTemplateID();
        outPacket.writeShort(SendOpcode.NPC_TALK.getValue());

        outPacket.write(nsi.getSpeakerType()); //always 4
        outPacket.writeInt(nsi.getTemplateID());
        outPacket.write(1); //override ?
        outPacket.writeInt(0); //override id;
        outPacket.write(type.getVal());  //type
        outPacket.writeShort(nsi.getParam()); //mask
        outPacket.write(nsi.getColor()); // 0 or 1
        switch (type) {
            case Say:
            case SayOk:
            case SayNext:
            case SayPrev:
                if ((nsi.getParam() & 4) != 0) {
                    outPacket.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.writeMapleAsciiString(nsi.getText());
                outPacket.writeBool(type.isPrevPossible());
                outPacket.writeBool(type.isNextPossible());
                outPacket.writeInt(type.getDelay());
                outPacket.write(1);
                break;
            case Say_2:
            case SayOk_2:
            case SayNext_2:
            case SayPrev_2:
                outPacket.writeMapleAsciiString(nsi.getText());
                outPacket.writeBool(type.isPrevPossible());
                outPacket.writeBool(type.isNextPossible());
                outPacket.writeInt(type.getDelay());
                break;
            case SayImage:
                String[] images = nsi.getImages();
                outPacket.write(images.length);
                for (String image : images) {
                    outPacket.writeMapleAsciiString(image);
                }
                break;
            case AskMenu:
            case AskAccept:
            case AskYesNo:
                if ((nsi.getParam() & 4) != 0) {
                    outPacket.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.writeMapleAsciiString(nsi.getText());
                break;
            case AskText:
            case AskBoxtext:
                if ((nsi.getParam() & 4) != 0) {
                    outPacket.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.writeMapleAsciiString(nsi.getText());
                outPacket.writeMapleAsciiString(nsi.getDefaultText());
                outPacket.writeShort(nsi.getMin());
                outPacket.writeShort(nsi.getMax());
                break;
            case AskNumber:
                outPacket.writeMapleAsciiString(nsi.getText());
                outPacket.writeInt(nsi.getDefaultNumber());
                outPacket.writeInt(nsi.getMin());
                outPacket.writeInt(nsi.getMax());
                break;
            case InitialQuiz:
                outPacket.write(nsi.getType());
                if (nsi.getType() != 1) {
                    outPacket.writeMapleAsciiString(nsi.getTitle());
                    outPacket.writeMapleAsciiString(nsi.getProblemText());
                    outPacket.writeMapleAsciiString(nsi.getHintText());
                    outPacket.writeInt(nsi.getMin());
                    outPacket.writeInt(nsi.getMax());
                    outPacket.writeInt(nsi.getTime()); // in seconds
                }
                break;
            case InitialSpeedQuiz:
                outPacket.write(nsi.getType());
                if (nsi.getType() != 1) {
                    outPacket.writeInt(nsi.getQuizType());
                    outPacket.writeInt(nsi.getAnswer());
                    outPacket.writeInt(nsi.getCorrectAnswers());
                    outPacket.writeInt(nsi.getRemaining());
                    outPacket.writeInt(nsi.getTime()); // in seconds
                }
                break;
            case ICQuiz:
                outPacket.write(nsi.getType());
                if (nsi.getType() != 1) {
                    outPacket.writeMapleAsciiString(nsi.getText());
                    outPacket.writeMapleAsciiString(nsi.getHintText());
                    outPacket.writeInt(nsi.getTime()); // in seconds
                }
                break;
            case AskAvatar:
                int[] options = nsi.getOptions();
                outPacket.writeBool(nsi.isAngelicBuster());
                outPacket.writeBool(nsi.isZeroBeta());
                outPacket.writeMapleAsciiString(nsi.getText());
                outPacket.writeZeroBytes(12);
                outPacket.write(options.length);
                for (int option : options) {
                    outPacket.writeInt(option);
                }
                outPacket.write(0);
                outPacket.writeInt(nsi.getRequireCard());
                break;
            case AskSlideMenu:
//                outPacket.writeInt(nsi.getDlgType());
//                // start CSlideMenuDlg::SetSlideMenuDlg
//                outPacket.writeInt(0); // last selected
//                StringBuilder sb = new StringBuilder();
//                for (DimensionalPortalType dpt : DimensionalPortalType.values()) {
//                    if (dpt.getMapID() != 0) {
//                        sb.append("#").append(dpt.getVal()).append("#").append(dpt.getDesc());
//                    }
//                }
//                outPacket.writeMapleAsciiString(sb.toString());
//                outPacket.writeInt(0);
                break;
            case AskSelectMenu:
                outPacket.writeInt(nsi.getDlgType());
                if (nsi.getDlgType() <= 0 || nsi.getDlgType() == 1) {
                    outPacket.writeInt(nsi.getDefaultSelect());
                    outPacket.writeInt(nsi.getSelectText().length);
                    for (String selectText : nsi.getSelectText()) {
                        outPacket.writeMapleAsciiString(selectText);
                    }
                }
                break;
            case SayIllustration:
            case SayIllustrationOk:
            case SayIllustrationNext:
            case SayIllustrationPrev:
                if ((nsi.getParam() & 4) != 0) {
                    outPacket.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                outPacket.writeMapleAsciiString(nsi.getText());
                outPacket.writeBool(type.isPrevPossible());
                outPacket.writeBool(type.isNextPossible());
                outPacket.writeInt((nsi.getParam() & 4) != 0 ? nsi.getOverrideSpeakerTemplateID() : overrideTemplate != 0 ? overrideTemplate : nsi.getTemplateID());
                outPacket.writeInt(nsi.getFaceIndex());
                outPacket.writeBool(nsi.isLeft());
                break;
        }
        if ((nsi.getParam() & 4) != 0) {
            nsi.setParam((byte) (nsi.getParam() ^ 4));
        }

        return outPacket;
    }


    public static OutPacket removeNpc(int objId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.REMOVE_NPC.getValue());
        outPacket.writeInt(objId);
        return outPacket;
    }

    public static OutPacket npcMove(int npcId, byte oneTimeAction, byte chatIdx, int duration, MovementInfo movement, byte keyPadState) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.NPC_ANIMATION.getValue());
        outPacket.writeInt(npcId);
        outPacket.write(oneTimeAction);
        outPacket.write(chatIdx);
        outPacket.writeInt(duration);
        if (movement != null) {
            movement.encode(outPacket);
        }
        return outPacket;
    }
}
