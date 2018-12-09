package bgu.spl.mics.application;
import java.io.FileReader;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
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

    public BookInventoryInfo[] getInvenyory(){
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader("input.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray books = (JSONArray)jsonObject.get("initialInventory");
            BookInventoryInfo[] myBooks = new BookInventoryInfo[books.size()];
            for(int i = 0; i<myBooks.length;i++){
                JSONObject curr = (JSONObject) books.get(i);
                String name = (String) curr.get("bookTitle");
                int amount =  (Integer) curr.get("amount");
                int price = (Integer) curr.get("price");
                myBooks[i]= new BookInventoryInfo(name,amount,price);
            }
            return myBooks;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public DeliveryVehicle[] getResources(){
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader("input.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray cars = (JSONArray)jsonObject.get("initialResources");
            DeliveryVehicle[] myCars = new DeliveryVehicle[cars.size()];
            for(int i = 0; i<myCars.length;i++){
                JSONObject curr = (JSONObject) cars.get(i);
                int license =  (Integer) curr.get("license");
                int speed = (Integer) curr.get("speed");
                myCars[i]= new DeliveryVehicle(license,speed);
            }
            return myCars;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Object[] getServices(){
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader("input.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject services = (JSONObject) jsonObject.get("services");
            Object[] myServices = new Object[6];
            myServices[0]=getTime(services);
            myServices[1]=  services.get("selling");
            myServices[2]=  services.get("inventoryService");
            myServices[3]=  services.get("logistics");
            myServices[4]=  services.get("resourcesService");
            return myServices;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int[] getTime(JSONObject services){
        int[] time = new int[2];
        JSONObject curr = (JSONObject) services.get("time");
        time[0] = (Integer) curr.get("speed");
        time[1] = (Integer) curr.get("duration");
        return time;
    }

    public Customer[] getCustomers(JSONObject services){
        Customer[] Customers;
        JSONArray customersJsonObject = (JSONArray) services.get("customers");
        Customers = new Customer[customersJsonObject.size()];
        for (int i = 0; i<Customers.length;i++){
            JSONObject curr= (JSONObject)customersJsonObject.get(i);
            int id = (Integer) curr.get("id");
            String name = (String) curr.get("name");
            String address = (String) curr.get("address");
            int distance = (Integer) curr.get("distance");
            JSONObject creditCard = (JSONObject) curr.get("creditCard");
            int[] creditcard = {(Integer)creditCard.get("number"),(Integer)creditCard.get("amount")};
            JSONArray ordersArr = (JSONArray)curr.get("orderSchedule");
            Object[] orders = new Object[ordersArr.size()];
            for(int j = 0; j<orders.length;j++){
                JSONObject order = (JSONObject) ordersArr.get(i);
                String bookTitle = (String)order.get("bookTitle");
                Integer tick = (Integer)order.get("tick");
                orders[j] = new Object[]{bookTitle, tick};
            }
        }
        return Customers;
    }

}
