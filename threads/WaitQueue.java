package nachos.threads;

/**
 * Data structure for a priority queue that will allow to store
 * threads. This queue is sorted by wake time.
 * 
 */
public class WaitQueue {
	
	
	
	// Data Fields
	private WaitNode head;
	private int numNodes;
	
	
	/**
	 * Unargumented constructor
	 */
	public WaitQueue() {
		
		this.head = null;
		numNodes = 0;
	}
	
	
	
	
	
	// Accessors
	
	/**
	 * Accessor for head
	 * 
	 * @return head (a "WaitNode(thread, wakeTime)" object)
	 */
	public WaitNode getWaitQueueHead() { return head; }
	
	
	/**
	 * Accessor for numNodes
	 * 
	 * @return numNodes (the number of nodes currently in the queue)
	 */
	public int getWaitQueueNumNodes() { return numNodes; }
	
	
	
	
	
	// Mutators
	
	
	/**
	 * Mutator allowing to set/modify the head node of the queue
	 * 
	 * @param head - The head node of the queue
	 */
	public void setWaitNodeHead(WaitNode head) {
		this.head = head;
	}
	
	
	
	
	
	
	// Methods
	
	/**
	 * Add node to the queue
	 * 
	 * @param node - A WaitNode object created with WaitNode(thread, wakeTime)
	 */
	public void addWaitNode(WaitNode node) {
		
		if(numNodes == 0) {
			head = new WaitNode(node.getWaitNodeThread(), node.getWaitNodeWakeTime());
			++numNodes;
			
		}
		else {
			WaitNode queueIndex = head;
			
			while(queueIndex != null) {
				
				if(node.getWaitNodeWakeTime() < head.getWaitNodeWakeTime()) {
					WaitNode tempHead = head;
					head = node;
					head.setPreviousWaitNode(null);
					head.setNextWaitNode(tempHead);
					head.getWaitNodeNext().setPreviousWaitNode(head);
					
					++numNodes;
					break;
				}
				else if (node.getWaitNodeWakeTime() < queueIndex.getWaitNodeWakeTime()) {
					System.out.println("test 2");
					queueIndex.getWaitNodePrevious().setNextWaitNode(node);
					node.setPreviousWaitNode(queueIndex.getWaitNodePrevious());
					node.setNextWaitNode(queueIndex);
					queueIndex.setPreviousWaitNode(node);
					
					head.setNextWaitNode(queueIndex.getWaitNodePrevious());
					++numNodes;
					break;
				}
				
				
				// check if this is the last element in list.
				// catches the case where the wakeTime is higher than any element in the queue
				if(queueIndex.getWaitNodeNext() == null) {
					queueIndex.setNextWaitNode(node);
					++numNodes;
					break;
				}
				
				queueIndex = queueIndex.getWaitNodeNext();
			}
			
		}
				
		
	}
	
	
	
	/**
	 * Method to remove a node from the priority queue
	 * 
	 */
	public void removeWaitNode() {
		
		if(numNodes == 0) {
			System.out.print("There are no threads to be removed from the wait queue.\n");
		}
		else {
			
			//remove head from list, move it to ready queue in Alarm class
			head = head.getWaitNodeNext();
			--numNodes;
		}
	
	}
	
	
	
	
	
	
	/**
	 * toString to output "Thread: THREADNAME | WakeTime: WAKETIME"
	 */
	public String toString() {
		String string = "";
		
		WaitNode index = head;
		while(index != null) {
			
			string += index.toString() + "\n";
			index = index.getWaitNodeNext();
		}
		
		
		return string;
	}
	
	
}
