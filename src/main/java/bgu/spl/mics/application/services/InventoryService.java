package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.CheckEnoughMoneyEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyStatus;
import bgu.spl.mics.application.passiveObjects.OrderResult;

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

@SuppressWarnings("ALL")
public class InventoryService extends MicroService{
	public AtomicReference<Inventory> inventory;


	public InventoryService(String name) {
		super("Inventory"+name);
		inventory.set(Inventory.getInstance());
	}

	@Override
	protected void initialize() {
		Callback<CheckAvailabilityEvent> checkCallback= c -> {
			int price=inventory.get().checkAvailabiltyAndGetPrice(c.getBookTitle()); //the book price
			if(price!=-1){//the book is available
				CheckEnoughMoneyEvent moneyEvent=new CheckEnoughMoneyEvent(price);
				Future<MoneyStatus> moneyStatusFuture=sendEvent(moneyEvent);
				synchronized (moneyStatusFuture) {
					while (!moneyStatusFuture.isDone()) {
						try {
							moneyStatusFuture.wait();
						} catch (InterruptedException e) {}
					}
				}
				if (moneyStatusFuture.get().isEnoughMoney())
					//the book available and there is enough money
					complete(c,moneyStatusFuture.get());
				else complete(c,new MoneyStatus(price,false));

			}
			else{//the book is not in stock
				complete(c, new MoneyStatus(price,false));
			}
		};
		this.subscribeEvent(CheckAvailabilityEvent.class,checkCallback);
	}
}
