package agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import communication.Constraint;
import communication.Message;
import communication.SupplierPreference;
import service.Service;
import strategies.Strategy;

public class Supplier extends Agent {
    Map<Integer, Strategy> strategies = new HashMap<>();

    public Supplier(String name) {
        super(name);
    }

    protected Message generate_offer(Message from) {
        Constraint preference = preferences.get(from.service.id);
        Strategy strategy = strategies.get(from.service.id);

        Double newPrice = strategy.generatePrice(preference.limitPrice, from.constraint.price, preference.price,
                from.counters);

        System.out.println("Supplier " + name + " generates offer: " + newPrice);

        return new Message(this, from.from, from.service,
                new SupplierPreference(
                        from.constraint.limitPrice,
                        newPrice,
                        from.constraint.maxDate),
                from.counters);
    }

    public Message generate_offer(Service service, Constraint preferences, Strategy strategy) {
        this.preferences.put(service.id, preferences);
        this.strategies.put(service.id, strategy);
        return new Message(this, service, preferences);
    }

    @Override
    public boolean evaluate_offer(Message offer) {
        Double baseOfferPrice = preferences.get(offer.service.id).price;
        Double priceDifference = Math.abs(offer.constraint.price - baseOfferPrice);
        Double percentage = priceDifference / baseOfferPrice;
        System.out.println("Supplier " + name + " evaluates offer: " + percentage);

        if (percentage < 0.05) {
            System.out.println("Supplier " + name + " accepts offer directly");
            return true;
        }

        if (super.evaluate_offer(offer) && r.nextDouble() > percentage) {
            System.out.println("Supplier " + name + " accepts offer");
            return true;
        }

        return false;
    }

    // @Override
    // public void receiveAndAct() {
    // System.out.println("Supplier " + name + " receives and acts, " +
    // this.messages.size() + " messages");
    // super.receiveAndAct();
    // }
}