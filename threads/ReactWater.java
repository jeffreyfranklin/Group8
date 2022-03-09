package nachos.threads;

import nachos.machine.*;

public class ReactWater{

	private int  hCounter, oCounter;
	private ThreadQueue hQueue, oQueue;
	private Lock conditionLock;
	
    /** 
     *   Constructor of ReactWater
     **/
    public ReactWater() {

    	hCounter = 0;
    	oCounter = 0;
    	hQueue = ThreadedKernel.scheduler.newThreadQueue(false);
    	oQueue = ThreadedKernel.scheduler.newThreadQueue(false);
    	conditionLock = new Lock();
    	
    } // end of ReactWater()

    /** 
     *   When H element comes, if there already exist another H element 
     *   and an O element, then call the method of Makewater(). Or let 
     *   H element wait in line. 
     **/ 
    public void hReady() {
    	
    	boolean intStatus = Machine.interrupt().disable();
    	
    	conditionLock.acquire();
    	while(!conditionLock.isHeldByCurrentThread()){
    		//Wait
    	}
    	hCounter++;
    	hQueue.waitForAccess(KThread.currentThread());
    	if(hCounter >= 2 && oCounter >= 1){
    		Makewater();
    	}
    	conditionLock.release();
    	KThread.sleep();
    	
    	Machine.interrupt().restore(intStatus);
    	
    } // end of hReady()
 
    /** 
     *   When O element comes, if there already exist another two H
     *   elements, then call the method of Makewater(). Or let O element
     *   wait in line. 
     **/ 
    public void oReady() {

    	boolean intStatus = Machine.interrupt().disable();
    	
    	conditionLock.acquire();
    	while(!conditionLock.isHeldByCurrentThread()){
    		//Wait
    	}
    	oCounter++;
    	Machine.interrupt().disable();
    	oQueue.waitForAccess(KThread.currentThread());
    	if(hCounter >= 2 && oCounter >= 1){
    		Makewater();
    	}
    	conditionLock.release();
    	KThread.sleep();
    	
    	Machine.interrupt().restore(intStatus);
    	
    } // end of oReady()
    
    /** 
     *   Print out the message of "water was made!".
     **/
    public void Makewater() {

    	//H element 1
    	hQueue.nextThread();
    	hCounter--;
    	//H element 2
    	hQueue.nextThread();
    	hCounter--;
    	//O element
    	oQueue.nextThread();
    	oCounter--;
    	System.out.println("water was made!");
    	
    } // end of Makewater()
    
    /**
     * Testing code
     */
    public static void selfTest(){
    	
    	long startTime, endTime, timeElapsed;
    	
    	//Case 1a
    	System.out.println("Testing Test Case 1a: More H elements than O elements (H first)");
    	startTime = System.nanoTime();
    	TestCase1a();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 1a completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 1b
    	System.out.println("Testing Test Case 1b: More H elements than O elements (O first)");
    	startTime = System.nanoTime();
    	TestCase1b();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 1b completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 1c
    	System.out.println("Testing Test Case 1c: More H elements than O elements (alternating)");
    	startTime = System.nanoTime();
    	TestCase1c();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 1c completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 2a
    	System.out.println("Testing Test Case 2a: More O elements than H elements (H first)");
    	startTime = System.nanoTime();
    	TestCase2a();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 2a completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 2b
    	System.out.println("Testing Test Case 2b: More O elements than H elements (O first)");
    	startTime = System.nanoTime();
    	TestCase2b();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 2b completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 2c
    	System.out.println("Testing Test Case 2c: More O elements than H elements (alternating)");
    	startTime = System.nanoTime();
    	TestCase2c();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 2c completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 3a
    	System.out.println("Testing Test Case 3a: Equal H and O elements (H first)");
    	startTime = System.nanoTime();
    	TestCase3a();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 3a completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 3b
    	System.out.println("Testing Test Case 3b: Equal H and O elements (O first)");
    	startTime = System.nanoTime();
    	TestCase3b();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 3b completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 3c
    	System.out.println("Testing Test Case 3c: Equal H and O elements (alternating)");
    	startTime = System.nanoTime();
    	TestCase3c();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 3c completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 4
    	System.out.println("Testing Test Case 4: Large amount of random H and O elements");
    	startTime = System.nanoTime();
    	TestCase4();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 4 completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    }
    
