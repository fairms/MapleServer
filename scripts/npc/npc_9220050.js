function start() {
    const HashMap = Java.type('java.util.HashMap');
    const options = new HashMap();
    options.put(null, '测试测试测试测试:');
    options.put(0, '更换发型');
    options.put(1, '打开次元之镜');
    options.put(2, '升级');
    options.put(3, '重置人物');
    options.put(4, '获取当前地图怪物');
    options.put(5, '获取当前地图掉落');
    options.put(6, '任务查看');
    var select = cm.sendAskMenu(options);
    cm.dropMessage(select);
    if (select === 0) {
        const options = [31050, 31040, 31000];
        const option = cm.sendAskAvatar("请选择:", 5150052, options);
        cm.dropMessage(option);
    } else if (select === 1) {
        cm.openUnityPortal();
    }
    cm.dispose();
}