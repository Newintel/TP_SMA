package strategies;

public class Dichotomy implements Strategy {

    @Override
    public Double generatePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        return (objective + basePrice) / 2;
    }

}
