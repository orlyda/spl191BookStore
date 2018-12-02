package bgu.spl.mics.application.passiveObjects;

import java.sql.Time;

public class FutureOrder {
    private final String bookTitle;
    private final int tick;

    public FutureOrder(String book,int tick){
        bookTitle=book;
        this.tick=tick;
    }
    public String getBookTitle(){
        return bookTitle;
    }
    public int getTick(){return tick;}
}
