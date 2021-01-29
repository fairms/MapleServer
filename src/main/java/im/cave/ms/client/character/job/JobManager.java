package im.cave.ms.client.character.job;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.job.adventurer.Archer;
import im.cave.ms.client.character.job.adventurer.Beginner;
import im.cave.ms.client.character.job.adventurer.Magician;
import im.cave.ms.client.character.job.adventurer.Pirate;
import im.cave.ms.client.character.job.adventurer.Thief;
import im.cave.ms.client.character.job.adventurer.Warrior;
import im.cave.ms.client.character.skill.AttackInfo;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.JobEnum;

import java.lang.reflect.InvocationTargetException;

public class JobManager {
    private static final Class<?>[] jobClasses = new Class[]{
//            BeastTamer.class,
            GM.class,
            Beginner.class,
            Warrior.class,
            Archer.class,
            Thief.class,
            Magician.class,
            Pirate.class,

//            Kinesis.class,
//            PinkBean.class,
//            Jett.class,
//
//            BlazeWizard.class,
//            DawnWarrior.class,
//            Mihile.class,
//            NightWalker.class,
//            Noblesse.class,
//            ThunderBreaker.class,
//            WindArcher.class,

//            Legend.class,
//            Aran.class,
//            Evan.class,
//            Luminous.class,
//            Mercedes.class,
//            Phantom.class,
//            Shade.class,
//
//            AngelicBuster.class,
//            Kaiser.class,
//
//            BattleMage.class,
//            Blaster.class,
//            Citizen.class,
//            Demon.class,
//            Mechanic.class,
//            WildHunter.class,
//            Xenon.class,
//
//            Hayato.class,
//            Kanna.class,
//
//            Zero.class,
//
//            Illium.class,
//            Ark.class
    };

    private short id;

    public JobEnum getJobEnum() {
        return JobConstants.getJobEnumById(getId());
    }

    public static void handleAttack(MapleClient c, AttackInfo attackInfo) {
        for (Class<?> clazz : jobClasses) {
            MapleJob job = null;
            try {
                job = (MapleJob) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (job != null && job.isHandlerOfJob(c.getPlayer().getJob())) {
                job.handleAttack(c, attackInfo);
            }
        }
    }

    public static void handleSkill(MapleClient c, InPacket in) throws Exception {
        for (Class<?> clazz : jobClasses) {
            MapleJob job = null;
            try {
                job = (MapleJob) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if (job != null && job.isHandlerOfJob(c.getPlayer().getJob())) {
                in.readInt(); // crc
                in.readInt(); // 00 00 00 00
                int skillID = in.readInt();
                int slv = in.readInt();
                job.handleSkill(c, skillID, slv, in);
            }
        }
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public static MapleJob getJobById(short id, MapleCharacter chr) {
        MapleJob job = null;
        for (var clazz : jobClasses) {
            try {
                job = (MapleJob) clazz.getConstructor(MapleCharacter.class).newInstance(chr);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException ignored) {
            }
            if (job != null && job.isHandlerOfJob(id)) {
                return job;
            }
        }
        return job;
    }
}
