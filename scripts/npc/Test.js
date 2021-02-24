function start() {
    const UIType = Java.type('im.cave.ms.enums.UIType');
    console.log(UIType.UI_STAT.getVal())
    let Randomizer = Java.type("im.cave.ms.tools.Randomizer");
    console.log(Randomizer.rand(20, 2000));
}