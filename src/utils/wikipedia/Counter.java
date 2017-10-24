package utils.wikipedia;

/**
 * Counter class
 * @author ksemer
 */
public class Counter {
	private int counter;

	/**
	 * Constructor
	 */
	public Counter() {
		counter = 0;
	}
	
	/**
	 * Constructor
	 * @param c
	 */
	public Counter(int c) {
		counter = c;
	}
	
	/**
	 * Increase counter
	 */
	public void increase() {
		counter++;
	}
	
	/**
	 * Add c to counter
	 * @param c
	 */
	public void increase(int c) {
		counter+=c;
	}
	
	/**
	 * Decrease counter
	 */
	public void decrease() {
		counter--;
	}
	
	/**
	 * Set the counter to val
	 * @param val
	 */
	public void set(int val) {
		counter = val;
	}
	
	/**
	 * Return counter
	 * @return
	 */
	public int getValue() {
		return counter;
	}
}