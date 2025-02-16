
package monitor;

import petrinet.Places;

/**
 * Interface for conflict resolution and priority policies.
 */
public interface Policy {
  /**
   * Checks whether a given transition is allowed to fire given the current state.
   * 
   * @param transitionId the id of the transition to check.
   * @param places       the current state of the places.
   * @return true if the transition is allowed; false otherwise.
   */
  boolean allowTransition(int transitionId, Places places);

  /**
   * Updates internal counters or state after a transition is fired.
   * 
   * @param transitionId the id of the transition that was fired.
   * @param places       the current state of the places.
   */
  void updateCounters(int transitionId, Places places);
}
