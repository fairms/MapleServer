package im.cave.ms.client.character.temp;


public class TwoStateTemporaryStat extends TemporaryStatBase {

    public TwoStateTemporaryStat(boolean dynamicTermSet) {
        super(dynamicTermSet);
    }

    @Override
    public int getMaxValue() {
        return 0;
    }

    @Override
    public boolean isActive() {
        return getNOption() != 0;
    }

}
