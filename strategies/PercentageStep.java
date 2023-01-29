package strategies;

public class PercentageStep extends Strategy {
    double delta;

    public PercentageStep(double _delta) {
        if (_delta < -1 || _delta > 1) {
            throw new IllegalArgumentException("Delta must be in [-1, 1]");
        }
        delta = _delta;
    }

    public Double internalGeneratePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        return objective * (1 + (Math.pow(Math.abs(delta), 2 / (step + step % 2)) * (delta / Math.abs(delta))));
    }
}
