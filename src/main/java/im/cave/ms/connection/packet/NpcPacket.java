package im.cave.ms.connection.packet;

import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.enums.ShopResultType;
import im.cave.ms.scripting.npc.NpcScriptInfo;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet
 * @date 11/30 21:20
 */
public class NpcPacket {

    public static OutPacket npcTalk(NpcMessageType type, NpcScriptInfo nsi) {
        OutPacket out = new OutPacket();
        int overrideTemplate = nsi.getOverrideSpeakerTemplateID();
        out.writeShort(SendOpcode.NPC_TALK.getValue());

        out.write(nsi.getSpeakerType()); //always 4
        out.writeInt(nsi.getTemplateID());
        out.write(1); //override ?
        out.writeInt(0); //override id;
        out.write(type.getVal());  //type
        out.writeShort(nsi.getParam()); //mask
        out.write(nsi.getColor()); // 0 or 1
        switch (type) {
            case Say:
            case SayOk:
            case SayNext:
            case SayPrev:
                if ((nsi.getParam() & 4) != 0) {
                    out.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                out.writeMapleAsciiString(nsi.getText());
                out.writeBool(type.isPrevPossible());
                out.writeBool(type.isNextPossible());
                out.writeInt(type.getDelay());
                out.write(1);
                break;
            case Say_2:
            case SayOk_2:
            case SayNext_2:
            case SayPrev_2:
                out.writeMapleAsciiString(nsi.getText());
                out.writeBool(type.isPrevPossible());
                out.writeBool(type.isNextPossible());
                out.writeInt(type.getDelay());
                break;
            case SayImage:
                String[] images = nsi.getImages();
                out.write(images.length);
                for (String image : images) {
                    out.writeMapleAsciiString(image);
                }
                break;
            case AskMenu:
            case AskAccept:
            case AskYesNo:
                if ((nsi.getParam() & 4) != 0) {
                    out.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                out.writeMapleAsciiString(nsi.getText());
                break;
            case AskText:
            case AskBoxtext:
                if ((nsi.getParam() & 4) != 0) {
                    out.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                out.writeMapleAsciiString(nsi.getText());
                out.writeMapleAsciiString(nsi.getDefaultText());
                out.writeShort(nsi.getMin());
                out.writeShort(nsi.getMax());
                break;
            case AskNumber:
                out.writeMapleAsciiString(nsi.getText());
                out.writeInt(nsi.getDefaultNumber());
                out.writeInt(nsi.getMin());
                out.writeInt(nsi.getMax());
                break;
            case InitialQuiz:
                out.write(nsi.getType());
                if (nsi.getType() != 1) {
                    out.writeMapleAsciiString(nsi.getTitle());
                    out.writeMapleAsciiString(nsi.getProblemText());
                    out.writeMapleAsciiString(nsi.getHintText());
                    out.writeInt(nsi.getMin());
                    out.writeInt(nsi.getMax());
                    out.writeInt(nsi.getTime()); // in seconds
                }
                break;
            case InitialSpeedQuiz:
                out.write(nsi.getType());
                if (nsi.getType() != 1) {
                    out.writeInt(nsi.getQuizType());
                    out.writeInt(nsi.getAnswer());
                    out.writeInt(nsi.getCorrectAnswers());
                    out.writeInt(nsi.getRemaining());
                    out.writeInt(nsi.getTime()); // in seconds
                }
                break;
            case ICQuiz:
                out.write(nsi.getType());
                if (nsi.getType() != 1) {
                    out.writeMapleAsciiString(nsi.getText());
                    out.writeMapleAsciiString(nsi.getHintText());
                    out.writeInt(nsi.getTime()); // in seconds
                }
                break;
            case AskAvatar:
                int[] options = nsi.getOptions();
                out.writeBool(nsi.isAngelicBuster());
                out.writeBool(nsi.isZeroBeta());
                out.writeMapleAsciiString(nsi.getText());
                out.writeZeroBytes(12);
                out.write(options.length);
                for (int option : options) {
                    out.writeInt(option);
                }
                out.write(0);
                out.writeInt(nsi.getRequireCard());
                break;
            case AskSlideMenu:
//                out.writeInt(nsi.getDlgType());
//                // start CSlideMenuDlg::SetSlideMenuDlg
//                out.writeInt(0); // last selected
//                StringBuilder sb = new StringBuilder();
//                for (DimensionalPortalType dpt : DimensionalPortalType.values()) {
//                    if (dpt.getMapID() != 0) {
//                        sb.append("#").append(dpt.getVal()).append("#").append(dpt.getDesc());
//                    }
//                }
//                out.writeMapleAsciiString(sb.toString());
//                out.writeInt(0);
                break;
            case AskSelectMenu:
                out.writeInt(nsi.getDlgType());
                if (nsi.getDlgType() <= 0 || nsi.getDlgType() == 1) {
                    out.writeInt(nsi.getDefaultSelect());
                    out.writeInt(nsi.getSelectText().length);
                    for (String selectText : nsi.getSelectText()) {
                        out.writeMapleAsciiString(selectText);
                    }
                }
                break;
            case SayIllustration:
            case SayIllustrationOk:
            case SayIllustrationNext:
            case SayIllustrationPrev:
                if ((nsi.getParam() & 4) != 0) {
                    out.writeInt(nsi.getOverrideSpeakerTemplateID());
                }
                out.writeMapleAsciiString(nsi.getText());
                out.writeBool(type.isPrevPossible());
                out.writeBool(type.isNextPossible());
                out.writeInt((nsi.getParam() & 4) != 0 ? nsi.getOverrideSpeakerTemplateID() : overrideTemplate != 0 ? overrideTemplate : nsi.getTemplateID());
                out.writeInt(nsi.getFaceIndex());
                out.writeBool(nsi.isLeft());
                break;
        }
        if ((nsi.getParam() & 4) != 0) {
            nsi.setParam((byte) (nsi.getParam() ^ 4));
        }

        return out;
    }

    public static OutPacket removeNpc(int objId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOVE_NPC.getValue());
        out.writeInt(objId);
        return out;
    }

    public static OutPacket spawnNpc(Npc npc) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SPAWN_NPC.getValue());
        out.writeInt(npc.getObjectId());
        out.writeInt(npc.getTemplateId());
        npc.encode(out);
        return out;
    }

