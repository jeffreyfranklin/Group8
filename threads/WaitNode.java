package nachos.threads;


/**
 * A node class for the WaitQueue data structure
 *
 */
public class WaitNode {
	
	
	// Data Fields
	private KThread thread;
	private long wakeTime;
	private WaitNode nextWaitNode;
	private WaitNode previousWaitNode;
	
	
	
	/**
	 * Constructor for waitNode object class
	 * 
	 * @param thread - Thread to be stored
	 * @param wakeTime - The thread's wake time
	 */
	public WaitNode(KThread thread, long wakeTime) {
		this.thread = thread;
		this.wakeTime = wakeTime;
		this.nextWaitNode = null;
		this.previousWaitNode = null;
	}
	
	
	
	// Accessors
	
	/**
	 * Accessor for thread
	 * 
	 * @return thread
	 */
	public KThread getWaitNodeThread() { return thread; }
	
	/**
	 * Accessor for wakeTime
	 * 
	 * @return wakeTime
	 */
	public long getWaitNodeWakeTime() { return wakeTime; }
	
	/**
	 * Accessor to get next node in queue
	 * 
	 * @return nextWaitNode
	 */
	public WaitNode getWaitNodeNext() { return nextWaitNode; }
	
	/**
	 * Accessor to get previous node in queue
	 * 
	 * @return previousWaitNode
	 */
	public WaitNode getWaitNodePrevious() { return previousWaitNode; }
	
	
	
	
	// Mutators
	
	/**
	 * Mutator
	 * 
	 * Allow a thread to be set to the current node
	 * 
	 * @param thread
	 */
	public void setWaitNodeThread(KThread thread) {
		this.thread = thread;
	}
	
	
	/**
	 * Mutator
	 * 
	 * Allows to set the time that a specific thread should wake
	 * 
	 * @param wakeTime - The time the thread should wake
	 */
	public void setWaitNodeWakeTime(long wakeTime) {
		this.wakeTime = wakeTime;
	}
	
	
	/**
	 * Mutator
	 * 
	 * @param nextWaitNode - Node that succeeds this node in the queue
	 */
	public void setNextWaitNode(WaitNode nextWaitNode) {
		this.nextWaitNode = nextWaitNode;
	}
	
	/**
	 * Mutator
	 * 
	 * @param previousWaitNode - Node that precedes this node in the queue
	 */
	public void setPreviousWaitNode(WaitNode previousWaitNode) {
		this.previousWaitNode = previousWaitNode;
	}
	
	
	
	
	
	// Methods
	
	
	/**
	 * toString for debugging/printing
	 */
	public String toString() {
		String temp = "Thread: " + thread.getName()
						+ " | WakeTime: " + wakeTime;
		
		return temp;
	}
	
}
