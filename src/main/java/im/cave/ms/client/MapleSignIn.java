package im.cave.ms.client;

import im.cave.ms.config.WorldConfig;
import im.cave.ms.constants.QuestConstants;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

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


    public static MaplePacketLittleEndianWriter getRewardPacket() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SIGNIN_REWARDS.getValue());
        mplew.write(0);
        mplew.write(1);
        mplew.writeLong(ZERO_TIME);
        mplew.writeLong(MAX_TIME);
        mplew.writeLong(signRewards.size());
        mplew.writeInt(QuestConstants.QUEST_EX_MOB_KILL_COUNT);
        mplew.writeInt(QuestConstants.MOB_KILL_COUNT_MAX);
        mplew.writeInt(signRewards.size());
        for (SignInRewardInfo signReward : signRewards) {
            mplew.writeInt(signReward.getRank());
            mplew.writeInt(signReward.getItemId());
            mplew.writeInt(signReward.getQuantity());
            if (signReward.getExpiredTime() > 0) {
                mplew.writeInt(1);
                mplew.writeInt(signReward.getExpiredTime());
            } else {
                mplew.writeLong(0);
            }
            mplew.writeInt(signReward.isCash);
            mplew.writeZeroBytes(6);
        }
        mplew.writeInt(MIN_LEVEL);
        mplew.writeZeroBytes(12);
        return mplew;
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
