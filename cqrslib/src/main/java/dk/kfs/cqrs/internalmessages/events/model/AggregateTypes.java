package dk.kfs.cqrs.internalmessages.events.model;

public enum AggregateTypes {
    klient("klient"),
    matrikel("matrikel"),
    celle("celle");

    private final String name;

    AggregateTypes(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
