function start() {
    var HashMap = Java.type('java.util.HashMap');
    var options = new HashMap();
    options.put(null, '请选择你要去的地点:');
    options.put(0, '#m100010000#');
    options.put(1, '废弃都市');
    options.put(2, '魔法密林');
    options.put(3, '勇士部落');
    options.put(4, '明珠港');
    options.put(5, '诺特勒斯');
    options.put(6, '林中之城');
    var select = cm.sendAskMenu(options);
    // cm.dropMessage(select);
    cm.dispose();
}