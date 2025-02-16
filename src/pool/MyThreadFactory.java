package pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom ThreadFactory to create threads with a specific naming convention
 * and track the maximum number of concurrently active threads.
 */
public class MyThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger threadCount = new AtomicInteger(0);
    // Counter for currently active threads.
    private final AtomicInteger currentActiveThreads = new AtomicInteger(0);
    // Maximum number of active threads reached at any moment.
    private final AtomicInteger maxActiveThreads = new AtomicInteger(0);

    /**
     * Constructs a MyThreadFactory with the specified base name for threads.
     *
     * @param baseName the base name for threads created by this factory.
     */
    public MyThreadFactory(String baseName) {
        this.baseName = baseName;
    }

    /**
     * Creates a new thread with a custom name and wraps the Runnable to track
     * the number of active threads.
     *
     * @param r the Runnable task to execute.
     * @return a new Thread instance with a custom name.
     */
    @Override
    public Thread newThread(Runnable r) {
        // Wrap the runnable to track active threads.
        Runnable wrappedRunnable = () -> {
            // Increment the counter at the start.
            int active = currentActiveThreads.incrementAndGet();
            // Update the maximum if necessary.
            maxActiveThreads.updateAndGet(max -> Math.max(max, active));
            try {
                r.run();
            } finally {
                // Decrement the counter when finished.
                currentActiveThreads.decrementAndGet();
            }
        };

        Thread thread = new Thread(wrappedRunnable);
        thread.setName(baseName + "-" + threadCount.incrementAndGet());
        return thread;
    }

    /**
     * Returns the maximum number of threads that were active simultaneously.
     *
     * @return the maximum number of active threads.
     */
    public int getMaxActiveThreads() {
        return maxActiveThreads.get();
    }
}
