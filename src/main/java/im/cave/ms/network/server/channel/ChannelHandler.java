package im.cave.ms.network.server.channel;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.ServerType;
import im.cave.ms.network.crypto.AESCipher;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.LoginPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.network.server.ErrorPacketHandler;
import im.cave.ms.network.server.channel.handler.InventoryHandler;
import im.cave.ms.network.server.channel.handler.MobHandler;
import im.cave.ms.network.server.channel.handler.NpcHandler;
import im.cave.ms.network.server.channel.handler.QuestHandler;
import im.cave.ms.network.server.channel.handler.UserHandler;
import im.cave.ms.network.server.channel.handler.WorldHandler;
import im.cave.ms.network.server.service.EventManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static im.cave.ms.client.MapleClient.AES_CIPHER;
import static im.cave.ms.client.MapleClient.CLIENT_KEY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.handler
 * @date 11/19 19:40
 */
public class ChannelHandler extends SimpleChannelInboundHandler<InPacket> {
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
        int sendIv = (int) (Math.random() * Integer.MAX_VALUE);
        int recvIv = (int) (Math.random() * Integer.MAX_VALUE);
        MapleClient client = new MapleClient(ctx.channel(), sendIv, recvIv);
        client.announce(LoginPacket.getHello(sendIv, recvIv, ServerType.CHANNEL));
        ctx.channel().attr(CLIENT_KEY).set(client);
        ctx.channel().attr(AES_CIPHER).set(new AESCipher());
        EventManager.addFixedRateEvent(client::sendPing, 0, 10000);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        Account account = c.getAccount();
        MapleCharacter player = c.getPlayer();
        if (player != null && !player.isChangingChannel()) {
            player.logout();
        } else if (player != null && player.isChangingChannel()) {
            player.setChangingChannel(false);
        } else if (account != null) {
            account.logout();
        }
        c.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        MapleClient c = ctx.channel().attr(CLIENT_KEY).get();
        if (c == null || c.getLoginStatus() == LoginStatus.SERVER_TRANSITION) {
            return;
        }
        int op = inPacket.readShort();
        if (c.mEncryptedOpcode.containsKey(op)) {
            op = c.mEncryptedOpcode.get(op);
        }
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(inPacket, (short) op);
//            inPacket.release();
            return;
        }
        switch (opcode) {
            case PLAYER_LOGIN:
                WorldHandler.handleEnterWorld(inPacket, c);
                break;
            case ERROR_PACKET:
                ErrorPacketHandler.handlePacket(inPacket);
                break;
            case GENERAL_CHAT:
                UserHandler.handleUserGeneralChat(inPacket, c);
                break;
            case USER_ACTIVATE_NICK_ITEM:
                UserHandler.handleUserActivateNickItem(inPacket, c);
                break;
            case USER_ACTIVATE_DAMAGE_SKIN:
                UserHandler.handleUserActivateDamageSkin(inPacket, c);
                break;
            case OPEN_WORLD_MAP:
                c.announce(UserPacket.openWorldMap());
                break;
            case PORTAL_SPECIAL:
                UserHandler.handleUserEnterPortalSpecialRequest(inPacket, c);
                break;
            case USER_QUEST_REQUEST:
                EventManager.addEvent(() -> QuestHandler.handleQuestRequest(inPacket, c), 0);
                break;
            case ENTER_PORTAL:
                UserHandler.handleUserEnterPortalRequest(inPacket, c);
                break;
            case USER_REQUEST_INSTANCE_TABLE:
                WorldHandler.handleInstanceTableRequest(inPacket, c);
                break;
            case USER_REQUEST_CHARACTER_POTENTIAL_SKILL_RAND_SET_UI:
                UserHandler.handleUserRequestCharacterPotentialSkillRandSetUi(inPacket, c);
                break;
            case CHANGE_CHANNEL:
                WorldHandler.handleChangeChannel(inPacket, c);
                break;
            case MOB_MOVE:
                MobHandler.handleMobMove(inPacket, c);
                break;
            case NPC_ANIMATION:
                NpcHandler.handleNpcAnimation(inPacket, c);
                break;
            case ITEM_MOVE:
                InventoryHandler.handleChangeInvPos(c, inPacket);
                break;
            case USER_STAT_CHANGE_ITEM_USE_REQUEST:
                InventoryHandler.handleUseItem(inPacket, c);
                break;
            case USER_SCRIPT_ITEM_USE_REQUEST:
                InventoryHandler.handleUserScriptItemUseRequest(inPacket, c);
                break;
            case EQUIP_ENCHANT_REQUEST:
                InventoryHandler.handleEquipEnchanting(inPacket, c);
                break;
            case USER_PORTAL_SCROLL_USE_REQUEST:
                InventoryHandler.handleUserPortalScrollUseRequest(inPacket, c);
                break;
            case USER_UPGRADE_ITEM_USE_REQUEST:
                InventoryHandler.handleUserUpgradeItemUseRequest(inPacket, c);
                break;
            case USER_FLAME_ITEM_USE_REQUEST:
                InventoryHandler.handleUserFlameItemUseRequest(inPacket, c);
                break;
            case USER_ABILITY_UP_REQUEST:
                UserHandler.handleAPUpdateRequest(inPacket, c);
                break;
            case USER_ABILITY_MASS_UP_REQUEST:
                UserHandler.handleAPMassUpdateRequest(inPacket, c);
                break;
            case USER_DAMAGE_SKIN_SAVE_REQUEST:
                UserHandler.handleUserDamageSkinSaveRequest(inPacket, c);
                break;
            case USER_SELECT_NPC:
                NpcHandler.handleUserSelectNPC(inPacket, c);
                break;
            case USER_SCRIPT_MESSAGE_ANSWER:
                NpcHandler.handleUserScriptMessageAnswer(inPacket, c);
                break;
            case TRUNK_OPERATION:
                WorldHandler.handleTrunkOperation(inPacket, c);
                break;
            case CHAR_HIT:
                UserHandler.handleHit(inPacket, c);
                break;
            case PLAYER_MOVE:
                UserHandler.handlePlayerMove(inPacket, c);
                break;
            case MIGRATE_TO_CASH_SHOP_REQUEST:
                WorldHandler.handleMigrateToCashShopRequest(inPacket, c);
                break;
            case CLOSE_RANGE_ATTACK:
            case RANGED_ATTACK:
            case MAGIC_ATTACK:
//            case SUMMON_ATTACK:
//            case TOUCH_MONSTER_ATTACK:
                UserHandler.handleAttack(inPacket, c, opcode);
                break;
            case WORLD_MAP_TRANSFER:
                UserHandler.handleWorldMapTransfer(inPacket, c);
                break;
            case CHANGE_STAT_REQUEST:
                UserHandler.handleChangeStatRequest(inPacket, c);
                break;
            case USER_SKILL_UP_REQUEST:
                UserHandler.handleSkillUp(inPacket, c);
                break;
            case USER_SKILL_USE_REQUEST:
                UserHandler.handleUseSkill(inPacket, c);
                break;
            case USER_SKILL_CANCEL_REQUEST:
                UserHandler.handleCancelBuff(inPacket, c);
                break;
            case USER_ADD_FAME_REQUEST:
                UserHandler.handleUserAddFameRequest(inPacket, c);
                break;
            case CHAR_INFO_REQUEST:
                UserHandler.handleCharInfoReq(inPacket, c);
                break;
            case COMBO_KILL_CHECK:
                WorldHandler.handleComboKill(inPacket, c);
                break;
            case SUMMON_MOVE:
                WorldHandler.handleSummonMove(inPacket, c);
                break;
            case CHANGE_QUICKSLOT:
                UserHandler.handleChangeQuickslot(inPacket, c);
                break;
            case MINI_ROOM:
                WorldHandler.handleMiniRoom(inPacket, c);
                break;
            case CHANGE_KEYMAP:
                UserHandler.handleChangeKeyMap(inPacket, c);
                break;
            case CHANGE_CHAR_REQUEST:
                WorldHandler.handleChangeCharRequest(inPacket, c);
                break;
            case UPDATE_TICK:
                c.getPlayer().setTick(inPacket.readInt());
                break;
            case SIGN_IN:
                WorldHandler.handleSignIn(inPacket, c);
                break;
            case UNITY_PORTAL_REQUEST:
                WorldHandler.handleUnityPortalSelect(inPacket, c);
                break;
            case USER_OPEN_MYSTERY_EGG:
                InventoryHandler.handleUserOpenMysteryEgg(inPacket, c);
                break;
            case SKILL_COMMAND_LOCK:
                c.getPlayer().changeSkillState(inPacket.readInt());
                break;
            case CANCEL_CHAIR:
                UserHandler.cancelChair(inPacket, c);
                break;
            case USER_SIT_REQUEST:
                UserHandler.handleUserSitRequest(inPacket, c);
                break;
            case PICK_UP_ITEM:
                UserHandler.handlePickUp(inPacket, c);
                break;
            case QUICK_MOVE_SELECT:
                WorldHandler.handleQuickMove(inPacket.readInt(), c);
                break;
            case BATTLE_ANALYSIS:
                WorldHandler.handleBattleAnalysis(inPacket, c);
                break;
            case EQUIP_EFFECT_OPT:
                UserHandler.handleEquipEffectOpt(inPacket.readInt(), c);
                break;
            case CPONG:
                c.pongReceived();
                break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.info("Client forcibly closed the game.");
        } else {
            cause.printStackTrace();
        }
    }


    private void handleUnknown(InPacket inPacket, short op) {
        log.warn("Unhandled opcode {}, packet {}",
                Integer.toHexString(op & 0xFFFF),
                inPacket);
    }
}
