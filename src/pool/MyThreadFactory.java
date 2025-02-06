
package pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom ThreadFactory to create threads with a specific naming convention.
 */
public class MyThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger threadCount = new AtomicInteger(0);

    /**
     * Constructs a MyThreadFactory with the specified base name for threads.
     *
     * @param baseName the base name for threads created by this factory.
     */
    public MyThreadFactory(String baseName) {
        this.baseName = baseName;
    }

    /**
     * Creates a new thread with a custom name.
     *
     * @param r the Runnable task to execute.
     * @return a new Thread instance with a custom name.
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(baseName + "-" + threadCount.incrementAndGet());
        // Optionally, you can set other thread properties here (e.g., daemon status).
        return thread;
    }
}
