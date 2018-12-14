package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.GetCarEvent;
import bgu.spl.mics.application.messages.ReleaseCarEvent;
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
	public static final long waitingTime = 20;
	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize(){
		Callback<DeliveryEvent> DeliveryCallback = c -> {
			Future<Future<DeliveryVehicle>> fd = sendEvent(new GetCarEvent());
			Future<DeliveryVehicle>f=fd.get();
			complete(c,null);
			while (!f.isDone()) {
				try {
					Thread.sleep(waitingTime);
					System.out.println("hello3");
				} catch (InterruptedException e) { e.printStackTrace();}
			}
			System.out.println(this.getName()+"Her");
			Future<DeliveryVehicle> RelVehicle = new Future();
			sendEvent(new ReleaseCarEvent(RelVehicle));
			f.get().deliver(c.getAddress(),c.getSpeed());
			System.out.println("Delivered");
			RelVehicle.resolve(f.get());
			System.out.println("Released");
		};
		this.subscribeEvent(DeliveryEvent.class,DeliveryCallback);
		this.subscribeBroadcast(TerminateBroadcast.class, c->this.terminate());
	}
}