    /**
     * There are more H elements than O elements. First 5 H elements are created and then 2 O
     * elements.
     */
    private static void TestCase1a(){
    	
    	final ReactWater testCase1a = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase1a.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase1a.oReady();
    		}
    	};
    	
    	KThread[] hThreads = new KThread[5];
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i] = new KThread(runHydrogen);
    		hThreads[i].setName("Hydrogen Thread " + (i + 1));
    	}
    	
    	KThread[] oThreads = new KThread[2];
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i] = new KThread(runOxygen);
    		oThreads[i].setName("Oxygen Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].fork();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].fork();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].join();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].join();
    	}
    	
    }
    
    /**
     * There are more H elements than O elements. First 2 O elements are created and then 5 H
     * elements.
     */
	private static void TestCase1b(){
	    	
		final ReactWater testCase1b = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase1b.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase1b.oReady();
    		}
    	};
    	
    	KThread[] oThreads = new KThread[2];
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i] = new KThread(runOxygen);
    		oThreads[i].setName("Oxygen Thread " + (i + 1));
    	}
    	
    	KThread[] hThreads = new KThread[5];
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i] = new KThread(runHydrogen);
    		hThreads[i].setName("Hydrogen Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].fork();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].fork();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].join();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].join();
    	}
	    	
	}
	
	/**
     * There are more H elements than O elements. Alternating between sets of 2 H elements and
     * 1 O element.
     */
	private static void TestCase1c(){
		
		final ReactWater testCase1c = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase1c.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase1c.oReady();
    		}
    	};
    	
    	KThread hThread1 = new KThread(runHydrogen);
    	hThread1.setName("Hydrogen Thread 1");
    	
    	KThread hThread2 = new KThread(runHydrogen);
    	hThread2.setName("Hydrogen Thread 2");
    	
    	KThread oThread1 = new KThread(runOxygen);
    	oThread1.setName("Oxygen Thread 1");
    	
    	KThread hThread3 = new KThread(runHydrogen);
    	hThread3.setName("Hydrogen Thread 3");
    	
    	KThread hThread4 = new KThread(runHydrogen);
    	hThread4.setName("Hydrogen Thread 4");
    	
    	KThread oThread2 = new KThread(runOxygen);
    	oThread2.setName("Oxygen Thread 2");
    	
    	KThread hThread5 = new KThread(runHydrogen);
    	hThread5.setName("Hydrogen Thread 5");
    	
    	hThread1.fork();
    	hThread2.fork();
    	oThread1.fork();
    	hThread3.fork();
    	hThread4.fork();
    	oThread2.fork();
    	hThread5.fork();
    	
    	hThread1.join();
    	hThread2.join();
    	oThread1.join();
    	hThread3.join();
    	hThread4.join();
    	oThread2.join();
    	hThread5.join();
		
	}
	
	/**
	 * There are more O elements than H elements. First 4 H elements are created and then 3 O
	 * elements.
	 */
	private static void TestCase2a(){
		
		final ReactWater testCase2a = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase2a.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase2a.oReady();
    		}
    	};
    	
    	KThread[] hThreads = new KThread[4];
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i] = new KThread(runHydrogen);
    		hThreads[i].setName("Hydrogen Thread " + (i + 1));
    	}
    	
    	KThread[] oThreads = new KThread[3];
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i] = new KThread(runOxygen);
    		oThreads[i].setName("Oxygen Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].fork();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].fork();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].join();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].join();
    	}
		
	}
	
	/**
	 * There are more O elements than H elements. First 3 O elements are created and then 4 H
	 * elements.
	 */
	private static void TestCase2b(){
    	
		final ReactWater testCase2b = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase2b.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase2b.oReady();
    		}
    	};
    	
    	KThread[] oThreads = new KThread[3];
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i] = new KThread(runOxygen);
    		oThreads[i].setName("Oxygen Thread " + (i + 1));
    	}
    	
    	KThread[] hThreads = new KThread[4];
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i] = new KThread(runHydrogen);
    		hThreads[i].setName("Hydrogen Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].fork();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].fork();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].join();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].join();
    	}
    	
    }
	
	/**
	 * There are more O elements than H elements. Alternating between sets of 2 H elements and
     * 1 O element.
	 */
	private static void TestCase2c(){
		
		final ReactWater testCase2c = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase2c.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase2c.oReady();
    		}
    	};
    	
    	KThread hThread1 = new KThread(runHydrogen);
    	hThread1.setName("Hydrogen Thread 1");
    	
    	KThread hThread2 = new KThread(runHydrogen);
    	hThread2.setName("Hydrogen Thread 2");
    	
    	KThread oThread1 = new KThread(runOxygen);
    	oThread1.setName("Oxygen Thread 1");
    	
    	KThread hThread3 = new KThread(runHydrogen);
    	hThread3.setName("Hydrogen Thread 3");
    	
    	KThread hThread4 = new KThread(runHydrogen);
    	hThread4.setName("Hydrogen Thread 4");
    	
    	KThread oThread2 = new KThread(runOxygen);
    	oThread2.setName("Oxygen Thread 2");
    	
    	KThread oThread3 = new KThread(runHydrogen);
    	oThread3.setName("Oxygen Thread 3");
    	
    	hThread1.fork();
    	hThread2.fork();
    	oThread1.fork();
    	hThread3.fork();
    	hThread4.fork();
    	oThread2.fork();
    	oThread3.fork();
    	
    	hThread1.join();
    	hThread2.join();
    	oThread1.join();
    	hThread3.join();
    	hThread4.join();
    	oThread2.join();
    	oThread3.join();
		
	}

	/**
	 * There are an equal amount of H and O elements. First 4 H elements are created and then 2
	 * O elements.
	 */
	private static void TestCase3a(){
    	
		final ReactWater testCase3a = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase3a.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase3a.oReady();
    		}
    	};
    	
    	KThread[] hThreads = new KThread[4];
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i] = new KThread(runHydrogen);
    		hThreads[i].setName("Hydrogen Thread " + (i + 1));
    	}
    	
    	KThread[] oThreads = new KThread[2];
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i] = new KThread(runOxygen);
    		oThreads[i].setName("Oxygen Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].fork();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].fork();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].join();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].join();
    	}
    	
    }
	
	/**
	 * There are an equal amount of H and O elements. First 2 O elements are created and then 4
	 * H elements.
	 */
	private static void TestCase3b(){
    	
		final ReactWater testCase3b = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase3b.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase3b.oReady();
    		}
    	};
    	
    	KThread[] oThreads = new KThread[2];
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i] = new KThread(runOxygen);
    		oThreads[i].setName("Oxygen Thread " + (i + 1));
    	}
    	
    	KThread[] hThreads = new KThread[4];
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i] = new KThread(runHydrogen);
    		hThreads[i].setName("Hydrogen Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].fork();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].fork();
    	}
    	for(int i = 0; i < oThreads.length; i++){
    		oThreads[i].join();
    	}
    	for(int i = 0; i < hThreads.length; i++){
    		hThreads[i].join();
    	}
    	
    }
	
	/**
	 * There are an equal amount of H and O elements. Alternating between sets of 2 H elements
	 * and 1 O element.
	 */
	private static void TestCase3c(){
		
		final ReactWater testCase3c = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase3c.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase3c.oReady();
    		}
    	};
    	
    	KThread hThread1 = new KThread(runHydrogen);
    	hThread1.setName("Hydrogen Thread 1");
    	
    	KThread hThread2 = new KThread(runHydrogen);
    	hThread2.setName("Hydrogen Thread 2");
    	
    	KThread oThread1 = new KThread(runOxygen);
    	oThread1.setName("Oxygen Thread 1");
    	
    	KThread hThread3 = new KThread(runHydrogen);
    	hThread3.setName("Hydrogen Thread 3");
    	
    	KThread hThread4 = new KThread(runHydrogen);
    	hThread4.setName("Hydrogen Thread 4");
    	
    	KThread oThread2 = new KThread(runOxygen);
    	oThread2.setName("Oxygen Thread 2");
    	
    	hThread1.fork();
    	hThread2.fork();
    	oThread1.fork();
    	hThread3.fork();
    	hThread4.fork();
    	oThread2.fork();
    	
    	hThread1.join();
    	hThread2.join();
    	oThread1.join();
    	hThread3.join();
    	hThread4.join();
    	oThread2.join();
		
	}
	
	/**
	 * There are a large amount of random H and O elements. 1000 random H or O elements are
	 * created to test the performance of the implementation.
	 */
	private static void TestCase4(){
    	
		final ReactWater testCase4 = new ReactWater();
    	
    	Runnable runHydrogen = new Runnable(){
    		public void run(){
    			testCase4.hReady();
    		}
    	};
    	
    	Runnable runOxygen = new Runnable(){
    		public void run(){
    			testCase4.oReady();
    		}
    	};
    	
    	KThread[] threads = new KThread[1000];
    	int numHydrogen = 0;
    	int numOxygen = 0;
    	for(int i = 0; i < threads.length; i++){
    		double random = Math.random();
    		if(random < 0.5){
    			numHydrogen++;
    			threads[i] = new KThread(runHydrogen);
    			threads[i].setName("Hydrogen Thread " + numHydrogen);
    		}else{
    			numOxygen++;
    			threads[i] = new KThread(runOxygen);
    			threads[i].setName("Hydrogen Thread " + numOxygen);
    		}
    	}
    	
    	for(int i = 0; i < threads.length; i++){
    		threads[i].fork();
    	}
    	for(int i = 0; i < threads.length; i++){
    		threads[i].join();
    	}
    	
    }

} // end of class ReactWater
