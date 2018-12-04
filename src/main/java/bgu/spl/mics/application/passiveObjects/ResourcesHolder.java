package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
 	}

 	private int findVehicle(DeliveryVehicle vehicle){
		for(int i = 0;i<this.vehicles.length();i++){
			if(this.vehicles.equals(vehicle))
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
		for (int i=0;i<vehicles.length;i++) {
			this.vehicles.getAndSet(i, vehicles[i]);
			this.acquired[i].set(false);
		}
	}

}
