package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseCarEvent implements Event<Future<DeliveryVehicle>> {
    private Future<DeliveryVehicle> deliveryVehicle;
    public ReleaseCarEvent(Future<DeliveryVehicle> deliveryVehicle){this.deliveryVehicle=deliveryVehicle;}

    public Future<DeliveryVehicle> getDeliveryVehicle() {
        return deliveryVehicle;
    }
}
