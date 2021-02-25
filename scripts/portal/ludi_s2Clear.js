function enter(pm) {
    let chr = pm.getChar();
    let party = chr.getParty();
    let partyQuest = party.getPartyQuest();
    if (partyQuest.hasPassed(2)) {
        let nextMap = party.getOrCreateFieldById(922010600);
        partyQuest.addMap(nextMap);
        pm.warp(nextMap);
        return true
    }
    return false;
}