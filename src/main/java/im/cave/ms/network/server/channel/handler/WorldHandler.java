package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.MapleSignIn;
import im.cave.ms.client.Trunk;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.QuickMoveInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.DimensionalMirror;
import im.cave.ms.enums.InstanceTableType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.TrunkOpType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.ChannelPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.packet.MaplePacketCreator;
import im.cave.ms.network.packet.PlayerPacket;
import im.cave.ms.network.packet.SummonPacket;
import im.cave.ms.network.server.Server;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static im.cave.ms.constants.QuestConstants.QUEST_EX_COMBO_KILL;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_SKILL_STATE;
import static im.cave.ms.constants.QuestConstants.SHARE_QUEST_EX_SIGNIN_LOG;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.server.channel.handler
 * @date 12/5 15:25
 */
public class WorldHandler {
    private static final Logger log = LoggerFactory.getLogger(WorldHandler.class);

    public static void handleInstanceTableRequest(InPacket inPacket, MapleClient c) {
        String requestStr = inPacket.readMapleAsciiString();
        int type = inPacket.readInt();
        int subType = inPacket.readInt();
        InstanceTableType itt = InstanceTableType.getByStr(requestStr);
        if (itt == null) {
            log.error(String.format("Unknown instance table type request %s, type %d, subType %d", requestStr, type, subType));
            return;
        }
        int value = 0;
        switch (itt) {
            // HyperSkills: both have the same requestStr. level = type * 5
            case HyperActiveSkill:
            case HyperPassiveSkill:
                if (subType == InstanceTableType.HyperActiveSkill.getSubType()) {
                    value = SkillConstants.getHyperActiveSkillSpByLv(type * 5);
                } else {
                    value = SkillConstants.getHyperPassiveSkillSpByLv(type * 5);
                }
                break;
            case HyperStatIncAmount:
                // type == level
                value = SkillConstants.getHyperStatSpByLv((short) type);
                break;
            case NeedHyperStatLv:
                // type == skill lv
                value = SkillConstants.getNeededSpForHyperStatSkill(type);
                break;
            case Skill_9200:
            case Skill_9201:
            case Skill_9202:
            case Skill_9203:
            case Skill_9204:
                // type == recommendSkillLevel - 1
                // subType == making skill level -1
//                value = MakingSkillRecipe.getSuccessProb(Integer.parseInt(requestStr), type + 1, chr.getMakingSkillLevel(Integer.parseInt(requestStr)));
                break;
            default:
                log.error(String.format("Unhandled instance table type request %s, type %d, subType %d", itt, type, subType));
                return;
        }
        c.announce(ChannelPacket.resultInstanceTable(requestStr, type, subType, value));

    }

