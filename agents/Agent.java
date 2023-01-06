package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import communication.Constraint;
import communication.Message;
import service.Service;

public abstract class Agent {
    static int MAX_COUNTERS = 8;
    Map<Integer, Constraint> preferences = new HashMap<>();
    List<Agent> acquaintances = new ArrayList<>();
    public List<Message> messages = new ArrayList<>();
    List<Message> read = new ArrayList<>();
    List<Service> accepted = new ArrayList<>();
    List<Service> refused = new ArrayList<>();
    String name;

    Random r = new Random();

    public Agent(String _name) {
        name = _name;
    }

    public void accept(Message message) {
        this.accepted.add(message.service);
        message.from.accepted.add(message.service);
    }

    public void refuse(Message message) {
        this.refused.add(message.service);
        message.from.refused.add(message.service);
    }

    public void send(Message message, Agent to) {
        message.to = to;
        message.to.messages.add(message);
    }

    public void send(Message message, List<Agent> to) {
        for (Agent agent : to) {
            Message m = new Message(message);
            this.send(m, agent);
        }
    }

    public void receiveAndAct() {
        if (this.messages.size() == 0) {
            return;
        }

        Message message = this.messages.remove(0);

        read.add(message);

        if (this.evaluate_offer(message) == false) {
            if (message.counters < MAX_COUNTERS) {
                message.counters++;
                this.send(this.generate_offer(message), message.from);
                // message.from.receiveAndAct();
            } else {
                System.out.println("Refused");
                this.refuse(message);
            }
        } else {
            System.out.println("Accepted");
            this.accept(message);
        }
    }

    public void addAcquaintance(Agent acquaintance) {
        if (this.acquaintances.contains(acquaintance)) {
            return;
        }

        this.acquaintances.add(acquaintance);
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