package strategies;

public class Step implements Strategy {
    double delta;

    public Step(double _delta) {
        delta = _delta;
    }

    @Override
    public Double generatePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        return objective + delta * step / 2;
    }

}
