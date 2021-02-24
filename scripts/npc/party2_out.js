/** 玩具城组队:退场处理 **/
const RecordType = Java.type("im.cave.ms.enums.RecordType");

const enterMap = 221023300;
const stage1NPCId = 2040036;
const stage2NPCId = 2040036;
const stage3NPCId = 2040036;
const outMap = 922010000;

function start() {
    let chr = cm.getChar();
    if (chr.getMapId() === outMap) {
        out(enterMap);
    } else {
        if (cm.sendAskYesNo("你要退出吗?") === 1) {
            out(outMap);
        }
    }
    cm.dispose();
}

function out(mapId) {
    cm.warp(mapId);
    if (mapId === enterMap) {
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage1NPCId, 0);
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage2NPCId, 0);
        cm.updateRecord(RecordType.NPC_TALK_COUNT, stage3NPCId, 0);
    }
}