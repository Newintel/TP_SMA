package agents;

import communication.BuyerPreference;
import communication.Constraint;
import communication.Message;
import service.Service;
import strategies.Strategy;

public class Buyer extends Agent {
    Strategy strategy;

    public Buyer(String name, Strategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    protected Message generate_offer(Message from) {
        Constraint preference = this.preferences.get(from.service.id);
        preference.price = Math.min(
                this.strategy.generatePrice(preference.limitPrice, from.constraint.price, preference.price,
                        from.counters),
                preference.limitPrice);
        System.out.println("Buyer " + name + " generates offer: " + preference.price);
        return new Message(this, from.from, from.service, preference, from.counters);
    }

    @Override
    public boolean evaluate_offer(Message offer) {
        Double baseOfferPrice = preferences.get(offer.service.id).price;
        Double priceDifference = Math.abs(offer.constraint.price - baseOfferPrice);
        Double percentage = priceDifference / baseOfferPrice;
        System.out.println("Buyer " + name + " evaluates offer: " + percentage);

        if (percentage < 0.05) {
            System.out.println("Buyer " + name + " accepts offer directly");
            return true;
        }

        if (super.evaluate_offer(offer) && r.nextDouble() > percentage) {
            System.out.println("Buyer " + name + " accepts offer");
            return true;
        }

        return false;
    }

    public void addPreference(Service s, BuyerPreference preference) {
        this.preferences.put(s.id, preference);
    }

    // @Override
    // public void receiveAndAct() {
    // System.out.println("Buyer " + name + " receives and acts, " +
    // this.messages.size() + " messages");
    // super.receiveAndAct();
    // }
}
