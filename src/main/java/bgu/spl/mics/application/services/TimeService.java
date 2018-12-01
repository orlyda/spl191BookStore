package bgu.spl.mics.application.services;


import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	public TimeService(String name, MessageBus messageBus) {
		super(name, messageBus);
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
