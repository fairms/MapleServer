/** 玩具城组队 **/

/** 常量 **/
const RecordType = Java.type("im.cave.ms.enums.RecordType");
const FieldEffect = Java.type("im.cave.ms.client.field.FieldEffect");

const stage1MapId = 922010100;
const stage2MapId = 922010400;
const stage2Inner = Array.of(922010401, 922010402, 922010403, 922010404, 922010405);
const stage3MapId = 922010600;
const stage4MapId = 922010700;
const stage5MapId = 922010800;
const stage6MapId = 922010900;

const stage1Npc = 2040036;
const stage2Npc = 2040039;
const stage3Npc = 2040041;
const stage4Npc = 2040042;
const stage5Npc = 2040043;
const stage6Npc = 2040044;
const quitNpc = 2040047;

const stage1ReqItem = 4001022;
const stage1ReqItemCount = 20;
const stage2ReqItem = 4001022;
const stage2ReqItemCount = 14;
const stage4ReqItem = 4001022;
const stage4ReqItemCount = 4;
const stage6ReqItem = 4001022;
const stage6ReqItemCount = 1;

/** 入口 **/
function start() {
    const mapId = cm.getMapId();
    switch (mapId) {
        case stage1MapId:
            stage1();
            break
        case stage2MapId:
            stage2();
            break
        case stage3MapId:
            stage3();
            break
        case stage4MapId:
            stage4();
            break
        case stage5MapId:
            stage5();
        case stage6MapId:
            stage6();
    }
    cm.dispose();
}


function stage1() {
    const chr = cm.getChar();
    const party = chr.getParty();
    const partyQuest = party.getPartyQuest();
    let talkCount = cm.getRecordValue(RecordType.NPC_TALK_COUNT, stage1Npc);
    if (talkCount < 1) {
        cm.sendNext("欢迎来到第1阶段,看看周围,是不是有很多#r老鼠#k请消灭它们并且带来20张#b通行证给我#k,如果你成功拿到了1张通行证，请交给你们的组长，然后再转交给我。")
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage1Npc, talkCount + 1);
    } else if (partyQuest.hasPassed(1)) {
        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendSayOkay("传送口已经打开,快点进入下一个阶段吧...")
        } else {
            cm.sendSayOkay("你们已经成功完成了第一阶段,赶快向第二阶段前进吧。");
        }
    } else {
        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendNext("快点消灭所有的怪物并收集通行证，然后交给组队长！");
        } else {
            if (chr.haveItem(stage1ReqItem, stage1ReqItemCount)) {
                cm.fieldEffect(FieldEffect.screen("quest/party/clear"));
                cm.fieldEffect(FieldEffect.playSound("Party1/Clear", 100));
                cm.fieldEffect(FieldEffect.objectStateByString("gate"));
                chr.consumeItem(stage1ReqItem, stage1ReqItemCount)
                party.giveExpInProgress(100);
                party.setPQProgress(Math.floor(100 / 6));
                partyQuest.pass(1);
                cm.sendNext("你们成功收集了#b20#k通行证。 已经成功完成了第一阶段。好了，我将开启通往下一个关卡的结界，时间不多了，你们赶快到那里进行第二阶段的挑战吧。")
            } else {
                cm.sendNext("消灭了所有的老鼠并且带来#b20#k张通行证才可以进入第二阶段。请检查一下你的背包~");
            }
        }
    }
}


