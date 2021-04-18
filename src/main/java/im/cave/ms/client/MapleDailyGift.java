package im.cave.ms.client;

import im.cave.ms.configs.Config;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.constants.QuestConstants;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 12/12 23:16
 */
public class MapleDailyGift {

    public static final int MIN_LEVEL = 33;
    private static List<CheckInRewardInfo> rewards = new ArrayList<>();
    public static final LocalDate lastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());


    static {
        initRewards();
    }


    public static CheckInRewardInfo getDailyRewardInfo(int day) {
        return rewards.get(day);
    }


    public static OutPacket init() {
        OutPacket out = new OutPacket(SendOpcode.DAILY_GIFT_RESULT);
        out.write(0); //type
        out.write(1);
        out.writeLong(ZERO_TIME);
        out.writeLong(MAX_TIME);
        out.writeLong(lastDay.getDayOfMonth());
        out.writeInt(QuestConstants.QUEST_EX_MOB_KILL_COUNT);
        out.writeInt(QuestConstants.MOB_KILL_COUNT_MAX);
        out.writeInt(rewards.size()); //最大就是28
        for (CheckInRewardInfo signReward : rewards) {
            out.writeInt(signReward.getRank()); //idx
            out.writeInt(signReward.getItemId());
            out.writeInt(signReward.getQuantity());
            out.writeLong(signReward.getExpiredTime());
            out.writeInt(signReward.isCash);
            out.writeZeroBytes(4);
        }
        out.writeInt(MIN_LEVEL);
        out.writeZeroBytes(12);
        return out;
    }

    public static void initRewards() {
        rewards = Config.worldConfig.getDailyGiftsRewards();
    }

    public static OutPacket getCheckInRewardPacket(int type, int itemId) {
        OutPacket out = new OutPacket(SendOpcode.DAILY_GIFT_RESULT);

        out.write(2); //type
        out.writeInt(type);
        out.writeInt(itemId);

        return out;
    }

    public static class CheckInRewardInfo {

        public int rank, itemId, quantity, expiredTime, isCash;

        CheckInRewardInfo() {

        }

        public int getRank() {
            return rank;
        }

        public int getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getExpiredTime() {
            return expiredTime;
        }

        public int getIsCash() {
            return isCash;
        }

    }
}
