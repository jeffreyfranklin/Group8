package nachos.threads;


/**
 * Data structure to allow to store threads that are ready to run
 *
 */
public class ReadyQueue {
	
	// Data Fields
	private ReadyNode head;
	private int numNodes;
	
	
	/**
	 * Unargumented Constructor
	 */
	public ReadyQueue() {
		
		this.head = null;
		this.numNodes = 0;
	}
	
	
	
	// Accessors
	
	/**
	 * Accessor for head
	 * 
	 * @return head
	 */
	public ReadyNode getReadyQueueHead() { return head; }
	
	
	/**
	 * Accessor
	 * 
	 * @return the number of threads in the queue
	 */
	public int getReadyQueueNumNodes() { return numNodes; }
	
	
	
	
	
	// Methods
	
	
	/**
	 * Method
	 * 
	 * Add nodes to the queue (threads)
	 * 
	 * @param thread
	 */
	public void addReadyNode(KThread thread) {
		
		ReadyNode newNode = new ReadyNode(thread, null);
		
		if(numNodes == 0) {
			head = newNode;
			++numNodes;
		}
		else {
			
			if(numNodes == 1) {
				head.setNextReadyNode(newNode);
				++numNodes;
			}
			else {
				
				ReadyNode index = head;
				
				while(index != null) {
					
					if(index.getReadyNodeNext() == null) {
						index.setNextReadyNode(newNode);
						++numNodes;
						break;
					}
					
					index = index.getReadyNodeNext();
				}
			}
		}
		
	}
	
	
	/**
	 * Method
	 * 
	 * Remove node from queue (thread)
	 */
	public void removeReadyNode() {
		
		if(numNodes == 0) {
			System.out.println("There are no threads in the ready queue at the moment.");
		}
		else {
			head = head.getReadyNodeNext();
			--numNodes;
		}
	}
	
	
	
	
	/**
	 * toString for debugging/printing
	 */
	public String toString() {
		String string = "";
		
		ReadyNode index = head;
		
		while(index != null) {
			string += "Thread: " + index.getReadyNode().getName()
						+ " has been added to the ready queue.\n";
			
			index = index.getReadyNodeNext();
		}
		
		return string;
	}
	
	
}
