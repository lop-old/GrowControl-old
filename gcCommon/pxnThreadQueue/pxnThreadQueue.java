package com.growcontrol.gcCommon.pxnThreadQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.growcontrol.gcCommon.TimeU;
import com.growcontrol.gcCommon.pxnUtils;
import com.growcontrol.gcCommon.pxnClock.pxnCoolDown;
import com.growcontrol.gcCommon.pxnLogger.pxnLog;
import com.growcontrol.gcCommon.pxnLogger.pxnLogger;


public class pxnThreadQueue implements Runnable {
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	// thread count limits
	public static final int HardLimit = 20;
	public static final int GlobalMax = 50;

	protected final ThreadGroup group;
	protected final List<Thread> threads;
	protected final String queueName;
	protected final BlockingQueue<pxnRunnable> queue = new ArrayBlockingQueue<pxnRunnable>(10);

	protected volatile int threadPriority = Thread.NORM_PRIORITY;
	protected volatile boolean stopping = false;
	protected volatile int maxThreads = 1;
	protected volatile int active = 0;
	protected volatile int runCount = 0;
	// cool-down
	protected final pxnCoolDown coolMaxReached = pxnCoolDown.get("5s");

	// main thread
	protected static volatile pxnThreadQueue mainThread = null;
	protected static final Object lock = new Object();

	protected static final List<pxnThreadQueue> instances = new ArrayList<pxnThreadQueue>();


	public static pxnThreadQueue getMain() {
		if(mainThread == null) {
			synchronized(lock) {
				if(mainThread == null)
					mainThread = new pxnThreadQueue("main");
			}
		}
		return mainThread;
	}
	public static void addToMain(String name, Runnable runnable) {
		getMain().addQueue(name, runnable);
	}


	public pxnThreadQueue(String name) {
		this(name, null);
	}
	public pxnThreadQueue(String name, Integer maxThreads) {
		if(name == null || name.isEmpty()) throw new NullPointerException("name cannot be null!");
		this.queueName = name;
		if(maxThreads != null)
			setMax(maxThreads);
		//getLogger().setBracers("(", ")");
		// main thread (single)
		if(name == "main") {
			threads = null;
			group = null;
			maxThreads = 1;
		// thread pool (multi)
		} else {
			threads = new ArrayList<Thread>();
			group = new ThreadGroup(this.queueName);
			addThread();
		}
		synchronized(instances) {
			instances.add(this);
		}
		// thread queue started
		addQueue("Thread-Startup", new Runnable() {
			@Override
			public void run() {
				getLogger().debug("Started thread queue..");
			}
		});
	}


	// thread count
	public static int getGlobalThreadCount() {
		int count = 0;
		synchronized(instances) {
			for(pxnThreadQueue queue : instances)
				count += queue.getThreadCount();
		}
		return count;
	}
	public int getThreadCount() {
		if(threads == null)
			return 1;
		return threads.size();
	}


	// run state
	public boolean isRunning() {
		return active > 0;
	}
	public boolean isStopping() {
		return stopping;
	}
	public int activeCount() {
		return active;
	}


	// max threads
	public int getMax() {
		return maxThreads;
	}
	public void setMax(int maxThreads) {
		if(queueName.equalsIgnoreCase("main")) return;
		this.maxThreads = pxnUtils.MinMax(maxThreads, 0, HardLimit);
	}


	// set thread name
	private void setThreadName() {
		setThreadName(null);
	}
	private void setThreadName(String name) {
		if(name == null || name.isEmpty())
			name = queueName;
		Thread.currentThread().setName(name);
	}


	// thread priority
	public void setPriority(int priority) {
		this.threadPriority = priority;
		synchronized(this.threads) {
			for(Thread thread : this.threads)
				thread.setPriority(priority);
		}
	}


	// run thread queue
	@Override
	public void run() {
		setThreadName();
		// main thread
		if(threads == null)
			if(isRunning() || stopping)
				return;
		int inactiveCount = 0;
		while(!stopping) {
			pxnRunnable task = null;
			try {
				//run = queue.take();
				task = queue.poll(1, TimeU.S);
				inactiveCount += 1;
			} catch (InterruptedException e) {
				getLogger().exception(e);
				break;
			}
			if(active < 0) active = 0;
			// inactive thread
			if(task == null) {
				// inactive thread after 5 minutes
				if(inactiveCount > 300 && threads != null) {
					getLogger().info("Inactive thread..");
					break;
				}
			// active thread
			} else {
				runCount++;
				active++;
				inactiveCount = 0;
				getSubLogger(task.getTaskName()).finest("Running thread task..");
				setThreadName(task.getTaskName());
				try {
					task.run();
					// low priority can sleep
					if(threadPriority <= (Thread.NORM_PRIORITY - Thread.MIN_PRIORITY) / 2 )
						pxnUtils.Sleep(10);
				} catch (Exception e) {
					getSubLogger(task.getTaskName()).exception(e);
				}
				setThreadName();
				active--;
			}
		}
//		getLogger().finer("Stopped thread queue");
	}


