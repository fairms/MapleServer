package im.cave.ms.net.server.channel;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.ServerType;
import im.cave.ms.net.netty.InPacket;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.packet.opcode.RecvOpcode;
import im.cave.ms.net.server.ErrorPacketHandler;
import im.cave.ms.net.server.channel.handler.ChangeChannelHandler;
import im.cave.ms.net.server.channel.handler.EnterPortalHandler;
import im.cave.ms.net.server.channel.handler.GeneralChatHandler;
import im.cave.ms.net.server.channel.handler.InventoryHandler;
import im.cave.ms.net.server.channel.handler.MobHandler;
import im.cave.ms.net.server.channel.handler.NpcHandler;
import im.cave.ms.net.server.channel.handler.PlayerHandler;
import im.cave.ms.net.server.channel.handler.PlayerLoggedinHandler;
import im.cave.ms.net.server.channel.handler.QuestHandler;
import im.cave.ms.net.server.channel.handler.SpecialPortalHandler;
import im.cave.ms.net.server.channel.handler.WorldHandler;
import im.cave.ms.provider.service.EventManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        int op = inPacket.readShort();
        if (c.mEncryptedOpcode.containsKey(op)) {
            op = c.mEncryptedOpcode.get(op);
        }
        RecvOpcode opcode = RecvOpcode.getOpcode(op);
        if (opcode == null) {
            handleUnknown(inPacket, (short) op);
            return;
        }
        switch (opcode) {
            case PLAYER_LOGIN:
                PlayerLoggedinHandler.handlePacket(inPacket, c);
                break;
            case ERROR_PACKET:
                ErrorPacketHandler.handlePacket(inPacket);
                break;
            case GENERAL_CHAT:
                GeneralChatHandler.handlePacket(inPacket, c);
                break;
            case OPEN_WORLD_MAP:
                c.announce(MaplePacketCreator.openWorldMap());
                break;
            case PORTAL_SPECIAL:
                SpecialPortalHandler.handlePacket(inPacket, c);
                break;
            case USER_QUEST_REQUEST:
                EventManager.addEvent(() -> QuestHandler.handleQuestRequest(inPacket, c), 0);
                break;
            case ENTER_PORTAL:
                EnterPortalHandler.handlePacket(inPacket, c);
                break;
            case REQUEST_INSTANCE_TABLE:
                WorldHandler.handleInstanceTableRequest(inPacket, c);
                break;
            case CHANGE_CHANNEL:
                ChangeChannelHandler.handlePacket(inPacket, c);
                break;
            case MOB_MOVE:
                MobHandler.handleMoveMob(inPacket, c);
                break;
            case NPC_ANIMATION:
                NpcHandler.handleNpcAnimation(inPacket, c);
                break;
            case ITEM_MOVE:
                InventoryHandler.handleChangeInvPos(c, inPacket);
                break;
            case USE_ITEM:
                InventoryHandler.handleUseItem(inPacket, c);
                break;
            case EQUIP_ENCHANT_REQUEST:
                InventoryHandler.handleEquipEnchanting(inPacket, c);
                break;
            case USER_ABILITY_UP_REQUEST:
                PlayerHandler.handleAPUpdateRequest(inPacket, c);
                break;
            case USER_ABILITY_MASS_UP_REQUEST:
                PlayerHandler.handleAPMassUpdateRequest(inPacket, c);
                break;
            case SELECT_NPC:
                NpcHandler.handleUserSelectNPC(inPacket, c);
                break;
            case TALK_ACTION:
                NpcHandler.handleAction(inPacket, c);
                break;
            case TRUNK_OPERATION:
                WorldHandler.handleTrunkOperation(inPacket, c);
                break;
            case CHAR_HIT:
                PlayerHandler.handleHit(inPacket, c);
                break;
            case PLAYER_MOVE:
                PlayerHandler.handlePlayerMove(inPacket, c);
                break;
            case MIGRATE_TO_CASH_SHOP_REQUEST:
                WorldHandler.handleMigrateToCashShopRequest(inPacket, c);
            case CLOSE_RANGE_ATTACK:
            case RANGED_ATTACK:
            case MAGIC_ATTACK:
//            case SUMMON_ATTACK:
//            case TOUCH_MONSTER_ATTACK:
                PlayerHandler.handleAttack(inPacket, c, opcode);
                break;
            case WORLD_MAP_TRANSFER:
                PlayerHandler.handleWorldMapTransfer(inPacket, c);
                break;
            case CHANGE_STAT_REQUEST:
                PlayerHandler.handleChangeStatRequest(inPacket, c);
                break;
            case SKILL_UP:
                PlayerHandler.handleSkillUp(inPacket, c);
                break;
            case USE_SKILL:
                PlayerHandler.handleUseSkill(inPacket, c);
                break;
            case CANCEL_BUFF:
                PlayerHandler.handleCancelBuff(inPacket, c);
                break;
            case CHAR_INFO_REQUEST:
                PlayerHandler.handleCharInfoReq(inPacket, c);
                break;
            case COMBO_KILL:
                WorldHandler.handleComboKill(inPacket, c);
                break;
            case CHANGE_QUICKSLOT:
                PlayerHandler.handleChangeQuickslot(inPacket, c);
                break;
            case CHANGE_KEYMAP:
                PlayerHandler.handleChangeKeyMap(inPacket, c);
                break;
            case CHANGE_CHAR_REQUEST:
                WorldHandler.handleChangeCharRequest(inPacket, c);
                break;
            case UPDATE_TICK:
                c.getPlayer().setTick(inPacket.readInt());
                break;
            case UNITY_PORTAL_SELECT:
                WorldHandler.handleUnityPortalSelect(inPacket, c);
                break;
            case SKILL_OPT:
                c.getPlayer().changeSkillState(inPacket.readInt());
                break;
            case CANCEL_CHAIR:
                PlayerHandler.cancelChair(inPacket, c);
                break;
            case USE_CHAIR:
                PlayerHandler.handleUseChair(inPacket, c);
                break;
            case PICK_UP:
                PlayerHandler.handlePickUp(inPacket, c);
                break;
            case QUICK_MOVE_SELECT:
                WorldHandler.handleQuickMove(inPacket.readInt(), c);
                break;
            case BATTLE_ANALYSIS:
                WorldHandler.handleBattleAnalysis(inPacket, c);
                break;
            case EQUIP_EFFECT_OPT:
                PlayerHandler.handleEquipEffectOpt(inPacket.readInt(), c);
                break;
            case CPONG:
                c.pongReceived();
                break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }


    private void handleUnknown(InPacket inPacket, short op) {
        log.warn("Unhandled opcode {}, packet {}",
                Integer.toHexString(op & 0xFFFF),
                inPacket);
    }
}
