const RecordType = Java.Type("im.cave.ms.enums.RecordType");
const map = 910001000;

function enter(pm) {
    const returnMap = cm.getRecordValue("RETURN_MAP", map);
    if (returnMap > 0) {
        pm.warp(returnMap, "profession");
        return true;
    } else {
        pm.getChar().enableAction();
        return false;
    }
}