    public static void handleMigrateToCashShopRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        inPacket.readByte(); // 00
        c.announce(MaplePacketCreator.getChannelChange(Server.getInstance().getCashShop(c.getWorld()).getPort()));
    }

    public static void handleBattleAnalysis(InPacket inPacket, MapleClient c) {
        byte opt = inPacket.readByte();
        if (opt == 1) {
            c.announce(MaplePacketCreator.startBattleAnalysis());
        }
    }

    public static void handleQuickMove(int npcId, MapleClient c) {
        List<QuickMoveInfo> quickMoveInfos = GameConstants.getQuickMoveInfos();
        QuickMoveInfo quickMoveInfo = Util.findWithPred(quickMoveInfos, q -> q.getTemplateID() == npcId);
        if (quickMoveInfo == null) {
            c.getPlayer().dropMessage("尝试HAck?");
            return;
        }

        NpcHandler.talkToNPC(c.getPlayer(), npcId);
    }

    public static void handleUnityPortalSelect(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(inPacket.readInt());
        int unityPortalId = inPacket.readInt();
        DimensionalMirror[] unityPortals = DimensionalMirror.values();
        DimensionalMirror unityPortal = Util.findWithPred(unityPortals, u -> u.getId() == unityPortalId);
        if (unityPortal == null) {
            player.dropMessage("尝试Hack?");
            return;
        }
        player.changeMap(unityPortal.getMapId());
    }

    public static void handleTrunkOperation(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Trunk trunk = player.getAccount().getTrunk();
        byte type = inPacket.readByte();
        TrunkOpType trunkOpType = TrunkOpType.getByVal(type);
        if (trunkOpType == null) {
            log.error(String.format("Unknown trunk request type %d.", type));
            return;
        }
        switch (trunkOpType) {
            case TrunkReq_GetItem:
                int pos = inPacket.readShort() - 1;
                short quantity = inPacket.readShort(); //todo
                if (pos >= 0 && pos < trunk.getItems().size()) {
                    Item item = trunk.getItems().get(pos);
                    if (player.getInventory(item.getInvType()).canPickUp(item)) {
                        trunk.removeItem(item);
                        player.addItemToInv(item);
                        InventoryType invType = item.getInvType();
                        List<Item> items = trunk.getItems(invType);
                        player.announce(ChannelPacket.getItemFromTrunk(items, trunk, invType));
                    } else {
                        player.announce(ChannelPacket.trunkMsg(TrunkOpType.TrunkRes_GetUnknown));
                    }
                }
                break;
            case TrunkReq_Money:
                long amount = inPacket.readLong();
                if (trunk.getMoney() >= amount) {
                    trunk.addMoney(-amount);
                    player.addMeso(amount);
                    player.announce(ChannelPacket.getMoneyFromTrunk(amount, trunk));
                } else {
                    player.announce(ChannelPacket.trunkMsg(TrunkOpType.TrunkRes_GetNoMoney));
                }
                break;
        }
    }

    public static void handleChangeCharRequest(InPacket inPacket, MapleClient c) {
        String account = inPacket.readMapleAsciiString();
        if (!account.equals(c.getAccount().getAccount())) {
            c.getPlayer().dropMessage("有问题");
            return;
        }
        c.announce(LoginPacket.changePlayer(c));
    }

    public static void handleComboKill(InPacket inPacket, MapleClient c) {
        int questId = inPacket.readInt();
        int combo = inPacket.readInt();
        inPacket.readInt();//unk
        MapleCharacter player = c.getPlayer();
        HashMap<String, String> options = new HashMap<>();
        options.put("ComboK", String.valueOf(combo));
        player.addQuestEx(questId, options);
        c.announce(PlayerPacket.message(MessageType.QUEST_RECORD_EX_MESSAGE, QUEST_EX_COMBO_KILL, player.getQuestsExStorage().get(QUEST_EX_COMBO_KILL), (byte) 0));
        c.announce(PlayerPacket.updateQuestEx(QUEST_EX_COMBO_KILL));

    }

    public static void handleSignIn(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Account account = player.getAccount();
        int itemId = inPacket.readInt();
        Map<String, String> log = account.getSharedQuestEx().get(SHARE_QUEST_EX_SIGNIN_LOG);
        if (log == null) {
            HashMap<String, String> options = new HashMap<>();
            options.put("count", "0");
            options.put("day", "0");
            options.put("date", String.valueOf(DateUtil.getDate()));
            account.addSharedQuestEx(SHARE_QUEST_EX_SIGNIN_LOG, options, true);
            c.announce(PlayerPacket.message(MessageType.WORLD_SHARE_RECORD_MESSAGE, SHARE_QUEST_EX_SIGNIN_LOG, account.getSharedQuestExStorage().get(QUEST_EX_SKILL_STATE), (byte) 0));
        }
        String count = account.getSharedQuestEx().get(SHARE_QUEST_EX_SIGNIN_LOG).get("count");
        String day = account.getSharedQuestEx().get(SHARE_QUEST_EX_SIGNIN_LOG).get("day");
        String date = account.getSharedQuestEx().get(SHARE_QUEST_EX_SIGNIN_LOG).get("date");
        if (date.equals(String.valueOf(DateUtil.getDate()))) {
            player.dropMessage("已经签到过了?");
        } else {
            MapleSignIn.SignInRewardInfo signRewardInfo = MapleSignIn.getSignRewardInfo(Integer.parseInt(day));
            if (signRewardInfo == null || itemId != signRewardInfo.getItemId()) {
                player.dropMessage("签到出错,请检查");
                return;
            }
            date = String.valueOf(DateUtil.getDate());
            count = String.valueOf(Integer.parseInt(count) + 1);
            day = String.valueOf(Integer.parseInt(day) + 1);
            HashMap<String, String> options = new HashMap<>();
            options.put("count", count);
            options.put("day", day);
            options.put("date", date);
            account.addSharedQuestEx(SHARE_QUEST_EX_SIGNIN_LOG, options, true);
            c.announce(PlayerPacket.message(MessageType.WORLD_SHARE_RECORD_MESSAGE, SHARE_QUEST_EX_SIGNIN_LOG, account.getSharedQuestExStorage().get(QUEST_EX_SKILL_STATE), (byte) 0));
            Item item = ItemData.getItemCopy(itemId, false);
            item.setQuantity(signRewardInfo.getQuantity());
            player.addItemToInv(item);
            c.announce(MapleSignIn.getSignInRewardPacket(2, itemId));
            c.announce(MapleSignIn.getSignInRewardPacket(0, itemId));
        }
    }

    public static void handleSummonMove(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        int objId = inPacket.readInt();
        MapleMapObj obj = player.getMap().getObj(objId);
        if (obj instanceof Summon) {
            Summon summon = (Summon) obj;
            MovementInfo movementInfo = new MovementInfo(inPacket);
            movementInfo.applyTo(summon);
            player.getMap().broadcastMessage(player, SummonPacket.summonMove(player.getId(), objId, movementInfo), false);
        }
    }
}