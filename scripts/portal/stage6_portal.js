function enter(pm) {
    let chr = pm.getChar();
    let party = chr.getParty();
    let partyQuest = party.getPartyQuest();
    if (partyQuest.hasPassed(3)) {
        let nextMap = party.getOrCreateFieldById(922010700);
        partyQuest.addMap(nextMap);
        party.warp(nextMap);
        return true
    }
    return false;
}