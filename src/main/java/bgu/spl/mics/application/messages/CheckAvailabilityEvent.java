package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class CheckAvailabilityEvent implements Event<OrderReceipt> {
    private String bookTitle;

    public CheckAvailabilityEvent(String book){
        bookTitle=book;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
