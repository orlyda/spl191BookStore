package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private HashMap<MicroService, BlockingQueue<Message>> ServiceMap;
    private HashMap<Class <? extends Broadcast>, List<MicroService>> BroadcastMap;
    private HashMap<Class<? extends Message> ,BlockingQueue<MicroService>> EventMap;
    private HashMap<Event,Future> FutureMap;
    private HashMap<MicroService,List<Class<? extends Event>>> EventSubscribe;
	private HashMap<MicroService,List<Class<? extends Broadcast>>> BroadSubscribe;

	private static class MessageBusImplHolder{private static MessageBus instance = new MessageBusImpl();}
    public static MessageBus getInstance(){
       return MessageBusImplHolder.instance;
    }
	private MessageBusImpl(){
		ServiceMap = new HashMap<>();
		BroadcastMap = new HashMap<>();
		EventMap = new HashMap<>();
		FutureMap = new HashMap<>();
		EventSubscribe = new HashMap<>();
		BroadSubscribe = new HashMap<>();
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
	    if(!EventMap.containsKey(type)) {
            EventMap.put(type, new LinkedBlockingQueue<>());
        }
        if(!EventMap.get(type).contains(m)) {
            try {
                EventMap.get(type).put(m);
            } catch (InterruptedException e) {}
            EventSubscribe.get(m).add(type);

		}
	}

	@Override
	public void subscribeBroadcast(Class <? extends Broadcast> type, MicroService m) {
	    if(!BroadcastMap.containsKey(type))
	        BroadcastMap.put(type,new LinkedList<>());
        if(!BroadcastMap.get(type).contains(m)) {
			BroadcastMap.get(type).add(m);
			BroadSubscribe.get(m).add(type);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
	    synchronized (FutureMap) {
            Future<T> f = FutureMap.get(e);
            f.resolve(result);
        }
	}

	@Override
	public void sendBroadcast(Broadcast b) {
	    List<MicroService> services = BroadcastMap.get(b.getClass());
        for (MicroService m: services) {
            try {
                ServiceMap.get(m).put((Message) b);
            } catch (InterruptedException e) {}
        }
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e){
		if(EventMap.containsKey(e.getClass())) {
           if(EventMap.get(e.getClass()).isEmpty())
               return null;
           else {
               BlockingQueue<MicroService> queue = EventMap.get(e.getClass());
               MicroService m = queue.poll();
               try {
                   ServiceMap.get(m).put(e);
               } catch (InterruptedException e1) {
               }
               queue.add(m);
               synchronized (FutureMap) {
                   Future<T> f = new Future<>();
                   FutureMap.put(e, f);
                   return f;
               }
           }
        }
		else
            return null;
	}

	@Override
	public void register(MicroService m) {
	    if(!ServiceMap.containsKey(m)) {
			ServiceMap.put(m, new LinkedBlockingQueue<>());
			if(!EventSubscribe.containsKey(m))
                EventSubscribe.put(m, new LinkedList<>());
			if(!BroadSubscribe.containsKey(m))
                BroadSubscribe.put(m, new LinkedList<>());
		}
	}

	@Override
	public void unregister(MicroService m){
	        ServiceMap.remove(m);
	        for(Class<? extends Event> c : EventSubscribe.get(m)){
	        	EventMap.get(c).remove(m);
	        }
		    for(Class<? extends Broadcast> c : BroadSubscribe.get(m)){
		    	BroadcastMap.get(c).remove(m);
		    }
		    EventSubscribe.remove(m);
		    BroadSubscribe.remove(m);
    }

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!ServiceMap.containsKey(m))
            throw new IllegalStateException();
		return ServiceMap.get(m).take();
	}


}
