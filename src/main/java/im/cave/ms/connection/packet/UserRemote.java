package im.cave.ms.connection.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.PetItem;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.character.skill.HitInfo;
import im.cave.ms.client.character.skill.MobAttackInfo;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.enums.BodyPart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static im.cave.ms.constants.QuestConstants.QUEST_EX_SOUL_EFFECT;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.packet
 * @date 12/29 12:42
 */
public class UserRemote {
    public static OutPacket hit(MapleCharacter player, HitInfo hitInfo) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOTE_HIT.getValue());
        out.writeInt(player.getId());
        out.write(hitInfo.type);
        out.writeInt(hitInfo.hpDamage);
        out.writeBool(hitInfo.isCrit);
        out.writeBool(hitInfo.hpDamage == 0);
        out.write(0);
        out.writeInt(hitInfo.templateID);
        out.write(hitInfo.action);
        out.writeInt(hitInfo.mobID);

        out.writeInt(0); // ignored
        out.writeInt(hitInfo.reflectDamage);
        out.writeBool(hitInfo.hpDamage == 0); // bGuard

        out.write(hitInfo.specialEffectSkill);
        if ((hitInfo.specialEffectSkill & 1) != 0) {
            out.writeInt(hitInfo.curStanceSkill);
        }
        out.writeInt(hitInfo.hpDamage);
        return out;
    }

    public static OutPacket effect(int id, Effect effect) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOTE_EFFECT.getValue());
        out.writeInt(id);
        effect.encode(out);
        return out;
    }

    public static OutPacket attack(MapleCharacter player, AttackInfo attackInfo) {
        OutPacket out = new OutPacket();
        switch (attackInfo.attackHeader) {
            case CLOSE_RANGE_ATTACK:
                out.writeShort(SendOpcode.REMOTE_CLOSE_RANGE_ATTACK.getValue());
                break;
            case RANGED_ATTACK:
                out.writeShort(SendOpcode.REMOTE_RANGED_ATTACK.getValue());
                break;
            case MAGIC_ATTACK:
                out.writeShort(SendOpcode.REMOTE_MAGIC_ATTACK.getValue());
                break;
        }
        out.writeInt(player.getId());
        out.write(attackInfo.fieldKey);
        out.write(attackInfo.mobCount << 4 | attackInfo.hits);
        out.writeInt(player.getLevel());
        out.writeInt(attackInfo.skillLevel);
        if (attackInfo.skillLevel > 0) {
            out.writeInt(attackInfo.skillId);
        }
        out.writeZeroBytes(10);
        out.write(attackInfo.attackAction);
        out.write(attackInfo.direction);

        out.writeShort(1);
        out.writeInt(0);
        out.writeShort(1);
        out.write(0);
        out.writeInt(0);

        for (MobAttackInfo mobAttackInfo : attackInfo.mobAttackInfo) {
            out.writeInt(mobAttackInfo.objectId);
            out.writeZeroBytes(13);
            for (long damage : mobAttackInfo.damages) {
                out.writeLong(damage);
            }
        }
        return out;
    }

    public static OutPacket charInfo(MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHAR_INFO.getValue());
        out.writeInt(chr.getId());
        out.writeInt(chr.getLevel());
        out.writeShort(chr.getJob());
        out.writeShort(chr.getSubJob());
        out.write(10);
        out.writeInt(chr.getFame());
        out.writeBool(chr.isMarried());
        //todo marriage = true
        out.write(0); //making skill size
        //9200
        //9201
        //9202
        out.writeMapleAsciiString("-"); //party name
        out.writeMapleAsciiString(""); // 联盟
        out.write(-1); //unk
        out.write(0);  //unk
        out.writeBool(chr.getPets().size() > 0); //has pet
        for (Pet pet : chr.getPets()) {
            PetItem petItem = pet.getPetItem();
            out.writeBool(true);
            out.writeInt(pet.getIdx());
            out.writeInt(petItem.getItemId());
            out.writeMapleAsciiString(pet.getName());
            out.write(petItem.getLevel());
            out.writeShort(petItem.getTameness());
            out.write(petItem.getRepleteness());
            out.writeShort(petItem.getPetSkill());
            out.writeInt(chr.getPetEquip(pet.getIdx(), 0));
            out.writeInt(petItem.getPetHue());
        }
        out.write(0); // SetPetInfo end
        Equip medal = chr.getEquippedEquip(BodyPart.Medal);
        out.writeInt(medal != null ? medal.getItemId() : 0);
        out.writeShort(0); //收藏数目
        //todo 收藏任务id+完成时间
        chr.encodeDamageSkins(out);
        out.write(chr.getStats().getCharismaLevel());
        out.write(chr.getStats().getInsightLevel());
        out.write(chr.getStats().getWillLevel());
        out.write(chr.getStats().getCraftLevel());
        out.write(chr.getStats().getSenseLevel());
        out.write(chr.getStats().getCharmLevel());
        out.write(0);
        out.writeLong(0);
        List<Item> chairs = chr.getChairs();
        out.writeInt(chairs.size());//椅子数
        for (Item chair : chairs) {
            out.writeInt(chair.getItemId());
        }
        List<Item> medals = chr.getMedals();
        out.writeInt(medals.size());
        for (Item item : medals) {
            out.writeInt(item.getItemId());
        }
        return out;
    }

    public static OutPacket showBlackboard(MapleCharacter chr, boolean show, String content) {
        OutPacket out = new OutPacket();
        out.writeInt(chr.getId());
        out.writeBool(show);
        if (show) {
            out.writeMapleAsciiString(content);
        }
        return out;
    }


    public static OutPacket emotion(Integer charId, int emotion, int duration, boolean byItemOption) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOTE_EMOTION.getValue());
        out.writeInt(charId);
        out.writeInt(emotion);
        out.writeInt(duration);
        out.writeBool(byItemOption);
        return out;
    }

    public static OutPacket remoteSetActivePortableChair(int charId, int chairId, int unk1, short unk2, int unk3, byte unk4) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOTE_SET_ACTIVE_PORTABLE_CHAIR.getValue());
        out.writeInt(charId);
        out.writeInt(chairId);
        out.writeInt(0);
        out.writeInt(unk3);
        out.write(unk4);
        out.writeBool(chairId != 0);
        out.writeInt(0);
        out.writeInt(unk1);
        out.writeShort(unk2);
        return out;
    }

    public static OutPacket charLookModified(MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOTE_AVATAR_MODIFIED.getValue());
        out.writeInt(chr.getId());
        out.write(1);
        chr.getCharLook().encode(out);
        out.writeInt(0);
        out.write(0xFF);
        out.writeInt(0);
        out.write(0xFF);
        out.writeZeroBytes(15);
        return out;
    }

    public static OutPacket showItemUpgradeEffect(int charId, boolean success, boolean enchantDlg, int uItemId, int eItemId, boolean boom) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_ITEM_UPGRADE_EFFECT.getValue());
        out.writeInt(charId);
        out.write(boom ? 2 : success ? 1 : 0);
        out.writeBool(enchantDlg);
        out.writeInt(uItemId);
        out.writeInt(eItemId);
        out.write(0); // 0 普通 1 消耗祝福
        return out;
    }

    public static OutPacket hiddenEffectEquips(MapleCharacter player) {
        OutPacket out = new OutPacket(SendOpcode.HIDDEN_EFFECT_EQUIP);
        out.writeInt(player.getId());
        List<Item> items = player.getEquippedInventory().getItems();
        List<Item> equips = items.stream().filter(item -> !((Equip) item).isShowEffect()).collect(Collectors.toList());
        out.writeInt(equips.size());
        for (Item equip : equips) {
            out.writeInt(equip.getPos());
        }
        out.writeBool(false);
        return out;
    }

    public static OutPacket setDamageSkin(MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_DAMAGE_SKIN.getValue());
        out.writeInt(chr.getId());
        out.writeInt(chr.getDamageSkin().getDamageSkinID());
        out.writeInt(0); //unk
        return out;
    }

    public static OutPacket showItemReleaseEffect(int charId, short ePos, boolean bonus) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_ITEM_RELEASE_EFFECT.getValue());
        out.writeInt(charId);
        out.writeShort(ePos);
        out.writeBool(bonus);
        return out;
    }

    public static OutPacket setSoulEffect(Integer charId, boolean set) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_SOUL_EFFECT.getValue());
        out.writeInt(charId);
        out.writeBool(set);
        return out;
    }

    public static OutPacket setSoulEffect(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_SOUL_EFFECT.getValue());
        out.writeInt(player.getId());
        Map<String, String> options = player.getQuestEx().get(QUEST_EX_SOUL_EFFECT);
        boolean set = false;
        if (options != null && options.containsKey("effect")) {
            set = options.get("effect").equals("1");
        }
        out.writeBool(set);
        return out;
    }
}
