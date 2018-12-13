package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class ServiceInitCheck {
    private static AtomicInteger numOfnotInit;
    private static AtomicInteger numOfServicesToTerminate;

    public static void SetInitCheck(int num){
        numOfnotInit=new AtomicInteger();
        numOfnotInit.set(num);
        numOfServicesToTerminate = new AtomicInteger();
        numOfServicesToTerminate.set(num);
        numOfServicesToTerminate.getAndIncrement();
    }

    public static void DecreaseInit(){

        numOfnotInit.getAndDecrement();
    }
    public static void DecreaseFinished(){numOfServicesToTerminate.getAndDecrement();
    //System.out.println(numOfServicesToTerminate.get());
        }
    public static boolean ready(){
        return numOfnotInit.get()==0;
    }
    public static boolean finished() {return  numOfServicesToTerminate.get()==0;}

}
