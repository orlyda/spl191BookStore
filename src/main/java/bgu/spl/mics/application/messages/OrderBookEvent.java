package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class OrderBookEvent implements Event<OrderReceipt> {
    private final Customer customer;
    private final String bookTitle;
    private final int tick;

    public OrderBookEvent(Customer c,String title,int tick){
        customer=c;
        bookTitle=title;
        this.tick=tick;
    }

    public final Customer getCustomer() {
        return customer;
    }

    public final String getBookTitle() {
        return bookTitle;
    }

    public int getTick() {
        return tick;
    }
}

