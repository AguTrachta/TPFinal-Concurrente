
package monitor;

import petrinet.Places;

/**
 * Implements a priority policy:
 * - The superior agent (e.g., transition 2) must generate at least 75% of the
 * reservations.
 * - The confirmation process (e.g., transition 6) must account for at least 80%
 * of confirmations.
 */
public class PriorityPolicy implements Policy {
  private int superiorCount = 0;
  private int inferiorCount = 0;
  private int confirmedCount = 0;
  private int cancelledCount = 0;

  @Override
  public boolean allowTransition(int transitionId, Places places) {
    if (transitionId == 2) { // Superior reservations
      int total = superiorCount + inferiorCount;
      if (total == 0)
        return true;
      double ratio = (double) superiorCount / total;
      // Allow if the ratio is still less than 75%
      return ratio < 0.75;
    } else if (transitionId == 3) { // Inferior reservations
      int total = superiorCount + inferiorCount;
      if (total == 0)
        return true;
      double ratio = (double) superiorCount / total;
      // Allow inferior reservations only if the superior ratio is already at least
      // 75%
      return ratio >= 0.75;
    } else if (transitionId == 6) { // Confirmation
      int total = confirmedCount + cancelledCount;
      if (total == 0)
        return true;
      double ratio = (double) confirmedCount / total;
      return ratio < 0.8;
    } else if (transitionId == 7) { // Cancellation
      int total = confirmedCount + cancelledCount;
      if (total == 0)
        return true;
      double ratio = (double) confirmedCount / total;
      return ratio >= 0.8;
    }
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

  // Optionally, add getters for analysis.
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
