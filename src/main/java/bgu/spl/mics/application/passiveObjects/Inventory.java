package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.HashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private BookInventoryInfo[] booksInfo;
	private static Inventory instance =new Inventory();
	public Inventory(){

	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
        return Inventory.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		booksInfo=new BookInventoryInfo[inventory.length];
		for (int i=0;i<inventory.length;i++)
			booksInfo[i]=inventory[i];
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		for (BookInventoryInfo i:booksInfo){
			if(i.getBookTitle().equals(book) &&i.decreaseAmount())
				return OrderResult.SUCCESSFULLY_TAKEN;
		}
		return OrderResult.NOT_IN_STOCK;
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		for (BookInventoryInfo i:booksInfo) {
			if(i.getBookTitle().equals(book)&&i.getAmountInInventory()>0)
				return i.getPrice();
		}
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		HashMap<String,Integer> map=new HashMap<>();
		for(int i=0;i<booksInfo.length;i++){
			map.put(booksInfo[i].getBookTitle(),booksInfo[i].getAmountInInventory());
		}
		try {
			FileOutputStream fileOut =
					new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(map);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

}
