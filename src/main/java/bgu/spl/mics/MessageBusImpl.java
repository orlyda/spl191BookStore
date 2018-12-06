package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.BlockingDeque;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private static MessageBus instance = null;
    private HashMap<MicroService,Queue<Message>> ServiceMap;
    private HashMap<Class <? extends Broadcast>, List<MicroService>> BroadcastMap;
    private HashMap<Class<? extends Message> ,BlockingDeque<MicroService>> EventMap;
    private HashMap<Event,Future> FutureMap;
    public static MessageBus getInstance(){
        if(instance == null)
            instance = new MessageBusImpl();
        return instance;
    }
	private MessageBusImpl(){
		ServiceMap = new HashMap<>();
		BroadcastMap = new HashMap<>();
		EventMap = new HashMap<>();
		FutureMap = new HashMap<>();
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if(!EventMap.get(type).contains(m))
            EventMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class <? extends Broadcast> type, MicroService m) {
        if(!BroadcastMap.get(type).contains(m))
            BroadcastMap.get(type).add(m);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		FutureMap.get(e).resolve(result);
		FutureMap.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
        for (MicroService m: BroadcastMap.get(b.getClass())) {
            ServiceMap.get(m).add(b);
        }
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
	    ServiceMap.get(EventMap.get(e).peek()).add(e);
		if(EventMap.get(e).add(EventMap.get(e).remove())) {
		    FutureMap.put(e,new Future<T>());
		    return FutureMap.get(e);
        }
		else
		    return null;
	}

	@Override
	public void register(MicroService m) {
	    if(!ServiceMap.containsKey(m))
	        ServiceMap.put(m, new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m){
	        ServiceMap.remove(m);
    }

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
        if (!ServiceMap.containsKey(m))
            throw new IllegalStateException();
        try {
        	while (this.ServiceMap.isEmpty())
            	wait();
        } catch (InterruptedException e) {
                throw new InterruptedException();
        }

		return ServiceMap.get(m).remove();
	}

	

}
