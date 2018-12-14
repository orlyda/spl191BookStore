package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyStatus;

import java.util.concurrent.atomic.AtomicReference;
/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

@SuppressWarnings("ALL")
public class InventoryService extends MicroService{
	public AtomicReference<Inventory> inventory;


	public InventoryService(String name) {
		super("Inventory"+name);
		inventory= new AtomicReference<>();
		inventory.set(Inventory.getInstance());
	}

	@Override
	protected void initialize() {
		Callback<CheckAvailabilityEvent> checkCallback= c -> {
			int price=inventory.get().checkAvailabiltyAndGetPrice(c.getBookTitle()); //the book price
			if(price!=-1){//the book is available
				if (c.getAvailableMoney()>=price) {
					//the book available and there is enough money
					complete(c, new MoneyStatus(price,true));
					inventory.get().take(c.getBookTitle());
				}
				else {
					complete(c, new MoneyStatus(price, false));
				}
			}
			else{//the book is not in stock
				complete(c, new MoneyStatus(price,false));
			}
		};
		this.subscribeEvent(CheckAvailabilityEvent.class,checkCallback);
		this.subscribeBroadcast(TerminateBroadcast.class, c-> this.terminate());
	}
}
