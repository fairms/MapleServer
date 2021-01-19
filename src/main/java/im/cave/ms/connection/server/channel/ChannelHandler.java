package im.cave.ms.connection.server.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.packet.opcode.RecvOpcode;
import im.cave.ms.connection.server.AbstractServerHandler;
import im.cave.ms.connection.server.ErrorPacketHandler;
import im.cave.ms.connection.server.channel.handler.ChatHandler;
import im.cave.ms.connection.server.channel.handler.InventoryHandler;
import im.cave.ms.connection.server.channel.handler.MobHandler;
import im.cave.ms.connection.server.channel.handler.NpcHandler;
import im.cave.ms.connection.server.channel.handler.PetHandler;
import im.cave.ms.connection.server.channel.handler.QuestHandler;
import im.cave.ms.connection.server.channel.handler.UserHandler;
import im.cave.ms.connection.server.channel.handler.WorldHandler;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.connection.server.world.World;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.ServerType;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.handler
 * @date 11/19 19:40
 */
public class ChannelHandler extends AbstractServerHandler {
    private static final Logger log = LoggerFactory.getLogger("Channel");
    private final int channel;
    private final int world;

    public ChannelHandler(int channelId, int worldId) {
        this.channel = channelId;
        this.world = worldId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(" Join in World-{} Channel-{}", world, channel);
        connected(ctx, ServerType.CHANNEL);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket in) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c == null || c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
            return;
        }
        int op = in.readShort();
        if (c.mEncryptedOpcode.containsKey(op)) {
            op = c.mEncryptedOpcode.get(op);
        }
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(in, (short) op);
            return;
        }
        switch (opcode) {
            case USER_CASH_POINT_REQUEST:
                c.announce(WorldPacket.queryCashPointResult(c.getAccount()));
                break;
            case USER_SLOT_EXPAND_REQUEST:
                InventoryHandler.handleUserSlotExpandRequest(in, c);
                break;
            case USER_ENTER_SERVER:
                WorldHandler.handleUserEnterServer(in, c, ServerType.CHANNEL);
                break;
            case ERROR_PACKET:
                ErrorPacketHandler.handlePacket(in);
                break;
            case GENERAL_CHAT:
                ChatHandler.handleUserGeneralChat(in, c);
                break;
            case CHAR_EMOTION:
                UserHandler.handleCharEmotion(in, c);
                break;
            case USER_ACTIVATE_EFFECT_ITEM:
                UserHandler.handleUserActivateEffectItem(in, c);
                break;
            case USER_ACTIVATE_NICK_ITEM:
                UserHandler.handleUserActivateNickItem(in, c);
                break;
            case USER_ACTIVATE_DAMAGE_SKIN:
                UserHandler.handleUserActivateDamageSkin(in, c);
                break;
            case OPEN_WORLD_MAP:
                c.announce(UserPacket.openWorldMap());
                break;
            case PORTAL_SPECIAL:
                UserHandler.handleUserEnterPortalSpecialRequest(in, c);
                break;
            case USER_QUEST_REQUEST:
                EventManager.addEvent(() -> QuestHandler.handleQuestRequest(in, c), 0);
                break;
            case USER_MACRO_SYS_DATA_MODIFIED:
                UserHandler.handleUserMacroSysDataModified(in, c);
                break;
            case USER_LOTTERY_ITEM_USE_REQUEST:
                InventoryHandler.handleUserLotteryItemUseRequest(in, c);
                break;
            case USER_TRANSFER_FIELD_REQUEST:
                UserHandler.handleChangeMapRequest(in, c);
                break;
            case USER_REQUEST_INSTANCE_TABLE:
                WorldHandler.handleInstanceTableRequest(in, c);
                break;
            case USER_REQUEST_CHARACTER_POTENTIAL_SKILL_RAND_SET_UI:
                UserHandler.handleUserRequestCharacterPotentialSkillRandSetUi(in, c);
                break;
            case GROUP_MESSAGE:
                ChatHandler.handleGroupMessage(in, c);
                break;
            case WHISPER:
                ChatHandler.handleWhisper(in, c);
                break;
            case USER_TRANSFER_CHANNEL_REQUEST:
                WorldHandler.handleChangeChannelRequest(in, c);
                break;
            case MOB_MOVE:
                MobHandler.handleMobMove(in, c);
                break;
            case NPC_ANIMATION:
                NpcHandler.handleNpcAnimation(in, c);
                break;
            case USER_GATHER_ITEM_REQUEST:
                InventoryHandler.handleUserGatherItemRequest(in, c);
                break;
            case USER_SORT_ITEM_REQUEST:
                InventoryHandler.handleUserSortItemRequest(in, c);
                break;
            case USER_CHANGE_SLOT_POSITION_REQUEST:
                InventoryHandler.handleChangeInvPos(c, in);
                break;
            case USER_STAT_CHANGE_ITEM_USE_REQUEST:
                InventoryHandler.handleUseItem(in, c);
                break;
            case USER_PET_FOOD_ITEM_USE_REQUEST:
                PetHandler.handleUserPetFoodItemUseRequest(in, c);
                break;
            case USER_SCRIPT_ITEM_USE_REQUEST:
                InventoryHandler.handleUserScriptItemUseRequest(in, c);
                break;
            case USER_CONSUME_CASH_ITEM_USE_REQUEST:
                InventoryHandler.handleUserConsumeCashItemUseRequest(in, c);
                break;
            case USER_CASH_PET_PICK_UP_ON_OFF_REQUEST:
                PetHandler.handleUserCashPetPickUpOnOffRequest(in, c);
                break;
            case USER_CASH_PET_SKILL_SETTING_REQUEST:
                PetHandler.handleUserCashPetSkillSetting(in, c);
                break;
            case EQUIP_ENCHANT_REQUEST:
                InventoryHandler.handleEquipEnchanting(in, c);
                break;
            case USER_ITEM_RELEASE_REQUEST:
                InventoryHandler.handleUserItemReleaseRequest(in, c);
                break;
            case USER_PORTAL_SCROLL_USE_REQUEST:
                InventoryHandler.handleUserPortalScrollUseRequest(in, c);
                break;
            case USER_FIELD_TRANSFER_REQUEST:
                WorldHandler.handleUserFieldTransferRequest(in, c);
                break;
            case USER_UPGRADE_ITEM_USE_REQUEST:
                InventoryHandler.handleUserUpgradeItemUseRequest(in, c);
                break;
            case USER_UPGRADE_ASSIST_ITEM_USE_REQUEST:
                InventoryHandler.handleUserUpgradeAssistItemUseRequest(in, c);
                break;
            case USER_HYPER_UPGRADE_ITEM_USE_REQUEST:
                InventoryHandler.handleUserHyperUpgradeItemUseRequest(in, c);
                break;
            case USER_FLAME_ITEM_USE_REQUEST:
                InventoryHandler.handleUserFlameItemUseRequest(in, c);
                break;
            case USER_ITEM_OPTION_UPGRADE_ITEM_USE_REQUEST:
                InventoryHandler.handleUserItemOptionUpgradeItemUseRequest(in, c);
                break;
            case USER_ABILITY_UP_REQUEST:
                UserHandler.handleAPUpdateRequest(in, c);
                break;
            case USER_ABILITY_MASS_UP_REQUEST:
                UserHandler.handleAPMassUpdateRequest(in, c);
                break;
            case USER_DAMAGE_SKIN_SAVE_REQUEST:
                UserHandler.handleUserDamageSkinSaveRequest(in, c);
                break;
            case USER_SELECT_NPC:
                NpcHandler.handleUserSelectNPC(in, c);
                break;
            case USER_SCRIPT_MESSAGE_ANSWER:
                NpcHandler.handleUserScriptMessageAnswer(in, c);
                break;
            case USER_SHOP_REQUEST:
                NpcHandler.handleUserShopRequest(in, c);
                break;
            case TRUNK_OPERATION:
                WorldHandler.handleTrunkOperation(in, c);
                break;
            case EXPRESS_REQUEST:
                WorldHandler.handleMapleExpressRequest(in, c);
                break;
            case CHAR_HIT:
                UserHandler.handleHit(in, c);
                break;
            case PLAYER_MOVE:
                UserHandler.handlePlayerMove(in, c);
                break;
            case MIGRATE_TO_CASH_SHOP_REQUEST:
                WorldHandler.handleMigrateToCashShopRequest(in, c);
                break;
            case CLOSE_RANGE_ATTACK:
            case RANGED_ATTACK:
            case MAGIC_ATTACK:
//            case SUMMON_ATTACK:
//            case TOUCH_MONSTER_ATTACK:
                UserHandler.handleAttack(in, c, opcode);
                break;
            case WORLD_MAP_TRANSFER:
                UserHandler.handleWorldMapTransfer(in, c);
                break;
            case CHANGE_STAT_REQUEST:
                UserHandler.handleChangeStatRequest(in, c);
                break;
            case USER_SKILL_UP_REQUEST:
                UserHandler.handleSkillUp(in, c);
                break;
            case USER_SKILL_USE_REQUEST:
                UserHandler.handleUseSkill(in, c);
                break;
            case USER_SKILL_CANCEL_REQUEST:
                UserHandler.handleCancelBuff(in, c);
                break;
            case USER_ADD_FAME_REQUEST:
                UserHandler.handleUserAddFameRequest(in, c);
                break;
            case CHAR_INFO_REQUEST:
                UserHandler.handleCharInfoReq(in, c);
                break;
            case USER_ACTIVATE_PET_REQUEST:
                PetHandler.handleUserActivatePetRequest(in, c);
                break;
            case USER_REGISTER_PET_AUTO_BUFF_REQUEST:
                PetHandler.handleUserRegisterPetAutoBuffRequest(in, c);
                break;
            case COMBO_KILL_CHECK:
//                WorldHandler.handleComboKill(in, c);
                break;
            case FAMILIAR:
                WorldHandler.handleFamiliarRequest(in, c);
                break;
            case USER_SOUL_EFFECT_REQUEST:
                UserHandler.handleUserSoulEffectRequest(in, c);
                break;
            case USER_AVATAR_MODIFY_COUPON_USE_REQUEST:
                InventoryHandler.handleUserAvatarModifyCouponUseRequest(in, c);
                break;
            case PET_MOVE:
                PetHandler.handlePetMove(in, c);
                break;
            case PET_ACTION_SPEAK:
                PetHandler.handlePetActionSpeak(in, c);
                break;
            case PET_SET_EXCEPTION_LIST:
                PetHandler.handlePetSetExceptionList(in, c);
                break;
            case PET_FOOD_ITEM_USE_REQUEST:
                PetHandler.handlePetFoodItemUse(in, c);
                break;
            case SUMMON_MOVE:
                WorldHandler.handleSummonMove(in, c);
                break;
            case ANDROID_MOVE:
                WorldHandler.handleAndroidMove(in, c);
                break;
            case CHANGE_QUICKSLOT:
                UserHandler.handleChangeQuickSlot(in, c);
                break;
            case SEND_MAPLE_MESSAGE:
                WorldHandler.handleSendMapleMessage(in, c);
                break;
            case CHAT_ROOM:
                WorldHandler.handleChatRoom(in, c);
                break;
            case TRADE_ROOM:
                WorldHandler.handleTradeRoom(in, c);
                break;
            case PARTY_REQUEST:
                WorldHandler.handlePartyRequest(in, c);
                break;
            case PARTY_INVITE_RESPONSE:
                WorldHandler.handlePartyInviteResponse(in, c);
                break;
            case FRIEND_REQUEST:
                WorldHandler.handleFriendRequest(in, c);
                break;
            case MAPLE_MESSAGE_REQUEST:
                WorldHandler.handleMapleMessageRequest(in, c);
                break;
            case CHANGE_KEYMAP:
                UserHandler.handleChangeKeyMap(in, c);
                break;
            case USER_HYPER_SKILL_UP_REQUEST:
            case USER_HYPER_STAT_UP_REQUEST:
                UserHandler.handleUserHyperUpRequest(in, c);
                break;
            case USER_HYPER_SKILL_RESET_REQUEST:
                UserHandler.handleUserHyperSkillResetRequest(in, c);
                break;
            case USER_HYPER_STAT_RESET_REQUEST:
                UserHandler.handleUserHyperStatResetRequest(in, c);
                break;
            case CHANGE_CHAR_REQUEST:
                WorldHandler.handleChangeCharRequest(in, c);
                break;
            case CHECK_TRICK_OR_TREAT_REQUEST:
                WorldHandler.handleCheckTrickOrTreatRequest(in, c);
                break;
            case ANDROID_SHOP_REQUEST:
                UserHandler.handleAndroidShopRequest(in, c);
                break;
            case UPDATE_TICK:
                c.getPlayer().setTick(in.readInt());
                break;
            case SIGN_IN:
                WorldHandler.handleSignIn(in, c);
                break;
            case UNITY_PORTAL_REQUEST:
                WorldHandler.handleUnityPortalSelect(in, c);
                break;
            case POTION_POT_USE_REQUEST:
                InventoryHandler.handlePotionPotUseRequest(in, c);
                break;
            case POTION_POT_ADD_REQUEST:
                InventoryHandler.handlePotionPotAddRequest(in, c);
                break;
            case POTION_POT_INC_REQUEST:
                InventoryHandler.handlePotionPotIncRequest(in, c);
                break;
            case USER_OPEN_MYSTERY_EGG:
                InventoryHandler.handleUserOpenMysteryEgg(in, c);
                break;
            case SKILL_COMMAND_LOCK:
                c.getPlayer().changeSkillState(in.readInt());
                break;
            case USER_SIT_REQUEST:
                UserHandler.handleUserSitRequest(in, c);
                break;
            case USER_PORTABLE_CHAIR_SIT_REQUEST:
                UserHandler.handleUserPortableChairSitRequest(in, c);
                break;
            case PICK_UP_ITEM:
                UserHandler.handlePickUp(in, c);
                break;
            case REQUEST_RECOMMEND_PLAYERS:
                WorldHandler.handleRequestRecommendPlayers(c);
                break;
            case QUICK_MOVE_SELECT:
                WorldHandler.handleQuickMove(in.readInt(), c);
                break;
            case BATTLE_ANALYSIS:
                WorldHandler.handleBattleAnalysis(in, c);
                break;
            case EQUIP_EFFECT_OPT:
                UserHandler.handleEquipEffectOpt(in.readInt(), c);
                break;
            case CPONG:
                c.pongReceived();
                break;
        }

    }

}
