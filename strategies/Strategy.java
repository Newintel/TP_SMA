package strategies;

public interface Strategy {
    Double generatePrice(Double limitPrice, Double basePrice, Double objective, int step);
}
