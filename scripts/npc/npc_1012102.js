function start() {
    var itemId = cm.sendAskText("请输入道具ID", "", 5, 10);
    cm.serverMsg(itemId);
    cm.dispose();
}