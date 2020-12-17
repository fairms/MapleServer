package im.cave.ms.client.field;

import im.cave.ms.client.field.obj.MapleMapObj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Sjonnie
 * Created on 6/9/2018.
 */
@Entity
@Table(name = "familiars")
public class Familiar extends MapleMapObj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int familiarId;
    private String name;
    private short grade;
    private int exp;
    private int skill;
    private int option1, option2, option3;

    public Familiar() {
        super(0);
    }

    public Familiar(int templateId) {
        super(templateId);
    }

}
