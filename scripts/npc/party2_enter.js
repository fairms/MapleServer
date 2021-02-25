/** 玩具城组队进场脚本 **/
const PartyQuestType = Java.type('im.cave.ms.enums.PartyQuestType');
const HashMap = Java.type('java.util.HashMap');

const pqEnterMap = 910002000;
const enterMap = 221023300;

const reqMinMembers = 1;
const reqMaxMembers = 6;
const reqMinLevel = 30;
const reqMaxLevel = 500;
const stage1MapId = 922010100;

function start() {
    const lines = new HashMap();
    lines.put(null, '#e<<组队任务：次元裂缝>#n');
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
            if (map === pqEnterMap) {//处于组队任务入场地图时
                if (cm.sendNext("如果你要挑战一下，我就会指引你到达塔的顶端......") === 1) {
                    cm.warp(enterMap);
                }
            } else if (map === enterMap) {
                let party = chr.getParty();
                let result = cm.partyRequireCheck(reqMinMembers, reqMaxMembers, reqMinLevel, reqMaxLevel, true);
                let success = result.getLeft();
                let msg = result.getRight();
                if (success) {
                    if (cm.hasInProgress(PartyQuestType.Party2)) {
                        cm.sendSayOkay("已经有队伍在里面了。")
                    } else {
                        let pq = party.startPQ(PartyQuestType.Party2);
                        let stage1 = party.getOrCreateFieldById(stage1MapId);
                        pq.addMap(stage1);
                        party.warp(stage1);
                    }
                } else {
                    cm.sendSayOkay(msg);
                }
            }
            break;
        case 2:
            cm.sendSayOkay("请向周围的朋友们请求组队。使用寻找组队(快捷键O)功能，可以在任何时间任何地点寻找组队。敬请参考。");
            break;
        case 3:
            cm.sendSayOkay("你每帮助我5次，我就会给你1个#i1022073:##b#t1022073##k。你只要再帮我#b5次#k，就可以获得#b#t1022073##k了。");
            break;
        case 4:
            cm.sendSayOkay(`#e<<组队任务：次元裂缝>#n\\n
#b#m220000000##k出现了次元裂缝！为了阻止入侵的怪物，我们迫切需要冒险家们自发的帮助。请和可以信赖的同伴一起，拯救#m220000000#！.必须消灭怪物，解决各种难关，战胜#r#o9300012##k。\\n
  - #e等级#n：50级以上#r(推荐等级 50～69 )#k\\n
  - #e限制时间#n：20分钟\\n
  - #e参加人数#n：3～6人\\n
  - #e获得物品#n：#i1022073:# #t1022073# #b(每帮助5次时获得)#k\\n                   各种消耗、其他、装备物品`);
            break;
        case 5:
            cm.sendSayOkay("今天剩余挑战次数是#r5次#k.");
            break;
        case -1:
            break;
    }
    cm.dispose();
}