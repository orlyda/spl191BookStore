package bgu.spl.mics.application.passiveObjects;

import org.junit.Test;
import static org.junit.Assert.*;
import com.google.gson.*;

public class InventoryTest {

    @Test
    public void getInstance() {
    }

    @Test
    public void load() {
        BookInventoryInfo book;
        JsonElement subObject;
        JsonObject jObject=new JsonObject();
        JsonObject inventoryObject = jObject.getAsJsonObject("initialInventory");
        JsonArray arr=inventoryObject.getAsJsonArray();
        BookInventoryInfo[] books=new BookInventoryInfo[arr.size()];
        for(int i=0;i<arr.size();i++){
            subObject=arr.get(i);
            String title=subObject.getAsString();
        }
    }

    @Test
    public void take() {
        Inventory test=new Inventory();
        assertEquals("The book is not in stock",OrderResult.NOT_IN_STOCK,test.take("Harry Potter"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
    }

    @Test
    public void printInventoryToFile() {
    }
}