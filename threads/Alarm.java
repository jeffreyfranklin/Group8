package nachos.threads;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	
	WaitQueue waitQueue;
	ReadyQueue readyQueue;
	
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
    	waitQueue = new WaitQueue();
    	readyQueue = new ReadyQueue();
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	KThread.yield();
		boolean loopFlag = true;
		while(loopFlag){
			
			if(waitQueue.getWaitQueueHead().getWaitNodeWakeTime() <= System.nanoTime()) {
				readyQueue.addReadyNode(waitQueue.getWaitQueueHead().getWaitNodeThread());
				waitQueue.removeWaitNode();
			}
			
		}
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
    	long wakeTime = System.nanoTime() + x;
		
		if(System.nanoTime() >= wakeTime) {
			KThread.currentThread().ready();
		}
		else {
			WaitNode node = new WaitNode(KThread.currentThread(),wakeTime);
			waitQueue.addWaitNode(node);
		}
    }
}
