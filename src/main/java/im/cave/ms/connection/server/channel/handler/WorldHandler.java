package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.MapleSignIn;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.job.JobManager;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.MobAttackInfo;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.QuickMoveInfo;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.client.multiplayer.Express;
import im.cave.ms.client.multiplayer.MapleNotes;
import im.cave.ms.client.multiplayer.friend.Friend;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.guilds.GuildMember;
import im.cave.ms.client.multiplayer.guilds.GuildSkill;
import im.cave.ms.client.multiplayer.miniroom.ChatRoom;
import im.cave.ms.client.multiplayer.miniroom.TradeRoom;
import im.cave.ms.client.multiplayer.party.Party;
import im.cave.ms.client.multiplayer.party.PartyMember;
import im.cave.ms.client.multiplayer.party.PartyResult;
import im.cave.ms.client.storage.Trunk;
import im.cave.ms.configs.Config;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.AndroidPacket;
import im.cave.ms.connection.packet.AuctionPacket;
import im.cave.ms.connection.packet.CashShopPacket;
import im.cave.ms.connection.packet.FamiliarPacket;
import im.cave.ms.connection.packet.LoginPacket;
import im.cave.ms.connection.packet.MessagePacket;
import im.cave.ms.connection.packet.MiniRoomPacket;
import im.cave.ms.connection.packet.QuestPacket;
import im.cave.ms.connection.packet.SummonPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.UserRemote;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.packet.opcode.RecvOpcode;
import im.cave.ms.connection.packet.result.ExpressResult;
import im.cave.ms.connection.packet.result.GuildResult;
import im.cave.ms.connection.packet.result.OnlineRewardResult;
import im.cave.ms.connection.server.Server;
import im.cave.ms.connection.server.auction.Auction;
import im.cave.ms.connection.server.cashshop.CashShopServer;
import im.cave.ms.connection.server.channel.MapleChannel;
import im.cave.ms.connection.server.world.World;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.AuctionAction;
import im.cave.ms.enums.BroadcastMsgType;
import im.cave.ms.enums.ChatRoomType;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.DimensionalMirror;
import im.cave.ms.enums.ExpressAction;
import im.cave.ms.enums.FieldOption;
import im.cave.ms.enums.FriendFlag;
import im.cave.ms.enums.FriendType;
import im.cave.ms.enums.GuildType;
import im.cave.ms.enums.InstanceTableType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.MapleNotesType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.PartyType;
import im.cave.ms.enums.ServerType;
import im.cave.ms.enums.TradeRoomType;
import im.cave.ms.enums.TrunkOpType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Pair;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static void handleInstanceTableRequest(InPacket in, MapleClient c) {
        String requestStr = in.readMapleAsciiString();
        int type = in.readInt();
        int subType = in.readInt();
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
        c.announce(WorldPacket.resultInstanceTable(requestStr, type, subType, value));
    }

    public static void handleMigrateToCashShopRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        in.readByte();
        c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
        player.enterCashShop();
    }

    public static void handleBattleAnalysis(InPacket in, MapleClient c) {
        byte opt = in.readByte();
        if (opt == 1) {
            c.announce(WorldPacket.startBattleAnalysis());
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

    public static void handleUnityPortalSelect(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(in.readInt());
        int unityPortalId = in.readInt();
        DimensionalMirror[] unityPortals = DimensionalMirror.values();
        DimensionalMirror unityPortal = Util.findWithPred(unityPortals, u -> u.getId() == unityPortalId);
        if (unityPortal == null) {
            player.dropMessage("尝试Hack?");
            return;
        }
        player.changeMap(unityPortal.getMapId());
    }

    public static void handleTrunkOperation(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Trunk trunk = player.getAccount().getTrunk();
        byte type = in.readByte();
        TrunkOpType trunkOpType = TrunkOpType.getByVal(type);
        if (trunkOpType == null) {
            log.error(String.format("Unknown trunk request type %d.", type));
            return;
        }
        switch (trunkOpType) {
            case TrunkReq_GetItem: {
                if (player.getMeso() < player.getNpc().getTrunkGet()) {
                    player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_GetNoMoney));
                    return;
                }
                byte inv = in.readByte();
                byte index = in.readByte();
                short quantity = in.readShort();
                List<Item> items = trunk.getItems(InventoryType.getTypeById(inv));
                if (index >= 0 && index < items.size()) {
                    Item item = items.get(index);
                    if (player.getInventory(item.getInvType()).canPickUp(item)) {
                        trunk.removeItem(item, quantity);
                        player.addItemToInv(item);
                        InventoryType invType = item.getInvType();
                        player.announce(WorldPacket.getItemFromTrunk(trunk, invType));
                        player.deductMoney(player.getNpc().getTrunkGet());
                    } else {
                        player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_PutNoSpace));
                    }
                } else {
                    player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_GetUnknown));
                }
                break;
            }
            case TrunkReq_PutItem: {
                if (player.getMeso() < player.getNpc().getTrunkPut()) {
                    player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_PutNoMoney));
                    return;
                }
                short pos = in.readShort();
                int itemId = in.readInt();
                short quantity = in.readShort();
                InventoryType invType = ItemConstants.getInvTypeByItemId(itemId);
                if (invType == null) {
                    player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_PutUnknown));
                    return;
                }
                Inventory inventory = player.getInventory(invType);
                Item item = inventory.getItem(pos);
                if (item == null || item.getItemId() != itemId) {
                    player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_PutUnknown));
                    return;
                }
                player.consumeItem(itemId, quantity, true);
                trunk.putItem(item, quantity);
                player.announce(WorldPacket.putItemToTrunk(trunk, item.getInvType()));
                player.deductMoney(player.getNpc().getTrunkPut());
                break;
            }
            case TrunkReq_Money: {
                long amount = in.readLong();
                long trunkMoney = trunk.getMeso();
                long meso = player.getMeso();
                if (trunkMoney - amount < 0 || meso + amount < 0) {
                    player.announce(WorldPacket.trunkMsg(TrunkOpType.TrunkRes_GetNoMoney));
                    return;
                }
                trunk.addMeso(-amount);
                player.addMeso(amount);
                player.announce(WorldPacket.getMoneyFromTrunk(amount, trunk));
                break;
            }
            case TrunkReq_SortItem: {
                trunk.sort(true);
                player.announce(WorldPacket.sortedTrunkItems(trunk));
                break;
            }
            case TrunkReq_CloseDialog: {
                player.setNpc(null);
                player.setConversation(false);
                break;
            }
        }
    }

    public static void handleChangeCharRequest(InPacket in, MapleClient c) {
        String account = in.readMapleAsciiString();
        if (!account.equals(c.getAccount().getAccount())) {
            c.close();
            return;
        }
        c.announce(LoginPacket.changePlayer(c));
    }

    public static void handleComboKill(InPacket in, MapleClient c) {
        int questId = in.readInt();
        int combo = in.readInt();
        in.readInt();//unk
        MapleCharacter player = c.getPlayer();
        HashMap<String, String> options = new HashMap<>();
        options.put("ComboK", String.valueOf(combo));
        player.addQuestExAndSendPacket(questId, options);
        c.announce(QuestPacket.updateQuestEx(QUEST_EX_COMBO_KILL));
    }

    public static void handleSignIn(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Account account = player.getAccount();
        int itemId = in.readInt();
        Map<String, String> log = account.getSharedQuestEx().get(SHARE_QUEST_EX_SIGNIN_LOG);
        if (log == null) {
            HashMap<String, String> options = new HashMap<>();
            options.put("count", "0");
            options.put("day", "0");
            options.put("date", String.valueOf(DateUtil.getDate()));
            account.addSharedQuestEx(SHARE_QUEST_EX_SIGNIN_LOG, options, true);
            c.announce(UserPacket.message(MessageType.WORLD_SHARE_RECORD_MESSAGE, SHARE_QUEST_EX_SIGNIN_LOG, account.getSharedQuestExStorage().get(QUEST_EX_SKILL_STATE), (byte) 0));
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
            c.announce(UserPacket.message(MessageType.WORLD_SHARE_RECORD_MESSAGE, SHARE_QUEST_EX_SIGNIN_LOG, account.getSharedQuestExStorage().get(QUEST_EX_SKILL_STATE), (byte) 0));
            Item item = ItemData.getItemCopy(itemId, false);
            item.setQuantity(signRewardInfo.getQuantity());
            player.addItemToInv(item);
            c.announce(MapleSignIn.getSignInRewardPacket(2, itemId));
            c.announce(MapleSignIn.getSignInRewardPacket(0, itemId));
        }
    }

    public static void handleSummonMove(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        int objId = in.readInt();
        MapleMapObj obj = player.getMap().getObj(objId);
        if (obj instanceof Summon) {
            Summon summon = (Summon) obj;
            MovementInfo movementInfo = new MovementInfo(in);
            movementInfo.applyTo(summon);
            player.getMap().broadcastMessage(player, SummonPacket.summonMove(player.getId(), objId, movementInfo), false);
        }
    }

    public static void handleChangeChannelRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        byte channel = in.readByte();
        in.readInt(); //tick
        if (c.getChannelId() == channel) {
            c.close(); //hack
            return;
        }
        //todo
        c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
        player.changeChannel(channel);
    }

    public static void handleUserEnterServer(InPacket in, MapleClient c, ServerType type) {
        int worldId = in.readInt();
        int charId = in.readInt();
        byte[] machineId = in.read(16);
        Pair<Byte, MapleClient> transInfo = Server.getInstance().getClientTransInfo(charId);
        if (transInfo == null) {
            c.close();
            return;
        }
        MapleClient oldClient = transInfo.getRight();
        if (!Arrays.equals(oldClient.getMachineID(), machineId)) {
//            c.close();
            log.debug("机器码改变 可能同时登录导致.");
//            return;
        }
        Account account = oldClient.getAccount();
        MapleCharacter player = oldClient.getPlayer();
        if (player == null || player.getId() != charId) {
            player = account.getCharacter(charId);
        }
        byte channel = transInfo.getLeft();
        c.setMachineID(machineId);
        c.setWorldId(worldId);
        c.setChannelId(channel);
        c.setAccount(account);
        Server.getInstance().removeTransfer(charId);
        player.setClient(c);
        player.setOnline(true);
        player.setAccount(c.getAccount());
        player.setChannel(channel);
        c.setPlayer(player);
        c.setLoginStatus(LoginStatus.LOGGEDIN);
        Server.getInstance().addAccount(c.getAccount());
        account.setOnlineChar(player);
        c.announce(UserPacket.initOpCodeEncryption(c));
        switch (type) {
            case CHANNEL: {
                MapleChannel mapleChannel = c.getMapleChannel();
                mapleChannel.addPlayer(player);
                player.setJobHandler(JobManager.getJobById(player.getJob(), player));
                c.announce(UserPacket.updateEventNameTag()); //updateEventNameTag
                c.announce(UserPacket.setSkillCoolTime(player));
                //todo init guild

                if (player.getHp() <= 0) {
                    player.setMapId(player.getMap().getReturnMap());
                    player.heal(50);
                }

                player.initBaseStats();
                player.buildQuestEx();
                //todo 分散到之后的请求中
                player.initMapTransferCoupon();
                player.changeMap(player.getMapId(), true);
                c.announce(UserPacket.keymapInit(player));
                c.announce(LoginPacket.account(player.getAccount()));
                c.announce(UserPacket.quickslotInit(player));
                c.announce(UserPacket.macroSysDataInit(player));
                c.announce(UserPacket.updateMaplePoint(player));
                c.getAccount().buildSharedQuestEx();

                c.announce(MapleSignIn.signinInit());

                Party party = player.getMapleWorld().getPartyById(player.getPartyId());
                if (party != null) {
                    player.setParty(party);
                    party.updatePartyMemberInfoByChr(player);
                } else {
                    player.setPartyId(0);
                    //todo 这里应该发送一个空的组队消息包
                }
                //todo init friend
                c.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Inbox, player.getInBox(), 0));
                c.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Outbox, player.getOutbox(), 0));
                c.announce(MessagePacket.broadcastMsg(Config.worldConfig.getWorldInfo(player.getWorld()).server_message, BroadcastMsgType.SLIDE));
                c.announce(WorldPacket.onlineRewardResult(OnlineRewardResult.onlineRewardsList(player)));

                player.announce(UserPacket.remainingMapTransferCoupon(player));

                if (player.getExpresses().size() > 0) {
                    c.announce(WorldPacket.expressResult(ExpressResult.haveNewExpress(player.getNewExpress())));
                }

                player.initPotionPot();


                break;
            }
            case CASHSHOP:
                CashShopServer cashShop = player.getMapleWorld().getCashShop();
                cashShop.addChar(player);
                c.announce(CashShopPacket.getWrapToCashShop(player));
                c.announce(CashShopPacket.setCashShop(cashShop));
                c.announce(CashShopPacket.initLockerDone(player.getAccount()));
                c.announce(CashShopPacket.initGiftDone());
                c.announce(CashShopPacket.initWishDone(player));
                c.announce(CashShopPacket.queryCashResult(player.getAccount()));
                c.announce(CashShopPacket.initCashShopEvent());
                break;
            case AUCTION:
                Auction auction = player.getMapleWorld().getAuction();
                c.announce(AuctionPacket.getWrapToAuction(player));
        }
    }

    public static void handleTradeRoom(InPacket in, MapleClient c) {
        byte val = in.readByte();
        MapleCharacter player = c.getPlayer();
        TradeRoomType type = TradeRoomType.getByVal(val);
        if (type == null) {
            log.error("Unknown TradeRoom Type {}", val);
            return;
        }
        TradeRoom tradeRoom = (TradeRoom) player.getMiniRoom();
        switch (type) {
            case TradeInviteRequest: {
                int charId = in.readInt();
                MapleCharacter other = player.getMap().getCharById(charId);
                if (other == null) {
                    player.chatMessage("Could not find that player.");
                    return;
                }
                other.announce(MiniRoomPacket.tradeInvite(player));
                break;
            }
            case Accept: {
                int charId = in.readInt();
                in.readShort(); // 00 00
                MapleCharacter other = player.getMap().getCharById(charId);
                if (other == null) {
                    other = Server.getInstance().getCharById(charId, player.getWorld());
                    if (other == null) {
                        player.chatMessage("交易已取消");
                        return;
                    }
                    if (other.getMiniRoom() != null) {
                        ((TradeRoom) other.getMiniRoom()).cancelTrade();
                        return;
                    }
                }
                if (other.getMiniRoom() == null) {
                    player.chatMessage("交易已取消");
                    return;
                }
                player.setMiniRoom(other.getMiniRoom());
                tradeRoom = ((TradeRoom) player.getMiniRoom());
                tradeRoom.setOther(player);
                player.announce(MiniRoomPacket.enterTrade(tradeRoom, 1));
                tradeRoom.getChr().announce(MiniRoomPacket.acceptTradeInvite(player));
                tradeRoom.sendTips();
                break;
            }
            case InviteResultStatic: { // 拒绝邀请
                int charId = in.readInt();
                MapleCharacter other = player.getMap().getCharById(charId);
                if (other == null) {
                    return;
                }
                ((TradeRoom) other.getMiniRoom()).cancelTrade();
                break;
            }
            case ExitTrade: {
                if (tradeRoom != null) {
                    tradeRoom.cancelTrade();
                }
                break;
            }
            case Chat: {
                player.setTick(in.readInt());
                String msg = in.readMapleAsciiString();
                if (tradeRoom == null) {
                    player.chatMessage("You are currently not in a room.");
                    return;
                }
                String msgWithName = String.format("%s : %s", player.getName(), msg);
                player.announce(MiniRoomPacket.chat(0, msgWithName, player));
                tradeRoom.getOtherChar(player).announce(MiniRoomPacket.chat(1, msgWithName, player));
                break;
            }
            case Create: {
                in.readShort(); // 04 00
                tradeRoom = new TradeRoom(player);
                player.setMiniRoom(tradeRoom);
                player.announce(MiniRoomPacket.enterTrade(tradeRoom, 0));
                break;
            }
            case PlaceItem:
            case PlaceItem_2:
            case PlaceItem_3:
            case PlaceItem_4: {
                byte invType = in.readByte();
                short bagPos = in.readShort();
                short quantity = in.readShort();
                byte tradePos = in.readByte();
                InventoryType ivt = InventoryType.getTypeById(invType);
                if (ivt == null) {
                    return;
                }
                Item item = player.getInventory(ivt).getItem(bagPos);
                if (item.getQuantity() < quantity) {
                    return;
                }
                if (!item.isTradable()) {
                    return;
                }
                if (player.getMiniRoom() == null) {
                    player.chatMessage("You are currently not trading.");
                    return;
                }
                Item offer = ItemData.getItemCopy(item.getItemId(), false);
                offer.setQuantity(quantity);
                if (tradeRoom.canAddItem(player)) {
                    int consumed = quantity > item.getQuantity() ? 0 : item.getQuantity() - quantity;
                    item.setQuantity(consumed + 1); // +1 because 1 gets consumed by consumeItem(item)
                    player.consumeItem(item);
                    tradeRoom.addItem(player, tradePos, offer);
                    MapleCharacter other = tradeRoom.getOtherChar(player);
                    player.announce(MiniRoomPacket.putItem(0, tradePos, offer));
                    other.announce(MiniRoomPacket.putItem(1, tradePos, offer));
                }
                break;
            }
            case SetMesos:
            case SetMesos_2:
            case SetMesos_3:
            case SetMesos_4: {
                long meso = in.readLong();
                if (tradeRoom == null) {
                    player.chatMessage("You are currently not trading.");
                    return;
                }
                if (meso < 0 || meso > player.getMeso()) {
                    return;
                }
                player.deductMoney(meso);
                player.addMeso(tradeRoom.getMoney(player));
                tradeRoom.putMoney(player, meso);
                MapleCharacter other = tradeRoom.getOtherChar(player);
                player.announce(MiniRoomPacket.putMeso(0, meso));
                other.announce(MiniRoomPacket.putMeso(1, meso));
                break;
            }
            case TradeConfirm:
            case TradeConfirm2:
            case TradeConfirm3: {
                MapleCharacter other = tradeRoom.getOtherChar(player);
                other.announce(MiniRoomPacket.tradeConfirm());
                if (tradeRoom.hasConfirmed(player)) {
                    boolean success = tradeRoom.completeTrade();
                    if (success) {
                        player.announce(MiniRoomPacket.tradeComplete(0));
                        other.announce(MiniRoomPacket.tradeComplete(1));
                    } else {
                        tradeRoom.cancelTrade();
                    }
                    player.setMiniRoom(null);
                    other.setMiniRoom(null);
                } else {
                    tradeRoom.addConfirmedPlayer(player);
                }
                break;
            }
            case TradeConfirmRemoteResponse:
                break;
        }
    }

    public static void handleUserFieldTransferRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        MapleMap map = player.getMap();

        if ((map.getFieldLimit() & FieldOption.TeleportItemLimit.getVal()) > 0 ||
                (map.getFieldLimit() & FieldOption.MigrateLimit.getVal()) > 0 ||
                (map.getFieldLimit() & FieldOption.PortalScrollLimit.getVal()) > 0) {
            player.chatMessage("You may not warp to that map.");
            UserPacket.enableActions();
            return;
        }
        int mapId = in.readInt();
        if (mapId == 7860) { //匠人街
            player.changeMap(GameConstants.ARDENTMILL);
        } else if (mapId == 26015) { //家族中心
            player.changeMap(200000301);
        }
    }

    public static void handlePartyRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        PartyType type = PartyType.getByVal(in.readByte());
        Party party = player.getParty();
        if (type == null) {
            log.error("未知组队请求.");
            return;
        }
        switch (type) {
            case PartyReq_CreateNewParty: {
                if (party != null) {
                    player.chatMessage("You are already in a party.");
                    return;
                }
                boolean appliable = in.readByte() != 0; //公开
                String name = in.readMapleAsciiString();
                party = Party.createNewParty(appliable, name, player.getMapleWorld());
                party.addPartyMember(player);
                party.broadcast(WorldPacket.partyResult(PartyResult.createNewParty(party)));
                break;
            }
            case PartyReq_WithdrawParty: {
                if (party.hasCharAsLeader(player)) {
                    party.disband();
                } else {
                    PartyMember leaver = party.getPartyMemberByID(player.getId());
                    party.broadcast(WorldPacket.partyResult(PartyResult.withdrawParty(party, leaver, true, false)));
                    party.removePartyMember(leaver);
                    party.updateFull();
                    player.enableAction();
                }
                break;
            }
            case PartyReq_InviteParty: {
                if (party == null) {
                    party = Party.createNewParty(true, GameConstants.DEFAULT_PARTY_NAME, player.getMapleWorld());
                    party.addPartyMember(player);
                    party.broadcast(WorldPacket.partyResult(PartyResult.createNewParty(party)));
                }
                String name = in.readMapleAsciiString();
                MapleCharacter other = Server.getInstance().findCharByName(name, player.getWorld());
                if (other == null) {
                    player.chatMessage("Can't find this player");
                    player.enableAction();
                    return;
                }
                if (party.hasInvited(other)) {
                    player.announce(MessagePacket.broadcastMsg(String.format("已向'%s'玩家发送过邀请，请耐心等待。", other.getName()), BroadcastMsgType.EVENT));
                } else {
                    other.announce(WorldPacket.partyResult(PartyResult.inviteIntrusion(party, player)));
                    player.announce(WorldPacket.partyResult(PartyResult.inviteSent(name)));
                }
                break;
            }
            case PartyReq_KickParty: {
                if (party == null) {
                    break;
                }
                int charId = in.readInt();
                party.expel(charId);
                break;
            }
            case PartyReq_PartySetting: {
                if (party == null) {
                    return;
                }
                boolean appliable = in.readByte() != 0;
                String name = in.readMapleAsciiString();
                party.broadcast(WorldPacket.partyResult(PartyResult.partySetting(appliable, name)));
            }
        }
    }

    public static void handlePartyInviteResponse(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        PartyType type = PartyType.getByVal(in.readByte());
        Party party;
        if (type == null) {
            log.error("未知组队请求.");
            return;
        }
        switch (type) {
            case PartyRes_InviteParty_Sent: { //收到组队邀请
                int partyId = in.readInt(); //party ID
                party = player.getMapleWorld().getPartyById(partyId);
                if (party != null) {
                    party.addInvitedChar(player);
                }
                break;
            }
            case PartyRes_InviteParty_Rejected: { //拒绝组队邀请
                int partyId = in.readInt();
                party = player.getMapleWorld().getPartyById(partyId);
                if (party != null) { //队长才能邀请人是吧
                    party.getPartyLeader().getChr().chatMessage(String.format("'%s'玩家拒绝了组队招待.", player.getName()));
                    party.removeInvited(player);
                }
                break;
            }
            case PartyRes_InviteParty_Accepted: {
                int partyId = in.readInt();
                party = player.getMapleWorld().getPartyById(partyId);
                if (party != null) {
                    party.addPartyMember(player);
                    party.removeInvited(player);
                }
                break;
            }
        }

    }

    public static void handleAndroidMove(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Android android = player.getAndroid();
        if (android == null) {
            return;
        }
        in.readInt();
        MovementInfo mi = new MovementInfo(in);
        mi.applyTo(android);
        player.getMap().broadcastMessage(player, AndroidPacket.move(android, mi), false);
    }

    public static void handleAndroidActionSet(InPacket in, MapleClient c) {
        byte b = in.readByte();
        byte b1 = in.readByte();
    }

    public static void handleFriendRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte val = in.readByte();
        FriendType ft = FriendType.getByVal(val);
        if (ft == null) {
            player.dropMessage("Unhandled request" + val);
            return;
        }
        switch (ft) {
            case FriendReq_SetFriend: {
                String name = in.readMapleAsciiString();
                MapleCharacter other = player.getMapleWorld().getCharByName(name);
                String group = in.readMapleAsciiString();
                String memo = in.readMapleAsciiString();
                boolean account = in.readByte() != 0;
                String nick = "";
                if (account) {
                    nick = in.readMapleAsciiString();
                    if (nick.equalsIgnoreCase("")) {
                        nick = name;
                    }
                }
                Friend friend = new Friend();
                friend.setFriendId(other.getId());
                friend.setGroup(group);
                friend.setMemo(memo);
                friend.setFriendAccountId(other.getAccId());
                if (account) {
                    friend.setNickname(nick);
                    friend.setFlag(FriendFlag.AccountFriendOffline);
                    player.getAccount().addFriend(friend);
                } else {
                    player.getFriends().add(friend);
                    friend.setFlag(FriendFlag.FriendOffline);
                }
                Friend otherFriend = new Friend();
                otherFriend.setFriendId(player.getId());
                otherFriend.setName(player.getName());
                otherFriend.setFriendAccountId(player.getAccId());
                otherFriend.setGroup(GameConstants.DEFAULT_FRIEND_GROUP);
                if (account) {
                    otherFriend.setNickname(player.getName());
                    otherFriend.setFlag(FriendFlag.AccountFriendRequest);
                    other.getAccount().addFriend(otherFriend);
                } else {
                    otherFriend.setFlag(FriendFlag.FriendRequest);
                    other.addFriend(otherFriend);
                }
                c.announce(WorldPacket.friendResult(FriendType.FriendRes_SendSingleFriendInfo, Collections.singletonList(friend)));
                c.announce(WorldPacket.friendResult(FriendType.FriendRes_SetFriend_Done, Collections.singletonList(friend)));
                other.announce(WorldPacket.friendResult(FriendType.FriendRes_Invite, Collections.singletonList(otherFriend)));
                break;
            }
            case FriendReq_DeleteFriend: {
                int charId = in.readInt();
                player.removeFriendByID(charId);
                player.announce(WorldPacket.friendResult(FriendType.FriendRes_DeleteFriend_Done, Collections.singletonList(new Friend())));
                break;
            }
            case FriendReq_RefuseFriend: {
                int charId = in.readInt();
                player.announce(WorldPacket.friendResult(FriendType.FriendRes_DeleteFriend_Done, null));
                break;
            }
            case FriendReq_IncMaxCount: {
                if (player.getBuddyCapacity() < 125) {
                    player.setBuddyCapacity((byte) (player.getBuddyCapacity() + 5));
                }
                player.announce(WorldPacket.friendResult(FriendType.FriendRes_IncMaxCount_Done, player, null));
                break;
            }
            case FriendReq_SetOffline: {
//                player.setOnline(false);
                //todo
                break;
            }
        }

    }

    public static void handleRequestRecommendPlayers(MapleClient c) {
        MapleCharacter player = c.getPlayer();
        MapleMap map = player.getMap();
        List<MapleCharacter> players = new ArrayList<>();
        for (MapleCharacter character : map.getCharacters()) {
            if (character.getParty() == null && player != character) {
                players.add(character);
                players.sort(Comparator.comparingInt(MapleCharacter::getLevel));
            }
        }
        player.announce(WorldPacket.recommendPlayers(players));
    }

    public static void handleChatRoom(InPacket in, MapleClient c) {
        byte val = in.readByte();
        MapleCharacter player = c.getPlayer();
        ChatRoomType type = ChatRoomType.getByVal(val);
        if (type == null) {
            log.error("Unknown ChatRoomType Type {}", val);
            return;
        }
        ChatRoom chatRoom = (ChatRoom) player.getMiniRoom();
        switch (type) {
            case Join: {
                in.readByte(); // 06
                chatRoom = new ChatRoom(player);
                player.setMiniRoom(chatRoom);
                break;
            }
            case ChatInviteRequest: {
                String name = in.readMapleAsciiString();
                MapleCharacter chr = player.getMapleWorld().getCharByName(name);
                if (chr == null) {
                    player.chatMessage(ChatType.Notice, "角色不存在");
                    return;
                }
                chr.announce(MiniRoomPacket.chatRoomInvite(player.getName()));
                chatRoom.broadcast(MiniRoomPacket.chatRoomInviteTip(name, false), player);
                chr.announce(MiniRoomPacket.chatRoomInviteTip(name, true));
                break;
            }
        }
    }

    public static void handleSendMapleNotes(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte val = in.readByte();
        String toCharName = in.readMapleAsciiString();
        MapleCharacter other = player.getMapleWorld().getCharByName(toCharName);
        if (other == null) {
            player.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Send_Fail, null, 1));
            return;
        }
        String msg = in.readMapleAsciiString();
        boolean isGm = in.readByte() != 0;
        player.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Send_Success, null, 0));
        MapleNotes message = MapleNotes.builder()
                .fromId(player.getId())
                .fromChr(player.getName())
                .toId(other.getId())
                .toChr(other.getName())
                .msg(msg)
                .createdTime(DateUtil.getFileTime(System.currentTimeMillis())).build();
        player.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Add_Sent, Collections.singletonList(message), 0));
    }

    public static void handleMapleNotesRequest(InPacket in, MapleClient c) {
        byte val = in.readByte();
        MapleCharacter player = c.getPlayer();
        MapleNotesType type = MapleNotesType.getByVal(val);
        if (type == null) {
            player.dropMessage("未知错误");
            return;
        }
        switch (type) {
            case Req_Read: {
                byte status = in.readByte();
                int msgId = in.readInt();
                MapleNotes message = Util.findWithPred(player.getInBox(), mapleMessage -> mapleMessage.getId() == msgId);
                if (message != null) {
                    message.setStatus(status);
                }
                player.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_InNotes_Read, null, msgId));
                break;
            }
            case Req_Delete_Sent_Notes: {
                in.readInt();
                int msgId = in.readInt();
                player.getOutbox().removeIf(msg -> msg.getId() == msgId);
                player.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Delete_Sent_Success, null, msgId));
            }
            case Req_Delete_Received_Notes: {
                in.readShort();
                in.readByte();
                int msgId = in.readInt();
                player.getInBox().removeIf(msg -> msg.getId() == msgId);
                player.announce(MessagePacket.mapleNotesResult(MapleNotesType.Res_Delete_Received_Success, null, msgId));
            }
        }
    }

    public static void handleMapleExpressRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        ExpressAction action = ExpressAction.getByVal(in.readByte());
        if (action == null) {
            player.dropMessage("error");
            player.enableAction();
            return;
        }
        switch (action) {
            case Req_Load:
                in.readShort(); // 01 00
                in.readByte(); // 20
                in.readInt(); // 01 00 00 00
                player.announce(WorldPacket.expressResult(ExpressResult.initLocker(player)));
                break;
            case Req_PickUp:
                int expressId = in.readInt();

                //todo
                player.getExpresses().removeIf(e -> e.getId() == expressId);
                player.announce(WorldPacket.expressResult(ExpressResult.remove(expressId, 0)));
                break;
            case Req_Drop:
                expressId = in.readInt();
                player.getExpresses().removeIf(e -> e.getId() == expressId);
                player.announce(WorldPacket.expressResult(ExpressResult.remove(expressId, 3)));
                break;
            case Req_Send_Normal:
                InventoryType invType = InventoryType.getTypeById(in.readByte());
                short pos = in.readShort();
                Item item = null;
                if (invType != null) {
                    item = player.getInventory(invType).getItem(pos);
                }
                boolean hasItem = in.readShort() != 0;
                if (hasItem && item == null) {
                    player.dropMessage("hack");
                    return;
                }
                int meso = in.readInt();
                int cost = 5000;
                if (meso > 0) {
                    cost += meso * 0.05;
                }
                if (player.getMeso() <= cost) {
                    player.enableAction();
                    return;
                }
                player.deductMoney(cost);
                String toCharName = in.readMapleAsciiString();

                if (MapleCharacter.nameValidate(toCharName) != 1) {
                    player.announce(WorldPacket.expressResult(ExpressResult.message(ExpressAction.Res_Please_Check_Name)));
                    return;
                }
                MapleCharacter toChar = MapleCharacter.getCharByName(toCharName);
                long now = DateUtil.getFileTime(System.currentTimeMillis());

                Express express = Express.builder()
                        .toId(toChar.getId()).toChar(toChar.getName())
                        .fromId(player.getId()).fromChar(player.getName())
                        .expiredDate(now + 30 * DateUtil.DAY)
                        .message(null).meso(meso).item(item)
                        .createdDate(now)
                        .build();
                DataBaseManager.saveToDB(express);
                player.announce(WorldPacket.expressResult(ExpressResult.message(ExpressAction.Res_Send_Success)));
                player.announce(WorldPacket.expressResult(ExpressResult.message(ExpressAction.Res_Send_Done)));
                break;
            case Req_Close_Dialog:
        }

    }

    public static void handleCheckTrickOrTreatRequest(InPacket in, MapleClient c) {
        String charName = in.readMapleAsciiString();
        MapleCharacter player = c.getPlayer();
        MapleCharacter chr = player.getMap().getCharByName(charName);
        c.announce(WorldPacket.checkTrickOrTreatResult(chr != null));
    }

    public static void handleFamiliarRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Familiar oldFamiliar = player.getFamiliar();
        in.readByte(); // 5  未知
        short size = in.readShort();
        if (in.available() != size) {
            return;
        }
        int act = in.readInt();
        switch (act) {
            case 1: //active
                short type = in.readShort();
                switch (type) {
                    case 1: //召唤
                        int id = in.readInt();
                        Familiar newFamiliar = player.getFamiliar(id);
                        player.setFamiliar(newFamiliar);
                        player.announce(FamiliarPacket.spawnFamiliar(oldFamiliar, newFamiliar, player));
                        break;
                    case 2: //收回
                        player.setFamiliar(null);
                        player.announce(FamiliarPacket.spawnFamiliar(oldFamiliar, null, player));
                        break;
                    case 3: //背包->怪怪图鉴
                        short pos = in.readShort();
                        in.readShort();
                        Item item = player.getConsumeInventory().getItem(pos);
                        if (item == null || !ItemConstants.isFamiliar(item.getItemId()) || item.getFamiliar() == null) {
                            player.enableAction();
                            return;
                        }
                        Familiar familiar = item.getFamiliar();
                        player.addFamiliar(familiar);
                        player.announce(FamiliarPacket.updateFamiliars(player));
                        player.consumeItem(item);
                        break;
                    case 14:
                        pos = (short) in.readInt(); //怪怪魔方
                        long familiarId = in.readLong();
                        item = player.getCashInventory().getItem(pos);
                        if (item == null || !ItemConstants.isFamiliar(item.getItemId()) || item.getFamiliar() == null) {
                            player.enableAction();
                            return;
                        }
                        familiar = player.getFamiliar((int) familiarId);
                        familiar.randomizer();
                        player.announce(FamiliarPacket.updateFamiliarToChar(familiar, player));
                }
                break;
            case 4:
            case 9: //move
                short s1 = in.readShort();
                byte b1 = in.readByte();
                MovementInfo movementInfo = new MovementInfo(in);
                movementInfo.applyTo(oldFamiliar);
                byte b2 = in.readByte();
                player.chatMessage(String.format("s1=%s,b1=%s,b2=%s", s1, b1, b2));
                break;
        }
    }

    public static void handleSummonSkill(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        MapleMap map = chr.getMap();

        int oid = in.readInt();
        in.readInt();
        int skillId = in.readInt();

        if (map.getObj(oid) != null && map.getObj(oid) instanceof Summon) {
            Summon summon = (Summon) map.getObj(oid);
            summon.onSkillUse(skillId);
        }
    }

    public static void handleGuildRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte val = in.readByte();
        GuildType type = GuildType.getTypeByVal(val);
        if (type == null) {
            player.chatMessage("Unhandled type val : " + val);
            return;
        }
        Guild guild = player.getGuild();
        switch (type) {
            case Req_RemoveGuild:
                if (guild == null || guild.getLeaderId() != player.getId()) {
                    return;
                }
                guild.disband();
                break;
            case Req_Search:
                byte searchType = in.readByte();
                World world = c.getWorld();
                Collection<Guild> guildCol;
                String searchTerm = in.readMapleAsciiString();
                boolean exact = in.readByte() != 0;
                guildCol = world.getGuildsByString(searchType, exact, searchTerm);
                player.announce(WorldPacket.guildSearchResult(searchType, exact, searchTerm, guildCol));
                break;
            case Req_SkillLevelSetUp:
                if (guild == null) {
                    return;
                }
                int skillId = in.readInt();
                boolean up = in.readByte() != 0;
                if (up) {
                    if (!SkillConstants.isGuildContentSkill(skillId) && !SkillConstants.isGuildNoblesseSkill(skillId)) {
                        return;
                    }
                    int spentSp = guild.getSpentSp();
                    if (SkillConstants.isGuildContentSkill(skillId)) {
                        if (spentSp >= guild.getLevel() * 2) {
                            return;
                        }
                    } else if (guild.getBattleSp() - guild.getSpentBattleSp() <= 0) { // Noblesse
                        return;
                    }
                    SkillInfo si = SkillData.getSkillInfo(skillId);
                    if (spentSp < si.getReqTierPoint()) {
                        return;
                    }
                    for (Map.Entry<Integer, Integer> entry : si.getReqSkills().entrySet()) {
                        int reqSkillID = entry.getKey();
                        int reqSlv = entry.getValue();
                        GuildSkill gs = guild.getSkillById(skillId);
                        if (gs == null || gs.getLevel() < reqSlv) {
                            return;
                        }
                    }
                    GuildSkill skill = guild.getSkillById(skillId);
                    if (skill == null) {
                        skill = new GuildSkill();
                        skill.setBuyCharName(player.getName());
                        skill.setExtendCharName(player.getName());
                        skill.setSkillId(skillId);
                        guild.addGuildSkill(skill);
                    }
                    if (skill.getLevel() >= si.getMaxLevel()) {
                        player.chatMessage("That skill is already at its max level.");
                        player.enableAction();
                        return;
                    }
                    skill.setLevel((byte) (skill.getLevel() + 1));
                    guild.addCommitmentToChar(player, 1000);
                    player.announce(UserPacket.message(MessageType.INC_GP_MESSAGE, 1000, null, (byte) 0));
                    guild.broadcast(WorldPacket.guildResult(GuildResult.setSkill(guild, skill, player.getId())));
                } else {
                    GuildSkill gs = guild.getSkillById(skillId);
                    if (gs == null || gs.getLevel() == 0) {
                        return;
                    }
                    if (guild.getGgp() < GameConstants.GGP_FOR_SKILL_RESET) {
                        return;
                    }
                    guild.setGgp(guild.getGgp() - GameConstants.GGP_FOR_SKILL_RESET);
                    gs.setLevel((byte) (gs.getLevel() - 1));
                    guild.broadcast(WorldPacket.guildResult(GuildResult.setSkill(guild, gs, player.getId())));
                }
                break;
            case Req_Setting:
                if (guild == null) {
                    return;
                }
                if (player.getId() != guild.getLeaderId()) {
                    return;
                }
                guild.setAppliable(in.readByte() != 0);
                guild.setTrendActive(in.readInt());
                guild.setTrendTime(in.readInt());
                guild.setTrendAges(in.readInt());
                guild.broadcast(WorldPacket.guildResult(GuildResult.updateSetting(guild)));
                break;
            case Req_Signin:
                if (guild == null) {
                    return;
                }
                guild.addCommitmentToChar(player, 50);
                break;
            case Req_Rank:
                if (guild == null) {
                    return;
                }
                player.announce(WorldPacket.guildResult(GuildResult.updateRank(guild)));
                break;
            case Req_CheckGuildName:
                world = c.getWorld();
                String name = in.readMapleAsciiString();
                long meso = player.getMeso();
                if (meso < GameConstants.CREATE_GUILD_COST) {
                    player.announce(MessagePacket.broadcastMsg("你没有足够的金币创建一个家族。当前创建家族需要: 5000000 的金币.", BroadcastMsgType.ALERT));
                    return;
                }
                if (!world.checkGuildName(name)) {
                    //todo
                    player.announce(MessagePacket.broadcastMsg("该家族名已被使用,请换一个.", BroadcastMsgType.ALERT));
                    return;
                }
                guild = new Guild();
                guild.setName(name);
                DataBaseManager.saveToDB(guild);
                player.setGuild(guild);
                guild = player.getGuild();
                guild.addMember(player);
                guild.setWorldId(player.getWorld());
                GuildMember gm = guild.getMemberByChar(player);
                player.announce(WorldPacket.guildResult(GuildResult.loadResult(guild)));
                gm.addCommitment(500);
                player.deductMoney(GameConstants.CREATE_GUILD_COST);
                break;
            case Req_SetNotice:
                if (guild == null) {
                    return;
                }
                //todo check right
                String notice = in.readMapleAsciiString();
                //check notice
                guild.setNotice(notice);
                guild.broadcast(WorldPacket.guildResult(GuildResult.setNoticeDone(guild, player)));
                break;
            case Req_SetGradeName:
                if (guild == null) {
                    return;
                }
                for (int i = 1; i <= 5; i++) {
                    String gradeName = in.readMapleAsciiString();
                    guild.setGradeName(gradeName, i);
                }
                guild.broadcast(WorldPacket.guildResult(GuildResult.updateGradeName(guild, player.getId())));
                break;
            case Req_SetGradeRight:
                if (guild == null) {
                    return;
                }
                for (int i = 1; i <= 5; i++) {
                    int right = in.readInt();
                    guild.setGradeRight(right, i);
                }
                guild.broadcast(WorldPacket.guildResult(GuildResult.setGradeRightDone(guild, player.getId())));
                break;
            case Req_SetGradeNameAndRight:
                if (guild == null) {
                    return;
                }
                for (int i = 1; i <= 5; i++) {
                    int right = in.readInt();
                    name = in.readMapleAsciiString();
                    guild.setGradeName(name, i);
                    guild.setGradeRight(right, i);
                }
                guild.broadcast(WorldPacket.guildResult(GuildResult.setGradeNameAndRightDone(guild, player.getId())));
                break;
            case Req_SetMemberGrade:
                if (guild == null) {
                    return;
                }
                int id = in.readInt();
                byte grade = in.readByte();
                gm = guild.getMemberByCharID(id);
                gm.setGrade(grade);
                guild.broadcast(WorldPacket.guildResult(GuildResult.setMemberGrade(guild, gm)));
                break;
            case Req_SetMark:
                //消耗15万GP，属实坑
                if (guild == null) {
                    return;
                }
                in.readByte();
                guild.setMarkBg(in.readShort());
                guild.setMarkBgColor(in.readByte());
                guild.setMark(in.readShort());
                guild.setMarkBg(in.readByte());
                guild.broadcast(WorldPacket.guildResult(GuildResult.setMark(guild)));
                for (GuildMember onlineMember : guild.getOnlineMembers()) {
                    MapleCharacter chr = onlineMember.getChr();
                    chr.getMap().broadcastMessage(UserRemote.guildNameChanged(chr));
                    chr.getMap().broadcastMessage(UserRemote.guildMarkChanged(chr));
                }
                player.announce(UserPacket.message(MessageType.SYSTEM_MESSAGE, 0, "本次修改耗费0Gp", (byte) 0));
                break;
            case Req_GuildsInApplication:
                break;
        }
    }

    public static void handleGuildRankRequest(MapleClient c) {
        MapleCharacter player = c.getPlayer();
        World world = player.getMapleWorld();
        Map<Integer, Guild> guilds = world.getGuilds();
        List<Guild> captureTheFlagGameRank = new ArrayList<>();
        List<Guild> undergroundWaterwayRank = new ArrayList<>();
        List<Guild> ggpWeaklyRank = guilds.values().stream().sorted(Comparator.comparingInt(Guild::getGgp)).collect(Collectors.toList());
        player.announce(WorldPacket.guildRank(ggpWeaklyRank, captureTheFlagGameRank, undergroundWaterwayRank));
    }

    public static void handleMigrateToAuctionRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
        player.enterAuction();
    }

    public static void handleAuctionRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int val = in.readInt();
        AuctionAction action = AuctionAction.getActionByVal(val);
        switch (action) {
            case Search:
                in.readLong();
                break;
            case QuickSearch:
                break;
            case Put_On_Sell:
                in.readInt();
                int itemId = in.readInt();
                int quantity = in.readInt();
                long price = in.readLong();
                in.readInt();
                InventoryType invType = InventoryType.getTypeById(in.readByte());
                if (invType == null) {
                    return;
                }
                Inventory inventory = player.getInventory(invType);
                int pos = in.readInt();
                in.readInt();
                break;
        }
    }

    public static void handleSummonAttack(InPacket in, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        MapleMap map = chr.getMap();
        AttackInfo ai = new AttackInfo();
        int objId = in.readInt();
        ai.attackHeader = RecvOpcode.SUMMON_ATTACK;
        ai.summon = (Summon) map.getObj(objId);
        in.readInt();
        ai.updateTime = in.readInt();
        ai.skillId = in.readInt();
        in.readInt();
        in.readByte();
        byte leftAndAction = in.readByte();
        ai.attackActionType = (byte) (leftAndAction & 0x7F);
        ai.left = (byte) (leftAndAction >>> 7) != 0;
        byte mask = in.readByte();
        ai.hits = (byte) (mask & 0xF);
        ai.mobCount = (mask >>> 4) & 0xF;
        in.readByte();
        ai.pos = in.readPosition();
        ai.pos3 = in.readPosition();
        in.readByte();
        in.readInt();
        in.skip(10);
        for (int i = 0; i < ai.mobCount; i++) {
            MobAttackInfo mai = new MobAttackInfo();
            mai.objectId = in.readInt();
            mai.templateID = in.readInt();
            mai.byteIdk1 = in.readByte();
            mai.byteIdk2 = in.readByte();
            mai.byteIdk3 = in.readByte();
            mai.byteIdk4 = in.readByte();
            mai.byteIdk5 = in.readByte();
            in.readInt(); //template Id;
            byte byteIdk6 = in.readByte();
            mai.rect = in.readShortRect();
            in.readInt();//100
            in.readInt();//900
            in.skip(6);
            long[] damages = new long[ai.hits];
            for (int j = 0; j < ai.hits; j++) {
                damages[j] = in.readLong();
            }
            mai.damages = damages;
            mai.mobUpDownYRange = in.readInt();
            in.readByte();
            in.readByte();
            Rect rect = in.readShortRect();
            Position pos = in.readPosition();
            in.skip(5);
        }
        in.readInt();
        UserHandler.handleAttack(c, ai);
    }
}
