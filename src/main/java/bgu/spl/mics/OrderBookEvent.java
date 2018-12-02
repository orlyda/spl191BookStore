package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;

public class OrderBookEvent implements Event {
    private final Customer customer;
    private final String bookTitle;

    public OrderBookEvent(Customer c,String title){
        customer=c;
        bookTitle=title;
    }

    public final Customer getCustomer() {
        return customer;
    }

    public final String getBookTitle() {
        return bookTitle;
    }
}

