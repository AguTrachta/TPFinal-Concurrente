import java.util.*;
import monitor.Monitor;
import monitor.MonitorInterface;
import monitor.Scheduler;
import monitor.BalancedPolicy; // You can also use PriorityPolicy if desired
import petrinet.Places;
import petrinet.Segment;
import petrinet.Transition;
import pool.PoolManager;
import pool.MyThreadFactory;
import utils.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.info("Starting Petri net simulation (3 segments, 2 threads).");

        // 1. Create the shared Places object and initialize places.
        // Place IDs: 1, 2, 3, 4, 5.
        Places places = new Places();
        places.addPlace(1, 4); // Starting token in Place 1.
        places.addPlace(2, 0);
        places.addPlace(3, 0);
        places.addPlace(4, 0);
        places.addPlace(5, 0);

        // 2. Create Transitions.
        // Transition 1: from Place 1 to Place 2.
        Map<Integer, Integer> preT1 = new HashMap<>();
        preT1.put(1, 1);
        Map<Integer, Integer> postT1 = new HashMap<>();
        postT1.put(2, 1);
        Transition t1 = new Transition(1, preT1, postT1);

        // Transition 2: from Place 2 to Place 3 (policy-controlled).
        Map<Integer, Integer> preT2 = new HashMap<>();
        preT2.put(2, 1);
        Map<Integer, Integer> postT2 = new HashMap<>();
        postT2.put(3, 1);
        Transition t2 = new Transition(2, preT2, postT2);

        // Transition 3: from Place 2 to Place 4 (policy-controlled).
        Map<Integer, Integer> preT3 = new HashMap<>();
        preT3.put(2, 1);
        Map<Integer, Integer> postT3 = new HashMap<>();
        postT3.put(4, 1);
        Transition t3 = new Transition(3, preT3, postT3);

        // Transition 4: from Place 3 to Place 5.
        Map<Integer, Integer> preT4 = new HashMap<>();
        preT4.put(3, 1);
        Map<Integer, Integer> postT4 = new HashMap<>();
        postT4.put(5, 1);
        Transition t4 = new Transition(4, preT4, postT4);

        // Transition 5: from Place 4 to Place 5.
        Map<Integer, Integer> preT5 = new HashMap<>();
        preT5.put(4, 1);
        Map<Integer, Integer> postT5 = new HashMap<>();
        postT5.put(5, 1);
        Transition t5 = new Transition(5, preT5, postT5);

        // 3. Build a Map of all transitions (for the Monitor).
        Map<Integer, Transition> transitions = new HashMap<>();
        transitions.put(t1.getId(), t1);
        transitions.put(t2.getId(), t2);
        transitions.put(t3.getId(), t3);
        transitions.put(t4.getId(), t4);
        transitions.put(t5.getId(), t5);

        // 4. Create a Policy (here using BalancedPolicy) and the Monitor.
        BalancedPolicy policy = new BalancedPolicy();
        MonitorInterface monitor = new Monitor(places, transitions, policy);

        // 5. Create the Segments.
        // Segment 1 handles Transition 1.
        List<Transition> seg1Transitions = new ArrayList<>();
        seg1Transitions.add(t1);
        Segment segment1 = new Segment("Segment 1", seg1Transitions, monitor, places);

        // Segment 2 handles Transitions 2 and 4.
        List<Transition> seg2Transitions = new ArrayList<>();
        seg2Transitions.add(t2);
        seg2Transitions.add(t4);
        Segment segment2 = new Segment("Segment 2", seg2Transitions, monitor, places);

        // Segment 3 handles Transitions 3 and 5.
        List<Transition> seg3Transitions = new ArrayList<>();
        seg3Transitions.add(t3);
        seg3Transitions.add(t5);
        Segment segment3 = new Segment("Segment 3", seg3Transitions, monitor, places);

        // 6. Prepare a list of segments.
        List<Segment> segments = new ArrayList<>();
        segments.add(segment1);
        segments.add(segment2);
        segments.add(segment3);

        // 7. Set up the thread pool with 2 threads.
        MyThreadFactory threadFactory = new MyThreadFactory("TestPoolThread");
        PoolManager poolManager = new PoolManager(2, threadFactory);

        // 8. Create and start the Scheduler (which monitors segments and submits
        // tasks).
        Scheduler scheduler = new Scheduler(segments, poolManager);
        Thread schedulerThread = new Thread(scheduler, "SchedulerThread");
        schedulerThread.start();

        // 9. Let the simulation run for a while (e.g., 5 seconds).
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // 10. Stop the scheduler and shut down the thread pool gracefully.
        scheduler.stop();
        try {
            schedulerThread.join();
        } catch (InterruptedException e) {
            logger.error("Failed to join scheduler thread: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        poolManager.shutdown();

        // 11. Print the final tokens in each place.
        System.out.println("Final tokens in Place 1: " + places.getTokenCount(1));
        System.out.println("Final tokens in Place 2: " + places.getTokenCount(2));
        System.out.println("Final tokens in Place 3: " + places.getTokenCount(3));
        System.out.println("Final tokens in Place 4: " + places.getTokenCount(4));
        System.out.println("Final tokens in Place 5: " + places.getTokenCount(5));

        logger.info("Petri net simulation ended.");
        logger.close();
    }
}
