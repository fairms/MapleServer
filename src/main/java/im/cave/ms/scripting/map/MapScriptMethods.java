package im.cave.ms.scripting.map;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.enums.FieldEffectType;
import im.cave.ms.scripting.AbstractPlayerInteraction;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.map
 * @date 11/27 22:07
 */
public class MapScriptMethods extends AbstractPlayerInteraction {

    public MapScriptMethods(MapleClient c) {
        super(c);
    }

    public void setMobCapacity(int capacity) {
        c.getPlayer().getMap().setFixedMobCapacity(capacity);
    }

    public void generateMobs(boolean init) {
        c.getPlayer().getMap().generateMobs(init);
    }

    public void showEffect(String effectPath, int delay) {
        FieldEffect effect = new FieldEffect();
        effect.setFieldEffectType(FieldEffectType.BackScreen);
        effect.setString(effectPath);
        effect.setArg1(delay);
        getChar().announce(WorldPacket.fieldEffect(effect));
    }

    public void npcDisableInfo(int[] npcs) {
        getChar().announce(WorldPacket.npcDisableInfo(npcs));
    }
}