function stage2() {
    const chr = cm.getChar();
    const party = chr.getParty();
    const partyQuest = party.getPartyQuest();
    let talkCount = cm.getRecordValue(RecordType.NPC_TALK_COUNT, stage2Npc);
    if (talkCount < 1) {
        cm.sendNext("欢迎来到第2阶段，让你的队员在次元洞内杀死所有的怪物并且收集14张通行证在来与我谈话。")
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage2Npc, talkCount + 1);
    } else if (partyQuest.hasPassed(2)) {
        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendSayOkay("传送口已经打开,快点进入下一个阶段吧...")
        } else {
            cm.sendSayOkay("恭喜你们完成第二阶段。时间已经不多了,赶快进入下一阶段吧。");
        }
    } else {
        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendNext("快点消灭所有的怪物并收集通行证，然后交给组队长！");
        } else {
            if (chr.haveItem(stage2ReqItem, stage2ReqItemCount) && stage2isCleared()) {
                chr.consumeItem(stage2ReqItem, stage2ReqItemCount)
                cm.fieldEffect(FieldEffect.screen("quest/party/clear"));
                cm.fieldEffect(FieldEffect.playSound("Party1/Clear", 100));
                cm.fieldEffect(FieldEffect.objectStateByString("gate"));
                party.giveExpInProgress(200);
                partyQuest.pass(2);
                party.setPQProgress(Math.floor(100 / (6 * 2)));
                cm.sendNext("你们成功收集了#b14#k通行证。 已经成功完成了第二阶段。好了，我将开启通往下一个关卡的结界，时间不多了，你们赶快到那里进行第三阶段的挑战吧。")
            } else {
                cm.sendNext("次元洞内的怪物没有清理完毕，赶快抓紧时间。");
            }
        }
    }
}

function stage3() {
    cm.sendSayOkay("你好!欢迎来到第三阶段!看看这里,你会看到很多数字盒子,如果你想通过这里到达下一个阶段,你必须找到正确的数字盒子。但是，如果你选择了错误的箱子，你将从原地重新开始!祝你好运。");
}


function stage4() {
    const chr = cm.getChar();
    const party = chr.getParty();
    const partyQuest = party.getPartyQuest();
    let talkCount = cm.getRecordValue(RecordType.NPC_TALK_COUNT, stage4Npc);
    if (talkCount < 1) {
        cm.sendNext("欢迎来到第四阶段,请和你的队友消灭地图上所有的怪物并且收集4张通行证,获取通行证,你们把通行证全部交给组队长。组队长再和我讲话，就可以顺利通关了·那么祝你—切顺利.")
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage4Npc, talkCount + 1);
    } else if (partyQuest.hasPassed(4)) {
        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendSayOkay("传送口已经打开,快点进入下一个阶段吧...")
        } else {
            cm.sendSayOkay("恭喜你们完成第四阶段。时间已经不多了,赶快进入下一阶段吧。");
        }
    } else {
        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendNext("快点消灭所有的怪物并收集通行证，然后交给组队长！");
        } else {
            if (chr.haveItem(stage4ReqItem, stage4ReqItemCount) && chr.getMap().getMobs().size() === 0) {
                chr.consumeItem(stage4ReqItem, stage4ReqItemCount)
                cm.fieldEffect(FieldEffect.screen("quest/party/clear"));
                cm.fieldEffect(FieldEffect.playSound("Party1/Clear", 100));
                cm.fieldEffect(FieldEffect.objectStateByString("gate"));
                party.giveExpInProgress(400);
                partyQuest.pass(4);
                party.setPQProgress(Math.floor(100 / (6 * 4)));
                cm.sendNext("你们成功收集了#b14#k通行证。 已经成功完成了第二阶段。好了，我将开启通往下一个关卡的结界，时间不多了，你们赶快到那里进行第三阶段的挑战吧。")
            } else {
                cm.sendNext("次元洞内的怪物没有清理完毕，赶快抓紧时间。");
            }
        }
    }
}

function stage5() {

}


function stage6() {

}

function stage2isCleared() {
    let chr = cm.getChar();
    let partyQuest = chr.getParty().getPartyQuest();
    for (let mapId of stage2Inner) {
        let map = partyQuest.getMap(mapId);
        if (map.getMobs().size() !== 0) {
            chr.chatMessage("地图:" + map.getId() + " 还有怪物没有清除");
            return false;
        }
    }
    return true;
}