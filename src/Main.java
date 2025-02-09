
import java.util.*;
import pool.PoolManager;
import pool.MyThreadFactory;
import monitor.Monitor;
import monitor.MonitorInterface;
import monitor.BalancedPolicy;
import monitor.PriorityPolicy;
import monitor.Policy;
import petrinet.Places;
import petrinet.Segment;
import petrinet.Transition;
import utils.Logger;

/**
 * Main class to test the Petri net with a BalancedPolicy.
 * 
 * The Petri net modeled here has:
 * - Two branches from place p3:
 * t2 (superior reservation) consumes from p3 and p6, then produces a token in
 * p9.
 * t3 (inferior reservation) consumes from p3 and p7, then produces a token in
 * p9.
 * - Two outcome transitions from p9:
 * t6 (confirmation) consumes from p9 and produces a token in p_confirm.
 * t7 (cancellation) consumes from p9 and produces a token in p_cancel.
 * 
 * p_confirm and p_cancel count the number of confirmed and cancelled
 * reservations.
 */
public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.info("Starting Petri net simulation with BalancedPolicy.");

        // 1. Create the shared Places object and initialize places.
        Places places = new Places();
        // p3: Branch point for reservation requests.
        places.addPlace(3, 10); // Start with 10 tokens so multiple reservations can occur.
        // p6 and p7: Reservation agent tokens.
        places.addPlace(6, 10); // Superior agent token.
        places.addPlace(7, 10); // Inferior agent token.
        // p9: Junction where both reservation branches deposit a token.
        places.addPlace(9, 0);
        // p_confirm and p_cancel: Counters for outcome.
        places.addPlace(100, 0); // p_confirm: Use an arbitrary id (e.g., 100) for confirmed count.
        places.addPlace(101, 0); // p_cancel: Use an arbitrary id (e.g., 101) for cancelled count.
        logger.info("Initialized Places: p3=10, p6=1, p7=1, p9=0, p_confirm=0, p_cancel=0.");

        // 2. Create transitions.
        // t2: Superior reservation (id = 2)
        // Pre: p3 (1 token), p6 (1 token); Post: p9 (1 token)
        Map<Integer, Integer> preT2 = new HashMap<>();
        preT2.put(3, 1);
        preT2.put(6, 1);
        Map<Integer, Integer> postT2 = new HashMap<>();
        postT2.put(9, 1);
        Transition t2 = new Transition(2, preT2, postT2);

        // t3: Inferior reservation (id = 3)
        // Pre: p3 (1 token), p7 (1 token); Post: p9 (1 token)
        Map<Integer, Integer> preT3 = new HashMap<>();
        preT3.put(3, 1);
        preT3.put(7, 1);
        Map<Integer, Integer> postT3 = new HashMap<>();
        postT3.put(9, 1);
        Transition t3 = new Transition(3, preT3, postT3);

        // t6: Confirmation (id = 6)
        // Pre: p9 (1 token); Post: p_confirm (1 token, using place id 100)
        Map<Integer, Integer> preT6 = new HashMap<>();
        preT6.put(9, 1);
        Map<Integer, Integer> postT6 = new HashMap<>();
        postT6.put(100, 1);
        Transition t6 = new Transition(6, preT6, postT6);

        // t7: Cancellation (id = 7)
        // Pre: p9 (1 token); Post: p_cancel (1 token, using place id 101)
        Map<Integer, Integer> preT7 = new HashMap<>();
        preT7.put(9, 1);
        Map<Integer, Integer> postT7 = new HashMap<>();
        postT7.put(101, 1);
        Transition t7 = new Transition(7, preT7, postT7);

        // 3. Build a mapping of transitions.
        Map<Integer, Transition> transitions = new HashMap<>();
        transitions.put(t2.getId(), t2);
        transitions.put(t3.getId(), t3);
        transitions.put(t6.getId(), t6);
        transitions.put(t7.getId(), t7);

        // 4. Create a Policy (BalancedPolicy) and a Monitor with the Places,
        // Transitions, and Policy.
        // Policy policy = new BalancedPolicy();
        Policy policy = new PriorityPolicy();
        MonitorInterface monitor = new Monitor(places, transitions, policy);
        logger.info("Monitor created with BalancedPolicy.");

        // 5. Create segments.
        // Segment Reservation: handles transitions t2 and t3.
        List<Transition> reservationTransitions = new ArrayList<>();
        reservationTransitions.add(t2);
        reservationTransitions.add(t3);
        Segment segmentReservation = new Segment("Segment Reservation", reservationTransitions, monitor, places);

        // Segment Outcome: handles transitions t6 and t7.
        List<Transition> outcomeTransitions = new ArrayList<>();
        outcomeTransitions.add(t6);
        outcomeTransitions.add(t7);
        Segment segmentOutcome = new Segment("Segment Outcome", outcomeTransitions, monitor, places);
        logger.info("Segments created for Reservation (t2,t3) and Outcome (t6,t7).");

        // 6. Set up the thread pool using our custom ThreadFactory and PoolManager.
        MyThreadFactory threadFactory = new MyThreadFactory("PolicyPoolThread");
        PoolManager poolManager = new PoolManager(4, threadFactory);
        logger.info("Thread pool set up with maximum 4 concurrent threads.");

        // Submit the segments to the thread pool.
        poolManager.submitTask(segmentReservation);
        poolManager.submitTask(segmentOutcome);
        logger.info("Submitted Reservation and Outcome segments to thread pool.");

        // 7. Let the simulation run for a while (e.g., 5 seconds).
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted during sleep: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // 8. Signal the segments to stop.
        segmentReservation.stop();
        segmentOutcome.stop();
        logger.info("Stop signals sent to segments.");

        // 9. Shutdown the thread pool gracefully.
        poolManager.shutdown();
        logger.info("Thread pool shutdown gracefully.");

        if (policy instanceof PriorityPolicy) {
            PriorityPolicy priorityPolicy = (PriorityPolicy) policy;
            System.out.println("Superior reservations count: " + priorityPolicy.getSuperiorCount());
            System.out.println("Inferior reservations count: " + priorityPolicy.getInferiorCount());
        }

        // 10. Print the final token counts for the outcome places.
        System.out.println("Final tokens in p_confirm (place id 100): " + places.getTokenCount(100));
        System.out.println("Final tokens in p_cancel (place id 101): " + places.getTokenCount(101));
        logger.info("Final Outcome: p_confirm = " + places.getTokenCount(100) +
                ", p_cancel = " + places.getTokenCount(101));

        logger.info("Petri net simulation with BalancedPolicy ended.");
        logger.close();
    }
}
