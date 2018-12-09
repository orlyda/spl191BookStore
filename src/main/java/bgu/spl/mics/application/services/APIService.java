package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CheckEnoughMoneyEvent;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private final Customer customer;
	private ArrayList<FutureOrder> futureOrders;
	private List<Future<OrderReceipt>> futureList;

	public APIService( ArrayList<FutureOrder> orders, Customer c,String name){
		super("APIService"+name);
		customer=c;
		futureOrders=orders;
		Comparator<FutureOrder> comparator= (futureOrder, t1) ->
				Integer.compare(t1.getTick(), futureOrder.getTick());
		futureOrders.sort(comparator);
		futureList= new ArrayList<>();
	}

	@Override
	protected void initialize() {
		Callback<TickBroadcast> tickCallback= t -> {
			ExecutorService e=Executors.newFixedThreadPool(futureOrders.size());
			CompletionService<OrderReceipt> cService=new ExecutorCompletionService<>(e);
			AtomicInteger i= new AtomicInteger();
			synchronized (futureOrders){//take order from the list in the correct tick
				// and send event that handle the order
			while(!futureOrders.isEmpty() && futureOrders.get(i.get()).getTick()==t.getTick()) {
				Future<OrderReceipt> orderFuture =
						this.sendEvent(new OrderBookEvent(customer, futureOrders.get(i.get()).getBookTitle(),
								futureOrders.get(i.get()).getTick()));
				futureOrders.remove(i.get());
			}}//now all the order were sent,need to wait until each of them will be completed,
			// and then add the OrderReceipt to customers list of OrderReceipts

		};
		this.subscribeBroadcast(TickBroadcast.class,tickCallback);
		this.subscribeEvent(CheckEnoughMoneyEvent.class, c -> {
			MoneyStatus status;
			if (customer.getAvailableCreditAmount()>=c.getPrice()) {
				status=new MoneyStatus(c.getPrice(),true);
			}
			else
				status=new MoneyStatus(c.getPrice(),false);
			complete(c,status);
		});
	}


	public Customer getCustomer() {
		return customer;
	}

	public List<FutureOrder> getFutureOrders() {
		return futureOrders;
	}

}
