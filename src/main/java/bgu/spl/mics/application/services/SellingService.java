package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

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
@SuppressWarnings("ALL")
public class SellingService extends MicroService{

	private AtomicReference<MoneyRegister> mr;
	public static final long waitingTime = 20;

	private int currentTick;//use to create order receipt

	public SellingService(String name) {
		super(name);
		mr = new AtomicReference<>();
		this.mr.set(MoneyRegister.getInstance());
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, c -> currentTick=c.getTick());
		Callback<BookOrderEvent> orderBookEventCallback= o -> {
			Future<OrderReceipt> fr = new Future<>();
			complete(o,fr);
			OrderReceipt receipt=new OrderReceipt(1,this.getName(),o.getCustomer().getId(),
					o.getBookTitle(),o.getTick(),currentTick);

			CheckAvailabilityEvent event=new CheckAvailabilityEvent(o.getBookTitle());
			Future<Integer> futureStatus= sendEvent(event);//get the book price, if the book out of stock, get -1
			futureStatus=waitUntilDone(futureStatus);
			synchronized (o.getCustomer()) {//check if the customer have enough money
				if (futureStatus.get() != -1 && futureStatus.get() <= o.getCustomer().getAvailableCreditAmount()) {
					TakeBookEvent bookEvent = new TakeBookEvent(o.getBookTitle());
					Future<Boolean> futureBook = sendEvent(bookEvent);
					futureBook = waitUntilDone(futureBook);
					if (futureBook.get()) {
						mr.get().chargeCreditCard(o.getCustomer(), futureStatus.get());
						receipt.setPrice(futureStatus.get());
						receipt.setIssuedTick(currentTick);
						mr.get().file(receipt);
					} }
				fr.resolve(receipt);
			}
		};
		this.subscribeEvent(BookOrderEvent.class,orderBookEventCallback);
		this.subscribeBroadcast(TerminateBroadcast.class, c-> this.terminate());
	}

	private Future waitUntilDone(Future f){
		synchronized (f){
			while (!f.isDone()){
				try {
					f.wait(waitingTime);
				}
				catch (InterruptedException e){}
			}
		}
		return f;
	}

}
