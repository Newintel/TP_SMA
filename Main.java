import agents.Buyer;
import agents.Supplier;
import service.PlaneTicket;
import strategies.Dichotomy;
import strategies.PercentageStep;
import strategies.Step;
import communication.BuyerPreference;
import communication.SupplierPreference;

import java.util.Date;
import java.util.GregorianCalendar;

public class Main {
    public static void main(String[] args) {
        Buyer a = new Buyer("b1", new PercentageStep(0.1));
        Buyer b = new Buyer("b2", new Dichotomy());
        Supplier s = new Supplier("s1");

        Date ticketDate = new GregorianCalendar(2015, 11, 15).getTime();
        PlaneTicket ticket = new PlaneTicket(1, ticketDate, "MPA", "CDG", "Air France", 200d);
        PlaneTicket ticket2 = new PlaneTicket(1, ticketDate, "MPA", "CDG", "Air France", 200d);

        Date maxDate = new GregorianCalendar(2016, 2, 15).getTime();
        SupplierPreference c = new SupplierPreference(200d, 300d, maxDate);
        SupplierPreference d = new SupplierPreference(200d, 300d, maxDate);
        a.addPreference(ticket, new BuyerPreference(100d, new GregorianCalendar(2015, 11, 25).getTime(), 280d));
        b.addPreference(ticket2, new BuyerPreference(100d, new GregorianCalendar(2015, 11, 25).getTime(), 280d));

        a.start();
        b.start();
        s.start();
        s.send(s.generate_offer(ticket, c, new PercentageStep(-0.1)), a);
        s.send(s.generate_offer(ticket2, d, new PercentageStep(-0.1)), b);
    }
}
