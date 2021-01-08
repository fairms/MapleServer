const map = 951000000;

function start() {
    if (cm.getMapId() === map) {
        const res = cm.sendAskYesNo("你好。怪物公园客车竭诚为大家提供最好的服务。你想回到原来的村里去吗？");
        if (res === 1) {
            cm.warp();
        }
    } else {
        const res = cm.sendAskYesNo("亲爱的顾客，你想到充满了欢乐的休彼德蔓的怪物公园去吗？");
        if (res === 0) {
            cm.sendSayOkay("那么等你考虑好了再来吧！");
        } else if (res === 1) {
            if (cm.sendSayNext("那么坐好了，我们马上出发！")) {
                cm.addQuestEx(map,);
                cm.warp(map);
            }
        }
    }
    cm.dispose();
}