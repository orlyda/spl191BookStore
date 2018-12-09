package bgu.spl.mics.application.services;


import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private Timer timer;
	private volatile int speed,duration;
	private AtomicInteger time;
	public TimeService(String name, int speed,int duration) {
		super(name);
		this.speed = speed;
		this.duration=duration;
	}
	@Override
	protected void initialize() {
		timer= new Timer();
		time = new AtomicInteger(1);
		timer.schedule(new timetask(),0, speed);
	}

	class timetask extends TimerTask{
        public void run(){
            if(time.get()<duration) {
                sendBroadcast(new TickBroadcast(time.get()));
                time.getAndIncrement();
            }
            else if(time.get()==duration)
            	sendBroadcast(new TerminateBroadcast());
	    }
    }
}

