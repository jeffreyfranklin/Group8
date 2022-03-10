package nachos.threads;

/**
 * A node class for the ReadyQueue data structure
 *
 */
public class ReadyNode {
	
	
	// Data Fields
	private KThread thread;
	private ReadyNode nextReadyNode;
	
	
	
	/**
	 * Constructor for ReadyNode object class
	 * 
	 * @param thread - Thread to be stored
	 * @param nextReadyNode - The next node in the queue
	 */
	public ReadyNode(KThread thread, ReadyNode nextReadyNode) {
		this.thread = thread;
		this.nextReadyNode = nextReadyNode;
	}
	
	
	
	// Accessors
	
	/**
	 * Accessor for thread
	 * 
	 * @return thread
	 */
	public KThread getReadyNode() { return thread; }
	
	/**
	 * Accessor to get next node in queue
	 * 
	 * @return nextReadyNode
	 */
	public ReadyNode getReadyNodeNext() { return nextReadyNode; }
	
	
	
	
	
	// Mutators
	
	/**
	 * Mutator
	 * 
	 * Allow a thread to be set to the current node
	 * 
	 * @param thread
	 */
	public void setReadyNode(KThread thread) {
		this.thread = thread;
	}
	
	
	
	
	/**
	 * Mutator
	 * 
	 * @param nextReadyNode - Node that succeeds this node in the queue
	 */
	public void setNextReadyNode(ReadyNode nextReadyNode) {
		this.nextReadyNode = nextReadyNode;
	}
	
	
	
	
	
	
	// Methods
	
	
	/**
	 * toString for debugging/printing
	 */
	public String toString() {
		String string = "Thread: " + thread.getName();
		
		return string;
	}
	
}
