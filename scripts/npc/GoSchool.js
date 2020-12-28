function start() {
    var res = cm.sendAskYesNo("你好吗？");
    cm.serverMsg(res);
    cm.dispose();
}