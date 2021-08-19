package dk.ksf.cqrs.events.model;

public enum AggregateTypes {
    klient("klient"),
    matrikel("matrikel");

    private final String name;

    AggregateTypes(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
