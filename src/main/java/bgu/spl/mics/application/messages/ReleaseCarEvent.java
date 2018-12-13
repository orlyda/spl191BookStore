package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseCarEvent implements Event<DeliveryVehicle> {
    private DeliveryVehicle deliveryVehicle;
    public ReleaseCarEvent(DeliveryVehicle deliveryVehicle){this.deliveryVehicle=deliveryVehicle;}

    public DeliveryVehicle getDeliveryVehicle() {
        return deliveryVehicle;
    }
}
