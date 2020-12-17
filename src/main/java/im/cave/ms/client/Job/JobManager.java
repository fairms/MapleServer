package im.cave.ms.client.Job;

import im.cave.ms.client.Job.adventurer.Beginner;
import im.cave.ms.client.Job.adventurer.Warrior;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.skill.AttackInfo;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.network.netty.InPacket;

import java.lang.reflect.InvocationTargetException;

/**
 * Created on 12/14/2017.
 */
public class JobManager {
    private static final Class[] jobClasses = new Class[]{
//            Archer.class,
//            BeastTamer.class,
            Beginner.class,
//            Kinesis.class,
//            Magician.class,
//            PinkBean.class,
//            Pirate.class,
//            Thief.class,
            Warrior.class,
//            Jett.class,
//
//            BlazeWizard.class,
//            DawnWarrior.class,
//            Mihile.class,
//            NightWalker.class,
//            Noblesse.class,
//            ThunderBreaker.class,
//            WindArcher.class,
//
//            Aran.class,
//            Evan.class,
//            Legend.class,
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

    public JobConstants.JobEnum getJobEnum() {
        return JobConstants.getJobEnumById(getId());
    }

    public static void handleAttack(MapleClient c, AttackInfo attackInfo) {
        for (Class clazz : jobClasses) {
            MapleJob job = null;
            try {
                job = (MapleJob) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (job != null && job.isHandlerOfJob(c.getPlayer().getJobId())) {
                job.handleAttack(c, attackInfo);
            }
        }
    }

    public static void handleSkill(MapleClient c, InPacket inPacket) {
        for (Class clazz : jobClasses) {
            MapleJob job = null;
            try {
                job = (MapleJob) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (job != null && job.isHandlerOfJob(c.getPlayer().getJobId())) {
                inPacket.readInt(); // crc
                inPacket.readInt(); // 00 00 00 00
                int skillID = inPacket.readInt();
                int slv = inPacket.readInt();
                job.handleSkill(c, skillID, slv, inPacket);
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
        for (Class clazz : jobClasses) {
            try {
                job = (MapleJob) clazz.getConstructor(MapleCharacter.class).newInstance(chr);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if (job != null && job.isHandlerOfJob(id)) {
                return job;
            }
        }
        return job;
    }
}
