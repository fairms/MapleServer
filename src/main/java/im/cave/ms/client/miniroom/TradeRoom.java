package im.cave.ms.client.miniroom;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.network.packet.MiniRoomPacket;
import im.cave.ms.tools.Tuple;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sjonnie
 * Created on 8/10/2018.
 */
@Setter
public class TradeRoom implements MiniRoom {
    private Map<MapleCharacter, List<Tuple<Integer, Item>>> offeredItems = new HashMap<>(); // wow
    private Map<MapleCharacter, Long> money = new HashMap<>();
    private Set<MapleCharacter> confirmedPlayers = new HashSet<>();
    private MapleCharacter chr;
    private MapleCharacter other;

    public TradeRoom(MapleCharacter chr, MapleCharacter other) {
        this.chr = chr;
        offeredItems.put(chr, new ArrayList<>());
        this.other = other;
        offeredItems.put(other, new ArrayList<>());
    }

    public TradeRoom(MapleCharacter chr) {
        this.chr = chr;
        offeredItems.put(chr, new ArrayList<>());
    }


    public void setOther(MapleCharacter other) {
        this.other = other;
        offeredItems.put(other, new ArrayList<>());
    }

    public MapleCharacter getOther() {
        return other;
    }

    public boolean canAddItem(MapleCharacter chr) {
        return getOfferedItems().get(chr).size() < GameConstants.MAX_TRADE_ITEMS;
    }

    public void addItem(MapleCharacter chr, int pos, Item item) {
        List<Tuple<Integer, Item>> items = getOfferedItems().get(chr);
        Tuple<Integer, Item> entry = new Tuple<>(pos, item);
        items.add(entry);
    }

    public MapleCharacter getChr() {
        return chr;
    }

    private Map<MapleCharacter, Long> getMoney() {
        return money;
    }

    public long getMoney(MapleCharacter chr) {
        return getMoney().getOrDefault(chr, 0L);
    }

    public void putMoney(MapleCharacter chr, long money) {
        getMoney().put(chr, money);
    }

    public Map<MapleCharacter, List<Tuple<Integer, Item>>> getOfferedItems() {
        return offeredItems;
    }

    public void restoreItems() {
        MapleCharacter[] MapleCharacters = new MapleCharacter[]{getChr(), getOther()};
        for (MapleCharacter chr : MapleCharacters) {
            if (chr == null) {
                continue;
            }
            for (Tuple<Integer, Item> entry : getOfferedItems().get(chr)) {
                chr.addItemToInv(entry.getRight());
            }
            chr.addMeso(getMoney(chr));
        }
    }

    public Set<MapleCharacter> getConfirmedPlayers() {
        return confirmedPlayers;
    }

    public void addConfirmedPlayer(MapleCharacter chr) {
        getConfirmedPlayers().add(chr);
    }

    public boolean hasConfirmed(MapleCharacter other) {
        return getConfirmedPlayers().contains(other);
    }

    public boolean completeTrade() {
        MapleCharacter chr = getChr();
        MapleCharacter other = getOther();
        // Ugly, but eh
        // Check if the MapleCharacteracters have enough space for all the items
        List<Item> items = new ArrayList<>();
        for (Tuple<Integer, Item> entry : getOfferedItems().get(other)) {
            items.add(entry.getRight());
        }
        if (!chr.canHold(items)) {
            chr.chatMessage("You do not have enough inventory space.");
            other.chatMessage(chr.getName() + " does not have enough inventory space.");
            return false;
        }
        for (Tuple<Integer, Item> entry : getOfferedItems().get(chr)) {
            items.add(entry.getRight());
        }
        if (!other.canHold(items)) {
            other.chatMessage("You do not have enough inventory space.");
            chr.chatMessage(chr.getName() + " does not have enough inventory space.");
            return false;
        }
        // Add all the items + money
        for (Tuple<Integer, Item> entry : getOfferedItems().get(chr)) {
            other.addItemToInv(entry.getRight());
        }
        other.addMeso(GameConstants.applyTax(getMoney(chr)));
        for (Tuple<Integer, Item> entry : getOfferedItems().get(other)) {
            chr.addItemToInv(entry.getRight());
        }
        chr.addMeso(GameConstants.applyTax(getMoney(other)));
        return true;
    }

    public void cancelTrade() {
        chr.setMiniRoom(null);
        chr.announce(MiniRoomPacket.cancelTrade(1));
        if (other != null) {
            other.setMiniRoom(null);
            other.announce(MiniRoomPacket.cancelTrade(0));
        }
        restoreItems();

    }

    public MapleCharacter getOtherChar(MapleCharacter chr) {
        return chr == getChr() ? getOther() : getChr();
    }

    public void sendTips() {
        getChr().announce(MiniRoomPacket.chat(0, "提示信息", getChr()));
        getOther().announce(MiniRoomPacket.chat(0, "提示信息", getOther()));
    }
}
