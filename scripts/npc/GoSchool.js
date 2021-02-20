/*
    @npc:
    @map:
    @desc:
 */

function start() {
    const res = cm.sendAskYesNo("你好吗？");
    cm.serverMsg(res);
    cm.dispose();
}