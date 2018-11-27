package bgu.spl.mics;

import bgu.spl.mics.application.services.InventoryService;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
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
        Future<String>f=m.sendEvent(e);
        if(f==null)
            Assert.fail();
    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
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
    public void unregister() {
    }

    @Test
    public void awaitMessage_register() {
        MessageBusImpl b=new MessageBusImpl();
        MicroService m=new SellingService();
        try {
            b.register(m);
            b.awaitMessage(m);
        }
        catch (InterruptedException e){
            Assert.fail();
        }
    }
}

