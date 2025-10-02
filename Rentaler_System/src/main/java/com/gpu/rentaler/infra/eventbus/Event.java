package com.gpu.rentaler.infra.eventbus;

class Event {
    private final String eventName;
    private final Object source;
    private final Object data;
    private final long timestamp;

    public Event(String eventName, Object source, Object data) {
        this.eventName = eventName;
        this.source = source;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getEventName() { return eventName; }
    public Object getSource() { return source; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }
}
