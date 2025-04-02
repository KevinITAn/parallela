class EventListener {
    private volatile int id;

    public EventListener(final int id, final EventSource eventSource) {
        this.id = id;
        // Add listener to the eventSource to get event notifications
        eventSource.registerListener(id, this);
    }

    public void onEvent(final int listenerID, final Event e) {
        // Check that the listener's ID called from the eventSource matches the listener's instance's is
        if (listenerID != id) {
            System.out.println("Inconsistent listener ID" + listenerID + " : " + e);
        }
    }
}
