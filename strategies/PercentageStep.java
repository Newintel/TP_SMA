package strategies;

public class PercentageStep implements Strategy {
    double delta;

    public PercentageStep(double _delta) {
        if (_delta < -1 || _delta > 1) {
            throw new IllegalArgumentException("Delta must be in [-1, 1]");
        }
        delta = _delta;
    }

    public Double generatePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        return objective * (1 + Math.pow(delta, step / 2));
    }
}
