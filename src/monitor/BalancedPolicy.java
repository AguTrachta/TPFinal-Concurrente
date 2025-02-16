
package monitor;

import petrinet.Places;

/**
 * Implements a balanced policy:
 * - The number of clients handled by the superior agent (e.g., via transition
 * 2)
 * should be balanced with those handled by the inferior agent (e.g., transition
 * 3).
 * - Similarly, the number of confirmations should match cancellations.
 */
public class BalancedPolicy implements Policy {
  // Internal counters for analysis (you could also retrieve these from the Places
  // if modeled there)
  private int superiorCount = 0;
  private int inferiorCount = 0;
  private int confirmedCount = 0;
  private int cancelledCount = 0;

  @Override
  public boolean allowTransition(int transitionId, Places places) {
    // Transition 2: Superior reservations (associated with place P6)
    // Transition 3: Inferior reservations (associated with place P7)
    // Transition 6: Confirmation
    // Transition 7: Cancellation
    if (transitionId == 2) { // Superior agent
      return superiorCount <= inferiorCount;
    } else if (transitionId == 3) { // Inferior agent
      return inferiorCount <= superiorCount;
    } else if (transitionId == 6) { // Confirmation
      return confirmedCount <= cancelledCount;
    } else if (transitionId == 7) { // Cancellation
      return cancelledCount <= confirmedCount;
    }
    // For transitions not governed by the policy, allow by default.
    return true;
  }

  @Override
  public void updateCounters(int transitionId, Places places) {
    if (transitionId == 2) {
      superiorCount++;
    } else if (transitionId == 3) {
      inferiorCount++;
    } else if (transitionId == 6) {
      confirmedCount++;
    } else if (transitionId == 7) {
      cancelledCount++;
    }
  }

  public int getSuperiorCount() {
    return superiorCount;
  }

  public int getInferiorCount() {
    return inferiorCount;
  }

  public int getConfirmedCount() {
    return confirmedCount;
  }

  public int getCancelledCount() {
    return cancelledCount;
  }
}
