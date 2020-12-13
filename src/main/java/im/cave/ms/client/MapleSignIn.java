package im.cave.ms.client;

import im.cave.ms.config.WorldConfig;
import im.cave.ms.constants.QuestConstants;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.net.packet.opcode.SendOpcode;

import java.util.ArrayList;
import java.util.List;

import static im.cave.ms.constants.GameConstants.MAX_TIME;
import static im.cave.ms.constants.GameConstants.ZERO_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 12/12 23:16
 */
public class MapleSignIn {
    public static final int MIN_LEVEL = 33;
    private static List<SignInRewardInfo> signRewards = new ArrayList<>();

    static {
        initSignRewards();
    }


    public static OutPacket getRewardPacket() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SIGNIN_REWARDS.getValue());
        outPacket.write(0);
        outPacket.write(1);
        outPacket.writeLong(ZERO_TIME);
        outPacket.writeLong(MAX_TIME);
        outPacket.writeLong(signRewards.size());
        outPacket.writeInt(QuestConstants.QUEST_EX_MOB_KILL_COUNT);
        outPacket.writeInt(QuestConstants.MOB_KILL_COUNT_MAX);
        outPacket.writeInt(signRewards.size());
        for (SignInRewardInfo signReward : signRewards) {
            outPacket.writeInt(signReward.getRank());
            outPacket.writeInt(signReward.getItemId());
            outPacket.writeInt(signReward.getQuantity());
            if (signReward.getExpiredTime() > 0) {
                outPacket.writeInt(1);
                outPacket.writeInt(signReward.getExpiredTime());
            } else {
                outPacket.writeLong(0);
            }
            outPacket.writeInt(signReward.isCash);
            outPacket.writeZeroBytes(6);
        }
        outPacket.writeInt(MIN_LEVEL);
        outPacket.writeZeroBytes(12);
        return outPacket;
    }

    public static void initSignRewards() {
        signRewards = WorldConfig.config.getSignInRewards();
    }

    public static class SignInRewardInfo {

        public int rank, itemId, quantity, expiredTime, isCash;

        SignInRewardInfo() {

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
