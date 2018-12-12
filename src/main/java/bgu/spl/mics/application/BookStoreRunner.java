package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        Pair<Customer,ArrayList<FutureOrder>>[] customers=getCustomers((JSONArray) services[5]);
        //the number of microService from each type
        int sellingNum=(Integer)services[1];
        int inventoryNum=(Integer)services[2];
        int logisticsNum=(Integer)services[3];
        int resourceNum=(Integer)services[4];

        int[] time=(int[])services[0];
        ExecutorService e = Executors.newFixedThreadPool
                (customers.length+sellingNum+inventoryNum+logisticsNum+resourceNum+1);
        for (int i=0;i<customers.length;i++){
            APIService a=new APIService(customers[i].getSecond(),customers[i].getFirst(),String.valueOf(i));
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
        Customer[] Customers = new Customer[customers.length];
        for(int i=0;i<customers.length;i++)
            Customers[i]= customers[i].getFirst();
        printCustomers("Customers.txt",Customers );
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
                long amount =  (Long) curr.get("amount");
                long price = (Long) curr.get("price");
                myBooks[i]= new BookInventoryInfo(name,(int)amount,(int)price);
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
            JSONArray resources = (JSONArray)jsonObject.get("initialResources");
            JSONObject medStage= (JSONObject) resources.get(0);
            JSONArray cars = (JSONArray) medStage.get("vehicles");
            DeliveryVehicle[] myCars = new DeliveryVehicle[cars.size()];
            for(int i = 0; i<myCars.length;i++){
                JSONObject curr = (JSONObject) cars.get(i);
                long license =  (Long) curr.get("license");
                long speed = (Long) curr.get("speed");
                myCars[i]= new DeliveryVehicle((int)license,(int)speed);
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
            Long selling = (Long)services.get("selling");
            Long inventoryService =(Long)services.get("inventoryService");
            Long logistics = (Long)services.get("logistics");
            Long resourcesService = (Long)services.get("resourcesService");
            myServices[0]=getTime(services);
            myServices[1]=  selling.intValue();
            myServices[2]=  inventoryService.intValue();
            myServices[3]=  logistics.intValue();
            myServices[4]=  resourcesService.intValue();
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
        long num1 = (Long) curr.get("speed");
        long num2 = (Long) curr.get("duration");
        time[0]=(int)num1;
        time[1]=(int)num2;
        return time;
    }

    public static Pair<Customer,ArrayList<FutureOrder>>[] getCustomers(JSONArray customersArr){
        Pair<Customer,ArrayList<FutureOrder>>[] Customers;
        Customers = new Pair[customersArr.size()];
        for (int i = 0; i<Customers.length;i++){
            JSONObject curr= (JSONObject)customersArr.get(i);
            long id = (Long) curr.get("id");
            String name = (String) curr.get("name");
            String address = (String) curr.get("address");
            long distance = (Long) curr.get("distance");
            JSONObject creditCard = (JSONObject) curr.get("creditCard");
            long creditCardNum = (Long) creditCard.get("number");
            long creditCardAmount = (Long) creditCard.get("amount");
            Customer c=new Customer(name,(int)id,address,(int)distance,(int)creditCardAmount,(int)creditCardNum);
            JSONArray ordersArr = (JSONArray)curr.get("orderSchedule");
            ArrayList<FutureOrder> orders = new ArrayList<>();
            for(int j = 0; j<ordersArr.size();j++){
                JSONObject order = (JSONObject) ordersArr.get(j);
                String bookTitle = (String)order.get("bookTitle");
                long tick = (Long) order.get("tick");
                orders.add(new FutureOrder(bookTitle,(int)tick));
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
            out.writeObject(map.toString());
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    public ArrayList<FutureOrder> toList(JSONArray ary){
        ArrayList<FutureOrder> arr = new ArrayList<>();
        for(int i=0; i<ary.size();i++){
            JSONObject obj= (JSONObject) ary.get(i);
            String s = (String) obj.get("bookTitle");
            long l = (Long)obj.get("tick");
            arr.add(new FutureOrder(s,(int)l));
        }
        return arr;
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