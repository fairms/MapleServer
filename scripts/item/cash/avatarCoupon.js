import {getLuckyProps} from '../../export/RandomItems'

function start() {
    let itemId = im.getItemId();
    let luckyProps = getLuckyProps(itemId);
    let prob = luckyProps.get(0).prob;
    im.dropMessage("概率是" + prob);
}