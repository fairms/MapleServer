package im.cave.ms.client.field;

import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.items.ItemOption;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.Randomizer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Map;

/**
 * @author Sjonnie
 * Created on 6/9/2018.
 */
@Entity
@Table(name = "familiars")
@Getter
@Setter
public class Familiar extends MapleMapObj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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

    public Familiar(long id, int familiarID, String name, byte grade) {
        super(0);
        this.familiarId = familiarID;
        this.name = name;
        this.grade = grade;
        this.skill = Randomizer.rand(800, 904) + 1;
        initOptions();
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
}

