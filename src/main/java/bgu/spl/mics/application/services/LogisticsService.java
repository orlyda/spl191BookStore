package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.GetCarEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	public static final long waitingTime = 50;
	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize(){
		Callback<DeliveryEvent> DeliveryCallback = c -> {
			Future<DeliveryVehicle> f = sendEvent(new GetCarEvent());
			while (!f.isDone()) {
				try {
					Thread.sleep(waitingTime);
				} catch (InterruptedException e) { e.printStackTrace();}
			}
			f.get().deliver(c.getAddress(),c.getSpeed());
			System.out.println("Delivered");
		};
		this.subscribeEvent(DeliveryEvent.class,DeliveryCallback);
		this.subscribeBroadcast(TerminateBroadcast.class, c->this.terminate());
	}
}
