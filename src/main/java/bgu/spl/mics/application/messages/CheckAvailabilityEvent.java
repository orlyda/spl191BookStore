package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MoneyStatus;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class CheckAvailabilityEvent implements Event<MoneyStatus> {
    private final String bookTitle;

    public CheckAvailabilityEvent(String book){
        bookTitle=book;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
