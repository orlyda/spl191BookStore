package bgu.spl.mics;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private HashMap<MicroService,Queue<Message>> ServiceMap = new HashMap<>();
    private HashMap<Class <? extends Broadcast>, List<MicroService>> BroadcastMap = new HashMap<>();
    private HashMap<Class<? extends Message> , Queue<MicroService>> EventMap = new HashMap<>();
    private HashMap<Event,Future> FutureMap = new HashMap<>();
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if(!EventMap.get(type).contains(m))
            EventMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if(!BroadcastMap.get(type).contains(m))
            BroadcastMap.get(type).add(m);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		FutureMap.get(e).resolve(result);
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
            wait();
        } catch (InterruptedException e) {
            if (!ServiceMap.containsKey(m))
                throw new InterruptedException();
        }

		return ServiceMap.get(m).remove();
	}

	

}
