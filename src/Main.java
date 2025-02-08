
import java.util.*;
import pool.PoolManager;
import pool.MyThreadFactory;
import monitor.Monitor;
import monitor.MonitorInterface;
import petrinet.Places;
import petrinet.Segment;
import petrinet.Transition;

/**
 * Main class to test a more complex Petri net.
 */
public class Main {
  public static void main(String[] args) {
    // 1. Create a shared Places object and initialize places.
    Places places = new Places();
    places.addPlace(1, 1); // Place 1 starts with 1 token.
    places.addPlace(2, 1); // Place 2 starts with 1 token.
    places.addPlace(3, 0); // Place 3 starts with 0 tokens.
    places.addPlace(4, 0); // Place 4 starts with 0 tokens.

    // 2. Create transitions.

    // Transition 1: Consumes 1 token from Place 1 and 1 token from Place 2, then
    // produces 1 token in Place 3.
    Map<Integer, Integer> pre1 = new HashMap<>();
    pre1.put(1, 1);
    pre1.put(2, 1);
    Map<Integer, Integer> post1 = new HashMap<>();
    post1.put(3, 1);
    Transition t1 = new Transition(1, pre1, post1);

    // Transition 2: Temporal transition; consumes 1 token from Place 3 and produces
    // 1 token in Place 4,
    // with a delay of 500ms.
    Map<Integer, Integer> pre2 = new HashMap<>();
    pre2.put(3, 1);
    Map<Integer, Integer> post2 = new HashMap<>();
    post2.put(4, 1);
    Transition t2 = new Transition(2, pre2, post2, true, 500);

    // 3. Build a mapping of transitions (transition id -> Transition).
    Map<Integer, Transition> transitions = new HashMap<>();
    transitions.put(t1.getId(), t1);
    transitions.put(t2.getId(), t2);

    // 4. Create a Monitor with the shared Places and transitions map.
    MonitorInterface monitor = new Monitor(places, transitions);

    // 5. Create segments.
    // Segment A will handle Transition 1.
    List<Transition> segATransitions = new ArrayList<>();
    segATransitions.add(t1);
    Segment segmentA = new Segment("Segment A", segATransitions, monitor, places);

    // Segment B will handle Transition 2.
    List<Transition> segBTransitions = new ArrayList<>();
    segBTransitions.add(t2);
    Segment segmentB = new Segment("Segment B", segBTransitions, monitor, places);

    // 6. Set up the thread pool using the custom ThreadFactory and PoolManager.
    MyThreadFactory threadFactory = new MyThreadFactory("PoolThread");
    PoolManager poolManager = new PoolManager(4, threadFactory);

    // Submit the segments to the thread pool.
    poolManager.submitTask(segmentA);
    poolManager.submitTask(segmentB);

    // 7. Let the simulation run for a while (e.g., 3 seconds).
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // 8. Signal the segments to stop.
    segmentA.stop();
    segmentB.stop();

    // 9. Shutdown the thread pool gracefully.
    poolManager.shutdown();

    // 10. Print the final token counts for each place.
    System.out.println("Final tokens in Place 1: " + places.getTokenCount(1));
    System.out.println("Final tokens in Place 2: " + places.getTokenCount(2));
    System.out.println("Final tokens in Place 3: " + places.getTokenCount(3));
    System.out.println("Final tokens in Place 4: " + places.getTokenCount(4));
  }
}
