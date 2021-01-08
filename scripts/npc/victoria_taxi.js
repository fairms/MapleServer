const maps = Array(100000000, 104000000, 102000000, 101000000, 103000000, 120000000, 105000000);

function start() {
    if (cm.sendAskNext("你好~!我是#p1012000#。你想快速又安全地移动到其他村庄吗? " +
        "那么就请使用令客户百分百满意的#b#p1012000##k吧。这次我给你免费优待!我将会送你去想去的地方。")) {
        const HashMap = Java.type('java.util.HashMap');
        const options = new HashMap();
        options.put(null, '请选择目的地。');
        for (let a = 0; a < maps.length; a++) {
            if (maps[a] !== cm.getMapId()) {
                options.put(a, "#m" + maps[a] + "#");
            }
        }
        const select = cm.sendAskMenu(options);
        if (select >= 0 && select < maps.length) {
            const res = cm.sendAskYesNo("看样子, 你好像已经没有什么事情需要在这里做了。确定要移动到#b#m" + maps[select] + "##k村庄吗?");
            if (res === 1) {
                cm.warp(maps[select]);
            } else if (res === 0) {
                cm.sendSayOkay("如果你想移动到其他村庄, 请随时使用我们的出租车~");
            }
        }
        cm.dispose();
    }
}