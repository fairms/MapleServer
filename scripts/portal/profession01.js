const RecordType = Java.type("im.cave.ms.enums.RecordType");
const map = 951000000;

function enter(pm) {
    pm.warp(map);
    pm.updateRecord(RecordType.RETURN_MAP, map, pm.getMapId())
    return true;
}