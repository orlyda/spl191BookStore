package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class ServiceInitCheck {
    private static AtomicInteger numOfnotInit;

    public static void SetInitCheck(int num){
        numOfnotInit=new AtomicInteger();
        numOfnotInit.set(num);
    }

    public static void DecreaseInit(){

        numOfnotInit.getAndDecrement();
    }

    public static boolean ready(){
        return numOfnotInit.get()==0;
    }


}
