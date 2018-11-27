package bgu.spl.mics.application.passiveObjects;

import java.sql.Time;

public class FutureOrder {
    private String bookTitle;
    private Time tick;

    public FutureOrder(String book,Time tick){
        bookTitle=book;
        this.tick=tick;
    }
    public String getBookTitle(){
        return bookTitle;
    }
    public Time getTick(){return tick;}
}
