package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class DeliveryEvent implements Event<DeliveryVehicle> {
    private String Address;
    private int speed;
    public DeliveryEvent(String Address,int speed) {
        this.Address=Address;
        this.speed= speed;
    }

    public int getSpeed() {
        return speed;
    }

    public String getAddress() {
        return Address;
    }
}
