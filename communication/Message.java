package communication;

import agents.Agent;
import service.Service;

public class Message {
    public Agent from;
    public Agent to;
    public Service service;
    public Constraint constraint;
    public int counters = 0;

    public Message(Agent from, Agent to, Service service, Constraint constraint, int counters) {
        this.from = from;
        this.to = to;
        this.service = service;
        this.constraint = constraint;
        this.counters = counters;
    }
    public Message(Agent from, Agent to, Service service, Constraint constraint) {
        this.from = from;
        this.to = to;
        this.service = service;
        this.constraint = constraint;
    }

    public Message(Agent from, Service service, Constraint constraint) {
        this.from = from;
        this.service = service;
        this.constraint = constraint;
    }

    public Message(Message message) {
        this.from = message.from;
        this.service = message.service;
        this.constraint = message.constraint;
    }
}
