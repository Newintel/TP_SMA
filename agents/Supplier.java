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
    Map<Integer, Message> accepting = new HashMap<>();

    public Supplier(String name) {
        super(name);
    }

    public void broadcast(Service service, SupplierPreference preference, Strategy strategy, int interest) {
        Message message = generate_offer(service, preference, strategy);
        message.counters = interest;
        Map<String, Status> statuses = new HashMap<>();
        counters.put(service.id, new HashMap<>());
        for (Agent agent : acquaintances) {
            statuses.put(agent.name, Status.WAITING);
            counters.get(service.id).put(agent.name, 0);
        }
        status.put(service.id, statuses);
        send(message, acquaintances);
    }

    // Generate offer from received message
    protected Message generate_offer(Message from) {
        Constraint preference = preferences.get(from.service.id);
        Strategy strategy = strategies.get(from.service.id);

        Double newPrice = Math.max(
                strategy.generatePrice(preference.limitPrice, from.constraint.price, preference.price,
                        from.counters),
                accepting.containsKey(from.service.id)
                        ? accepting.get(from.service.id).constraint.price
                        : 0);

        System.out.println("Supplier " + name + " generates offer: " + newPrice + " on id=" + from.service.id + " for "
                + from.from.name);

        return new Message(this, from.from, from.service,
                new SupplierPreference(
                        from.constraint.limitPrice,
                        newPrice,
                        from.constraint.maxDate),
                from.counters);
    }

    // Generate new offer
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

        if (percentage < 0.05 * (1 << interest.getOrDefault(offer.service.id, 0))) {
            System.out.println("Supplier " + name + " can accept offer from " + offer.from.name + " directly");
            return true;
        }

        if (super.evaluate_offer(offer) && r.nextDouble() > percentage) {
            System.out.println("Supplier " + name + " can accept offer from " + offer.from.name);
            return true;
        }

        return false;
    }

    @Override
    public void accept(Message message) {
        HashMap<String, Status> statuses = (HashMap<String, Status>) status.get(message.service.id);
        if (statuses == null) {
            statuses = new HashMap<>();
            status.put(message.service.id, statuses);
        }
        if (statuses.keySet().size() > 1) {
            if (accepting.containsKey(message.service.id) == false
                    || accepting.get(message.service.id).constraint.price < message.constraint.price) {
                accepting.put(message.service.id, message);
            }
            return;
        }
        definitelyAccept(message);
    }

    public void definitelyAccept(Message message) {
        HashMap<String, Status> statuses = (HashMap<String, Status>) status.get(message.service.id);
        for (Entry<String, Status> entry : statuses.entrySet()) {
            entry.setValue(
                    entry.getKey().equals(message.from.name)
                            ? Status.ACCEPTED
                            : Status.REFUSED);
        }
        this.send(message.accepted(), message.from);
    }

    @Override
    public void refuse(Message message) {
        status.get(message.service.id).put(message.from.name, Status.REFUSED);
        super.refuse(message);
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
        HashMap<String, Status> statuses = (HashMap<String, Status>) status.getOrDefault(message.service.id,
                new HashMap<>());

        if (accepting.containsKey(message.service.id)
                && statuses.values().stream().anyMatch(s -> s == Status.ACCEPTED) == false) {
            Map<String, Integer> messageCounters = counters.get(message.service.id);
            Message m_accepting = accepting.get(message.service.id);

            if (messageCounters != null && messageCounters.entrySet().stream()
                    .anyMatch(e -> e.getValue() < m_accepting.counters) == false) {
                Message m = accepting.remove(message.service.id);
                if (m_accepting.constraint.price > message.constraint.price) {
                    System.out.println("Supplier " + name + " prefers accepting from " + m_accepting.from.name);
                    definitelyAccept(m);
                    send(m.accepted(), m.from);
                    return true;
                }
                System.out.println("Supplier " + name + " prefers accepting from " + message.from.name);
                definitelyAccept(message);
                send(message.accepted(), message.from);
                return true;
            }
        }

        if (status.containsKey(message.service.id)) {
            if (statuses.get(message.from.name) != Status.WAITING) {
                System.out.println(
                        "Supplier " + name + " ignores message from " + message.from.name + " because of status "
                                + statuses.get(message.from.name));
                return true;
            }
        }
        if (message.status != Status.WAITING) {
            if (message.status == Status.ACCEPTED) {
                accept(message);
            } else {
                refuse(message);
            }
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
