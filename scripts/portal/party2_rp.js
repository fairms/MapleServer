const FieldEffect = Java.type("im.cave.ms.client.field.FieldEffect");

function enter(pm) {
    let chr = pm.getChar();
    let map = chr.getMap();
    let partyQuest = chr.getParty().getPartyQuest();
    if (partyQuest == null) {
        return false;
    }
    let portalName = pm.getPortalName();
    chr.chatMessage(portalName);
    if (portalName.startsWith("pt")) {
        let num = portalName.replace("pt", "");
        let level = Math.floor(parseInt(num) / 10);
        let pick = num % 10
        let answer = partyQuest.getParam1();
        let answerOptions = answer.split(',');
        if (answerOptions[level] === pick.toString()) {
            map.addEffectAndBroadcast(FieldEffect.objectStateByString(portalName.replace("pt", "an")));
            chr.teleport("np0" + level);
            chr.enableAction();
            if (level === 9) {
                cm.fieldEffect(FieldEffect.screen("quest/party/clear"));
                cm.fieldEffect(FieldEffect.playSound("Party1/Clear", 100));
                party.giveExpInProgress(300);
                partyQuest.pass(3);
                party.setPQProgress(Math.floor(100 / (6 * 3)));
            }
            return true;
        } else {
            chr.teleport("npFail");
            return false;
        }
    }
}