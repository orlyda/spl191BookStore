package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;

public class OrderBookEvent implements Event {
    private Customer customer;
    private String bookTitle;

    public OrderBookEvent(Customer c,String title){
        customer=c;
        bookTitle=title;
    }
}
