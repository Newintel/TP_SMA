package communication;

import java.util.Date;

public class BuyerPreference extends Constraint {
    public BuyerPreference(Double price, Date maxDate, Double maxPrice) {
        super(price, maxDate);
        this.limitIsMax = true;
        this.limitPrice = maxPrice;
    }
}
