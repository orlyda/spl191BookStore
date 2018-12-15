package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Pair;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

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
	private static final long waitingTime = 20;
	private final Customer customer;
	private ArrayList<FutureOrder> futureOrders;
	//private List<Future<OrderReceipt>> futureList;

	public APIService( ArrayList<FutureOrder> orders, Customer c,String name){
		super("APIService"+name);
		customer=c;
		futureOrders=orders;
		Comparator<FutureOrder> comparator= (futureOrder, t1) ->{
			if(futureOrder.getTick()<t1.getTick())
				return -1;
			if(futureOrder.getTick()>t1.getTick())
				return 1;
			return 0;
		};
		futureOrders.sort(comparator);
		//futureList= new ArrayList<>();
	}

	@Override
	protected void initialize(){
		Callback<TickBroadcast> tickCallback= t -> {
			if(futureOrders.size()>0){//check there are more orders to send
				Pair<Integer,CompletionService<OrderReceipt>> p=tickManager(t.getTick());
				int j=p.getFirst();
				CompletionService<OrderReceipt> cService=p.getSecond();
				while (j>0){//there are more orders that is not completed
					try {//wait for all the orders to be completed, when order is complete, send the delivery
						OrderReceipt orderReceipt=cService.take().get();
						if(orderReceipt.getPrice()!=-1){//the book was in stock and the customer had enough money
							customer.addReceipt(orderReceipt);
							sendEvent(new DeliveryEvent(customer.getAddress(),customer.getDistance()));
						}
						//System.out.println("order completed");
						j--;
					} catch (Exception e1) { e1.printStackTrace(); } }}
		};
		this.subscribeBroadcast(TickBroadcast.class,tickCallback);
		this.subscribeBroadcast(TerminateBroadcast.class, c-> this.terminate());
	}
	/**When the tick broadcast happen,the tickManager checks if there are orders that need to be ordered,
	if so,send all the events that supposed to be ordered in this tick*/
	public Pair<Integer,CompletionService<OrderReceipt>> tickManager(int tick){

		ExecutorService e=Executors.newFixedThreadPool(futureOrders.size());
		CompletionService<OrderReceipt> cService=new ExecutorCompletionService<>(e);
		int j =0;
		synchronized (futureOrders){//take order from the list in the correct tick
			// and send event that handle the order
			while((!futureOrders.isEmpty()) && futureOrders.get(0).getTick()==tick) {
				FutureOrder fut= futureOrders.remove(0);
				cService.submit(new Callable<OrderReceipt>() {
					@Override
					public synchronized OrderReceipt call(){
						Future<OrderReceipt> f = sendEvent(new BookOrderEvent(customer, fut.getBookTitle(),
								fut.getTick(),customer.getAvailableCreditAmount())).get();
						while (!f.isDone()) {
							try {//wait until the order process is completed
								wait(waitingTime);
								System.out.println("hello2");
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						return f.get();
					}});
				j++; }}
		e.shutdown();
		return new Pair<>(j,cService);
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<FutureOrder> getFutureOrders() {
		return futureOrders;
	}

}
