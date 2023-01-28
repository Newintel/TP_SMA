package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import communication.Constraint;
import communication.Message;
import communication.Status;
import communication.SupplierPreference;
import service.Service;
import strategies.Goal;
import strategies.Strategy;

public class Supplier extends Agent {
    Map<Integer, Strategy> strategies = new HashMap<>();
    Map<Integer, Map<String, Status>> status = new HashMap<>();
    List<Service> services = new ArrayList<>();

    public Supplier(String name) {
        super(name);
    }

    public void broadcast(Service service, SupplierPreference preference, Strategy strategy) {
        Message message = generate_offer(service, preference, strategy);
        Map<String, Status> statuses = new HashMap<>();
        for (Agent agent : acquaintances) {
            statuses.put(agent.name, Status.WAITING);
        }
        status.put(service.id, statuses);
        send(message, acquaintances);
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
        this.strategies.put(service.id, strategy.withGoal(Goal.HIGHER));
        this.services.add(service);
        return new Message(this, service, preferences);
    }

    @Override
    public boolean evaluate_offer(Message offer) {
        Double baseOfferPrice = preferences.get(offer.service.id).price;
        Double priceDifference = Math.abs(offer.constraint.price - baseOfferPrice);
        Double percentage = priceDifference / baseOfferPrice;

        if (percentage < 0.05) {
            System.out.println("Supplier " + name + " accepts offer from " + offer.from.name + " directly");
            return true;
        }

        if (super.evaluate_offer(offer) && r.nextDouble() > percentage) {
            System.out.println("Supplier " + name + " accepts offer from " + offer.from.name);
            return true;
        }

        return false;
    }

    @Override
    public void accept(Message message) {
        HashMap<String, Status> statuses = (HashMap<String, Status>) status.get(message.service.id);
        for (Entry<String, Status> entry : statuses.entrySet()) {
            entry.setValue(
                    entry.getKey().equals(message.from.name)
                            ? Status.ACCEPTED
                            : Status.REFUSED);
        }
    }

    @Override
    public void refuse(Message message) {
        status.get(message.service.id).put(message.from.name, Status.REFUSED);
    }

    @Override
    public void send(Message message, Agent to) {
        super.send(message, to);
        HashMap<String, Status> statuses = (HashMap<String, Status>) status.get(message.service.id);
        if (statuses == null) {
            statuses = new HashMap<>();
            status.put(message.service.id, statuses);
        }
        Status serviceStatus = statuses.get(to.name);
        if (serviceStatus == null) {
            statuses.put(to.name, Status.WAITING);
        }
    }

    @Override
    public boolean internalReceiveAndAct(Message message) {
        if (status.containsKey(message.service.id)) {
            HashMap<String, Status> statuses = (HashMap<String, Status>) status.get(message.service.id);
            if (statuses.get(message.from.name) != Status.WAITING) {
                System.out.println(
                        "Supplier " + name + " ignores message from " + message.from.name + " because of status "
                                + statuses.get(message.from.name));
                return true;
            }
        }
        if (message.status != Status.WAITING) {
            return true;
        }
        return false;
    }

    public void subscribe(List<Buyer> agents) {
        for (Buyer agent : agents) {
            addAcquaintance(agent);
        }
    }

    // @Override
    // public void receiveAndAct() {
    // System.out.println("Supplier " + name + " receives and acts, " +
    // this.messages.size() + " messages");
    // super.receiveAndAct();
    // }
}
