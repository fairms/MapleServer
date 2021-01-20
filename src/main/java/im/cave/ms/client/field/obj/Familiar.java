package im.cave.ms.client.field.obj;

import im.cave.ms.client.character.items.ItemOption;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.StringData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.tools.Randomizer;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import java.util.Map;


@Entity
@Table(name = "familiar")
@Getter
@Setter
public class Familiar extends MapleMapObj {
    @Id
    private long itemId;
    private int familiarId;
    private String name;
    private byte grade;
    private byte level = 1;
    private int exp;
    private int skill;
    private int option1, option2, option3;

    public Familiar() {
        super(0);
    }

    public Familiar(int templateId) {
        super(templateId);
    }

    public Familiar(int familiarID, String name, byte grade) {
        super(0);
        this.familiarId = familiarID;
        this.name = name;
        this.grade = grade;
        this.skill = Util.getRandomFromCollection(StringData.getFamiliarSkills().keySet());
        initOptions();
    }

    public static Familiar generate(int itemId) {
        int familiarID = ItemData.getFamiliarId(itemId);
        ItemInfo ii = ItemData.getItemInfoById(itemId);
        String name = StringData.getConsumeItemName(itemId);
        return new Familiar(familiarID, name, (byte) ii.getGrade());
    }

    private void initOptions() {
        Map<Integer, ItemOption> familiarOptions = ItemData.getFamiliarOptions();
        for (int i = 0; i < 3; ++i) {
            while (true) {
                ItemOption itemOption = familiarOptions.get(Randomizer.nextInt(familiarOptions.size()));
                if (i == 0) {
                    if (itemOption.getId() / 10000 == grade) {
                        setOption(i, itemOption.getId());
                        break;
                    }
                } else {
                    if (itemOption.getId() / 10000 == grade || itemOption.getId() / 10000 == grade - 1) {
                        setOption(i, itemOption.getId());
                        break;
                    }
                }
            }
        }
    }


    public void setOption(final int i, final int option) {
        switch (i) {
            case 0: {
                option1 = option;
            }
            case 1: {
                option2 = option;
            }
            case 2: {
                option3 = option;
            }
        }
    }


    public int getOption(int type) {
        switch (type) {
            case 0:
                return option1;
            case 1:
                return option2;
            case 2:
                return option3;
        }
        return 0;
    }


    public void encode(OutPacket out) {
        out.writeInt(getFamiliarId());
        out.writeInt(0);
        out.writeInt(2);
        out.writeInt(getFamiliarId());
        out.writeAsciiString(getName(), 13);
        out.write(0);
        out.writeShort(getLevel());
        out.writeShort(getSkill());
        out.writeShort(131);
        out.writeInt(getExp());
        out.writeShort(getLevel());
        out.writeShort(getOption1());
        out.writeShort(getOption2());
        out.writeShort(getOption3());
        out.write(8);
        out.write(getGrade());
        out.writeInt(82009);
        out.writeShort(0);
    }
}

