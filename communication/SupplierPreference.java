package communication;

import java.util.Date;

public class SupplierPreference extends Constraint {
    public SupplierPreference(Double minPrice, Double price, Date maxDate) {
        super(price, maxDate);
        this.limitPrice = minPrice;
        this.limitIsMax = false;
    }
}
