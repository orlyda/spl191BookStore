package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.List;

public class FiftyPercentDiscount implements Broadcast {
    private List<String> booksInDiscount;

    FiftyPercentDiscount (List<String> booksTitle){
       booksInDiscount=booksTitle;
   }

   public List<String> getBooksInDiscount(){
        return booksInDiscount;
   }


}
