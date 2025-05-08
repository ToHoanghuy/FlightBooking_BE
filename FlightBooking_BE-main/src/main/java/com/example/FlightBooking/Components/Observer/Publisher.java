package com.example.FlightBooking.Components.Observer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Publisher {
    private List<Subscriber> subscribers = new ArrayList<>();
    private String mainState;
    private String eventType;

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void notifySubscribers() {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(mainState, eventType);
        }
    }

    public void setMainState(String mainState, String eventType) {
        this.mainState = mainState;
        this.eventType = eventType;
        notifySubscribers();
    }
}
