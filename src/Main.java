import java.util.List;
import java.util.Scanner;

import monitor.Monitor;
import monitor.Policy;
import monitor.PriorityPolicy;
import monitor.BalancedPolicy;
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

        // Ask the user to choose a policy.
        Policy policy = choosePolicy();

        // Start the stopwatch.
        long startTime = System.currentTimeMillis();

        // Construct the Petri net using the selected policy.
        PetriNet net = new PetriNet(policy);

        // Retrieve the segments, places, and monitor.
        List<Segment> segments = net.getSegments();
        Places places = net.getPlaces();
        Monitor monitor = (Monitor) net.getMonitor();

        // Configure the thread pool with 4 threads using the custom factory.
        MyThreadFactory threadFactory = new MyThreadFactory("TestPoolThread");
        PoolManager poolManager = new PoolManager(4, threadFactory);

        // Start the Monitor Scheduler.
        monitor.startScheduler(segments, poolManager);

        // Wait until the invariant condition is met (T11 fired 186 times).
        synchronized (monitor.getInvariantLock()) {
            while (monitor.getT0Counter() < 187) {
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
        monitor.stopScheduler();
        poolManager.shutdownNow();

        // Stop the stopwatch and calculate elapsed time.
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // Print final token counts in each Place.
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

        // Print policy-specific statistics.
        if (monitor.getPolicy() instanceof PriorityPolicy) {
            PriorityPolicy prioPolicy = (PriorityPolicy) monitor.getPolicy();
            System.out.println("Superior reservations count: " + prioPolicy.getSuperiorCount());
            System.out.println("Inferior reservations count: " + prioPolicy.getInferiorCount());
            System.out.println("Confirmed reservations count: " + prioPolicy.getConfirmedCount());
            System.out.println("Cancelled reservations count: " + prioPolicy.getCancelledCount());

            double fourthInvariant = prioPolicy.getSuperiorCount() * 0.8;
            double thirdInvariant = prioPolicy.getSuperiorCount() * 0.2;
            double secondInvariant = prioPolicy.getInferiorCount() * 0.8;
            double firstInvariant = prioPolicy.getInferiorCount() * 0.2;

            System.out.println("First invariant (T3 and T7): " + Math.round(firstInvariant));
            System.out.println("Second invariant (T3 and T6): " + Math.round(secondInvariant));
            System.out.println("Third invariant (T2 and T7): " + Math.round(thirdInvariant));
            System.out.println("Fourth invariant (T2 and T6): " + Math.round(fourthInvariant));
        }

        if (monitor.getPolicy() instanceof BalancedPolicy) {
            BalancedPolicy balPolicy = (BalancedPolicy) monitor.getPolicy();
            System.out.println("Superior reservations count: " + balPolicy.getSuperiorCount());
            System.out.println("Inferior reservations count: " + balPolicy.getInferiorCount());
            System.out.println("Confirmed reservations count: " + balPolicy.getConfirmedCount());
            System.out.println("Cancelled reservations count: " + balPolicy.getCancelledCount());
        }

        // Print the maximum number of simultaneously running tasks measured by PoolManager.
        System.out.println("Maximum number of concurrently running tasks: " 
                + poolManager.getMaxConcurrentTasks());

        logger.info("Petri net simulation ended.");
        logger.info("Elapsed time: " + elapsedTime + " ms");
        logger.close();
    }

    /**
     * Asks the user to choose a policy:
     * 1 for BalancedPolicy or 2 for PriorityPolicy.
     * PriorityPolicy is used by default if an invalid option is chosen.
     *
     * @return the selected Policy instance.
     */
    private static Policy choosePolicy() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Select a policy:");
            System.out.println("1 - Balanced Policy");
            System.out.println("2 - Priority Policy");
            System.out.print("Enter your choice: ");

            int choice = 2; // default option
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            switch (choice) {
                case 1:
                    System.out.println("Balanced Policy selected.");
                    return new BalancedPolicy();
                case 2:
                    System.out.println("Priority Policy selected.");
                    return new PriorityPolicy();
                default:
                    System.out.println("Invalid choice. Defaulting to Priority Policy.");
                    return new PriorityPolicy();
            }
        }
    }
}