    public static OutPacket changeNpcController(Npc npc, boolean isController, boolean remove) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        out.writeBool(isController);
        out.writeInt(npc.getObjectId());
        if (!remove) {
            out.writeInt(npc.getTemplateId());
            npc.encode(out);
        }
        return out;
    }

    public static OutPacket npcAnimation(int npcId, byte oneTimeAction, byte chatIdx, int duration, MovementInfo movement, byte keyPadState) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.NPC_ANIMATION.getValue());
        out.writeInt(npcId);
        out.write(oneTimeAction);
        out.write(chatIdx);
        out.writeInt(duration);
        if (movement != null) {
            movement.encode(out);
        }
        return out;
    }

    public static OutPacket openShop(int npcId, int petTemplateId, NpcShop shop, List<NpcShopItem> repurchaseItems) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.NPC_SHOP_OPEN.getValue());
        out.writeInt(npcId);
        out.writeBool(petTemplateId != 0);
        if (petTemplateId != 0) {
            out.writeInt(petTemplateId);
        }
        shop.encode(out, repurchaseItems);
        return out;
    }

    public static OutPacket shopResult(ShopResultType type) {
        return shopResult(type, false, 0, 0, null, null);
    }

    public static OutPacket shopResult(ShopResultType type, NpcShop shop, List<NpcShopItem> repurchaseItems) {
        return shopResult(type, false, 0, 0, shop, repurchaseItems);
    }

    public static OutPacket shopResult(ShopResultType type, boolean repurchase, int index) {
        return shopResult(type, repurchase, index, 0, null, null);
    }


    public static OutPacket shopResult(ShopResultType type, boolean repurchase, int index, int itemId, NpcShop shop, List<NpcShopItem> repurchaseItems) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.NPC_SHOP_RESULT.getValue());
        out.write(type.getVal());
        switch (type) {
            case FullInvMsg:
            case Buy: {
                out.writeBool(repurchase);
                if (repurchase) {
                    out.write(index);
                } else {
                    out.writeInt(itemId);
                    out.writeInt(1000000);
                }
                out.writeInt(0);
                break;
            }
            case SellResult:
                shop.encode(out, repurchaseItems);
                break;
        }
        return out;
    }

    public static OutPacket avatarChangeSelector(int uPos, int itemId, List<Integer> options) {
        OutPacket out = new OutPacket(SendOpcode.CHAR_AVATAR_CHANGE_SELECT);
        out.write(1);
        out.writeInt(1);
        out.writeInt(uPos);
        out.writeInt(itemId);
        out.writeShort(0);
        out.write(options.size());
        options.forEach(out::writeInt);
        return out;
    }

    public static OutPacket avatarChangedResult(int itemId, short bodyPart, int before, int after) {
        OutPacket out = new OutPacket(SendOpcode.CHAR_AVATAR_CHANGE_RESULT);
        out.writeInt(itemId);
        out.write(1);
        out.write(1);
        out.writeInt(1);
        out.writeShort(bodyPart);
        out.writeInt(before);
        out.writeInt(after);
        return out;
    }

    public static OutPacket npcDisableInfo(List<Integer> npcs) {
        OutPacket out = new OutPacket(SendOpcode.LIMITED_NPC_DISABLE_INFO);
        out.write(npcs.size());
        for (Integer npcId : npcs) {
            out.writeInt(npcId);
        }
        return out;
    }
}
