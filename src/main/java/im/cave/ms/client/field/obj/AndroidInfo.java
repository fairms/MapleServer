package im.cave.ms.client.field.obj;

import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.field.obj
 * @date 1/1 17:35
 */
@Setter
@Getter
public class AndroidInfo {
    private byte type;
    private byte gender;
    private boolean shopUsable;
    private int chatBalloon;
    private int nameTag;
    private int accessory;
    private int longcoat;
    private int shoes;
    private List<Integer> skins = new ArrayList<>();
    private List<Integer> faces = new ArrayList<>();
    private List<Integer> hairs = new ArrayList<>();

    public AndroidInfo(byte type) {
        this.type = type;
    }

    public void addSkin(int skin) {
        skins.add(skin);
    }

    public void addFace(int face) {
        faces.add(face);
    }

    public void addHair(int hair) {
        hairs.add(hair);
    }

    public short getRandomFace() {
        return Util.getRandomFromCollection(faces).shortValue();
    }

    public short getRandomHair() {
        return Util.getRandomFromCollection(hairs).shortValue();
    }

    public short getRandomSkins() {
        return Util.getRandomFromCollection(skins).shortValue();
    }
}
