
package monitor;

import petrinet.Places;
import petrinet.Transition;
import utils.Logger;
import java.util.Map;
import java.util.List;
import pool.PoolManager;
import petrinet.Segment;

/**
 * The Monitor coordinates the firing of transitions.
 * It checks that transitions are enabled, consults the active Policy,
 * fires the transition, and updates counters.
 *
 * This version also maintains a counter for T11 (the designated closing
 * transition) and uses an extra lock (invariantLock) to notify waiting threads
 * when T11 fires enough times.
 * 
 * Additionally, the Monitor now manages the Scheduler thread.
 */
public class Monitor implements MonitorInterface {

  private final Places places;
  // A map of transition id to Transition object.
  private final Map<Integer, Transition> transitions;
  // The active policy (could be BalancedPolicy or PriorityPolicy).
  private final Policy policy;
  private static final Logger logger = Logger.getInstance();

  // Counter for how many times T11 has fired.
  private int t0Counter = 0;
  // Lock object used to signal when the T11 counter reaches 186.
  private final Object invariantLock = new Object();

  // For Scheduler management:
  private Thread schedulerThread;
  private Scheduler scheduler;

  /**
   * Constructs a Monitor with the given Places, Transitions, and Policy.
   *
   * @param places      the shared Places object.
   * @param transitions a mapping from transition IDs to Transition objects.
   * @param policy      the active Policy object to enforce rules.
   */
  public Monitor(Places places, Map<Integer, Transition> transitions, Policy policy) {
    this.places = places;
    this.transitions = transitions;
    this.policy = policy;
    logger.info("Monitor created with policy: " + policy.getClass().getSimpleName());
  }

  /**
   * Attempts to fire the specified transition.
   * This method is synchronized to ensure safe concurrent access.
   *
   * @param transitionId the identifier of the transition to fire.
   * @return true if the transition was successfully fired; false otherwise.
   */
  @Override
  public synchronized boolean fireTransition(int transitionId) {
    Transition transition = transitions.get(transitionId);
    if (transition == null) {
      logger.error("Transition " + transitionId + " not found.");
      return false;
    }

    if (!transition.isEnabled(places)) {
      // Transition is not enabled (tokens missing).
      return false;
    }

    // Consult the policy before firing.
    if (!policy.allowTransition(transitionId, places)) {
      return false;
    }

    try {
      transition.fire(places);
      // Update policy counters after successful firing.
      policy.updateCounters(transitionId, places);

      if (!places.checkInvariants()) {
        logger.error("Invariants violated after firing transition " + transitionId + ".");
        return false;
      }

      // If this is the closing transition (T11), update the counter.
      // (Note: In your original code T11 is represented by transition id 0.)
      if (transitionId == 0) {
        t0Counter++;
        // If we have reached 186 invariants, notify waiting threads.
        if (t0Counter >= 187) {
          synchronized (invariantLock) {
            invariantLock.notifyAll();
          }
        }
      }

      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Transition " + transitionId + " firing was interrupted.");
      return false;
    } catch (Exception e) {
      logger.error("Error firing transition " + transitionId + ": " + e.getMessage());
      return false;
    }
  }

  /**
   * Returns the count of how many times the closing transition has fired.
   *
   * @return the T11 firing counter.
   */
  public synchronized int getT0Counter() {
    return t0Counter;
  }

  /**
   * Returns the lock object used for waiting for invariant completion.
   *
   * @return the invariantLock.
   */
  public Object getInvariantLock() {
    return invariantLock;
  }

  public Policy getPolicy() {
    return policy;
  }

  /**
   * Starts the scheduler thread with the provided segments and pool manager.
   *
   * @param segments    the list of segments to be scheduled.
   * @param poolManager the thread pool manager.
   */
  public void startScheduler(List<Segment> segments, PoolManager poolManager) {
    scheduler = new Scheduler(segments, poolManager);
    schedulerThread = new Thread(scheduler, "SchedulerThread");
    schedulerThread.start();
    logger.info("Scheduler thread started by Monitor.");
  }

  /**
   * Stops the scheduler thread gracefully.
   */
  public void stopScheduler() {
    if (scheduler != null) {
      scheduler.stop();
      try {
        schedulerThread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("Scheduler thread interruption during stop: " + e.getMessage());
      }
      logger.info("Scheduler thread stopped.");
    }
  }
}
