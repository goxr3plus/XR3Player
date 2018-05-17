package main.java.com.goxr3plus.xr3player.streamplayer;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory with the ability to set the thread name prefix. This class is exactly similar to
 * {@link java.util.concurrent.Executors#defaultThreadFactory()} from JDK8, except for the thread naming feature.
 *
 * <p>
 * The factory creates threads that have names on the form <i>prefix-N-thread-M</i>, where <i>prefix</i> is a string provided in the constructor,
 * <i>N</i> is the sequence number of this factory, and <i>M</i> is the sequence number of the thread created by this factory.
 */
public class ThreadFactoryWithNamePrefix implements ThreadFactory {
	
	// Note:  The source code for this class was based entirely on 
	// Executors.DefaultThreadFactory class from the JDK8 source.
	// The only change made is the ability to configure the thread
	// name prefix.
	
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	
	/**
	 * Creates a new ThreadFactory where threads are created with a name prefix of <code>prefix</code>.
	 *
	 * @param prefix
	 *            Thread name prefix. Never use a value of "pool" as in that case you might as well have used
	 *            {@link java.util.concurrent.Executors#defaultThreadFactory()}.
	 */
	public ThreadFactoryWithNamePrefix(String prefix) {
		SecurityManager s = System.getSecurityManager();
		group = ( s != null ) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}
