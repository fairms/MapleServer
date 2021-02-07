const RecordType = Java.Type("im.cave.ms.enums.RecordType");
const map = 200000301;

function enter(pm) {
    const returnMap = cm.getRecordValue("RETURN_MAP", map);
    if (returnMap > 0) {
        let sp = cm.findSPNearNpc(2010011, map);
        pm.warp(returnMap, sp);
        return true;
    } else {
        pm.getChar.announce("脚本执行错误");
        pm.getChar().enableAction();
        return false;
    }
}