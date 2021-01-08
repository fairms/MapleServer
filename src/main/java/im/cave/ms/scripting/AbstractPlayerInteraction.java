package im.cave.ms.scripting;


import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.enums.ChatType;
import im.cave.ms.network.packet.WorldPacket;

import java.util.Calendar;

public class AbstractPlayerInteraction {

    public MapleClient c;

    public AbstractPlayerInteraction(MapleClient c) {
        this.c = c;
    }

    public MapleClient getClient() {
        return c;
    }

    public MapleCharacter getPlayer() {
        return c.getPlayer();
    }

    public MapleCharacter getChar() {
        return c.getPlayer();
    }

    public int getJob() {
        return getPlayer().getJob();
    }

    public int getLevel() {
        return getPlayer().getLevel();
    }

    public MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    public int getHourOfDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public void serverMsg(String content) {
        c.announce(WorldPacket.chatMessage(content, ChatType.Tip));
    }

    public void dropMessage(Integer content) {
        c.announce(WorldPacket.chatMessage(String.valueOf(content), ChatType.Tip));
    }

    public void dropMessage(String content) {
        c.getPlayer().dropMessage(content);
    }

    public boolean forceCompleteQuest(int questId) {
        c.getPlayer().getQuestManager().completeQuest(questId);
        return true;
    }

    public boolean forceStartQuest(int questId) {
        c.getPlayer().getQuestManager().addQuest(questId);
        return true;
    }

    public void openUnityPortal() {
        c.announce(WorldPacket.unityPortal());
    }

    public void warp(int mapId) {
        c.getPlayer().changeMap(mapId);
    }

    public int getMapId() {
        return c.getPlayer().getMapId();
    }
}