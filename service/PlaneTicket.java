package service;

import java.util.Date;

public class PlaneTicket extends Service {
    Date date;
    String departure;
    String arrival;
    String company;
    Double price;

    public PlaneTicket(int id, Date date, String departure, String arrival, String company, Double price) {
        this.id = id;
        this.date = date;
        this.departure = departure;
        this.arrival = arrival;
        this.company = company;
        this.price = price;
    }

}
