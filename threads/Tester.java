package nachos.threads;

public class Tester {
	
	public static void main(String[] args) {
		
		KThread thread = new KThread();
		KThread thread2 = new KThread();
		KThread thread3 = new KThread();
		thread.setName("1");
		thread2.setName("2");
		thread3.setName("3");
		
		WaitQueue waitQueue = new WaitQueue();
		ReadyQueue readyQueue = new ReadyQueue();
		
		WaitNode node = new WaitNode(thread, 6);
		waitQueue.addWaitNode(node);
		WaitNode node2 = new WaitNode(thread2, 5);
		waitQueue.addWaitNode(node2);
		WaitNode node3 = new WaitNode(thread3, 4);
		waitQueue.addWaitNode(node3);
		
		System.out.print(waitQueue.toString());
		
		System.out.println();
		
		waitQueue.removeWaitNode();
		waitQueue.removeWaitNode();
		waitQueue.removeWaitNode();
		waitQueue.removeWaitNode();
		System.out.println(waitQueue.toString());
		
		
		readyQueue.addReadyNode(thread);
		readyQueue.addReadyNode(thread2);
		readyQueue.addReadyNode(thread3);
		System.out.println(readyQueue.toString());
		readyQueue.removeReadyNode();
		readyQueue.removeReadyNode();
		readyQueue.removeReadyNode();
		readyQueue.removeReadyNode();
		System.out.println(readyQueue.toString());
	}
}
