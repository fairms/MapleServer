/*
    转职脚本
    todo
 */

function start() {
    let HashMap = Java.type('java.util.HashMap');
    let options = new HashMap();
    const jobId = cm.getJob();
    let advancedJobs = cm.getAdvancedJobs(jobId);
    if (advancedJobs.size() === 0) {
        cm.sendSayOkay("已经完成所有转职。");
    } else {
        options.put(null, "请选择你的路线");
        for (let advancedJob of advancedJobs) {
            options.put(advancedJob.getJob(), advancedJob.getName());
            console.log(advancedJob.getName())
        }
        const selected = cm.sendAskMenu(options);
        if (selected > 0) {
            let jobReqLev = cm.getJobReqLev(selected);
            if (cm.getLevel() < jobReqLev) {
                cm.sendSayOkay("等级不足!");
            } else {
                cm.changeJob(selected);
                cm.sendSayOkay("完成转职了。")
            }
        }
    }
    cm.dispose();
}