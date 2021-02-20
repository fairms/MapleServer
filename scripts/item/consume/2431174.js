/*
    荣誉勋章
 */
function start(im) {
    let Randomizer = Java.type("im.cave.ms.tools.Randomizer");
    im.addHonerPoint(Randomizer.rand(10, 120));
    return true;
}