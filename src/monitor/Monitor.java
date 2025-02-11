package monitor;

import petrinet.Places;
import petrinet.Transition;
import utils.Logger;
import java.util.Map;

/**
 * The Monitor coordinates the firing of transitions.
 * It checks that transitions are enabled, consults the active Policy,
 * fires the transition, and updates counters.
 *
 * This version also maintains a counter for T11 (the designated closing
 * transition)
 * and uses an extra lock (invariantLock) to notify waiting threads when T11
 * fires enough times.
 */
public class Monitor implements MonitorInterface {

  private final Places places;
  // A map of transition id to Transition object.
  private final Map<Integer, Transition> transitions;
  // The active policy (could be BalancedPolicy or PriorityPolicy).
  private final Policy policy;
  private static final Logger logger = Logger.getInstance();

  // Counter for how many times T11 has fired.
  private int t11Counter = 0;
  // Lock object used to signal when the T11 counter reaches 186.
  private final Object invariantLock = new Object();

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
      // logger.debug("Transition " + transitionId + " is not enabled.");
      return false;
    }

    // Consult the policy before firing.
    if (!policy.allowTransition(transitionId, places)) {
      // logger.debug("Policy did not allow transition " + transitionId + " to
      // fire.");
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

      // If this is the closing transition T11, update the counter.
      if (transitionId == 11) {
        t11Counter++;
        // If we have reached 186 invariants, notify waiting threads.
        if (t11Counter >= 186) {
          synchronized (invariantLock) {
            invariantLock.notifyAll();
          }
        }
      }

      // logger.info("Transition " + transitionId + " fired successfully.");
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
   * Returns the count of how many times T11 has fired.
   * This count can be used as a measure of completed invariant cycles.
   *
   * @return the T11 firing counter.
   */
  public synchronized int getT11Counter() {
    return t11Counter;
  }

  /**
   * Returns the lock object used for waiting for invariant completion.
   *
   * @return the invariantLock.
   */
  public Object getInvariantLock() {
    return invariantLock;
  }
}
