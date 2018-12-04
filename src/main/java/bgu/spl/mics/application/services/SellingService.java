package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.MoneyStatus;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private AtomicReference<MoneyRegister> mr;
	private int currentTick;//use to create order receipt

	public SellingService(String name) {
		super(name);
		this.mr.set(MoneyRegister.getInstance());
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, c -> {
			currentTick=c.getTick();
		});

		Callback<OrderBookEvent> orderBookEventCallback= o -> {
			OrderReceipt receipt=new OrderReceipt(1,this.getName(),o.getCustomer().getId(),
					o.getBookTitle(),o.getTick(),currentTick);

			CheckAvailabilityEvent event=new CheckAvailabilityEvent(o.getBookTitle());
			Future<MoneyStatus> futureStatus= sendEvent(event);
			while (!futureStatus.isDone()){
				try {
					wait();
				}
				catch (InterruptedException e){}
			}
			if(futureStatus.get().isEnoughMoney()){
				mr.get().chargeCreditCard(o.getCustomer(),futureStatus.get().getBookPrice());
				receipt.setPrice(futureStatus.get().getBookPrice());
				receipt.setIssuedTick(currentTick);
				mr.get().file(receipt);
				complete(o,receipt);
			}
			else {
				receipt.setPrice(-1);
				receipt.setIssuedTick(currentTick);
				complete(o, receipt);
			}
		};
		this.subscribeEvent(OrderBookEvent.class,orderBookEventCallback);
	}

}
