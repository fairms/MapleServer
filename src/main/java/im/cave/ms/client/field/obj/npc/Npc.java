package im.cave.ms.client.field.obj.npc;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.NpcPacket;
import im.cave.ms.tools.Rect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.life
 * @date 11/28 13:00
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Npc extends MapleMapObj {
    private boolean enabled = true;
    private int presentItemID;
    private byte presentItemState;
    private int presentItemTime;
    private int noticeBoardType;
    private int noticeBoardValue;
    private int alpha; // if hideToLocalUser is true
    private String localRepeatEffect;
    private Map<Integer, String> scripts = new HashMap<>();
    private boolean move;
    private int trunkGet;
    private int trunkPut;
    private boolean shop;
    private Rect npcRect = new Rect();

    public Npc(int npcId) {
        super(npcId);
    }


    public Rect getDCRange() {
        int x = getPosition().getX();
        int y = getPosition().getY();
        return new Rect((x + npcRect.getLeft()), (y + npcRect.getTop()), (x + npcRect.getRight()), (y + npcRect.getBottom()));
    }

    @Override
    public void sendLeavePacket(MapleCharacter chr) {
        chr.announce(NpcPacket.removeNpc(getObjectId()));
    }

    @Override
    public void sendSpawnPacket(MapleCharacter chr) {
        chr.announce(NpcPacket.spawnNpc(this));
    }

    @Override
    public void notifyControllerChange(MapleCharacter controller) {
        for (MapleCharacter chr : getMap().getCharInRect(getVisibleRect())) {
            chr.announce(NpcPacket.changeNpcController(this, controller == chr, false));
        }
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeShort(getPosition().getX());
        outPacket.writeShort(getPosition().getY());
        outPacket.writeBool(isMove());
        outPacket.writeBool(!isFlip());
        outPacket.writeShort(getFh());
        outPacket.writeShort(getRx0());
        outPacket.writeShort(getRx1());
        outPacket.writeBool(!isHide());
        //todo
        outPacket.writeZeroBytes(9);
        outPacket.writeInt(-1);
        outPacket.writeZeroBytes(12);
    }

    public Npc deepCopy() {
        Npc copy = new Npc(getTemplateId());
        copy.setLifeType(getLifeType());
        copy.setX(getX());
        copy.setY(getY());
        copy.setMobTime(getMobTime());
        copy.setFlip(isFlip());
        copy.setHide(isHide());
        copy.setFh(getFh());
        copy.setCy(getCy());
        copy.setRx0(getRx0());
        copy.setRx1(getRx1());
        copy.setLimitedName(getLimitedName());
        copy.setUseDay(isUseDay());
        copy.setUseNight(isUseNight());
        copy.setHold(isHold());
        copy.setNoFoothold(isNoFoothold());
        copy.setDummy(isDummy());
        copy.setSpine(isSpine());
        copy.setMobTimeOnDie(isMobTimeOnDie());
        copy.setRegenStart(getRegenStart());
        copy.setMove(isMove());
        copy.setMobAliveReq(getMobAliveReq());
        copy.setTrunkGet(getTrunkGet());
        copy.setTrunkPut(getTrunkPut());
        copy.setNpcRect(getNpcRect());
        copy.getScripts().putAll(getScripts());
        return copy;
    }


    public Rect getDC() {
        return npcRect;
    }

    public void setDC(Rect npcRect) {
        this.npcRect = npcRect;
    }
}
