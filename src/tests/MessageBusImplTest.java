package bgu.spl.mics;

import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    @Test
    public void subscribeEvent() {
    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
        Broadcast b  =new ExampleBroadcast("Orly");
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {
    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
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

