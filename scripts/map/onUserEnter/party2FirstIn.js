/** 玩具城组队:初始  **/
const hidden = Array();
const timeLimit = 1800;

function start() {

    let chr = ms.getChar();
    ms.npcDisableInfo(hidden);
    ms.beginClock(1800);
    chr.getParty().setPQProgress(0);
    let itemQuantity = chr.getItemQuantity(4001022);
    chr.consumeItem(4001022, itemQuantity);

}