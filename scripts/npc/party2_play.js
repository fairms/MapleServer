/** 玩具城组队 **/
const RecordType = Java.type("im.cave.ms.enums.RecordType");
const FieldEffect = Java.type("im.cave.ms.client.field.FieldEffect");

const stage1MapId = 922010100;
const stage2MapId = 922010400;
const stage2Inner = Array.of(922010401, 922010402, 922010403, 922010404, 922010405);
const stage3MapId = 922010600;
const stage4MapId = 922010700;
const stage5MapId = 922010800;
const stage6MapId = 922010900;
const allMaps = Array.of(stage1MapId, stage2MapId, stage3MapId, stage4MapId, stage5MapId, stage6MapId);
allMaps.concat(stage2Inner);

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
    }
    cm.dispose();
}


function stage1() {
    let talkCount = cm.getRecordValue(RecordType.NPC_TALK_COUNT, stage1Npc);
    if (talkCount < 1) {
        cm.sendNext("欢迎来到第1阶段,看看周围,是不是有很多#r老鼠#k请消灭它们并且带来20张#b通行证给我#k,如果你成功拿到了1张通行证，请交给你们的组长，然后再转交给我。")
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage1Npc, talkCount + 1);
    } else {
        const chr = cm.getChar();
        const party = chr.getParty();

        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendNext("请让队长和我对话.");
        } else {
            if (chr.haveItem(stage1ReqItem, stage1ReqItemCount)) {
                cm.sendPQProgressInMaps(16, allMaps);
                cm.fieldEffect(FieldEffect.screen("quest/party/clear"));
                cm.fieldEffect(FieldEffect.playSound("Party1/Clear", 100));
                cm.fieldEffect(FieldEffect.objectStateByString("gate"));
                chr.consumeItem(stage1ReqItem, stage1ReqItemCount)
                party.giveExpInMaps(100, allMaps);
                cm.sendNext("你们成功收集了#b20#k通行证。 已经成功完成了第一阶段。好了，我将开启通往下一个关卡的结界，时间不多了，你们赶快到那里进行第二阶段的挑战吧。")
            } else {
                cm.sendNext("消灭了所有的老鼠并且带来#b20#k张通行证才可以进入第二阶段。请检查一下你的背包~");
            }
        }
    }
}


function stage2() {
    let talkCount = cm.getRecordValue(RecordType.NPC_TALK_COUNT, stage2Npc);
    if (talkCount < 1) {
        cm.sendNext("欢迎来到第2阶段，让你的队员在次元洞内杀死所有的怪物并且收集14张通行证在来与我谈话。")
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage2Npc, talkCount + 1);
    } else {
        const chr = cm.getChar();
        const party = chr.getParty();

        if (chr.getId() !== party.getPartyLeaderId()) {
            cm.sendNext("请让你的队长和我对话.");
        } else {
            if (chr.haveItem(stage2ReqItem, stage2ReqItemCount) && stage2isCleared()) {
                cm.sendPQProgressInMaps(16, allMaps);
                chr.consumeItem(stage2ReqItem, stage2ReqItemCount)
                cm.fieldEffect(FieldEffect.screen("quest/party/clear"));
                cm.fieldEffect(FieldEffect.playSound("Party1/Clear", 100));
                cm.fieldEffect(FieldEffect.objectStateByString("gate"));
                party.giveExpInMaps(200, allMaps);
                cm.sendNext("你们成功收集了#b14#k通行证。 已经成功完成了第二阶段。好了，我将开启通往下一个关卡的结界，时间不多了，你们赶快到那里进行第三阶段的挑战吧。")
            } else {
                cm.sendNext("次元洞内的怪物没有清理完毕，赶快抓紧时间。");
            }
        }
    }
}

function stage3() {

}

function stage4() {

}

function stage5() {

}

function stage2isCleared() {
    let channel = chr.getMapleChannel();
    for (let mapId of stage2Inner) {
        let map = channel.getMap(mapId);
        if (map.getMobs().size() !== 0) {
            return false;
        }
    }
    return true;
}