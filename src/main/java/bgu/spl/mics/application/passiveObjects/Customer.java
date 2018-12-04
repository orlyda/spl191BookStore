package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {
	AtomicReference<List<Future>> futureOrders;

	public AtomicReference<List<Future>> getFutureOrders() {
		return futureOrders;
	}

	public void addFutureOrders(Future<OrderReceipt> future) {
		this.futureOrders.get().add(future);
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		// TODO Implement this
		return null;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		// TODO Implement this
		return 0;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		// TODO Implement this
		return null;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		// TODO Implement this
		return 0;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		// TODO Implement this
		return null;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		// TODO Implement this
		return 0;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		// TODO Implement this
		return 0;
	}
	
}
