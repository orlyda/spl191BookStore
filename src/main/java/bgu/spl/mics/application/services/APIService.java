package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FiftyPercentDiscount;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.FutureOrder;
import java.util.concurrent.atomic.*;
import java.util.List;

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
	private Customer customer;
	private List<FutureOrder> futureOrders;
	private String name;

	public APIService() {
		super("APIService");
	}
	public APIService(List<FutureOrder> orders,Customer c){
		super("APIService");
		customer=c;
		futureOrders=orders;
	}

	@Override
	protected void initialize() {
	/*	Broadcast b=new FiftyPercentDiscount();
		Callback c;
		//this.subscribeBroadcast(b.getClass(),c);
*/
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<FutureOrder> getFutureOrders() {
		return futureOrders;
	}

}
