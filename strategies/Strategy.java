package strategies;

public abstract class Strategy {
    public Goal goal;

    public Strategy withGoal(Goal goal) {
        this.goal = goal;
        return this;
    }

    public abstract Double internalGeneratePrice(Double limitPrice, Double basePrice, Double objective, int step);

    public Double generatePrice(Double limitPrice, Double basePrice, Double objective, int step) {
        if (goal == Goal.LOWER && basePrice < objective) {
            return basePrice;
        } else if (goal == Goal.HIGHER && basePrice > objective) {
            return basePrice;
        } else {
            return internalGeneratePrice(limitPrice, basePrice, objective, step);
        }
    }
}
