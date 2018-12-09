package bgu.spl.mics.application;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import jdk.incubator.http.internal.common.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    public static void main(String[] args) {
        MessageBus messageBus= MessageBusImpl.getInstance();
        //create the inventory, and load the books to it.
        BookInventoryInfo[] inventoryInfos=getInventory();
        Inventory inventory=Inventory.getInstance();
        inventory.load(inventoryInfos);
        //create the vehicles
        DeliveryVehicle[] vehicles=getResources();

        Object[] services=getServices();

        Pair<Customer,ArrayList<FutureOrder>>[] customers=getCustomers((JSONObject) services[5]);
        //the number of microService from each type
        int sellingNum=(Integer)services[1];
        int inventoryNum=(Integer)services[2];
        int logisticsNum=(Integer)services[3];
        int resourceNum=(Integer)services[4];

        int[] time=getTime((JSONObject) services[0]);
        ExecutorService e = Executors.newFixedThreadPool
                (customers.length+sellingNum+inventoryNum+logisticsNum+resourceNum+1);
        for (int i=0;i<customers.length;i++){
            APIService a=new APIService(customers[i].second,customers[i].first,String.valueOf(i));
            e.execute(a);
        }
        for(int i=0;i<sellingNum;i++){
            SellingService s=new SellingService(String.valueOf(i));
            e.execute(s);
        }
        for(int i=0;i<inventoryNum;i++){
            InventoryService in=new InventoryService(String.valueOf(i));
            e.execute(in);
        }
        for(int i=0;i<logisticsNum;i++){
            LogisticsService l=new LogisticsService(String.valueOf(i));
            e.execute(l);
        }
        for(int i=0;i<resourceNum;i++){
            ResourceService r=new ResourceService(String.valueOf(i));
        }
        TimeService t=new TimeService("TimeService",time[0],time[1]);
        e.execute(t);

    }

    public static BookInventoryInfo[] getInventory(){
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
    public static DeliveryVehicle[] getResources(){
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
    public static Object[] getServices(){
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
            //
            myServices[5]=  services.get("customers");
            return myServices;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static int[] getTime(JSONObject services){
        int[] time = new int[2];
        JSONObject curr = (JSONObject) services.get("time");
        time[0] = (Integer) curr.get("speed");
        time[1] = (Integer) curr.get("duration");
        return time;
    }

    public static Pair<Customer,ArrayList<FutureOrder>>[] getCustomers(JSONObject services){
        Pair<Customer,ArrayList<FutureOrder>>[] Customers;
        JSONArray customersJsonObject = (JSONArray) services.get("customers");
        Customers = new Pair[customersJsonObject.size()];
        for (int i = 0; i<Customers.length;i++){
            JSONObject curr= (JSONObject)customersJsonObject.get(i);
            int id = (Integer) curr.get("id");
            String name = (String) curr.get("name");
            String address = (String) curr.get("address");
            int distance = (Integer) curr.get("distance");
            JSONObject creditCard = (JSONObject) curr.get("creditCard");
            int creditCardNum = (Integer)creditCard.get("number");
            int creditCardAmount = (Integer)creditCard.get("amount");
            Customer c=new Customer(name,id,address,distance,creditCardAmount,creditCardNum);
            JSONArray ordersArr = (JSONArray)curr.get("orderSchedule");
            ArrayList<FutureOrder> orders = new ArrayList<>();
            for(int j = 0; j<ordersArr.size();j++){
                JSONObject order = (JSONObject) ordersArr.get(i);
                String bookTitle = (String)order.get("bookTitle");
                int tick = (int)order.get("tick");
                orders.add(new FutureOrder(bookTitle,tick));
            }
            Customers[i]=new Pair<>(c,orders);
        }
        return Customers;
    }
    public static void printCustomers (String filename, Customer[] arr){
        HashMap<Integer,Customer> map = new HashMap<>();
        for(int i = 0; i<arr.length;i++){
            map.put(arr[i].getId(),arr[i]);
        }
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(map);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

}
/*        Customer[] Customers;
        JSONArray customersJsonObject = (JSONArray) services.get("customers");
        Customers = new Customer[customersJsonObject.size()];
        for (int i = 0; i<Customers.length;i++){
            JSONObject curr= (JSONObject)customersJsonObject.get(i);
            int id = (Integer) curr.get("id");
            String name = (String) curr.get("name");
            String address = (String) curr.get("address");
            int distance = (Integer) curr.get("distance");
            JSONObject creditCard = (JSONObject) curr.get("creditCard");
            int creditCardNum = (Integer)creditCard.get("number");
            int creditCardAmount = (Integer)creditCard.get("amount");
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
        */