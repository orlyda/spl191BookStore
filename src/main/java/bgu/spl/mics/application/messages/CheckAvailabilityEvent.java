package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent implements Event<Integer> {
    private final String bookTitle;

    public CheckAvailabilityEvent(String book){

        bookTitle=book;
    }

    public String getBookTitle() {
        return bookTitle;
    }

}
