const FieldEffect = Java.type("im.cave.ms.client.field.FieldEffect");

function enter(pm) {
    let chr = pm.getChar();
    let map = chr.getMap();
    let partyQuest = chr.getParty().getPartyQuest();
    if (partyQuest == null) {
        return;
    }
    let portalName = pm.getPortalName();
    if (portalName.startsWith("pt")) {
        let num = portalName.replace("pt", "");
        let level = Math.floor(parseInt(num) / 10);
        let pick = num % 10
        let answer = partyQuest.getParam1();
        let answerOptions = answer.split(',');
        if (answerOptions[level] === pick.toString()) {
            map.addEffectAndBroadcast(FieldEffect.objectStateByString(portalName.replace("pt", "an")));
            chr.teleport("np0" + level);
        } else {
            chr.teleport("npFail");
        }
    }
}