package im.cave.ms.connection.packet.result;

import im.cave.ms.client.HotTimeReward;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.HotTimeRewardResultType;
import lombok.Data;

import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet.result
 * @date 1/20 11:04
 */
@Data
public class HotTimeRewardResult {
    private HotTimeRewardResultType type;
    private List<HotTimeReward> rewards;
    private HotTimeReward reward;

    public static HotTimeRewardResult hotTimeRewardsList(MapleCharacter chr) {
        HotTimeRewardResult rewardResult = new HotTimeRewardResult();
        rewardResult.setType(HotTimeRewardResultType.LIST);
        rewardResult.setRewards(chr.getHotTimeRewards());
        return rewardResult;
    }

    public static HotTimeRewardResult getItem(HotTimeReward reward) {
        HotTimeRewardResult rewardResult = new HotTimeRewardResult();
        rewardResult.setType(HotTimeRewardResultType.GET_ITEM);
        rewardResult.setReward(reward);
        return rewardResult;

    }
}
