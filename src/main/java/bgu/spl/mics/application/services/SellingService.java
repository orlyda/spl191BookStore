package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.FiftyPercentDiscount;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.Callable;
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

	public SellingService(String name, AtomicReference<MoneyRegister> mr) {
		super(name);
		this.mr = mr;
	}

	@Override
	protected void initialize() {
		Callback<FiftyPercentDiscount> discountCallback=new Callback<FiftyPercentDiscount>() {
			@Override
			public void call(FiftyPercentDiscount c) {

			}
		};// TODO
		Callback<OrderBookEvent> orderBookEventCallback= o -> {
			CheckAvailabilityEvent event=new CheckAvailabilityEvent(o.getBookTitle());
			Future<OrderReceipt> orderReceiptFuture= sendEvent(event);
			while (!orderReceiptFuture.isDone()){
				try {
					wait();
				}
				catch (InterruptedException e){}
			}
		};
		this.subscribeEvent(OrderBookEvent.class,orderBookEventCallback);
	}

}
