import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.json.simple.*;

import java.io.*;
import java.nio.file.*;


public class InventoryTest {
    public BookInventoryInfo[] getBooks() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("inputTest.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray books = (JSONArray) jsonObject.get("initialInventory");
            BookInventoryInfo[] myBooks = new BookInventoryInfo[books.size()];
            for (int i = 0; i < myBooks.length; i++) {
                JSONObject curr = (JSONObject) books.get(i);
                String name = (String) curr.get("bookTitle");
                long amount = (Long) curr.get("amount");
                long price = (Long) curr.get("price");
                myBooks[i] = new BookInventoryInfo(name, (int) amount, (int) price);
            }
            return myBooks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Test
    public void take() {
        Inventory test=new Inventory();
        test.load(getBooks());
        Assert.assertEquals("The book is not in stock", OrderResult.NOT_IN_STOCK,test.take("Harry Potter"));
        assertEquals("The book is in stock",OrderResult.SUCCESSFULLY_TAKEN,test.take("The Hunger Games"));

    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        Inventory test=new Inventory();
        test.load(getBooks());
        assertEquals("The book is not in stock",-1,test.checkAvailabiltyAndGetPrice("Harry Potter"));
        assertEquals("The book is in available",102,test.checkAvailabiltyAndGetPrice("The Hunger Games"));

    }

}