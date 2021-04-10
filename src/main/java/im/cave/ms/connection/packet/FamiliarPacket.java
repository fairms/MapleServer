package im.cave.ms.connection.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.movement.Movement;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.tools.HexTool;
import im.cave.ms.tools.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 1/18 21:51
 * todo 搞懂这些！！！
 */
public class FamiliarPacket {
    public static OutPacket familiarResult(MapleCharacter chr, byte type, OutPacket action, Familiar familiar) {
        OutPacket out = new OutPacket(SendOpcode.FAMILIAR);
        out.writeInt(chr.getId());
        out.write(type); // 5 7
        switch (type) {
            case 5:
                byte[] data = action.getData();
                out.writeShort(data.length);
                out.write(data);
                action.release();
                break;
            case 7:
                out.writeInt(familiar.getObjectId());
                out.writeInt(-1692623269);
                out.writeInt(chr.getAccId());
                out.writeInt(chr.getId());
                out.write(1);
                out.writeShort(2);
                out.writeInt(chr.getAccId());
                out.writeInt(chr.getId());
                out.writeShort(3);
                out.writeInt(1);
                out.writeShort(4);
                out.writeLong(7);
                out.writeInt(2);
                out.writeInt(familiar.getFamiliarId());
                out.writeZeroBytes(14);

                break;
        }
        return out;
    }

    public static OutPacket updateFamiliars(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.FAMILIAR);

        out.writeInt(chr.getId());
        out.write(7);
        out.writeInt(1);
        out.writeInt(-1723358014);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.write(1);
        out.writeShort(5); //all familiar
        List<Familiar> familiars = chr.getFamiliars();
        out.writeShort(familiars.size());
        int i = 1;
        for (Familiar familiar : familiars) {
            out.writeShort(i);
            familiar.encode(out);
        }
        out.writeInt(0);
        out.write(0);

        return out;
    }

    public static OutPacket revealFamiliars(List<Familiar> familiars) {
        OutPacket out = new OutPacket();
        out.writeInt(1);
        out.writeShort(11);
        out.write(6);
        for (Familiar familiar : familiars) {
            out.writeInt(familiar.getFamiliarId());
            out.write(familiar.getGrade());
            out.writeInt(0);
        }
        return out;
    }


    public static void encode(OutPacket out, List<Short> parts, MapleCharacter chr) {

        //12 D5 5A 82
        for (Short part : parts) {
            out.writeShort(part);
            switch (part) {
                case 0:
                    out.write(HexTool.getByteArrayFromHexString("C2 A4 47 99"));
                    out.writeInt(chr.getAccId());
                    out.writeInt(chr.getId());
                    out.write(1);
                    break;
                case 1:
                    out.writeZeroBytes(20);
                    break;
                case 2:
                    out.writeInt(chr.getAccId());
                    out.writeInt(chr.getId());
                    break;
                case 3:
                    out.writeInt(1);
                    break;
                case 4:
                    out.writeInt(0); //当前召唤的怪怪
                    //
                case 5:
                    List<Familiar> familiars = chr.getFamiliars();
                    out.writeShort(familiars.size());
                    int i = 1;
                    for (Familiar familiar : familiars) {
                        out.writeShort(i);
                        familiar.encode(out);
                    }
                    out.writeShort(0);
                    break;
                case 7:
                    out.writeShort(0);
                    break;
                case 8:
                    List<Integer> ll = Arrays.asList(1, 2, 3);
                    out.writeShort(ll.size());
                    for (Integer integer : ll) {
                        out.writeInt(integer);
                    }
                    out.writeShort(0);
                    break;
                case 11:
                    out.write(HexTool.getByteArrayFromHexString("91 02 A1 D4"));
                    break;
                case 12:
                    out.writeZeroBytes(7);
                    out.writeInt(2);

            }
        }
    }


    public static OutPacket showRevealFamiliars(MapleCharacter chr, List<Familiar> familiars) {
        return familiarResult(chr, (byte) 5, revealFamiliars(familiars), null);
    }

    public static OutPacket spawnFamiliar(Familiar before, Familiar after, MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.FAMILIAR);
        out.writeInt(chr.getId());
        out.write(7);
        out.writeInt(before == null ? 1 : before.getObjectId());
        out.writeInt(-1692623269);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        if (before != null && after != null) {
            out.writeShort(2);
            out.writeInt(after.getObjectId());
            out.writeInt(-1692623269);
            out.writeInt(chr.getAccId());
            out.writeInt(chr.getId());

        }
        if (after != null) {
            out.writeBool(true);
            out.writeShort(2);//2
            out.writeInt(chr.getAccId());
            out.writeInt(chr.getId());
            out.writeShort(3);//3
            out.writeInt(1);
            out.writeShort(4);//5
            after.encode(out);
            out.writeShort(5);//5
            Position position = after.getPosition();
            out.writePositionInt(position);
            out.writeShort(6);//6
            out.writeInt(2000);
            out.writeShort(7);//7
            out.writeInt(2000);
            out.writeShort(0);
            out.write(0);
        } else {
            out.writeShort(2);
        }

        out.writeInt(1);
        out.write(-1723358014);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.write(1);
        out.writeShort(4);
        out.writeInt(after == null ? 0 : after.getObjectId());
        out.writeShort(0);
        out.write(0);
        return out;
    }

    public static OutPacket updateFamiliarToChar(Familiar familiar, MapleCharacter player) {
        OutPacket out = new OutPacket(SendOpcode.FAMILIAR);

        out.writeInt(player.getId());
        out.write(7);
        out.writeInt(1);
        out.writeInt(-1723358014);
        out.writeInt(player.getAccId());
        out.writeInt(player.getId());
        out.write(1);
        out.writeShort(5);
        out.writeShort(1);
        out.writeShort(1);
        familiar.encode(out);
        out.writeInt(0);
        out.write(0);

        return out;
    }

    public static OutPacket moveFamiliar(MapleCharacter chr, MovementInfo movementInfo) {
        OutPacket out = new OutPacket();
        out.writeInt(4);
        out.writeShort(2);
        out.write(120);  //状态或者方向
        movementInfo.encode(out);
        out.write(0);
        return familiarResult(chr, (byte) 5, out, null);
    }
}
