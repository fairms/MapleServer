package im.cave.ms.client.skill;

import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.skill
 * @date 12/1 21:52
 */
public class AttackInfo {
    public byte fieldKey;
    public byte hits;
    public int mobCount;
    public int skillId;
    public int skillLevel;
    public int keyDown;
    public byte idk;
    public boolean left;
    public short attackAction;
    public byte attackActionType;
    public byte direction;
    public byte idk0;
    public byte attackSpeed;
    public byte reduceCount;
    public int psdTargetPlus;
    public int someId;
    public List<MobAttackInfo> mobAttackInfo = new ArrayList<>();
    public int y;
    public int x;
    public short forcedY;
    public short forcedX;
    public short rcDstRight;
    public short rectRight;
    public int option;
    public int[] mists;
    public short forcedYSh;
    public short forcedXSh;
    public byte force;
    public short delay;
    public short[] shortArr;
    public byte addAttackProc;
    public int grenadeId;
    public byte zero;
    public int bySummonedID;
    public Position ptTarget = new Position();
    public int finalAttackLastSkillID;
    public byte finalAttackByte;
    public boolean ignorePCounter;
    public int spiritCoreEnhance;
    public Position ptAttackRefPoint = new Position();
    public Position idkPos = new Position();
    public Position pos = new Position();
    public byte fh;
    public Position teleportPt = new Position();
    public short Vx;
    public Position grenadePos;
    public Rect rect;
    public int elemAttr;
    public int areaPAD;
    public int attackCount;
    public int wt;
    public int ar01Mad;
    public Position pos3;
    //    public Summon summon;
    public int updateTime;
    public int bulletID;
    public short mobMove;
    public boolean isJablin;
    public int bulletCount;
    public Position bodyRelMove;
    public Position keyDownRectMoveXY;
    public int tick;
    public int passiveSLV;
    public int passiveSkillID;
    public byte someMask;
    public byte buckShot;
    public int option3;
    public int buckShotSkillID;
    public int buckShotSlv;
    public byte passiveAddAttackCount;
    public byte showFixedDamage;
    public boolean isDragonAttack;
    public byte mastery;
    public byte actionSpeed;
    public byte shootRange;
    public byte byteIdk2;
    public byte byteIdk3;
    public byte byteIdk4;
    public byte byteIdk5;
    public RecvOpcode attackHeader;
    public int requestTime;
    public int summonID;
    public boolean boxAttack;
}
