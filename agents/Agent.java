package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import communication.Constraint;
import communication.Message;

public abstract class Agent {
    static int MAX_COUNTERS = 8;
    Map<Integer, Constraint> preferences = new HashMap<>();
    List<Agent> acquaintances = new ArrayList<>();
    public List<Message> messages = new ArrayList<>();
    List<Message> read = new ArrayList<>();
    String name;
    Map<Integer, Map<String, Integer>> counters = new HashMap<>();
    Map<Integer, Integer> interest = new HashMap<>();

    Random r = new Random();

    public Agent(String _name) {
        name = _name;
    }

    public void accept(Message message) {
        this.send(message.accepted(), message.from);
    }

    public void refuse(Message message) {
        this.send(message.refused(), message.from);
    }

    public void send(Message message, Agent to) {
        message.to = to;
        message.from = this;
        to.messages.add(message);
    }

    public void send(Message message, List<Agent> to) {
        for (Agent agent : to) {
            Message m = new Message(message);
            this.send(m, agent);
        }
    }

    public abstract boolean internalReceiveAndAct(Message message);

    public void receiveAndAct() {
        if (this.messages.size() == 0) {
            return;
        }

        Message message = this.messages.remove(0);

        read.add(message);

        if (counters.containsKey(message.service.id) == false) {
            counters.put(message.service.id, new HashMap<>());
        }
        counters.get(message.service.id).put(message.from.name, message.counters);

        if (this.internalReceiveAndAct(message)) {
            return;
        }

        if (this.evaluate_offer(message) == false) {
            if (message.counters < MAX_COUNTERS) {
                message.counters++;
                Message m = this.generate_offer(message);
                if (m != null) {
                    this.send(m, message.from);
                }
            } else {
                this.refuse(message);
            }
        } else {
            this.accept(message);
        }
    }

    public void addAcquaintance(Agent acquaintance) {
        if (this.acquaintances.contains(acquaintance)) {
            return;
        }

        this.acquaintances.add(acquaintance);
        acquaintance.acquaintances.add(this);
    }

    protected abstract Message generate_offer(Message from);

    public boolean evaluate_offer(Message offer) {
        return this.preferences.get(offer.service.id).isSatisfiedBy(offer.constraint);
    }

    public Thread start() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    receiveAndAct();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();

        return thread;
    }
}