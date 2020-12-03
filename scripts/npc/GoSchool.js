function start() {
    var res = cm.sendAskYesNo("你好吗？");
    cm.serverMsg(res);
    // cm.sendAskAccept("可以吗？") // 38
    // cm.sendSayOkay("测试");
    cm.dispose();
}