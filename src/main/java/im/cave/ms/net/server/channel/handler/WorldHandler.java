package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.Trunk;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.QuickMoveInfo;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.DimensionalMirror;
import im.cave.ms.enums.InstanceTableType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.TrunkOpType;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.server.Server;
import im.cave.ms.tools.Util;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.server.channel.handler
 * @date 12/5 15:25
 */
public class WorldHandler {
    private static final Logger log = LoggerFactory.getLogger(WorldHandler.class);

    public static void handleInstanceTableRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        String requestStr = slea.readMapleAsciiString();
        int type = slea.readInt();
        int subType = slea.readInt();
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

    public static void handleMigrateToCashShopRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(slea.readInt());
        slea.readByte(); // 00
        c.announce(MaplePacketCreator.getChannelChange(Server.getInstance().getCashShop(c.getWorld()).getPort()));
    }

    public static void handleBattleAnalysis(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte opt = slea.readByte();
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

    public static void handleUnityPortalSelect(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        player.setTick(slea.readInt());
        int unityPortalId = slea.readInt();
        DimensionalMirror[] unityPortals = DimensionalMirror.values();
        DimensionalMirror unityPortal = Util.findWithPred(unityPortals, u -> u.getId() == unityPortalId);
        if (unityPortal == null) {
            player.dropMessage("尝试Hack?");
            return;
        }
        player.changeMap(unityPortal.getMapId());
    }

    public static void handleTrunkOperation(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        Trunk trunk = player.getAccount().getTrunk();
        byte type = slea.readByte();
        TrunkOpType trunkOpType = TrunkOpType.getByVal(type);
        if (trunkOpType == null) {
            log.error(String.format("Unknown trunk request type %d.", type));
            return;
        }
        switch (trunkOpType) {
            case TrunkReq_GetItem:
                int pos = slea.readShort() - 1;
                short quantity = slea.readShort(); //todo
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
                long amount = slea.readLong();
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

    public static void handleChangeCharRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        String account = slea.readMapleAsciiString();
        if (!account.equals(c.getAccount().getAccount())) {
            c.getPlayer().dropMessage("有问题");
            return;
        }
        c.announce(LoginPacket.changePlayer(c));
    }
}
