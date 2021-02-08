const map = 200000301;
const npc_Leia = 2010011;

function enter(pm) {
    const returnMap = pm.getRecordValue("RETURN_MAP", map);
    if (returnMap > 0) {
        let sp = pm.findSPNearNpc(returnMap, npc_Leia);
        pm.warp(returnMap, sp);
        return true;
    } else {
        pm.warp(100000000, 0);//回射手咯
        pm.getChar().enableAction();
        return false;
    }
}