package communication;

import java.util.Date;

public abstract class Constraint {
    public Double limitPrice = -1d;
    public Double price;
    public Date maxDate;
    protected boolean limitIsMax;

    public Constraint(Double price, Date maxDate) {
        this.price = price;
        this.maxDate = maxDate;
    }

    public boolean isSatisfiedBy(Constraint constraint) {
        return this.maxDate.after(constraint.maxDate) && (this.limitPrice >= constraint.price) == limitIsMax;
    }
}
