package im.cave.ms.scripting.item;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.DamageSkinSaveData;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.enums.DamageSkinType;
import im.cave.ms.enums.QuestStatus;
import im.cave.ms.network.packet.QuestPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.scripting.AbstractPlayerInteraction;
import im.cave.ms.tools.Randomizer;

import static im.cave.ms.constants.QuestConstants.QUEST_DAMAGE_SKIN;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.portal
 * @date 11/28 14:16
 */

public class ItemScriptAction extends AbstractPlayerInteraction {

    private final int itemId;
    private final int npcId;

    public ItemScriptAction(MapleClient c, int itemId, int npcId) {
        super(c);
        this.itemId = itemId;
        this.npcId = npcId;
    }

    public void setDamageSkin(int itemId) {
        MapleCharacter chr = getChar();
        DamageSkinType error = null;
        QuestManager qm = chr.getQuestManager();
        Quest quest = qm.getQuests().getOrDefault(QUEST_DAMAGE_SKIN, null);
        if (quest == null) {
            quest = new Quest(QUEST_DAMAGE_SKIN, QuestStatus.Started);
            qm.addQuest(quest);
        }
        DamageSkinSaveData damageSkinSaveData = DamageSkinSaveData.getByItemID(itemId);
        quest.setQrValue(String.valueOf(damageSkinSaveData.getDamageSkinID()));
        chr.addDamageSkin(damageSkinSaveData);
        chr.setDamageSkin(damageSkinSaveData);
        chr.announce(QuestPacket.questRecordMessage(quest));
    }

    public void consumeItem(int itemId, int quantity) {
        c.getPlayer().consumeItem(itemId, 1);
    }


    public void addHonerPoint() {
        if (itemId == 2431174) {
            c.getPlayer().addHonerPoint(Randomizer.rand(20, 120));
            c.getPlayer().announce(UserPacket.inventoryRefresh(true));
        }
    }

    public int getNpcId() {
        return npcId;
    }

    public int getItemId() {
        return itemId;
    }
}
