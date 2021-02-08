const map = 200000301;

function start() {
    let res = cm.sendAskYesNo("你好。我是负责家族支援工作的蕾雅。为了工作方便，我来到了英雄公馆，为大家提供帮助。你想到英雄公馆去处理家族相关事务吗？");
    if (res === 0) {
        cm.sendNext("想去英雄公馆的话，请再来找我。");
    } else if (res === 1) {
        if (cm.sendNext("好的，我马上把你送过去。") === 1) {
            cm.updateRecord("RETURN_MAP", map, cm.getMapId());
            cm.warp(map);
        }
    }

    cm.dispose();
}