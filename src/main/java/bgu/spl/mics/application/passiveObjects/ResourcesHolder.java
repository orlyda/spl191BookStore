package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	
	/**
     * Retrieves the single instance of this class.
     */
	private static ResourcesHolder instance =null;
	private AtomicReferenceArray<DeliveryVehicle> vehicles;
	private AtomicBoolean[] acquired;
	private Semaphore sem;//used for counting free vehicles
	private ResourcesHolder(){}
	public static ResourcesHolder getInstance() {
		if(instance==null)
			instance = new ResourcesHolder();
		return instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> f = new Future();
		Thread take = new Thread(new takeThread(f));
		take.run();
		return f;
	}

	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		this.acquired[findVehicle(vehicle)].set(false);
		sem.release();
 	}

 	private int findVehicle(DeliveryVehicle vehicle){
		for(int i = 0;i<this.vehicles.length();i++){
			if(this.vehicles.get(i)==vehicle)
				return i;
		}
		return -1;
	}

	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.vehicles=new AtomicReferenceArray<DeliveryVehicle>(vehicles.length);
		this.acquired=new AtomicBoolean[vehicles.length];
		this.sem=new Semaphore(vehicles.length);
		for (int i=0;i<vehicles.length;i++) {
			this.vehicles.getAndSet(i, vehicles[i]);
			this.acquired[i]=new AtomicBoolean();
			this.acquired[i].set(false);
		}
	}

	private class takeThread implements Runnable {
		private Future f;
		public takeThread(Future f){
			this.f=f;
		}
		public void run(){
			try {
				sem.acquire();
			}
			catch (InterruptedException e){}
			boolean found = false;
			int i =0;
			synchronized (ResourcesHolder.getInstance()){
				while (!found){
					if(!acquired[i].get()){
						acquired[i].set(true);
						found=true;
						f.resolve(vehicles.get(i));
					}
					i++;
				}
			}
		}
	}
}
