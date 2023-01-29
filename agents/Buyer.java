package agents;

import java.util.ArrayList;
import java.util.List;

import communication.BuyerPreference;
import communication.Constraint;
import communication.Message;
import communication.Status;
import service.Service;
import strategies.Strategy;

public class Buyer extends Agent {
    Strategy strategy;
    List<Service> services = new ArrayList<>();

    public Buyer(String name, Strategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    protected Message generate_offer(Message from) {
        Constraint preference = this.preferences.get(from.service.id);
        Double generated = this.strategy.generatePrice(preference.limitPrice, from.constraint.price, preference.price,
                from.counters);
        preference.price = Math.min(
                generated,
                preference.limitPrice);
        System.out.println("Buyer " + name + " generates offer: " + preference.price + " for id=" + from.service.id);
        return new Message(this, from.from, from.service, preference, from.counters);
    }

    @Override
    public boolean evaluate_offer(Message offer) {
        Constraint preference = this.preferences.get(offer.service.id);

        double objective = 1 / 2;

        Double baseOfferPrice = preferences.get(offer.service.id).price;
        Double signedPriceDifference = offer.constraint.price - baseOfferPrice;
        Double priceDifference = Math.abs(signedPriceDifference);

        Double percentage = signedPriceDifference > 0 ? priceDifference / baseOfferPrice
                : priceDifference / (preference.limitPrice * 20 * objective);

        if (percentage < 0.05) {
            System.out.println("Buyer " + name + " accepts offer from " + offer.from.name + " directly");
            return true;
        }

        if (super.evaluate_offer(offer) && r.nextDouble() > percentage) {
            System.out.println("Buyer " + name + " accepts offer from " + offer.from.name);
            return true;
        }

        return false;
    }

    public void addPreference(Service s, BuyerPreference preference) {
        this.preferences.put(s.id, preference);
    }

    public void subscribe(Supplier s) {
        addAcquaintance(s);
    }

    public void subscribe(List<Supplier> suppliers) {
        for (Supplier s : suppliers) {
            subscribe(s);
        }
    }

    @Override
    public void accept(Message message) {
    }

    @Override
    public void refuse(Message message) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean internalReceiveAndAct(Message message) {
        if (message.status == Status.ACCEPTED) {
            services.add(message.service);
            System.out.println("Supplier " + message.from.name + " sold id=" + message.service.id + " to " + name);
            return true;
        }

        if (message.status == Status.REFUSED) {
            return true;
        }
        return false;
    }

    // @Override
    // public void receiveAndAct() {
    // System.out.println("Buyer " + name + " receives and acts, " +
    // this.messages.size() + " messages");
    // super.receiveAndAct();
    // }
}
