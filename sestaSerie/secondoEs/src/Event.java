final class Event {
    private final long num;

    public Event(final long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Event: " + num;
    }
}
