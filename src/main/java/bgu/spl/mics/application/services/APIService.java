package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.FiftyPercentDiscount;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.FutureOrder;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	private final Customer customer;
	private ArrayList<FutureOrder> futureOrders;

	public APIService( ArrayList<FutureOrder> orders, Customer c){
		super("APIService");
		customer=c;
		futureOrders=orders;
		Comparator<FutureOrder> comparator= (futureOrder, t1) ->
				Integer.compare(t1.getTick(), futureOrder.getTick());
		futureOrders.sort(comparator);
	}

	@Override
	protected void initialize() {
		Callback<TickBroadcast> tickCallback= t -> {
			int i=0;
			//synchronized (futureOrders){
			while(!futureOrders.isEmpty() && futureOrders.get(i).getTick()==t.getTick()) {
				Future<OrderReceipt> orderFuture =
						this.sendEvent(new OrderBookEvent(customer, futureOrders.get(i).getBookTitle()));
				futureOrders.remove(i);
				i++;
			}//}
		};
		this.subscribeBroadcast(TickBroadcast.class,tickCallback);
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<FutureOrder> getFutureOrders() {
		return futureOrders;
	}

}
