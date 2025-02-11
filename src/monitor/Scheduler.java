
package monitor;

import java.util.List;
import pool.PoolManager;
import petrinet.Segment;
import utils.Logger;
import utils.TransitionNotifier;

/**
 * Scheduler is a dedicated thread that continuously monitors all segments
 * and submits them to the thread pool for execution when they have enabled
 * transitions.
 * This implementation uses a ReentrantLock and Condition for efficient waiting.
 */
public class Scheduler implements Runnable {
    private final List<Segment> segments;
    private final PoolManager poolManager;
    private volatile boolean running = true;
    private static final Logger logger = Logger.getInstance();

    public Scheduler(List<Segment> segments, PoolManager poolManager) {
        this.segments = segments;
        this.poolManager = poolManager;
    }

    /**
     * Checks if any segment is ready (i.e. has at least one enabled transition).
     */
    private boolean anySegmentIsReady() {
        for (Segment segment : segments) {
            if (segment.canBeScheduled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        logger.info("Scheduler started.");
        while (running) {
            boolean scheduled = false;
            // Check each segment to see if it can be scheduled.
            for (Segment segment : segments) {
                if (segment.canBeScheduled()) {
                    // logger.info("Scheduler submitting segment: " + segment.getSegmentName());
                    poolManager.submitTask(segment);
                    scheduled = true;
                }
            }
            if (!scheduled) {
                // Use our ReentrantLock and Condition to wait until a change occurs.
                TransitionNotifier.lock.lock();
                try {
                    // Double-check the condition once the lock is acquired.
                    if (!anySegmentIsReady()) {
                        try {
                            logger.debug("Scheduler awaiting signal on transitionsEnabled condition.");
                            TransitionNotifier.transitionsEnabled.await();
                        } catch (InterruptedException e) {
                            logger.warn("Scheduler interrupted during await.");
                            Thread.currentThread().interrupt();
                        }
                    }
                } finally {
                    TransitionNotifier.lock.unlock();
                }
            }
        }
        logger.info("Scheduler stopped.");
    }

    /**
     * Stops the scheduler gracefully and signals any waiting thread.
     */
    public void stop() {
        running = false;
        TransitionNotifier.lock.lock();
        try {
            TransitionNotifier.transitionsEnabled.signalAll();
        } finally {
            TransitionNotifier.lock.unlock();
        }
    }
}
