/** 玩具城组队关卡初始化 **/
const stage1MapId = 922010100;
const stage2MapId = 922010400;
const stage2Inner = Array.of(922010401, 922010402, 922010403, 922010404, 922010405);
const stage3MapId = 922010600;
const stage4MapId = 922010700;
const stage5MapId = 922010800;
const stage6MapId = 922010900;

function start() {
    let mapId = ms.getMapId();
    let party = ms.getChar().getParty();
    let partyQuest = party.getPartyQuest();
    if (partyQuest == null) {
        return;
    }
    ms.disablePortal("next00");
    switch (mapId) {
        case stage1MapId:
            break
        case stage2MapId:
            for (let hole of stage2Inner) {
                let holeMap = party.getOrCreateFieldById(hole);
                partyQuest.addMap(holeMap);
            }
            break
        case stage3MapId:
            let answerOptions = [];
            for (let i = 0; i < 10; i++) {
                answerOptions.push(randomNum(0, 2))
            }
            partyQuest.setParam1(answerOptions.toString());
            break
        case stage4MapId:
            break;
        case stage5MapId: //计算题
            break;
        case stage6MapId: //召唤阿莉莎
            let map = chr.getMap();
            map.spawnMob(mob, x, y);
            break;
    }
}


//生成从minNum到maxNum的随机数
function randomNum(minNum, maxNum) {
    switch (arguments.length) {
        case 1:
            return parseInt(Math.random() * minNum + 1, 10);
        case 2:
            return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
        default:
            return 0;
    }
}