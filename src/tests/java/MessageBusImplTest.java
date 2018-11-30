package java;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.services.InventoryService;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    @Test
    public void subscribeEvent() {
        ExampleEvent e=new ExampleEvent("Orly");
        MessageBusImpl m=new MessageBusImpl();;
        MicroService mic=new InventoryService();
        m.subscribeEvent(e.getClass(),mic);
        Future<String> f=m.sendEvent(e);
        if(f==null)
            Assert.fail();
    }

    @Test
    public void sendEvent() {
        Event<String> e=new ExampleEvent("Orly");
        MessageBusImpl m=new MessageBusImpl();
        Future<String> f=m.sendEvent(e);
        if(f!=null)
            Assert.fail();
    }

    @Test
    public void awaitMessage_register() {
        MessageBusImpl b=new MessageBusImpl();
        MoneyRegister mo=new MoneyRegister();
        MicroService m=new SellingService("Orly",mo);
        try {
            b.register(m);
            b.awaitMessage(m);
        }
        catch (InterruptedException e){
            Assert.fail();
        }
    }
}