	// queue thread shutdown
	public void Shutdown() {
		addQueue("Thread-Queue-Stopping", new Runnable() {
			@Override
			public void run() {
				getLogger().fine("Stopping thread queue..");
				stopping = true;
			}
		});
	}
	// exit program
	public static void Exit() {
		mainThread.addQueue("Exit", new Runnable() {
			@Override
			public void run() {
				// threads still running
				displayStillRunning();
				System.out.println();
				System.out.println();
				System.exit(0);
			}
		});
	}
	// display threads still running
	private static void displayStillRunning() {
		String[] threadNames = pxnUtils.getThreadNames();
		if(threadNames == null || threadNames.length == 0) return;
		String msg = "Threads still running:  [ "+Integer.toString(threadNames.length)+" ]";
		for(String name : threadNames)
			msg += "\n  "+name;
		pxnLog.get().Publish(msg);
	}


	// add to queue
	public void addQueue(String name, Runnable run) {
		if(run == null) throw new NullPointerException("run can't be null!");
		// run in main thread queue
		if(getMax() <= 0) {
			addToMain(name, run);
			return;
		}
		try {
			pxnRunnable r = new pxnRunnable(name, run);
			if(queue.offer(r, 1, TimeU.S))
				getSubLogger(name).finest("Thread task queued..");
			else
				getSubLogger(name).severe("Thread queue is full!!!");
		} catch (InterruptedException e) {
			getSubLogger(name).exception(e);
		}
		// add new thread (if all are in use)
		addThread();
	}


	// add new thread (if all are in use)
	protected synchronized void addThread() {
		if(stopping || getMax() <= 0) return;
		// main thread
		if(this.threads == null) return;
		// thread count (this pool)
		int count = threads.size();
		int free = count - active;
		// thread count (global)
		int globalCount = getGlobalThreadCount();
		int globalFree = GlobalMax - globalCount;
		getLogger().finest(
			"Pool size: "+Integer.toString(count)+" [ "+Integer.toString(getMax())+" ]  "+
			"Active/Free: "+
				Integer.toString(active)+"/"+
				Integer.toString(free)+"  "+
			"Global: "+Integer.toString(globalCount)+" [ "+GlobalMax+" ]"
		);
		// use existing thread
		if(free > 0) return;
		// restart existing thread
		if(active < count){
			synchronized(this.threads) {
		        for(Thread t : this.threads) {
					//if(t == null) continue;
					if(t.isAlive()) continue;
					t.start();
					break;
				}
//TODO:
//				// drop extra threads
//				if(count - active > 8) {
//					for(Thread t : this.threads)
//				}
			}
			return;
		}
		// global max threads
		if(globalFree <= 0) {
			if(coolMaxReached.Again())
				getLogger().warning("Global max threads limit [ "+Integer.toString(globalCount)+" ] reached!");
			else
				getLogger().finest("Global max threads limit [ "+Integer.toString(globalCount)+" ] reached!");
			pxnUtils.Sleep(10);
			return;
		}
		// max threads
		if(count >= getMax()) {
			if(getMax() > 1) {
				if(coolMaxReached.Again())
					getLogger().warning("Max threads limit [ "+Integer.toString(count)+" ] reached!");
				else
					getLogger().finest("Max threads limit [ "+Integer.toString(count)+" ] reached!");
			}
			pxnUtils.Sleep(10);
			return;
		}
		// new thread
		synchronized(this.threads) {
			Thread thread = new Thread(group, this);
			this.threads.add(thread);
			thread.start();
			thread.setPriority(threadPriority);
		}
//		Thread thread = new Thread(this);
//		thread.setName("pxnThreadQueue_"+queueName);
//		this.threads.add(thread);
//		thread.start();
//		getLogger().info(
//			"Threads: "+count+
//			" active: "+Integer.toString(active)+
//			" max: "+Integer.toString(getMax())
//		);
	}


	// logger
	private volatile pxnLogger log = null;
	private final Object logLock = new Object();
	protected pxnLogger getLogger() {
		if(log == null) {
			synchronized(logLock) {
				if(log == null)
					log = pxnLog.get(queueName);
			}
		}
		return log;
	}
	protected pxnLogger getSubLogger(String name) {
		pxnLogger log = getLogger().get(name);
		log.setBracers("(", ")");
		return log;
	}


}
