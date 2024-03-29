package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.GetCarEvent;
import bgu.spl.mics.application.messages.ReleaseCarEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private static int waitingTime =20;
	private ResourcesHolder resourcesHolderRef;
	public ResourceService(String name){
		super(name);
		resourcesHolderRef = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
        Callback<GetCarEvent> getCarEventCallback =c->  {
          Future<DeliveryVehicle> f = resourcesHolderRef.acquireVehicle();
          this.complete(c,f);
        };
        Callback<ReleaseCarEvent> releaseCarEventCallback = c->{
        	Future<DeliveryVehicle> f = c.getDeliveryVehicle();
        	while(!f.isDone())
        		try{Thread.sleep(waitingTime);} catch (InterruptedException e) {
					e.printStackTrace();
				}
			resourcesHolderRef.releaseVehicle(f.get());
        complete(c,null);};
		this.subscribeEvent(GetCarEvent.class,getCarEventCallback);
        this.subscribeEvent(ReleaseCarEvent.class,releaseCarEventCallback);
		this.subscribeBroadcast(TerminateBroadcast.class, c->this.terminate());
	}

}
