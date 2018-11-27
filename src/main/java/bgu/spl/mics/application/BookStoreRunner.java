package bgu.spl.mics.application;
import java.io.FileReader;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader("input.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray books = (JSONArray)jsonObject.get("initialInventory");
            BookInventoryInfo[] myBooks = new BookInventoryInfo[books.size()];
            for(int i = 0; i<myBooks.length;i++){
                JSONObject curr = (JSONObject) books.get(i);
                String name = (String) curr.get("bookTitle");
                long amount =  (Long) curr.get("amount");
                long price = (Long) curr.get("price");
                myBooks[i]= new BookInventoryInfo(name,(int)amount,(int)price);
            }
            for(BookInventoryInfo b: myBooks){
                System.out.println(b.getBookTitle()+"," + b.getAmountInInventory()+","+b.getPrice());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
