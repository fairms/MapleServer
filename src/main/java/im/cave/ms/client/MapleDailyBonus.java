package im.cave.ms.client;

import im.cave.ms.configs.Config;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.constants.QuestConstants;

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
public class MapleDailyBonus {
    public static final int MIN_LEVEL = 33;
    private static List<CheckInRewardInfo> rewards = new ArrayList<>();

    static {
        initRewards();
    }


    public static CheckInRewardInfo getDailyRewardInfo(int day) {
        return rewards.get(day);
    }


    public static OutPacket init() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.DAILY_BONUS_INIT.getValue());
        out.write(0);
        out.write(1);
        out.writeLong(ZERO_TIME);
        out.writeLong(MAX_TIME);
        out.writeLong(31);
        out.writeInt(QuestConstants.QUEST_EX_MOB_KILL_COUNT);
        out.writeInt(QuestConstants.MOB_KILL_COUNT_MAX);
        out.writeInt(rewards.size());
        for (CheckInRewardInfo signReward : rewards) {
            out.writeInt(signReward.getRank());
            out.writeInt(signReward.getItemId());
            out.writeInt(signReward.getQuantity());
            if (signReward.getExpiredTime() > 0) {
                out.writeInt(1);
                out.writeInt(signReward.getExpiredTime());
            } else {
                out.writeLong(0);
            }
            out.writeInt(signReward.isCash);
            out.writeZeroBytes(6);
        }
        out.writeInt(MIN_LEVEL);
        out.writeZeroBytes(12);
        return out;
    }

    public static void initRewards() {
        rewards = Config.worldConfig.getDailyBonusRewards();
    }

    public static OutPacket getCheckInRewardPacket(int type, int itemId) {
        OutPacket out = new OutPacket();
        out.write(2);
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
