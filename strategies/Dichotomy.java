package strategies;

public class Dichotomy extends Strategy {

    @Override
    public Double internalGeneratePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        return (objective + basePrice) / 2;
    }

}
