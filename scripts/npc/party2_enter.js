/** 玩具城组队进场脚本 **/
const HashMap = Java.type('java.util.HashMap');

const pqEnterMap = 910002000;
const enterMap = 221023300;

const reqMinMembers = 1;
const reqMaxMembers = 6;
const reqMinLevel = 30;
const reqMaxLevel = 500;
const stage1MapId = 922010100;
const stage2MapId = 922010400;
const stage2Inner = Array.of(922010401, 922010402, 922010403, 922010404, 922010405);
const stage3MapId = 922010600;
const stage4MapId = 922010700;
const stage5MapId = 922010800;
const stage6MapId = 922010900;
const allMaps = Array.of(stage1MapId, stage2MapId, stage3MapId, stage4MapId, stage5MapId, stage6MapId);
allMaps.concat(stage2Inner);

function start() {
    const lines = new HashMap();
    lines.put(null, '#e<<组队任务：次元裂缝>>#n');
    lines.put(null, "");
    lines.put(null, '从这里往上到处都是很危险的东西，你不能继续往上走了。你想和队员们一起齐心协力，完成任务吗？如果想挑战的话，就通过#b所属组队的队长#k来和我说话。');
    lines.put(1, '#b我想参加组队任务。');
    lines.put(2, '我想寻找组队。');
    lines.put(3, '我想领取#t1022073#。');
    lines.put(4, '我想听听说明。');
    lines.put(5, '我想知道今天的剩余挑战次数。');

    let select = cm.sendAskMenu(lines);
    const chr = cm.getChar();
    const map = cm.getMapId();
    switch (select) {
        case 1:
            if (map === pqEnterMap) { //处于组队任务入场地图时
                if (cm.sendNext("如果你要挑战一下，我就会指引你到达塔的顶端......") === 1) {
                    cm.warp(enterMap);
                }
            } else if (map === enterMap) {
                let party = chr.getParty();
                let result = cm.partyRequireCheck(reqMinMembers, reqMaxMembers, reqMinLevel, reqMaxLevel, true);
                let success = result.getLeft();
                let msg = result.getRight();
                if (success) {
                    party.clearFieldInstances(stage1MapId);
                    let stage1 = party.getOrCreateFieldById(stage1MapId);
                    //创建一个组队地图  这样就可以一个线多个组队? 我擦 很奇怪
                    party.warp(stage1);
                } else {
                    cm.sendSayOkay(msg);
                }
            }
            break;
        case 2:
            break;
        case 3:
            break;
        case 4:
            break;
        case 5:
            break;
        case -1:
            break;
    }
    cm.dispose();
}