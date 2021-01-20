package im.cave.ms.connection.packet.result;

import im.cave.ms.client.OnlineReward;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.OnlineRewardResultType;
import lombok.Data;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet.result
 * @date 1/20 11:04
 */
@Data
public class OnlineRewardResult {
    private OnlineRewardResultType type;
    private List<OnlineReward> rewards;
    private OnlineReward reward;

    public static OnlineRewardResult onlineRewardsList(MapleCharacter chr) {
        OnlineRewardResult rewardResult = new OnlineRewardResult();
        rewardResult.setType(OnlineRewardResultType.LIST);
        rewardResult.setRewards(chr.getOnlineRewards());
        return rewardResult;
    }

    public static OnlineRewardResult getItem(OnlineReward reward) {
        OnlineRewardResult rewardResult = new OnlineRewardResult();
        rewardResult.setType(OnlineRewardResultType.GET_ITEM);
        rewardResult.setReward(reward);
        return rewardResult;

    }
}
