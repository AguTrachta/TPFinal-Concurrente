
import java.util.List;
import monitor.Monitor;
import monitor.Scheduler;
import pool.MyThreadFactory;
import pool.PoolManager;
import utils.Logger;
import utils.PetriNet;
import petrinet.Places;
import petrinet.Segment;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.info("Starting Petri net simulation.");

        // Start the timer.
        long startTime = System.currentTimeMillis();

        // Build the Petri net.
        PetriNet net = new PetriNet();

        // Retrieve segments, places, and the monitor.
        List<Segment> segments = net.getSegments();
        Places places = net.getPlaces();
        Monitor monitor = (Monitor) net.getMonitor();

        // Set up the thread pool with 4 threads.
        MyThreadFactory threadFactory = new MyThreadFactory("TestPoolThread");
        PoolManager poolManager = new PoolManager(4, threadFactory);

        // Create and start the Scheduler.
        Scheduler scheduler = new Scheduler(segments, poolManager);
        Thread schedulerThread = new Thread(scheduler, "SchedulerThread");
        schedulerThread.start();

        // Wait until the invariant condition is met (T11 fired 186 times).
        synchronized (monitor.getInvariantLock()) {
            while (monitor.getT11Counter() < 186) {
                try {
                    monitor.getInvariantLock().wait();
                } catch (InterruptedException e) {
                    logger.error("Main thread interrupted while waiting for invariants: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        logger.info("Completed 186 T-invariants (T11 fired 186 times).");

        // Stop the Scheduler and immediately shut down the thread pool.
        scheduler.stop();
        try {
            schedulerThread.join();
        } catch (InterruptedException e) {
            logger.error("Failed to join scheduler thread: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        poolManager.shutdownNow();

        // Stop the timer and compute elapsed time.
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // Print the final tokens for each place.
        System.out.println("Final tokens in Place 0: " + places.getTokenCount(0));
        System.out.println("Final tokens in Place 1: " + places.getTokenCount(1));
        System.out.println("Final tokens in Place 2: " + places.getTokenCount(2));
        System.out.println("Final tokens in Place 3: " + places.getTokenCount(3));
        System.out.println("Final tokens in Place 4: " + places.getTokenCount(4));
        System.out.println("Final tokens in Place 5: " + places.getTokenCount(5));
        System.out.println("Final tokens in Place 6: " + places.getTokenCount(6));
        System.out.println("Final tokens in Place 7: " + places.getTokenCount(7));
        System.out.println("Final tokens in Place 8: " + places.getTokenCount(8));
        System.out.println("Final tokens in Place 9: " + places.getTokenCount(9));
        System.out.println("Final tokens in Place 10: " + places.getTokenCount(10));
        System.out.println("Final tokens in Place 11: " + places.getTokenCount(11));
        System.out.println("Final tokens in Place 12: " + places.getTokenCount(12));
        System.out.println("Final tokens in Place 13: " + places.getTokenCount(13));
        System.out.println("Final tokens in Place 14: " + places.getTokenCount(14));

        logger.info("Petri net simulation ended.");
        logger.info("Elapsed time: " + elapsedTime + " ms");
        logger.close();
    }
}
