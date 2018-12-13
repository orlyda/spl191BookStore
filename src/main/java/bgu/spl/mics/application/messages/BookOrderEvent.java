package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private final int availableMoney;
    private final Customer customer;
    private final String bookTitle;
    private final int tick;

    public BookOrderEvent(Customer c, String title, int tick, int money){
        customer=c;
        bookTitle=title;
        this.tick=tick;
        availableMoney=money;
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

    public int getAvailableMoney() {
        return availableMoney;
    }
}

