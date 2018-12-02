package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.HashMap;
import java.util.concurrent.atomic.*;
/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	public AtomicReference<Inventory> inventory;


	public InventoryService(Inventory i, MessageBus bus) {
		super("Inventory",bus);
		inventory.getAndSet(i);
	}

	@Override
	protected void initialize() {
		Callback<CheckAvailabilityEvent> checkCallback= c -> {
		//	inventory.get().take()
		};
	}//need to creat another event which check that the costumer have money,and then to take and complete.

}
