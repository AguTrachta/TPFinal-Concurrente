package monitor;

import petrinet.Places;
import petrinet.Transition;
import utils.Logger;
import java.util.Map;

/**
 * The Monitor coordinates the firing of transitions.
 * It checks that transitions are enabled, consults the active Policy,
 * fires the transition, and updates counters.
 */
public class Monitor implements MonitorInterface {

  private final Places places;
  // A map of transition id to Transition object.
  private final Map<Integer, Transition> transitions;
  // The active policy (could be BalancedPolicy or PriorityPolicy).
  private final Policy policy;

  private static final Logger logger = Logger.getInstance();

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
      logger.debug("Transition " + transitionId + " is not enabled.");
      return false;
    }

    // Consult the policy before firing.
    if (!policy.allowTransition(transitionId, places)) {
      logger.debug("Policy did not allow transition " + transitionId + " to fire.");
      return false;
    }

    try {
      transition.fire(places);
      // Update the policy counters after a successful fire.
      policy.updateCounters(transitionId, places);

      if (!places.checkInvariants()) {
        logger.error("Invariants violated after firing transition " + transitionId + ".");
        return false;
      }

      logger.info("Transition " + transitionId + " fired successfully.");
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
}

