import agents.Buyer;
import agents.Supplier;
import service.PlaneTicket;
import strategies.Dichotomy;
import strategies.Goal;
import strategies.PercentageStep;
import communication.BuyerPreference;
import communication.SupplierPreference;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Buyer b1 = new Buyer("b1", new PercentageStep(0.20).withGoal(Goal.LOWER));
        Buyer b2 = new Buyer("b2", new Dichotomy().withGoal(Goal.LOWER));
        Buyer b3 = new Buyer("b3", new Dichotomy().withGoal(Goal.LOWER));
        Supplier s = new Supplier("s1");
        Supplier s2 = new Supplier("s2");

        Date ticketDate = new GregorianCalendar(2015, 11, 15).getTime();
        PlaneTicket ticket = new PlaneTicket(1, ticketDate, "MPA", "CDG", "Air France", 200d);
        PlaneTicket ticket2 = new PlaneTicket(2, ticketDate, "MPA", "CDG", "Air France", 200d);

        Date maxDate = new GregorianCalendar(2016, 2, 15).getTime();
        SupplierPreference c = new SupplierPreference(200d, 300d, maxDate);
        SupplierPreference d = new SupplierPreference(200d, 300d, maxDate);

        b1.addPreference(ticket2, new BuyerPreference(100d, new GregorianCalendar(2015, 11, 25).getTime(), 280d), 2);
        b1.addPreference(ticket, new BuyerPreference(100d, new GregorianCalendar(2015, 11, 25).getTime(), 280d), 1);
        b2.addPreference(ticket2, new BuyerPreference(200d, new GregorianCalendar(2015, 11, 25).getTime(), 280d), 0);
        b2.addPreference(ticket, new BuyerPreference(200d, new GregorianCalendar(2015, 11, 25).getTime(), 280d), 3);
        b3.addPreference(ticket2, new BuyerPreference(230d, new GregorianCalendar(2015, 11, 25).getTime(), 280d), 1);
        b3.addPreference(ticket, new BuyerPreference(230d, new GregorianCalendar(2015, 11, 25).getTime(), 280d), 4);

        List<Buyer> buyers = new ArrayList<>();
        buyers.add(b1);
        buyers.add(b2);
        buyers.add(b3);

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(s);
        suppliers.add(s2);

        for (Buyer bu : buyers) {
            bu.subscribe(suppliers);
            bu.start();
        }

        for (Supplier su : suppliers) {
            su.subscribe(buyers);
            su.start();
        }

        s.broadcast(ticket, c, new PercentageStep(-0.1), 1);
        s2.broadcast(ticket2, d, new PercentageStep(-0.2), 2);
    }
}
