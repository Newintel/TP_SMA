package strategies;

public class Step extends Strategy {
    double delta;

    public Step(double _delta) {
        delta = _delta;
    }

    @Override
    public Double internalGeneratePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        return objective + delta * step / 2;
    }

}
