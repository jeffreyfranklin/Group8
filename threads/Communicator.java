package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
	
	private int listenersWaiting, messageWaiting;
	private boolean messageExists;
	private Lock conditionLock;
	private Condition2 speakCondition, listenCondition;
	
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	
    	listenersWaiting = 0;
    	messageWaiting = 0;
    	messageExists = false;
    	conditionLock = new Lock();
    	speakCondition = new Condition2(conditionLock);
    	listenCondition = new Condition2(conditionLock);
    	
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	
    	conditionLock.acquire();
    	while(!conditionLock.isHeldByCurrentThread()){
    		//Wait
    	}
    	while(listenersWaiting == 0 || messageExists == true){
    		speakCondition.sleep();
    	}
    	messageWaiting = word;
    	messageExists = true;
    	listenCondition.wake();
    	conditionLock.release();
    	
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	
    	conditionLock.acquire();
    	while(!conditionLock.isHeldByCurrentThread()){
    		//Wait
    	}
    	listenersWaiting++;
    	while(!messageExists){
    		speakCondition.wake();
    		listenCondition.sleep();
    	}
    	listenersWaiting--;
    	int temp = messageWaiting;
    	messageExists = false;
    	speakCondition.wake();
    	conditionLock.release();
    	return temp;
    	
    }
    
    /**
     * Testing code
     */
    public static void selfTest(){
    	
    	long startTime, endTime, timeElapsed;
    	
    	//Case 1a
    	System.out.println("Testing Test Case 1a: More speakers than listeners (speakers first)");
    	startTime = System.nanoTime();
    	TestCase1a();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 1a completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 1b
    	System.out.println("Testing Test Case 1b: More speakers than listeners (listeners first)");
    	startTime = System.nanoTime();
    	TestCase1b();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 1b completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 1c
    	System.out.println("Testing Test Case 1c: More speakers than listeners (alternating)");
    	startTime = System.nanoTime();
    	TestCase1c();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 1c completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 2a
    	System.out.println("Testing Test Case 2a: More listeners than speakers (speakers first)");
    	startTime = System.nanoTime();
    	TestCase2a();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 2a completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 2b
    	System.out.println("Testing Test Case 2b: More listeners than speakers (listeners first)");
    	startTime = System.nanoTime();
    	TestCase2b();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 2b completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 2c
    	System.out.println("Testing Test Case 2c: More listeners than speakers (alternating)");
    	startTime = System.nanoTime();
    	TestCase2c();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 2c completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 3a
    	System.out.println("Testing Test Case 3a: Equal speakers and listeners (speakers first)");
    	startTime = System.nanoTime();
    	TestCase3a();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 3a completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 3b
    	System.out.println("Testing Test Case 3b: Equal speakers and listeners (listeners first)");
    	startTime = System.nanoTime();
    	TestCase3b();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 3b completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 3c
    	System.out.println("Testing Test Case 3c: Equal speakers and listeners (alternating)");
    	startTime = System.nanoTime();
    	TestCase3c();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 3c completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    	//Case 4
    	System.out.println("Testing Test Case 4: Large amount of random speakers and listeners");
    	startTime = System.nanoTime();
    	TestCase4();
    	endTime = System.nanoTime();
    	timeElapsed = endTime - startTime;
    	System.out.println("Case 4 completed in " + timeElapsed / 1000000 + " milliseconds.");
    	
    }
    
    /**
     * There are more speakers than listeners. First 3 speakers are created and then 2
     * listeners.
     */
    private static void TestCase1a(){
    	
    	final Communicator testCase1a = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase1a.listen());
    		}
    	};
    	
    	KThread[] speakThreads = new KThread[3];
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i] = new KThread();
    		speakThreads[i].setName("Speak Thread " + (i + 1));
    	}
		speakThreads[0].setTarget(new Runnable(){
			public void run(){
				testCase1a.speak(1);
			}
		});
		
		speakThreads[1].setTarget(new Runnable(){
			public void run(){
				testCase1a.speak(2);
			}
		});
		
		speakThreads[2].setTarget(new Runnable(){
			public void run(){
				testCase1a.speak(3);
			}
		});
		
		KThread[] listenThreads = new KThread[2];
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i] = new KThread(runListener);
    		listenThreads[i].setName("Listener Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i].fork();
    	}
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i].fork();
    	}
    	speakThreads[0].join();
    	
    }
    
    /**
     * There are more speakers than listeners. First 2 listeners are created and then 3
     * speakers.
     */
    private static void TestCase1b(){
    	
    	final Communicator testCase1b = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase1b.listen());
    		}
    	};
		
		KThread[] listenThreads = new KThread[2];
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i] = new KThread(runListener);
    		listenThreads[i].setName("Listener Thread " + (i + 1));
    	}
    	
    	KThread[] speakThreads = new KThread[3];
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i] = new KThread();
    		speakThreads[i].setName("Speak Thread " + (i + 1));
    	}
		speakThreads[0].setTarget(new Runnable(){
			public void run(){
				testCase1b.speak(1);
			}
		});
		
		speakThreads[1].setTarget(new Runnable(){
			public void run(){
				testCase1b.speak(2);
			}
		});
		
		speakThreads[2].setTarget(new Runnable(){
			public void run(){
				testCase1b.speak(3);
			}
		});

    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i].fork();
    	}
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i].fork();
    	}
    	listenThreads[0].join();
    	
    }

    /**
     * There are more speakers than listeners. Alternating between speakers and listeners.
     */
    private static void TestCase1c(){
    	
    	final Communicator testCase1c = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase1c.listen());
    		}
    	};
    	
    	KThread speakThread1 = new KThread();
    	speakThread1.setName("Speak Thread 1");
    	speakThread1.setTarget(new Runnable(){
			public void run(){
				testCase1c.speak(1);
			}
		});
    	
    	KThread listenThread1 = new KThread(runListener);
    	listenThread1.setName("Listener Thread 1");
    	
    	KThread speakThread2 = new KThread();
    	speakThread2.setName("Speak Thread 2");
    	speakThread2.setTarget(new Runnable(){
			public void run(){
				testCase1c.speak(2);
			}
		});
    	
    	KThread listenThread2 = new KThread(runListener);
    	listenThread2.setName("Listener Thread 2");
    	
    	KThread speakThread3 = new KThread();
    	speakThread3.setName("Speak Thread 3");
    	speakThread3.setTarget(new Runnable(){
			public void run(){
				testCase1c.speak(3);
			}
		});
    	
    	speakThread1.fork();
    	listenThread1.fork();
    	speakThread2.fork();
    	listenThread2.fork();
    	speakThread3.fork();
    	
    	speakThread1.join();
    	
    }
    
    /**
     * There are more listeners than speakers. First 2 speakers are created and then 3
     * listeners.
     */
    private static void TestCase2a(){
    	
    	final Communicator testCase2a = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase2a.listen());
    		}
    	};
    	
    	KThread[] speakThreads = new KThread[2];
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i] = new KThread();
    		speakThreads[i].setName("Speak Thread " + (i + 1));
    	}
		speakThreads[0].setTarget(new Runnable(){
			public void run(){
				testCase2a.speak(1);
			}
		});
		
		speakThreads[1].setTarget(new Runnable(){
			public void run(){
				testCase2a.speak(2);
			}
		});
		
		KThread[] listenThreads = new KThread[3];
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i] = new KThread(runListener);
    		listenThreads[i].setName("Listener Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i].fork();
    	}
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i].fork();
    	}
    	speakThreads[0].join();
    	
    }
    
    /**
     * There are more listeners than speakers. First 2 listeners are created and then 3
     * speakers.
     */
    private static void TestCase2b(){
    	
    	final Communicator testCase2b = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase2b.listen());
    		}
    	};
		
		KThread[] listenThreads = new KThread[3];
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i] = new KThread(runListener);
    		listenThreads[i].setName("Listener Thread " + (i + 1));
    	}
    	
    	KThread[] speakThreads = new KThread[2];
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i] = new KThread();
    		speakThreads[i].setName("Speak Thread " + (i + 1));
    	}
		speakThreads[0].setTarget(new Runnable(){
			public void run(){
				testCase2b.speak(1);
			}
		});
		
		speakThreads[1].setTarget(new Runnable(){
			public void run(){
				testCase2b.speak(2);
			}
		});

    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i].fork();
    	}
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i].fork();
    	}
    	listenThreads[0].join();
    	
    }
    
    /**
     * There are more listeners than speakers. Alternating between speakers and listeners.
     */
    private static void TestCase2c(){
    	
    	final Communicator testCase2c = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase2c.listen());
    		}
    	};
    	
    	KThread speakThread1 = new KThread();
    	speakThread1.setName("Speak Thread 1");
    	speakThread1.setTarget(new Runnable(){
			public void run(){
				testCase2c.speak(1);
			}
		});
    	
    	KThread listenThread1 = new KThread(runListener);
    	listenThread1.setName("Listener Thread 1");
    	
    	KThread speakThread2 = new KThread();
    	speakThread2.setName("Speak Thread 2");
    	speakThread2.setTarget(new Runnable(){
			public void run(){
				testCase2c.speak(2);
			}
		});
    	
    	KThread listenThread2 = new KThread(runListener);
    	listenThread2.setName("Listener Thread 2");
    	
    	KThread listenThread3 = new KThread(runListener);
    	listenThread3.setName("Listener Thread 3");
    	
    	speakThread1.fork();
    	listenThread1.fork();
    	speakThread2.fork();
    	listenThread2.fork();
    	listenThread3.fork();
    	
    	speakThread1.join();
    	
    }
    
    /**
	 * There are an equal amount of speakers and listeners. First 2 speakers are created
	 * and then 2 listeners.
	 */
    private static void TestCase3a(){
    	
    	final Communicator testCase3a = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase3a.listen());
    		}
    	};
    	
    	KThread[] speakThreads = new KThread[2];
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i] = new KThread();
    		speakThreads[i].setName("Speak Thread " + (i + 1));
    	}
		speakThreads[0].setTarget(new Runnable(){
			public void run(){
				testCase3a.speak(1);
			}
		});
		
		speakThreads[1].setTarget(new Runnable(){
			public void run(){
				testCase3a.speak(2);
			}
		});
		
		KThread[] listenThreads = new KThread[2];
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i] = new KThread(runListener);
    		listenThreads[i].setName("Listener Thread " + (i + 1));
    	}
    	
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i].fork();
    	}
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i].fork();
    	}
    	speakThreads[0].join();
    	
    }
    
    /**
	 * There are an equal amount of speakers and listeners. First 2 listeners are created
	 * and then 2 speakers.
	 */
    private static void TestCase3b(){
    	
    	final Communicator testCase3b = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase3b.listen());
    		}
    	};
		
		KThread[] listenThreads = new KThread[2];
    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i] = new KThread(runListener);
    		listenThreads[i].setName("Listener Thread " + (i + 1));
    	}
    	
    	KThread[] speakThreads = new KThread[2];
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i] = new KThread();
    		speakThreads[i].setName("Speak Thread " + (i + 1));
    	}
		speakThreads[0].setTarget(new Runnable(){
			public void run(){
				testCase3b.speak(1);
			}
		});
		
		speakThreads[1].setTarget(new Runnable(){
			public void run(){
				testCase3b.speak(2);
			}
		});

    	for(int i = 0; i < listenThreads.length; i++){
    		listenThreads[i].fork();
    	}
    	for(int i = 0; i < speakThreads.length; i++){
    		speakThreads[i].fork();
    	}
    	listenThreads[0].join();
    	
    }
    
    /**
	 * There are an equal amount of speakers and listeners. Alternating between speakers
	 * and listeners.
	 */
    private static void TestCase3c(){
    	
    	final Communicator testCase3c = new Communicator();
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			System.out.println(testCase3c.listen());
    		}
    	};
    	
    	KThread speakThread1 = new KThread();
    	speakThread1.setName("Speak Thread 1");
    	speakThread1.setTarget(new Runnable(){
			public void run(){
				testCase3c.speak(1);
			}
		});
    	
    	KThread listenThread1 = new KThread(runListener);
    	listenThread1.setName("Listener Thread 1");
    	
    	KThread speakThread2 = new KThread();
    	speakThread2.setName("Speak Thread 2");
    	speakThread2.setTarget(new Runnable(){
			public void run(){
				testCase3c.speak(2);
			}
		});
    	
    	KThread listenThread2 = new KThread(runListener);
    	listenThread2.setName("Listener Thread 2");
    	
    	speakThread1.fork();
    	listenThread1.fork();
    	speakThread2.fork();
    	listenThread2.fork();
    	
    	speakThread1.join();
    	
    }
    
    /**
	 * There are a large amount of random speakers and listeners. 100 random speakers
	 * and listeners are created to test the performance of the implementation.
	 */
    private static void TestCase4(){
    	
    	final Communicator testCase4 = new Communicator();
    	
    	Runnable runSpeaker = new Runnable(){
    		public void run(){
    			testCase4.speak((int)(Math.random() * 10));
    		}
    	};
    	
    	Runnable runListener = new Runnable(){
    		public void run(){
    			testCase4.listen();
    		}
    	};
    	
    	KThread[] threads = new KThread[100];
    	int numSpeakers = 0;
    	int numListeners = 0;
    	for(int i = 0; i < threads.length; i++){
    		double random = Math.random();
    		if(random < 0.5){
    			numSpeakers++;
    			threads[i] = new KThread(runSpeaker);
    			threads[i].setName("Speak Thread " + numSpeakers);
    		}else{
    			numListeners++;
    			threads[i] = new KThread(runListener);
    			threads[i].setName("Listener Thread " + numListeners);
    		}
    	}
    	
    	for(int i = 0; i < threads.length; i++){
    		threads[i].fork();
    	}
    	threads[0].join();
    	
    }

}
